import java.util.Random;
import weka.core.Instances;
import java.util.ArrayList;
import java.util.Collections;

public class Pride 
{
    ArrayList <Lion> lions;

    public Pride()
    {
        this.lions = new ArrayList<Lion>();
    }

    public void AddLionToPride(Lion lion)
    {
        lion.setIsNomad(false);
        this.lions.add(lion);
    }

    public static void setLionsToPride(Lion[] lions, Pride[] prides)
    {
        Random random = new Random();

        /* At this moment it is just a hardcoded value but finally should be calculated based on input number of lions */
        int minNumberOfLions = 5;

        /* Create prides only once */
        for(int i = 0; i < prides.length; i++)
        {
            prides[i] = new Pride();
        }

        /* One iteration per each lion */
        for(int i = 0; i < lions.length; i++)
        {
            if(lions[i].isNomad() == false)
            {
                /* Infinite loop */
                while(true)
                {
                    int randomIndex = random.nextInt(prides.length);

                    /* If we still have too small prides and current one is not among them --> skip to the next iteration */
                    if(!areAllPridesBigEnough(prides, minNumberOfLions) && prides[randomIndex].lions.size() > minNumberOfLions) continue;
                    
                    lions[i].setNumberOfPride(randomIndex);
                    prides[randomIndex].AddLionToPride(lions[i]);
                    break;
                }
            }
        }
    }

    /* Checks if we still have to small prides */
    private static boolean areAllPridesBigEnough(Pride[] prides, int treshold)
    {
        boolean returnValue = true;

        for(int i = 0; i < prides.length; i++)
        {
            if(prides[i].lions.size() < treshold) returnValue = false;
        }

        return returnValue;
    } 

    public static void setLionsGenderForPrides(Pride pride, int percentToBeMale)
    {
        Random random = new Random();
        int numberToBeMale = (int)Math.ceil(percentToBeMale * pride.lions.size() / 100.0);
        int numberOfMales = 0;

        /* Initially all lions are marked as females */
        for(int i = 0; i < pride.lions.size(); i++)
        {
            pride.lions.get(i).setGender("female");
        }

        /* set percent of pride (defined in ParameterFile) as male lions */
        while(numberOfMales < numberToBeMale)
        {
            int randomIndex = random.nextInt(pride.lions.size());
            pride.lions.get(randomIndex).setGender("male");
            numberOfMales++;
        }
    }

    public static void setLionsGenderForNomads(ArrayList<Lion> nomads, int percentToBeFemale, int numberOfNomads)
    {
        Random random = new Random();
        int numberToBeFemale = (int)Math.ceil((percentToBeFemale) * numberOfNomads / 100.0);
        int numberOfFemales = 0;

        /* Initially all lions are marked as females */
        for(int i = 0; i < nomads.size(); i++)
        {
            nomads.get(i).setGender("male");    
        }

        /* set percent of nomads (defined in ParameterFile) as female lions */
        while(numberOfFemales < numberToBeFemale)
        {
            int randomIndex = random.nextInt(nomads.size());
            nomads.get(randomIndex).setGender("female");
            numberOfFemales++;
        }
    }

