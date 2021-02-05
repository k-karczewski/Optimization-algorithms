import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Main {

	/* Hyperparameters for optimized parameters and classifier */
	private static ParameterFile parameters;

	/***********************************************************************************************
	 * Function: main Description: Main function for Cuckoo Optmization Algorithm.
	 * 
	 * @throws Exception
	 ***********************************************************************************************/
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 5; i++) {
			runOptimization(10, "RT");
		}
	}

	/***********************************************************************************************
	 * Function: runOptimization Description: Starts Cuckoo Optimization Algorithm.
	 * 
	 * @param initNumberOfCuckoos - Number of solutions on init phase of algorithm.
	 *                            Number of solutions is limited in ParameterFile
	 *                            and that value will be used during optimization.
	 * @param nameOfClassifier    - Name of classifier which parameters will be
	 *                            optimized. Allowed values are: "J48", "JRip",
	 *                            "MLP", "RT", "SL". Each classifier has its own
	 *                            hyperparameters defined in ParameterFile.
	 * @throws Exception
	 ***********************************************************************************************/
	public static void runOptimization(int initNumberOfCuckoos, String nameOfClassifier) throws Exception {
		/* load dataset */
		DataSource source = new DataSource("C:/Users/Konrad/Documents/magisterka/datasets/glass.arff");
		Instances data = source.getDataSet();
		parameters = new ParameterFile(nameOfClassifier);

		data.setClassIndex(data.numAttributes() - 1);

		Instances[][] split = ClassifierClass.crossValidationSplit(data, parameters.NUMBER_OF_FOLDS());
		Cuckoo[] cuckoos = new Cuckoo[initNumberOfCuckoos];
		long currentTime = System.currentTimeMillis();

		/* start of step 1 - init */
		for (int i = 0; i < cuckoos.length; i++) 
		{
			cuckoos[i] = new Cuckoo(parameters.CLASSIFIER_TO_OPTIMIZE());
			if (!cuckoos[i].isPositionInCorrectRange(cuckoos[i].getHabitat(), parameters.CUCKOO_RANGES())) 
			{
				cuckoos[i].setHabitat(Cuckoo.repairPosition(cuckoos[i].getHabitat(), parameters.CUCKOO_RANGES()));
			}

			long timeBeforeOptimization = System.currentTimeMillis();
			cuckoos[i].setProfit(Math.round(ClassifierClass.classify(cuckoos[i].getHabitat(), split, parameters.CLASSIFIER_TO_OPTIMIZE()) * 100.0) / 100.0);

			long timeAfterOptimization = System.currentTimeMillis();

			/* classification does not count in optimization time add time that
			 * classification taken to current time */
			currentTime += (timeAfterOptimization - timeBeforeOptimization);
		}
		/* end of step 1 - init */

		while (isTimePassed(currentTime) == false) 
		{
			int totalNumberOfEggs = 0;

			/* start of step 2 - dedicate eggs */
			for (int i = 0; i < cuckoos.length; i++) 
			{
				cuckoos[i].initializeEggs();
				totalNumberOfEggs += cuckoos[i].getNumberOfEggs();
			}
			/* end of step 2 - dedicate eggs */

			/* start of step 3 - define ELR */
			for (int i = 0; i < cuckoos.length; i++) 
			{
				cuckoos[i].countEggLayingRadius((float) totalNumberOfEggs, parameters.MAX_VAL_OF_ELR(),
						parameters.VAR_HI(), parameters.VAR_LOW());
			}
			/* end of step 3 - define ELR */

			/* start of step 4 - lay eggs within ELR */
			for (int i = 0; i < cuckoos.length; i++) 
			{
				cuckoos[i].layEggsWithinELR(parameters.CUCKOO_RANGES());
				for (int j = 0; j < cuckoos[i].eggs.length; j++) {
					long timeBeforeOptimization = System.currentTimeMillis();
					cuckoos[i].getEgg(j)
							.setProfit(Math.round(ClassifierClass.classify(cuckoos[i].getEgg(j).getHabitat(), split,
									parameters.CLASSIFIER_TO_OPTIMIZE()) * 100.0) / 100.0);

					long timeAfterOptimization = System.currentTimeMillis();

					/* classification does not count in optimization time */
					currentTime += (timeAfterOptimization - timeBeforeOptimization);
				}
			}
			/* end of step 4 - lay eggs within ELR */

			/* start of step 5 - kill eggs 10% with lower profit */
			int eggsToKill = 10 * totalNumberOfEggs / 100;
			int tmpNumOfEggs = totalNumberOfEggs;
			int analyzedCuckoos = 0;

			while (eggsToKill > 0 && analyzedCuckoos < tmpNumOfEggs) 
			{
				for (int i = 0; i < cuckoos.length; i++) {
					for (int j = 0; j < cuckoos[i].getNumberOfEggs(); j++) 
					{
						if (cuckoos[i].getProfit() > cuckoos[i].getEgg(j).getProfit()) 
						{
							cuckoos[i].killEgg(j);
							totalNumberOfEggs--;
							eggsToKill--;
							break;
						}
						analyzedCuckoos++;
					}
					if (eggsToKill <= 0) {
						break;
					}
				}
			}
			/* end of step 5 - kill eggs with lower profit */

			/*
			 * start of step 6, 7 - let eggs hatch and chicks grow, evaluate habitat of
			 * newly grown cuckoo
			 */
			List<Cuckoo> cuckoosList = new ArrayList<Cuckoo>();

			/* copy old cuckoos */
			for (int i = 0; i < cuckoos.length; i++) 
			{
				cuckoosList.add(cuckoos[i]);
			}

			/* hatch cuckoos from eggs */
			for (int i = 0; i < cuckoos.length; i++) 
			{
				for (int j = 0; j < cuckoos[i].getNumberOfEggs(); j++) 
				{
					cuckoosList.add(new Cuckoo(cuckoos[i].getEgg(j)));
				}
				cuckoos[i].killAllEggs();
			}

			Collections.sort(cuckoosList, Collections.reverseOrder());
			cuckoos = cuckoosList.toArray(new Cuckoo[cuckoosList.size()]);
			/*
			 * end of step 6, 7 - let eggs hatch and chicks grow, evaluate habitat of newly
			 * grown cuckoo
			 */

			/*
			 * start of step 8 - limit maximum number of cuckoos and kill those who live in
			 * worst habits
			 */
			if (cuckoos.length > parameters.MAX_NUM_OF_CUCKOOS()) 
			{
				int cuckoosToKill = cuckoos.length - parameters.MAX_NUM_OF_CUCKOOS();
				Cuckoo[] newCuckoos = new Cuckoo[cuckoos.length - cuckoosToKill];

				for (int i = 0; i < newCuckoos.length; i++) 
				{
					newCuckoos[i] = cuckoos[i];
				}

				cuckoos = newCuckoos;
			}

			/*
			 * end of step 8 - limit maximum number of cuckoos and kill those who live in
			 * worst habits
			 */

			/*
			 * start of step 9 - cluster cuckoos and find best group and select goal habitat
			 */
			KMeans kmeans = new KMeans(cuckoos);
			kmeans.clustering(parameters.NUMBER_OF_CLUSTERS(), 10, null);

			int[] groups = kmeans.getLabels();
			setGroups(cuckoos, groups);
			double[] meanProfits = countMeanProfitOfGroups(cuckoos);
			int goalGroup = findGoalGroup(meanProfits);
			float[] bestHabitat = kmeans.getCentroid(goalGroup);
			/*
			 * end of step 9 - cluster cuckoos and find best group and select goal habitat
			 */

			/* start of step 10 - let new cuckoo population immigrate toward goal habitat */
			float[][] pointToMove = new float[cuckoos.length][];
			float[] deviations = new float[cuckoos.length];

			for (int i = 0; i < cuckoos.length; i++) 
			{
				Random random = new Random();
				pointToMove[i] = new float[bestHabitat.length];
				pointToMove[i] = calculatePointToImmigrate(cuckoos[i].getHabitat(), bestHabitat);

				/* get random angle between -30deg and 30deg */
				deviations[i] = (float) (random.nextInt(30 + 30) - (30));
				deviations[i] = (float) Math.toRadians(deviations[i]);
			}
			double param1 = 0;
			double param2 = 0;

			int numOfParameters = bestHabitat.length;
			int[] parametersToDeviate = findParametersToDeviade(numOfParameters);

			for (int i = 0; i < cuckoos.length; i++) 
			{
				if (cuckoos[i].isYoung() == true) {
					float[] habitat = cuckoos[i].getHabitat();
					param1 = (Math.cos(deviations[i]) * pointToMove[i][0])
							+ (-Math.sin(deviations[i]) * pointToMove[i][1]);
					param2 = (Math.sin(deviations[i]) * pointToMove[i][0])
							+ (Math.cos(deviations[i]) * pointToMove[i][1]);

					for (int j = 0; j < habitat.length; j++) {
						if (parametersToDeviate[0] == j) {
							if (habitat[j] > pointToMove[i][j]) {
								habitat[j] -= param1;
							} else {
								habitat[j] += param1;
							}
						} else if (parametersToDeviate[1] == j) {
							if (habitat[j] > pointToMove[i][j]) {
								habitat[j] -= param2;
							} else {
								habitat[j] += param2;
							}
						} else {
							habitat[j] += pointToMove[i][j];
						}
					}

					cuckoos[i].setHabitat(habitat);
					cuckoos[i].setYoung(false);
				}
			}
			/* end of step 10 - let new cuckoo population immigrate toward goal habitat */

			double percentDone = (System.currentTimeMillis() - currentTime) * 100.0
					/ (parameters.TIME_TO_OPTIMIZATION() * 1000);
			System.out.println("Wykonano: " + percentDone + "%");
		}

		findBestSolution(cuckoos);
		System.out.println("done");
	}

	/***********************************************************************************************
	 * Function: calculatePointToImmigrate Description: Calculates point to move for
	 * each cuckoo. Move is done towards best solution however length of move is
	 * random percent of distance between points.
	 * 
	 * @param habitat     - Current solution.
	 * @param bestHabitat - Best solution found.
	 ***********************************************************************************************/
	private static float[] calculatePointToImmigrate(float[] habitat, float[] bestHabitat) 
	{
		float random = (float) (Math.random() * 100.0f / 100.0f);
		float[] newHabitat = new float[habitat.length];

		for (int i = 0; i < habitat.length; i++) 
		{
			float diff;
			if (bestHabitat[i] > habitat[i]) {
				diff = bestHabitat[i] - habitat[i];
			} else {
				diff = habitat[i] - bestHabitat[i];
			}

			diff *= random;
			newHabitat[i] = diff;
		}

		return newHabitat;
	}

	/***********************************************************************************************
	 * Function: setGroups Description: Sets number of group for each solution
	 * (cuckoo).
	 * 
	 * @param cuckoos - Array of solutions.
	 * @param groups  - Array of sorted group list.
	 ***********************************************************************************************/
	public static void setGroups(Cuckoo[] cuckoos, int[] groups) 
	{
		for (int i = 0; i < cuckoos.length; i++) {
			cuckoos[i].setGroups(groups[i]);
		}
	}

	/***********************************************************************************************
	 * Function: countMeanProfitOfGroups Description: Counts mean solution for each
	 * group.
	 * 
	 * @param cuckoos - Array of solutions.
	 * @return Array with mean solution counted for each group
	 ***********************************************************************************************/
	public static double[] countMeanProfitOfGroups(Cuckoo[] cuckoos) 
	{
		List<Cuckoo> group0 = new ArrayList<Cuckoo>();
		List<Cuckoo> group1 = new ArrayList<Cuckoo>();
		List<Cuckoo> group2 = new ArrayList<Cuckoo>();
		List<Cuckoo> group3 = new ArrayList<Cuckoo>();
		List<Cuckoo> group4 = new ArrayList<Cuckoo>();
		double[] avgs = new double[parameters.NUMBER_OF_CLUSTERS()];

		for (int i = 0; i < cuckoos.length; i++) 
		{
			if (cuckoos[i].getGroup() == 0) 
			{
				group0.add(cuckoos[i]);
			} else if (cuckoos[i].getGroup() == 1) 
			{
				group1.add(cuckoos[i]);
			} else if (cuckoos[i].getGroup() == 2) 
			{
				group2.add(cuckoos[i]);
			} else if (cuckoos[i].getGroup() == 3) 
			{
				group3.add(cuckoos[i]);
			} else 
			{
				group4.add(cuckoos[i]);
			}
		}

		Cuckoo[][] c = new Cuckoo[parameters.NUMBER_OF_CLUSTERS()][];
		c[0] = new Cuckoo[group0.size()];
		c[0] = group0.toArray(c[0]);
		c[1] = new Cuckoo[group1.size()];
		c[1] = group1.toArray(c[1]);
		c[2] = new Cuckoo[group2.size()];
		c[2] = group2.toArray(c[2]);
		c[3] = new Cuckoo[group3.size()];
		c[3] = group3.toArray(c[3]);
		c[4] = new Cuckoo[group4.size()];
		c[4] = group4.toArray(c[4]);

		for (int i = 0; i < c.length; i++) 
		{
			double sum = 0;
			for (int j = 0; j < c[i].length; j++) 
			{
				sum += c[i][j].getProfit();
			}

			avgs[i] = sum / c[i].length;
		}

		return avgs;
	}

	/***********************************************************************************************
	 * Function: findGoalGroup Description: Checks which group of cuckoos have best
	 * mean solution.
	 * 
	 * @param meanProfits - Stores profits of all groups.
	 * @return id of group with best mean solution.
	 ***********************************************************************************************/
	public static int findGoalGroup(double[] meanProfits) 
	{
		double maxVal = meanProfits[0];
		int goal = 0;

		for (int i = 1; i < meanProfits.length; i++) 
		{
			if (maxVal < meanProfits[i]) 
			{
				maxVal = meanProfits[i];
				goal = i;
			}
		}
		return goal;
	}

	/***********************************************************************************************
	 * Function: findBestSolution Description: Looks for best found solution during
	 * optimization
	 * 
	 * @param cuckoos - Array of solutions
	 * @throws IOException
	 ***********************************************************************************************/
	public static void findBestSolution(Cuckoo[] cuckoos) throws IOException
	{
		double bestFitness = Double.NEGATIVE_INFINITY;
		float[] bestSolution = cuckoos[0].getHabitat();
		String outputStr = "";
		
		for(int i = 0; i < cuckoos.length; i++)
		{
			if(cuckoos[i].getProfit() > bestFitness)
			{
				bestFitness = cuckoos[i].getProfit();
				bestSolution = cuckoos[i].getHabitat();
			}
		}
		
		if(parameters.CLASSIFIER_TO_OPTIMIZE() == "J48")
		{
			outputStr ="Optimal parameters are: " + "\nMin Num Obj: "+ (int)bestSolution[0] +"\nConfideceFactor: "+  bestSolution[1] 
					+ "\nWith Fitness: " + bestFitness + '\n';
			System.out.println("Optimal parameters are: " + "\nMin Num Obj: "+ (int)bestSolution[0] +"\nConfideceFactor: "+  bestSolution[1] 
				+ "\nWith Fitness: " + bestFitness);
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "JRip")
		{
			outputStr ="Optimal parameters are: " + "\nFolds "+ bestSolution[0] +"\nminNo: "+  bestSolution[1] +"\nOptimizations: "
					+ bestSolution[2]+"\nWith Fitness: " + bestFitness + '\n';
			System.out.println("Optimal parameters are: " + "\nFolds "+ bestSolution[0] +"\nminNo: "+  bestSolution[1] +"\nOptimizations: "
					+ bestSolution[2]+"\nWith Fitness: " + bestFitness);
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "MLP")
		{
			outputStr ="Optimal parameters are: " + "\nLearning Rate: "+ bestSolution[0] +"\nMomentum: "+  bestSolution[1] +"\nTraining Time: "+
					bestSolution[2]+"\nValidation Set Size: "+ bestSolution[3] + "\nWith Fitness: " + bestFitness + '\n';
			System.out.println("Optimal parameters are: " + "\nLearning Rate: "+ bestSolution[0] +"\nMomentum: "+  bestSolution[1] +"\nTraining Time: "+
					bestSolution[2]+"\nValidation Set Size: "+ bestSolution[3] + "\nWith Fitness: " + bestFitness);
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "RT")
		{
			outputStr ="Optimal parameters are: " + "\nK Value "+ bestSolution[0] +"\nMax Depth: "+ bestSolution[1] +"\nMin num: "+  
					bestSolution[2]+ "\nNum Folds: "+  bestSolution[3] + "\nWith Fitness: " + bestFitness + '\n';
			System.out.println("Optimal parameters are: " + "\nK Value "+ bestSolution[0] +"\nMax Depth: "+ bestSolution[1] +"\nMin num: "+  
					bestSolution[2]+ "\nNum Folds: "+  bestSolution[3] + "\nWith Fitness: " + bestFitness);
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "SL")
		{
			outputStr ="Optimal parameters are: " + "\nmaxBoostingIterations "+ bestSolution[0] +"\nnumBoostingIterations: "+  bestSolution[1] +"\nweightTrimBeta: "
					+  bestSolution[2]+"\nWith Fitness: " + bestFitness + '\n';
			System.out.println("Optimal parameters are: " + "\nmaxBoostingIterations "+ bestSolution[0] +"\nnumBoostingIterations: "+  bestSolution[1] +"\nweightTrimBeta: "
					+  bestSolution[2]+"\nWith Fitness: " + bestFitness);
		}
		else
		{
			/* this will never happen */
			outputStr ="Incorrect classifier";
			System.out.println("Incorrect classifier");
		}
		
		Writer output = new BufferedWriter(new FileWriter(parameters.CLASSIFIER_TO_OPTIMIZE() + ".txt", true));
		output.append(outputStr);
		output.close();
	}

	/***********************************************************************************************
	* Function: isTimePassed
	* Description: Checks if time to optimization passed
	* @param currentTime - Stores value of time in miliseconds
	* @return FALSE if time has not elapsed yet. TRUE if time has elapsed.
	***********************************************************************************************/
	private static boolean isTimePassed(long currentTime)
	{
		if(System.currentTimeMillis() - currentTime > (parameters.TIME_TO_OPTIMIZATION() * 1000))
		{  
			return true;
		} 
		else
		{
			return false;
		}	 
	}
	
	/***********************************************************************************************
	* Function: findParametersToDeviade
	* Description: Finds randomly two parameters that will be deviaded by random angle
	* @param numOfParameters - count of parameters optimized by cuckoo optimization
	* @return numbers of parameters to deviade
	***********************************************************************************************/
	private static int[] findParametersToDeviade(int numOfParameters)
	{
		Random random = new Random();
		int[] returnValues = new int [2];
		for(int i = 0; i < 2; i++)
		{
			returnValues[i] = random.nextInt(numOfParameters);
		}

		/* check if both values are different */
		boolean paramsTheSame = true;
		while(paramsTheSame)
		{
			if(returnValues[0] == returnValues[1])
			{
				returnValues[1] = random.nextInt(numOfParameters);
			}
			else
			{
				paramsTheSame = false;
			}
		}

		/* if elements not sorted ascending - sort them */
		if(returnValues[0] > returnValues[1])
		{
			int tmp = returnValues[0];
			returnValues[0] = returnValues[1];
			returnValues[1] = tmp;
		}

		return returnValues;
	}
}
