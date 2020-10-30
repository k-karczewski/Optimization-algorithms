import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Random;

public class Main {
	
	private static Random random = new Random();
	private static ParameterFile parameters; 
	
	public static void main(String[] args) throws Exception
	{
		for(int i = 0 ; i < 5; i++)
		{
			runOptimization(20, "SL");
		}
	}
	
	
	public static void runOptimization(int numberOfParticles, String classifier) throws Exception
	{
		//DataSource source = new DataSource("C:/Users/Konrad/Documents/magisterka/datasets/winequality-white.arff");
		//DataSource source = new DataSource("C:/Users/Konrad/Documents/magisterka/datasets/iris.csv");	
		DataSource source = new DataSource("C:/Users/Konrad/Documents/magisterka/datasets/glass.arff");
		Instances data = source.getDataSet();
		parameters = new ParameterFile(classifier);
		double inertiaFactor = parameters.INERTIA_FACTOR();
		
		data.setClassIndex(data.numAttributes()-1);
		Instances[][] split = ClassifierClass.crossValidationSplit(data, parameters.NUMBER_OF_FOLDS());
		Swarm swarm = new Swarm(numberOfParticles, parameters.CLASSIFIER_TO_OPTIMIZE());
		// swarm.getParticlesPositions();

		long currentTime = System.currentTimeMillis();
		int i = 0;
		
		while(isTimePassed(currentTime))
		{
			for(Particle particle: swarm.getParticles())
			{
				float[] particleOldPosition = particle.getPosition();
				boolean areCorrectRanges = particle.isPositionInCorrectRange(parameters.PARTICLE_RANGES());
				
				if(!areCorrectRanges)
				{
					//fix parameters here
					particle.repairParticlePosition(parameters.PARTICLE_RANGES());
					System.out.println("Parameters fixed");
				}

				double currentFitness = 0;
				
				if(Particle.arePositionsTheSame(particle.getPosition(), particle.getBestPosition()) == false)
				{
					long timeBeforeOptimization = System.currentTimeMillis();
					currentFitness = ClassifierClass.classify(particleOldPosition, split, parameters.CLASSIFIER_TO_OPTIMIZE());
					long timeAfterOptimization = System.currentTimeMillis();
					/* classification does not count in optimization time add time that classification taken to current time */
					currentTime += (timeAfterOptimization - timeBeforeOptimization);
				}
				else
				{
					currentFitness = particle.getBestFitness();
				}
					
				particle.setFitness(currentFitness);
					
				if(currentFitness > particle.getBestFitness())
				{
					particle.setBestFitness(currentFitness);
					particle.setBestPosition(particleOldPosition);
						
					if(currentFitness > swarm.getBestFitness())
					{
						swarm.setBestFitness(currentFitness);
						swarm.setBestPosition(particleOldPosition);
					}
				}
				
				float[] currentPosition = particle.getPosition();
				float[] currentSpeed = particle.getSpeed();
				
				/* move particle */
				for(int j = 0; j < currentPosition.length; j++)
				{
					currentPosition[j] += currentSpeed[j];
				}
				
				/* calculate and add speed for next iteration */
				if(parameters.CLASSIFIER_TO_OPTIMIZE() == "J48")
				{
					currentSpeed[0] = getNewParticleSpeedForDirectionInt(particle, swarm, 0, inertiaFactor);
					currentSpeed[1] = Math.round(getNewParticleSpeedForDirectionFloat(particle, swarm, 1, inertiaFactor)*100.0f)/100.0f;
				}
				else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "JRip")
				{
					currentSpeed[0] = getNewParticleSpeedForDirectionInt(particle, swarm, 0, inertiaFactor);
					currentSpeed[1] = Math.round(getNewParticleSpeedForDirectionFloat(particle, swarm, 1, inertiaFactor)*100.0f)/100.0f;
					currentSpeed[2] = getNewParticleSpeedForDirectionInt(particle, swarm, 2, inertiaFactor);
				}
				else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "MLP")
				{
					for(int j = 0; j < currentPosition.length/2; j++)
					{
						currentSpeed[j] = Math.round(getNewParticleSpeedForDirectionFloat(particle, swarm, j, inertiaFactor)*100.0f)/100.0f;
						currentSpeed[j+2] = getNewParticleSpeedForDirectionInt(particle, swarm, j+2, inertiaFactor);
					}
				}
				else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "RT")
				{
					currentSpeed[0] = getNewParticleSpeedForDirectionInt(particle, swarm, 0, inertiaFactor);
					currentSpeed[1] = getNewParticleSpeedForDirectionInt(particle, swarm, 1, inertiaFactor);
					currentSpeed[2] = Math.round(getNewParticleSpeedForDirectionFloat(particle, swarm, 2, inertiaFactor)*100.0f)/100.0f;
					currentSpeed[3] = getNewParticleSpeedForDirectionInt(particle, swarm, 3, inertiaFactor);
				}
				else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "SL")
				{
					currentSpeed[0] = getNewParticleSpeedForDirectionInt(particle, swarm, 0, inertiaFactor);
					currentSpeed[1] = getNewParticleSpeedForDirectionInt(particle, swarm, 1, inertiaFactor);
					currentSpeed[2] = Math.round(getNewParticleSpeedForDirectionFloat(particle, swarm, 2, inertiaFactor)*100.0f)/100.0f;
				}
				else
				{
					/* this will never happen */
					System.out.println("Incorrect classifier");
				}

			}
			
			if(inertiaFactor > 0.0)
			{
				inertiaFactor -= 0.0002;
			}
			
			double percentDone = (System.currentTimeMillis() - currentTime) * 100.0 / (parameters.TIME_TO_OPTIMIZATION() * 1000);
			System.out.println("Wykonano: " + percentDone + "%");
		}
	
		System.out.println();
		
		String output = "";
		if(parameters.CLASSIFIER_TO_OPTIMIZE() == "J48")
		{
			output = "Optimal parameters are: " + "\nMin Num Obj: "+ (int)swarm.getBestPosition()[0] +"\nConfideceFactor: "+  swarm.getBestPosition()[1] 
					+ "\nWith Fitness: " + swarm.getBestFitness() + '\n';
			System.out.println("Optimal parameters are: " + "\nMin Num Obj: "+ (int)swarm.getBestPosition()[0] +"\nConfideceFactor: "+  swarm.getBestPosition()[1] 
				+ "\nWith Fitness: " + swarm.getBestFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "JRip")
		{
			output = "Optimal parameters are: " + "\nFolds "+ swarm.getBestPosition()[0] +"\nminNo: "+  swarm.getBestPosition()[1] +"\nOptimizations: "
					+  swarm.getBestPosition()[2]+"\nWith Fitness: " + swarm.getBestFitness() + '\n';
			System.out.println("Optimal parameters are: " + "\nFolds "+ swarm.getBestPosition()[0] +"\nminNo: "+  swarm.getBestPosition()[1] +"\nOptimizations: "
					+  swarm.getBestPosition()[2]+"\nWith Fitness: " + swarm.getBestFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "MLP")
		{
			output = "Optimal parameters are: " + "\nLearning Rate: "+ swarm.getBestPosition()[0] +"\nMomentum: "+  swarm.getBestPosition()[1] +"\nTraining Time: "+
					swarm.getBestPosition()[2]+"\nValidation Set Size: "+ swarm.getBestPosition()[3] + "\nWith Fitness: " + swarm.getBestFitness() + '\n';
			
			System.out.println("Optimal parameters are: " + "\nLearning Rate: "+ swarm.getBestPosition()[0] +"\nMomentum: "+  swarm.getBestPosition()[1] +"\nTraining Time: "+
					swarm.getBestPosition()[2]+"\nValidation Set Size: "+ swarm.getBestPosition()[3] + "\nWith Fitness: " + swarm.getBestFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "RT")
		{
			output = "Optimal parameters are: " + "\nK Value "+ swarm.getBestPosition()[0] +"\nMax Depth: "+  swarm.getBestPosition()[1] +"\nMin num: "+  
					swarm.getBestPosition()[2]+ "\nNum Folds: "+  swarm.getBestPosition()[3] + "\nWith Fitness: " + swarm.getBestFitness() + '\n';
			System.out.println("Optimal parameters are: " + "\nK Value "+ swarm.getBestPosition()[0] +"\nMax Depth: "+  swarm.getBestPosition()[1] +"\nMin num: "+  
					swarm.getBestPosition()[2]+ "\nNum Folds: "+  swarm.getBestPosition()[3] + "\nWith Fitness: " + swarm.getBestFitness());
		}
		else if(parameters.CLASSIFIER_TO_OPTIMIZE() == "SL")
		{
			output = "Optimal parameters are: " + "\nmaxBoostingIterations "+ swarm.getBestPosition()[0] +"\nnumBoostingIterations: "+  swarm.getBestPosition()[1] +"\nweightTrimBeta: "
					+  swarm.getBestPosition()[2]+"\nWith Fitness: " + swarm.getBestFitness() + '\n';
			System.out.println("Optimal parameters are: " + "\nmaxBoostingIterations "+ swarm.getBestPosition()[0] +"\nnumBoostingIterations: "+  swarm.getBestPosition()[1] +"\nweightTrimBeta: "
					+  swarm.getBestPosition()[2]+"\nWith Fitness: " + swarm.getBestFitness());
		}
		else
		{
			/* this will never happen */
			System.out.println("Incorrect classifier");
		}
		
		Writer writer = new BufferedWriter(new FileWriter(parameters.CLASSIFIER_TO_OPTIMIZE() + ".txt", true));
		writer.append(output);
		writer.close();
	}
		
	
	private static float getNewParticleSpeedForDirectionFloat(Particle particle, Swarm swarm, int direction, double factor) 
	{

		return (float) ((factor * particle.getSpeed()[direction]) + 
				(randomizePercentage(parameters.COGNITIVE_WEIGHT()[direction]) * (particle.getBestPosition()[direction] - particle.getPosition()[direction])) +
				(randomizePercentage(parameters.SOCIAL_WEIGHT()[direction]) * (swarm.getBestPosition()[direction] - particle.getPosition()[direction])));
	}
	private static int getNewParticleSpeedForDirectionInt(Particle particle, Swarm swarm, int direction, double factor) 
	{

		return (int) ((factor * particle.getSpeed()[direction]) + 
				(randomizePercentage(parameters.COGNITIVE_WEIGHT()[direction]) * (particle.getBestPosition()[direction] - particle.getPosition()[direction])) +
				(randomizePercentage(parameters.SOCIAL_WEIGHT()[direction]) * (swarm.getBestPosition()[direction] - particle.getPosition()[direction])));
	}
	
	private static double randomizePercentage(double value) 
	{
		return (random.nextDouble() * value);
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
	