    public static void sendGroupsToHunt(ArrayList<ArrayList<Lion>> groups, float[] prey, int centerGroupId, Instances[][] split, ParameterFile parameters) throws Exception
    {    
        Random random = new Random();
        float[] newPosition;
        Lion attackingLion;
        int attacksToBeDone = 0;   
        int attacksDone = 0;
        boolean attackDone = false;

        for(int i = 0; i <groups.size(); i++)
        {
            attacksToBeDone += groups.get(i).size();
        }

        /* each while loop iteration is one lion attack */
        while(attacksDone < attacksToBeDone)
        {
            int indexOfGroup = random.nextInt(3); /* returns 0, 1, 2 */
            attackDone = false;
           
            int indexOfLion = random.nextInt(groups.get(indexOfGroup).size());
            attackingLion = groups.get(indexOfGroup).get(indexOfLion);

            if(attackingLion.getAttacked() == false && attackingLion.isHunter() == true)
            {
                newPosition = new float[attackingLion.getPosition().length];
                    
                if(centerGroupId == indexOfGroup)
                {
                    for(int i = 0; i < attackingLion.getPosition().length; i++)
                    {
                        if(attackingLion.getPosition()[i] < prey[i])
                        {
                            newPosition[i] = attackingLion.getPosition()[i] + random.nextFloat() * (prey[i] - attackingLion.getPosition()[i]);
                        }
                        else
                        {
                            newPosition[i] = prey[i] + random.nextFloat() * (attackingLion.getPosition()[i] - prey[i]);
                        }
                    }  
                }
                else
                {
                    for(int i = 0; i < attackingLion.getPosition().length; i++)
                    {
                        float boundary = (prey[i] * 2) - attackingLion.getPosition()[i];

                        if(boundary < prey[i])
                        {
                            newPosition[i] = boundary + random.nextFloat() * (prey[i] - boundary);
                        }
                        else
                        {
                            newPosition[i] = prey[i] + random.nextFloat() * (boundary - prey[i]);
                        }
                    }
                }
                attackingLion.setPosition(newPosition);
                attackingLion.setAttacked(true);
                attackDone = true;
                attacksDone++;
            }

            /* every case of moving lion has been covered */
			/* count fitness of new position */
            /* move prey if it is better */
            
            if(attackDone == true)
            {
                if(attackingLion.isPositionInCorrectRange(parameters.PARAMETERS_RANGES()) == false)
                {
                    attackingLion.repairLionPosition(parameters.PARAMETERS_RANGES());
                }
                double fitness = ClassifierClass.classify(attackingLion.getPosition(), split, parameters.NAME_OF_CLASSIFIER());
                
                /* if current place is better than last one */
                if(fitness > attackingLion.getBestFitness())
                {                  
                    double percentsOfImprovement = 0;  
                    /* move prey */
                    if(attackingLion.getFitness() > 1)
                    {
                        percentsOfImprovement = fitness * 100.0 / attackingLion.getFitness();
                    }
                    else
                    {
                        percentsOfImprovement = fitness * 100.0 / 1.0;
                    }
                    
                    attackingLion.setFitness(Math.round(fitness * 100.0) / 100.0);

                    for(int i = 0; i < prey.length; i++)
                    {
                        prey[i] = (float) (prey[i] + random.nextFloat() * (percentsOfImprovement/100.0) * (prey[i] - attackingLion.getPosition()[i]));

                        if(prey[i] < parameters.PARAMETERS_RANGES()[i][0])
                        {
                            prey[i] = parameters.PARAMETERS_RANGES()[i][0];
                        }
                        
                        if(prey[i] > parameters.PARAMETERS_RANGES()[i][1])
                        {
                            prey[i] = parameters.PARAMETERS_RANGES()[i][1];
                        }
                    }
                }
                else
                {
                    attackingLion.setFitness(Math.round(fitness*100.0)/100.0);
                }
            }
        }
    }

    public static int findCenterGroup(ArrayList<ArrayList<Lion>> groups)
    {
        int retValue = 0;
        float maxFitness = 0;

        float[] groupsFitnesses = new float[groups.size()];

        for(int i = 0; i < groupsFitnesses.length; i++)
        {
            groupsFitnesses[i] = countGroupFitness(groups.get(i));
        

            if(groupsFitnesses[i] > maxFitness)
            {
                retValue = i;
                maxFitness = groupsFitnesses[i];
            }
        }

        return retValue;
    }

    private static float countGroupFitness(ArrayList<Lion> group)
    {
        float sum = 0;
        for(int i = 0; i < group.size(); i++)
        {
            sum += group.get(i).getFitness();
        }

        return sum;
    }

    public static float[] generatePrey(ArrayList<ArrayList<Lion>> groups)
    {
        float[] spotOfPrey = new float[groups.get(0).get(0).getPosition().length] ;
        
        int numOfDimensions = spotOfPrey.length;
        for(int i = 0; i < numOfDimensions; i++)
        {
            float sum = 0;
            int lengthOfGroups = 0;
            
            for(int numberOfGroup = 0; numberOfGroup < groups.size(); numberOfGroup++)
            {
                for(int j = 0; j < groups.get(numberOfGroup).size(); j++)
                {
                    sum += groups.get(numberOfGroup).get(j).getPosition()[i];
                }
                lengthOfGroups += groups.get(numberOfGroup).size();
            }
            spotOfPrey[i] = sum / lengthOfGroups;
        }

        return spotOfPrey;
    }

