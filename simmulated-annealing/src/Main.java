import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Main {
	
	private static ParameterFile parameters;
	public static void main(String [] args) throws Exception
	{			
		for(int i = 0; i < 5; i++)
		{
			runOptimization("JRip");
		}
	}
	
	public static void runOptimization(String nameOfClassifier) throws Exception
	{
		DataSource source = new DataSource("C:/Users/Public/magisterka/datasets/glass.arff");
						
		Instances data = source.getDataSet();
		parameters = new ParameterFile(nameOfClassifier);
		
		data.setClassIndex(data.numAttributes()-1);

		Instances[][] split = ClassifierClass.crossValidationSplit(data, parameters.NUMBER_OF_FOLDS());

		Solution solution = new Solution(parameters.CLASSIFIER_TO_OPTIMIZE());
		Solution bestSolution = new Solution(parameters.CLASSIFIER_TO_OPTIMIZE());
		double temperature = 10000;
		double cooler = 0.3;
		
		long currentTime = System.currentTimeMillis();

		while(isTimePassed(currentTime))
		{
			Solution newSolution = new Solution(parameters.CLASSIFIER_TO_OPTIMIZE());
			
			long timeBeforeOptimization = System.currentTimeMillis();
			double currentFitness = Math.round(ClassifierClass.classify(solution.getPosition(), split, parameters.CLASSIFIER_TO_OPTIMIZE())*100.0)/100.0;
			double newFitness = Math.round(ClassifierClass.classify(newSolution.getPosition(), split, parameters.CLASSIFIER_TO_OPTIMIZE())*100.0)/100.0;
			long timeAfterOptimization = System.currentTimeMillis();
			currentTime += (timeAfterOptimization - timeBeforeOptimization);
			
			solution.setFitness(currentFitness);
			newSolution.setFitness(newFitness);
			
            if (acceptanceProbability(currentFitness, newFitness, temperature) > Math.random()) 
            {
            	solution = newSolution;
            }
            
            if(solution.getFitness() > bestSolution.getFitness())
            {
            	bestSolution.setFitness(solution.getFitness());
            }
            
            temperature *= 1 - cooler;
			double percentDone = (System.currentTimeMillis() - currentTime) * 100.0 / (parameters.TIME_TO_OPTIMIZATION() * 1000);
			System.out.println("Wykonano: " + percentDone + "%");
		}
		
		String outputStr = "";
		if(parameters.CLASSIFIER_TO_OPTIMIZE() == "J48")
		{
			outputStr = "Optimal parameters are: " + "\nMin Num Obj: "+ (int)bestSolution.getPosition()[0] +"\nConfideceFactor: "+  bestSolution.getPosition()[1] 
					+ "\nWith Fitness: " + bestSolution.getFitness() + '\n';
			System.out.println("Optimal parameters are: " + "\nMin Num Obj: "+ (int)bestSolution.getPosition()[0] +"\nConfideceFactor: "+  bestSolution.getPosition()[1] 
				+ "\nWith Fitness: " + bestSolution.getFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "JRip")
		{
			outputStr = "Optimal parameters are: " + "\nFolds "+ bestSolution.getPosition()[0] +"\nminNo: "+  bestSolution.getPosition()[1] +"\nOptimizations: "
					+  bestSolution.getPosition()[2]+"\nWith Fitness: " + bestSolution.getFitness() + '\n';
			
			System.out.println("Optimal parameters are: " + "\nFolds "+ bestSolution.getPosition()[0] +"\nminNo: "+  bestSolution.getPosition()[1] +"\nOptimizations: "
					+  bestSolution.getPosition()[2]+"\nWith Fitness: " + bestSolution.getFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "MLP")
		{
			outputStr = "Optimal parameters are: " + "\nLearning Rate: "+ bestSolution.getPosition()[0] +"\nMomentum: "+  bestSolution.getPosition()[1] +"\nTraining Time: "+
					bestSolution.getPosition()[2]+"\nValidation Set Size: "+ bestSolution.getPosition()[3] + "\nWith Fitness: " + bestSolution.getFitness() + '\n';
					
			System.out.println("Optimal parameters are: " + "\nLearning Rate: "+ bestSolution.getPosition()[0] +"\nMomentum: "+  bestSolution.getPosition()[1] +"\nTraining Time: "+
					bestSolution.getPosition()[2]+"\nValidation Set Size: "+ bestSolution.getPosition()[3] + "\nWith Fitness: " + bestSolution.getFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "RT")
		{
			outputStr = "Optimal parameters are: " + "\nK Value "+ bestSolution.getPosition()[0] +"\nMax Depth: "+  bestSolution.getPosition()[1] +"\nMin num: "+  
					bestSolution.getPosition()[2]+ "\nNum Folds: "+  bestSolution.getPosition()[3] + "\nWith Fitness: " + bestSolution.getFitness() + '\n';
			
			System.out.println("Optimal parameters are: " + "\nK Value "+ bestSolution.getPosition()[0] +"\nMax Depth: "+  bestSolution.getPosition()[1] +"\nMin num: "+  
					bestSolution.getPosition()[2]+ "\nNum Folds: "+  bestSolution.getPosition()[3] + "\nWith Fitness: " + bestSolution.getFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "SL")
		{
			outputStr = "Optimal parameters are: " + "\nmaxBoostingIterations "+ bestSolution.getPosition()[0] +"\nnumBoostingIterations: "+  bestSolution.getPosition()[1] +"\nweightTrimBeta: "
					+  bestSolution.getPosition()[2]+"\nWith Fitness: " + bestSolution.getFitness() + '\n';
			
			System.out.println("Optimal parameters are: " + "\nmaxBoostingIterations "+ bestSolution.getPosition()[0] +"\nnumBoostingIterations: "+  bestSolution.getPosition()[1] +"\nweightTrimBeta: "
					+  bestSolution.getPosition()[2]+"\nWith Fitness: " + bestSolution.getFitness());
		}
		else
		{
			/* this will never happen */
			System.out.println("Incorrect classifier");
		}
		
		Writer output = new BufferedWriter(new FileWriter(parameters.CLASSIFIER_TO_OPTIMIZE() + ".txt", true));
		output.append(outputStr);
		output.close();
	}
	
    // Calculate the acceptance probability
    public static double acceptanceProbability(double currentFitness, double newFitness, double temperature) {
        // Accept new solution if is's better
        if (newFitness > currentFitness) {
            return 1.0;
        }
        // Calculate an acceptance probability If the new solution is worse
        return Math.exp((currentFitness - newFitness) / temperature);
    }
    
	private static boolean isTimePassed(long currentTime)
	{
		if(System.currentTimeMillis() - currentTime > (parameters.TIME_TO_OPTIMIZATION() * 1000))
		{  
			return false;
		} 
		else
		{
			return true;
		}	 
	}
}
