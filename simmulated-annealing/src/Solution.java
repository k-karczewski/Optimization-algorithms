import java.util.Random;

public class Solution {
	private float[] position;
	private double fitness = 0;
	private Random random;
	
	public Solution(String nameOfClassifier)
	{
		this.random = new Random();
		
		switch(nameOfClassifier)
		{
			case "J48":
				this.position = new float[2];
				this.fitness = 0;
				/* set random value (in range 1 - 20) for learningRate */
				this.position[0] = 1.0f + random.nextFloat() * (20.0f - 1.0f);
				this.position[0] = Math.round(position[0]*100.0f)/100.0f;
					
				/* set random value (in range 0.001 - 0.49f) for momentum */
				this.position[1] = 0.001f + random.nextFloat() * (0.500f - 0.001f);
				this.position[1] = Math.round(position[1]*1000.0f)/1000.0f;
				break;
			
			case "JRip":
				this.position = new float[3];
				this.fitness = 0;
				position[0] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				position[0] = Math.round(position[0]);
				
				/* set random value (in range 1 - 50) for minNo */
				position[1] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				position[1] = Math.round(position[1] * 100.0f) / 100.0f;
					
				/* set random value (in range 1 - 50) for Optimizations */
				position[2] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				/* make sure that trainingTime is integer value*/
				position[2] = Math.round(position[2]);
				break;
			
			case "MLP":
				this.position = new float[4];
				this.fitness = 0;
				
				/* set random value (in range 0 - 1) for learningRate */
				this.position[0] = random.nextFloat();
				this.position[0] = Math.round(this.position[0]*100.0f)/100.0f;
				/* set random value (in range 0 - 1) for momentum */
				this.position[1] = random.nextFloat();
				this.position[1] = Math.round(this.position[1]*100.0f)/100.0f;
				/* set random value (in range 10 - 2500) for trainingTime */
				this.position[2] = 10.0f + random.nextFloat() * (2500.0f - 10.0f);
				/* make sure that trainingTime is integer value*/
				this.position[2] = Math.round(this.position[2]);
				/* set random value (in range 0 - 99) for validationSetSize */
				this.position[3] = random.nextFloat() * (99.0f);
				/* make sure that validationSetSize is integer value*/
				this.position[3] = Math.round(this.position[3]);
				break;
			
			case "RT":
				this.position = new float[4];
				this.fitness = 0;
				
				/* set random value (in range 0 - 50) for KValue */
				this.position[0] = random.nextFloat() * (50.0f);
				this.position[0] = Math.round(this.position[0]);
				
				/* set random value (in range 0 - 50) for maxDepth */
				this.position[1] = random.nextFloat() * (50.0f);
				this.position[1] = Math.round(this.position[1]);
					
				/* set random value (in range 0 - 50.0) for minNum */
				this.position[2] = random.nextFloat() * (50.0f);
				/* make sure that trainingTime is integer value*/
				this.position[2] = Math.round(this.position[2] * 100.0f) / 100.0f;
				
				/* set random value (in range 2 - 50) for numFolds */
				this.position[3] = 2.0f + random.nextFloat() * (50.0f - 2.0f);
				this.position[3] = Math.round(this.position[3]);
				break;
			
			case "SL":
				this.position = new float[3];
				this.fitness = 0;
				
				/* set random value (in range 0 - 10 000) for maxBoostingIterations */
				this.position[0] = random.nextFloat() * (10000.0f);
				this.position[0] = Math.round(this.position[0]);
				
				/* set random value (in range 0 - 10 000) for numBoostingIterations */
				this.position[1] = random.nextFloat() * (10000.0f);
				this.position[1] = Math.round(this.position[1]);
					
				/* set random value (in range 0 - 5.0) for trainingTime */
				this.position[2] = random.nextFloat() * (5.0f);
				/* make sure that trainingTime is integer value*/
				this.position[2] = Math.round(this.position[2] * 100.0f) / 100.0f;
				break;
			
			default:
				/* this will never happen*/
				break;
		
		}
	
	}
	
	public float[] getPosition()
	{
		return this.position;
	}
	
	public void setPosition(float[] position)
	{
		for (int i = 0; i < this.position.length; i++)
		{
			this.position[i] = position[i];
		}
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
}
