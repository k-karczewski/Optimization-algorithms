
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;


public class ClassifierClass {
	/***********************************************************************************************
	 * Function: classify
	 * Description: Creates J48 classifier and evaluates accuracy of classification.
	 * @param ???
	 * @param split
	 * @return Mean result of classification in percents
	 * @throws Exception
	 ***********************************************************************************************/
	public static double classify(float[] parameters, Instances[][] split, String nameOfClassifier) throws Exception
	{
		Instances[] trainingSplits = split[0];
		Instances[] testingSplits = split[1];
		
		Classifier classifier = null; 
		
		switch(nameOfClassifier)
		{
			case "J48":		
				classifier = new J48();
				((J48)classifier).setMinNumObj((int)parameters[0]);
				((J48)classifier).setConfidenceFactor(parameters[1]);
				break;

			case "JRip":				
				classifier = new JRip();
				((JRip)classifier).setFolds((int)parameters[0]);
				((JRip)classifier).setMinNo(parameters[1]);
				((JRip)classifier).setOptimizations((int)parameters[2]);
				break;

			case "MLP":	
				classifier = new MultilayerPerceptron();
				((MultilayerPerceptron)classifier).setLearningRate(parameters[0]);
				((MultilayerPerceptron)classifier).setMomentum(parameters[1]);
				((MultilayerPerceptron)classifier).setTrainingTime((int)parameters[2]);
				((MultilayerPerceptron)classifier).setValidationSetSize((int)parameters[3]);
				break;

			case "RT":
				classifier = new RandomTree();
				((RandomTree)classifier).setKValue((int)parameters[0]);
				((RandomTree)classifier).setMaxDepth((int)parameters[1]);
				((RandomTree)classifier).setMinNum(parameters[2]);
				((RandomTree)classifier).setNumFolds((int)parameters[3]);
				break;

			case "SL":		
				classifier = new SimpleLogistic();
				((SimpleLogistic)classifier).setMaxBoostingIterations((int)parameters[0]);
				((SimpleLogistic)classifier).setNumBoostingIterations((int)parameters[1]);
				((SimpleLogistic)classifier).setWeightTrimBeta(parameters[2]);
				break;

			default:
				/* this will never happen */
				break;
		}
		

		
		double result = 0;
		ArrayList<Prediction> predictions = new ArrayList<Prediction>();	
		
		for(int i = 0; i < trainingSplits.length; i++)
		{
			Evaluation validation = classifyFold(classifier, trainingSplits[i], testingSplits[i]);
			
			predictions.addAll(validation.predictions());

		}	
		result = calculateAccuracy(predictions);
		//System.out.println(result);
		return result;
	}
	
	
	/***********************************************************************************************
	 * Function: crossValidationSplit
	 * Description: Randomly splits the dataset into training and validation data.
	 * @param data
	 * @param numberOfFolds
	 * @return Split of input dataset to test and train sets.
	 ***********************************************************************************************/
	public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds)
	{
		Instances[][] split = new Instances[2][numberOfFolds];
		for(int i = 0; i < numberOfFolds; i++)
		{
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}
		
		return split;
	}
	
	/***********************************************************************************************
	 * Function: classifyFold
	 * Description: Classifies test and training set of data by classifier passed in parameter.
	 * @param model
	 * @param trainingSet
	 * @param testSet
	 * @return Evaluation of training and test datasets.
	 * @throws Exception
	 ***********************************************************************************************/
	public static Evaluation classifyFold(Classifier model, Instances trainingSet, Instances testSet) throws Exception
	{
		Evaluation evaluation = new Evaluation(testSet);
		
		model.buildClassifier(trainingSet);
		
		evaluation.evaluateModel(model, testSet);

		return evaluation;
	}
	
	/***********************************************************************************************
	 * Function: calculateAccuracy
	 * Description: Calculates accuracy of evaluation.
	 * @param predictions
	 * @return Accuracy of evaluation in percents.
	 * @throws Exception
	 ***********************************************************************************************/
	public static double calculateAccuracy(ArrayList<Prediction> predictions) throws Exception
	{
		double correct = 0;
		double incorrect = 0;
		for(int i = 0; i < predictions.size(); i++)
		{
			Prediction np = predictions.get(i);
			if(np.predicted() == np.actual())
			{
				correct++;
			}
			else
			{
				incorrect++;
			}
		}
		return (correct / (correct + incorrect)) * 100;
	}
//	public static double calculateAccuracy(ArrayList<Prediction> predictions) throws Exception
//	{
//		double correct = 0;
//		for(int i = 0; i < predictions.size(); i++)
//		{
//			Prediction np = predictions.get(i);
//			if(np.predicted() == np.actual())
//			{
//				correct++;
//			}
//		}
//		return correct * 100 / predictions.size();
//	}
	
	
	/***********************************************************************************************
	 * Function: countMeanResult
	 * Description: Calculates mean accuracy value of all cross validation sets.
	 * @param results
	 * @return Accuracy of evaluation in percents.
	 * @throws Exception
	 ***********************************************************************************************/
	
	public static double countMeanResult(double[] results)
	{
		double sum = 0;
		for(int i = 0; i < results.length; i++)
		{
			sum += results[i];
		}
		
		return sum/results.length;
	}
}
