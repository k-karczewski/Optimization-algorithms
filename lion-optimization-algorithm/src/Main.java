import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import weka.core.Instances;
import java.util.Comparator;

import weka.core.converters.ConverterUtils.DataSource;

public class Main
{
    private static ParameterFile parameters;
    public static void main(String[] args) throws Exception
    {
        runOptimization(20, "J48");
        System.out.println("main works!");
    }

    public static void runOptimization(int initNumberOfLions, String nameOfClassifier) throws Exception
    {
        DataSource source = new DataSource("C:/Users/Konrad/Documents/magisterka/datasets/iris.csv");
		Instances data = source.getDataSet();
		parameters = new ParameterFile(nameOfClassifier);

		data.setClassIndex(data.numAttributes()-1);
	
		Instances[][] split = ClassifierClass.crossValidationSplit(data, parameters.NUMBER_OF_FOLDS());
        /* initialization */
        Lion[] lions = new Lion[initNumberOfLions];
        ArrayList<Lion> nomads = new ArrayList<Lion>();

        /* create lions in random places */
        for(int i = 0; i < lions.length; i++)
        {
            lions[i] = new Lion(nameOfClassifier, parameters.PARAMETERS_RANGES());
        }

        /* set %N of generated solutions as nomad lions */
        int countOfNomads = (int) Math.floor(parameters.PERCENT_TO_BE_NOMAD() * initNumberOfLions / 100.0);
        Lion.setRandomLionsAsNomad(lions, countOfNomads);

        for(int i = 0; i < lions.length; i++)
        {
            if(lions[i].isNomad() == true)
            {
                nomads.add(lions[i]);
            }
        }

        /* set rest of resident lions to prides randomly */
        Pride[] prides = new Pride[parameters.NUMBER_OF_PRIDES()];
        Pride.setLionsToPride(lions, prides);

        /* set lions' gender */
        for(int i = 0; i < prides.length; i++)
        {
            Pride.setLionsGenderForPrides(prides[i], parameters.PERCENT_OF_MALES_IN_PRIDE());
        }

        /* set gender of nomads */
        Pride.setLionsGenderForNomads(nomads, parameters.PERCENT_OF_FEMALES_IN_NOMADS(), countOfNomads);

        /* calculate initial fitnesses of lions */
        for(int i = 0; i < lions.length; i++)
        {
            lions[i].setFitness(Math.round(ClassifierClass.classify(lions[i].getPosition(), split, nameOfClassifier)*100.0)/100.0);
            lions[i].setBestFitness(lions[i].getFitness());
            lions[i].setBestPosition(lions[i].getPosition());
        }
        /* end of initialization */

        /* go hunting */
        for(Pride pride: prides)
        {
            ArrayList<ArrayList<Lion>> groups = Pride.dividePrideToGroups(pride, parameters.NUMBER_OF_GROUPS());
            int centerGroup = Pride.findCenterGroup(groups);
            float[] prey = Pride.generatePrey(groups);
            Pride.sendGroupsToHunt(groups, prey, centerGroup, split, parameters);
        }
        /* end of hunting */

        /* moving toward safe place */
        /* for each pride */
        for(Pride pride: prides)
        {
            int tournamentSize = Pride.getTournamentSize(pride);
            float[] destination = Pride.findPointByTournament(pride, tournamentSize);
            for(int j = 0; j < pride.lions.size(); j++)
            {
                Lion currentLion = pride.lions.get(j);
                /* if lion is a female and did not attack in previous step */
                if(currentLion.getGender().equals("female") && currentLion.getAttacked() == false)
                {
                    /* move rest of females toward destination */
                    float[] newPosition = new float[currentLion.getPosition().length];
                    newPosition = currentLion.moveTowardSafePosition(destination, parameters.NAME_OF_CLASSIFIER());
                   
                    /* TODO:  not sure yet if fitness should be calculated here or in next step */
                    /* TODO:  lions from step 2 still DO NOT HAVE updated their best finesses */
                    currentLion.setPosition(newPosition);
                    /* if parameters exceeded allowed range */
                    if(!currentLion.isPositionInCorrectRange(parameters.PARAMETERS_RANGES()))
                    {
                        currentLion.repairLionPosition(parameters.PARAMETERS_RANGES());
                    }
                    /* calculate fitness with fixed parameters */
                    currentLion.setFitness(Math.round(ClassifierClass.classify(currentLion.getPosition(), split, nameOfClassifier)*100)/100);
                    /* if fitness is greater than best fitness */
                    if(currentLion.getFitness() > currentLion.getBestFitness())
                    {
                        /* set new best fitness and best position */
                        currentLion.setBestFitness(currentLion.getFitness());
                        currentLion.setBestPosition(currentLion.getPosition());
                    }
                }
            }
        }
        /* end of moving toward safe place */

        /* start of roaming */
        for(Pride pride: prides)
        {
            List<Lion> males = pride.lions.stream().filter(x -> x.getGender().equals("male")).collect(Collectors.toList());

            for(Lion male: males)
            {
                Random random = new Random();
                int numberOfPlacesToRoam = random.nextInt(pride.lions.size()) + 1;
                ArrayList<Integer> randomIndexes = new ArrayList<Integer>();

                randomIndexes = Helpers.createIndexes(pride.lions.size());

                Collections.shuffle(randomIndexes);

                float[][] placesToRoam = new float[numberOfPlacesToRoam][];

                /* find places to roam */
                for(int i = 0; i < placesToRoam.length; i++)
                {
                    placesToRoam[i] = new float[pride.lions.get(i).getPosition().length];

                    for(int j = 0; j < placesToRoam[i].length; j++)
                    {
                        placesToRoam[i][j] = pride.lions.get(randomIndexes.get(i)).getPosition()[j];
                    }
                }

                /* move toward roam place */
                for(int i = 0; i < placesToRoam.length; i++)
                {
                    male.setPosition(male.moveTowardRoamPoint(placesToRoam[i]));

                    if(!male.isPositionInCorrectRange(parameters.PARAMETERS_RANGES()))
                    {
                        male.repairLionPosition(parameters.PARAMETERS_RANGES());
                    }

                    male.setFitness(ClassifierClass.classify(male.getPosition(), split, parameters.NAME_OF_CLASSIFIER()));
                    
                    /* if fitness is greater than best fitness */
                    if(male.getFitness() > male.getBestFitness())
                    {
                        /* set new best fitness and best position */
                        male.setBestFitness(male.getFitness());
                        male.setBestPosition(male.getPosition());
                    }
                }
            }
        }

        /* sort nomads descending by fitness */
        Collections.sort(nomads, Collections.reverseOrder());
        
        for(Lion nomad: nomads)
        {
            Lion bestNomad = nomads.get(0);
            double probability = 0.1 + Math.min(0.5, (nomad.getFitness() - bestNomad.getFitness())/bestNomad.getFitness());

            for(int i = 0; i < nomad.getPosition().length; i++)
            {
                if(Math.random() < probability)
                {
                    nomad.setPosition(Lion.generateRandomPosition(nameOfClassifier, parameters.PARAMETERS_RANGES()));
                }
            }

            if(!nomad.isPositionInCorrectRange(parameters.PARAMETERS_RANGES()))
            {
                nomad.repairLionPosition(parameters.PARAMETERS_RANGES());
            }
            nomad.setFitness(Math.round(ClassifierClass.classify(nomad.getPosition(), split, nameOfClassifier)*100.0)/100.0);
        }

        /* end of roaming */

        /**********************************************************************************/
        /************************************************************************* Mating */
        Random rand = new Random();

        /* For each pride */
        for(int p = 0; p < prides.length; p++)
        {
            /* For each dimension */
            // TODO: remove hardcoded digit and calculate number of dimensions
            for(int j = 0; j < 2; j++)
            {
                /* Females related calculations */
                List<Lion> femalesInPride = prides[p].lions.stream().filter(x -> x.getGender().equals("female")).collect(Collectors.toList());
                int percentOfFemalesToMate = rand.nextInt(100) + 1; /* (0-99) + 1 */  
                int numberOfFemalesToMate = Math.round(((femalesInPride.size() * percentOfFemalesToMate) / 100));
                /* Males related calculations */
                int numberOfMales = prides[p].lions.size() - femalesInPride.size();
                int percentOfMalesToMate = rand.nextInt(100) + 1; /* (0-99) + 1 */  
                int numberOfMalesToMate = Math.round(((numberOfMales * percentOfMalesToMate) / 100));
                /* Female mates with at least one male */
                if(numberOfMalesToMate == 0) numberOfMalesToMate = 1;

                /* randomly generated number with a normal distribution with mean value 0.5 and standard deviation 0.1 */    
                double beta = rand.nextGaussian() + 0.5 * 0.1;

                double offspring_01 = beta * numberOfFemalesToMate;
                for(int i = 0; i < numberOfMalesToMate; i++) 
                {
                    offspring_01 += (((1 - beta) / numberOfMalesToMate) * 1 * 1);
                }
                offspring_01 = Math.round(offspring_01);
                if(offspring_01 < 0) offspring_01 = 0;

                double offspring_02 = (1- beta) * numberOfFemalesToMate;
                for(int i = 0; i < numberOfMalesToMate; i++) 
                {
                    offspring_02 += ((beta / numberOfMalesToMate) * 1 * 1);
                }
                offspring_02 = Math.round(offspring_02);
                if(offspring_02 < 0) offspring_02 = 0;

                /* At this moment we know how big the offspring is. */
                /* We need to random which offspring is of male and which of female */
                boolean maleOrFemale = rand.nextBoolean(); /* 1 for male, 0 for female */

                String gender = (maleOrFemale) ? "male" : "female";
                Helpers.addNewLions((int)offspring_01, gender, prides[p].lions, p, nameOfClassifier, parameters.PARAMETERS_RANGES(), split, false);

                gender = (!maleOrFemale) ? "male" : "female";
                Helpers.addNewLions((int)offspring_02, gender, prides[p].lions, p, nameOfClassifier, parameters.PARAMETERS_RANGES(), split, false);
            }
        }

        /* Nomads */
        /* For each dimension */
        // TODO: remove hardcoded digit and calculate number of dimensions
        for(int j = 0; j < 2; j++)
        {
            /* Nomad females related calculations */
            List<Lion> nomadFemales = nomads.stream().filter(x -> x.getGender().equals("female")).collect(Collectors.toList());
            int percentOfNomadFemalesToMate = rand.nextInt(100) + 1; /* (0-99) + 1 */  
            int numberOfNomadFemalesToMate = Math.round(((nomadFemales.size() * percentOfNomadFemalesToMate) / 100));

            /* randomly generated number with a normal distribution with mean value 0.5 and standard deviation 0.1 */    
            double beta = rand.nextGaussian() + 0.5 * 0.1;

            double nomadOffspring_01 = beta * numberOfNomadFemalesToMate + (((1 - beta) / 1) * 1 * 1);
            nomadOffspring_01 = Math.round(nomadOffspring_01);
            if(nomadOffspring_01 < 0) nomadOffspring_01 = 0;

            double nomadOffspring_02 = (1- beta) * numberOfNomadFemalesToMate + ((beta / 1) * 1 * 1);
            nomadOffspring_02 = Math.round(nomadOffspring_02);
            if(nomadOffspring_02 < 0) nomadOffspring_02 = 0;

            /* At this moment we know how big the offspring is. */
            /* We need to random which offspring is of male and which of female */
            boolean maleOrFemale = rand.nextBoolean(); /* 1 for male, 0 for female */

            String gender = (maleOrFemale) ? "male" : "female";
            Helpers.addNewLions((int)nomadOffspring_01, gender, nomads, 0, nameOfClassifier, parameters.PARAMETERS_RANGES(), split, true);

            gender = (!maleOrFemale) ? "male" : "female";
            Helpers.addNewLions((int)nomadOffspring_02, gender, nomads, 0, nameOfClassifier, parameters.PARAMETERS_RANGES(), split, false);
        }

        // TODO: By Mating, LOA share information between genders while new cubs inherit character from both genders.

        /****************************************************************** End of mating */
        /**********************************************************************************/

        /************************************************************************ Defense */
        /**********************************************************************************/
        /* Defense against new mature resident males. */
        /* For each pride */
        for(Pride pride: prides)
        {
            /* Get all males in pride */
            List<Lion> allMalesInPride = pride.lions.stream().filter(x -> x.getGender().equals("male")).collect(Collectors.toList());
            int numberOfMalesToBecomeNomad = Math.round((allMalesInPride.size() * parameters.PERCENT_OF_WEAKEST_MALES_TO_BECOME_NOMAD()) / 100);
            // TODO: number calculated above will be equal 0 in most cases if 10% used as threshold
            // TODO: clarify if line below is necessary
            if(numberOfMalesToBecomeNomad == 0) numberOfMalesToBecomeNomad = 1;

            /* Sort males by fitness (ascending) */
            Collections.sort(allMalesInPride);

            /* Set x weakest lions as nomads */
            for(int i = 0; i < numberOfMalesToBecomeNomad; i++)
            {
                Lion lionToBeNomad = allMalesInPride.get(i);
                lionToBeNomad.setIsNomad(true);
                lionToBeNomad.setNumberOfPride(-1);     /* set default value back */
                pride.lions.remove(lionToBeNomad);      /* remove lion from pride */
                nomads.add(lionToBeNomad);              /* set as a nomad */
            }
        }

        /* Defense against nomad males. */
        /* For each nomad */
        for(int n = 0; n < nomads.size(); n++)
        {
            /* Create and initialize binary template */
            int binaryTemplate[] = new int[prides.length];
            for(int i = 0; i < binaryTemplate.length; i++)
            {
                if(0 == rand.nextInt(1))
                {
                    binaryTemplate[i] = 0b01;
                }
                else
                {
                    binaryTemplate[i] = 0b00;
                }
            }

            /* For each pride */
            for(int j = 0; j < prides.length; j++)
            {
                if(binaryTemplate[j] == 1)
                {
                    /* All male lions in the pride */
                    List<Lion> allMalesInPride = prides[j].lions.stream().filter(x -> x.getGender().equals("male")).collect(Collectors.toList());
                    /* For each male lion in the pride */
                    for(Lion prideLion: allMalesInPride)
                    {
                        /* Check if nomad lion is better than pride lion */
                        if(nomads.get(n).getBestFitness() > prideLion.getBestFitness())
                        {
                            /* Nomad lion become resident */
                            nomads.get(n).setNumberOfPride(j);
                            prides[j].lions.add(nomads.get(n));
                            nomads.remove(n);

                            /* Resident lion becomes monad */
                            prideLion.setIsNomad(true);
                            prideLion.setNumberOfPride(-1);           
                            nomads.add(prideLion);     
                            prides[j].lions.remove(prideLion);   
                        }
                    }
                }
            }            
        }
        /***************************************************************** End of defense */
        /**********************************************************************************/

        /********************************************************************** Migration */
        /**********************************************************************************/
        /* First, go through all prides and set calculated number of females as nomads */
        for(Pride pride: prides)
        {
            /* All females in currect pride */
            List<Lion> femalesInPride = pride.lions.stream().filter(x -> x.getGender().equals("female")).collect(Collectors.toList());
            /* Max number of females allowed in current pride */
            int maxNumberOfFemalesInPride = ((pride.lions.size() * parameters.PERCENT_OF_FEMALES_IN_PRIDE()) / 100);

            /* Current surplus of females in pride */
            int surplus = femalesInPride.size() - maxNumberOfFemalesInPride;
            if(surplus < 0) surplus = 0;
            /* Percent of the maximum number of females in a pride to become nomads */
            int immigratieRate = (int)Math.ceil(((maxNumberOfFemalesInPride * parameters.FEMALE_IMMIGRATE_RATE()) / 100));
            if(immigratieRate == 0) immigratieRate = 1;
            /* Total number of females in current pride to become nomads */
            int totalNumberToBecomeNomads = surplus + immigratieRate;

            for(int i = 0; i < totalNumberToBecomeNomads; i++)
            {
                /* Females are selected randomly */
                int randomIndex = rand.nextInt(femalesInPride.size() - 1);

                /* Set female as nomad and restore default pride number */
                Lion randomFemale = femalesInPride.get(randomIndex);
                randomFemale.setNumberOfPride(-1);
                randomFemale.setIsNomad(true);
                
                /* Remove from pride and add to nomads */
                femalesInPride.remove(randomFemale);
                nomads.add(randomFemale);
            }
        }
        
        /* Select all females and sort according to their fitness */
        List<Lion> nomadFemales = nomads.stream().filter(x -> x.getGender().equals("female")).collect(Collectors.toList());
        Collections.sort(nomadFemales, Collections.reverseOrder());

        /* This list stores identifiers of prides with still missing females */
        /* This assures that 'while' loop below won't be executed more times than necessary */
        ArrayList<Integer> pridesToGo = Helpers.createIndexes(prides.length);

        /* Nomad females are sorted by fitness, loop over them starting from best ones */
        for(Lion nomadFemale: nomadFemales)
        {
            int prideNumber = 0;
            while(!pridesToGo.isEmpty())
            {
                /* Randomly select pride */
                int randomIndex = (pridesToGo.size() > 1) ? rand.nextInt(pridesToGo.size() - 1) : 0;
                prideNumber = pridesToGo.get(randomIndex);

                /* Number females in currect pride */
                int femalesInPride = prides[prideNumber].lions.stream().filter(x -> x.getGender().equals("female")).collect(Collectors.toList()).size();
                /* All empty places in current pride are now filled */
                if(((femalesInPride * 100) / prides[prideNumber].lions.size()) >= parameters.PERCENT_OF_FEMALES_IN_PRIDE())
                {
                    pridesToGo.remove(pridesToGo.indexOf(prideNumber));
                    continue;
                }
                else
                {
                    break;
                }
            }

            /* Iterate till all empty females places in all prides are filled */
            if(!pridesToGo.isEmpty())
            {
                /* Strong nomad female becomes pride female */
                prides[prideNumber].lions.add(nomadFemale); 
                nomadFemale.setNumberOfPride(prideNumber);
                nomadFemale.setIsNomad(false);

                /* Female has been added to the pride, so have to be removed from nomads as well */
                nomads.remove(nomadFemale);
            }
            else
            {
                continue;
            }
        }

        /* Kill the weakests nomad females */
        /* Sort nomad females according to their fitness (ascending) */
        nomadFemales = nomads.stream().filter(x -> x.getGender().equals("female")).collect(Collectors.toList());
        Collections.sort(nomadFemales);

        int numberOfAllNomads = nomads.size();
        int numberOfNomadFemales = nomadFemales.size();
        int maxNumberOfNomadFemales = ((numberOfAllNomads * parameters.PERCENT_OF_FEMALES_IN_NOMADS()) / 100);
        int numberOfNomadFemalesToKill = numberOfNomadFemales - maxNumberOfNomadFemales;
        if(numberOfNomadFemalesToKill < 0) numberOfNomadFemalesToKill = 0;

        for(int i = 0; i < numberOfNomadFemalesToKill; i++)
        {
            Lion currentNomand = nomadFemales.remove(0); /* remove always first one */
            nomads.remove(currentNomand);
        }
        /*************************************************************** End of migration */
        /**********************************************************************************/

        System.out.println("done");
    }
}