    private static boolean checkDistribution(int sizeOfGroup, int threshold)
    {
        if(sizeOfGroup >= threshold)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static boolean findAttackers()
    {
        Random random = new Random();
        int randomNumber = random.nextInt(100);

        /* 65% on success */
        if(randomNumber < 64)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static ArrayList<ArrayList<Lion>> dividePrideToGroups(Pride pride, int numberOfGroups)
    {
        ArrayList<Lion>[] tmpGroups = new ArrayList[numberOfGroups];
        ArrayList<ArrayList<Lion>> groups = new ArrayList<ArrayList<Lion>>();
        boolean lionsDistributed = false;

        for(int i = 0; i < tmpGroups.length; i++)
        {
            tmpGroups[i] = new ArrayList<Lion>();
        }

        while(!lionsDistributed)
        {
            ArrayList<Integer> indexes = new ArrayList<Integer>();

            indexes = Helpers.createIndexes(pride.lions.size());

            Collections.shuffle(indexes);
            Collections.shuffle(pride.lions);

            for(int i = 0; i < pride.lions.size(); i++)
            {
                Lion currentLion = pride.lions.get(indexes.get(i));
                if(currentLion.getGender().equals("female"))
                {
                    if(findAttackers() == true)
                    {
                        int groupIndex = indexes.get(i) % numberOfGroups;
                        currentLion.setHunter(true);
                        tmpGroups[groupIndex].add(currentLion);
                    }
                }
            }

            /* check if every group has at least 1 members, if not do distribution again */
            for(int i = 0; i < tmpGroups.length; i++)
            {
                lionsDistributed = checkDistribution(tmpGroups[i].size(), 1); 
                
                /* if distribution not satisfied, set lions as not hunters again */
                if(lionsDistributed == false)
                {
                    for(int j = 0; j < pride.lions.size(); j++)
                    {
                        pride.lions.get(j).setHunter(false);
                    }

                    /* remove wrongly distributed elements from collection */
                    for(int j = 0; j < tmpGroups.length; j++)
                    {
                        for(int k = tmpGroups[j].size()-1; k >= 0; k--)
                        {
                            tmpGroups[j].remove(k);
                        }
                    }
                    break;
                }
            }
        }

        for(int i = 0; i < tmpGroups.length; i++)
        {
            groups.add(tmpGroups[i]);
        }

        return groups;
    }

    public static int getTournamentSize(Pride pride)
    {
        int successRateOfPride = 0;
		int tournamentSize = 0; 
        
        /* if fitness of current iteration is greater than best fitness add as success */
		for(int i = 0; i < pride.lions.size(); i++)
		{	
			if(pride.lions.get(i).getFitness() > pride.lions.get(i).getBestFitness())
			{
				pride.lions.get(i).setSuccessRate(1);
			}
			else
			{
				pride.lions.get(i).setSuccessRate(0);
			}
			successRateOfPride += pride.lions.get(i).getSuccessRate();
		}
        tournamentSize = (int) Math.max(2, Math.ceil(successRateOfPride / 2.0f));

        /* clear success rate */
        for(int i = 0; i < pride.lions.size(); i++)
        {
            pride.lions.get(i).setSuccessRate(0);
        }
        
        return tournamentSize;
    }

    public static float[] findPointByTournament(Pride pride, int tournamentSize)
    {	
		Lion[] tournamentLions = new Lion[tournamentSize];
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		/* choose tournament lions randomly */        
        indexes = Helpers.createIndexes(pride.lions.size());

		/* to be sure that indexes are different*/
		Collections.shuffle(indexes);
		
		for(int j = 0; j < tournamentSize; j++)
		{
			tournamentLions[j] = pride.lions.get(indexes.get(j));
		}
		
		/* tournament finished - found winner */
        float[] winner = findBestPosition(tournamentLions);
        
        return winner;
    }

    private static float[] findBestPosition(Lion[] tournamentLions) 
	{
		float[] winner = new float [tournamentLions[0].getBestPosition().length];
		int indexOfWinner = 0;
		double maxFitness = tournamentLions[0].getBestFitness();
		
		for(int i = 1; i < tournamentLions.length; i++)
		{
			if(tournamentLions[i].getBestFitness() > maxFitness)
			{
				maxFitness = tournamentLions[i].getBestFitness();
				indexOfWinner = i;
			}
		}
		
		for(int j = 0; j < winner.length; j++)
		{
			winner[j] = tournamentLions[indexOfWinner].getBestPosition()[j];
		}
		
		return winner;
    }
}