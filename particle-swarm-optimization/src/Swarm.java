import java.util.Random;

public class Swarm {
	private Particle[] particles;
	private float[] bestPosition;
	private double bestFitness = Double.NEGATIVE_INFINITY;

	public Swarm(int particlesCount, String nameOfClassifier)
	{
		Random random = new Random();
		particles = new Particle[particlesCount];
		for(int i = 0; i < particlesCount; i++)
		{
			float[] initialPosition = null;
			float[] initialSpeed = null;
			
			switch(nameOfClassifier)
			{
				case "J48":
					
					initialPosition = new float[2];
					initialSpeed = new float[2];
					this.bestPosition = new float[2];
					/* set random value (in range 1 - 20) for learningRate */
					initialPosition[0] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
					initialPosition[0] = Math.round(initialPosition[0]);
					
					/* set random value (in range 0.001 - 0.49f) for momentum */
					initialPosition[1] = 0.001f + random.nextFloat() * (0.4999f - 0.001f);
					initialPosition[1] = Math.round(initialPosition[1]*1000.0f)/1000.0f;
				
					/* create random speed for all parameters*/
					initialSpeed[0] = -1.0f + random.nextFloat() * (1.0f + 1.0f);
					initialSpeed[0] = Math.round(initialSpeed[0]);
					
					initialSpeed[1] = -0.1f + random.nextFloat() * (0.1f + 0.1f);
					initialSpeed[1] = Math.round(initialSpeed[1]*100.0f)/100.0f;
					break;
					
				case "JRip":
					initialPosition = new float[3];
					initialSpeed = new float[3];
					this.bestPosition = new float[initialPosition.length];
					
					/* set random value (in range 1 - 50) for Folds */
					initialPosition[0] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
					initialPosition[0] = Math.round(initialPosition[0]);
					
					/* set random value (in range 1 - 50) for minNo */
					initialPosition[1] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
					initialPosition[1] = Math.round(initialPosition[1] * 100.0f) / 100.0f;
						
					/* set random value (in range 1 - 50) for Optimizations */
					initialPosition[2] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
					/* make sure that trainingTime is integer value*/
					initialPosition[2] = Math.round(initialPosition[2]);

					
					/* create random speed for all parameters*/
					initialSpeed[0] = -2.0f + random.nextFloat() * (2.0f + 2.0f);
					initialSpeed[0] = Math.round(initialSpeed[0]);
					
					initialSpeed[1] = -0.5f + random.nextFloat() * (0.5f + 0.5f);
					initialSpeed[1] = Math.round(initialSpeed[1] * 100.0f) / 100.0f;
					
					initialSpeed[2] = -2.0f + random.nextFloat() * (2.0f + 2.00f);
					initialSpeed[2] = Math.round(initialSpeed[2]) ;
					break;
					
				case "MLP":	
					initialPosition = new float[4];
					initialSpeed = new float[4];
					this.bestPosition = new float[initialPosition.length];
					
					/* set random value (in range 0 - 1) for learningRate */
					initialPosition[0] = random.nextFloat();
					initialPosition[0] = Math.round(initialPosition[0]*100.0f)/100.0f;
					/* set random value (in range 0 - 1) for momentum */
					initialPosition[1] = random.nextFloat();
					initialPosition[1] = Math.round(initialPosition[1]*100.0f)/100.0f;
					/* set random value (in range 10 - 2500) for trainingTime */
					initialPosition[2] = 10.0f + random.nextFloat() * (2500.0f - 10.0f);
					/* make sure that trainingTime is integer value*/
					initialPosition[2] = Math.round(initialPosition[2]);
					/* set random value (in range 0 - 99) for validationSetSize */
					initialPosition[3] = random.nextFloat() * (99.0f);
					/* make sure that validationSetSize is integer value*/
					initialPosition[3] = Math.round(initialPosition[3]);
					
					/* create random speed for all parameters*/
					initialSpeed[0] = 0.001f + random.nextFloat() * (0.2f - 0.001f);
					initialSpeed[0] = Math.round(initialSpeed[0]*100.0f)/100.0f;
					
					initialSpeed[1] = -0.9f + random.nextFloat() * (0.2f + 0.9f);
					initialSpeed[1] = Math.round(initialSpeed[1]*100.0f)/100.0f;
					
					initialSpeed[2] = -1000.0f + random.nextFloat() * (1000.0f + 1000.0f);
					initialSpeed[2] = Math.round(initialSpeed[2]);
					
					initialSpeed[3] = -5.0f + random.nextFloat() * (5.0f + 5.0f);
					initialSpeed[3] = Math.round(initialSpeed[3]);
					break;
					
				case "RT":
					initialPosition = new float[4];
					initialSpeed = new float[4];
					this.bestPosition = new float[initialPosition.length];
					/* set random value (in range 0 - 50) for KValue */
					initialPosition[0] = random.nextFloat() * (50.0f);
					initialPosition[0] = Math.round(initialPosition[0]);
					
					/* set random value (in range 0 - 50) for maxDepth */
					initialPosition[1] = random.nextFloat() * (50.0f);
					initialPosition[1] = Math.round(initialPosition[1]);
						
					/* set random value (in range 0 - 50.0) for minNum */
					initialPosition[2] = random.nextFloat() * (50.0f);
					/* make sure that trainingTime is integer value*/
					initialPosition[2] = Math.round(initialPosition[2] * 100.0f) / 100.0f;
					
					/* set random value (in range 1 - 50) for numFolds */
					initialPosition[3] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
					initialPosition[3] = Math.round(initialPosition[3]);
					
					/* create random speed for all parameters*/
					initialSpeed[0] = -5.0f + random.nextFloat() * (5.0f + 5.0f);
					initialSpeed[0] = Math.round(initialSpeed[0]);
					
					initialSpeed[1] = -5.0f + random.nextFloat() * (5.0f + 5.0f);
					initialSpeed[1] = Math.round(initialSpeed[1]);
					
					initialSpeed[2] = -5.0f + random.nextFloat() * (5.0f + 5.f);
					initialSpeed[2] = Math.round(initialSpeed[2] * 100.0f) / 100.0f;
					
					initialSpeed[3] = -5.0f + random.nextFloat() * (5.0f + 5.0f);
					initialSpeed[3] = Math.round(initialSpeed[3]);
					break;
					
				case "SL":	
					initialPosition = new float[3];
					initialSpeed = new float[3];
					this.bestPosition = new float[initialPosition.length];
					
					/* set random value (in range 0 - 10 000) for maxBoostingIterations */
					initialPosition[0] = random.nextFloat() * (10000.0f);
					initialPosition[0] = Math.round(initialPosition[0]);
					
					/* set random value (in range 0 - 10 000) for numBoostingIterations */
					initialPosition[1] = random.nextFloat() * (10000.0f);
					initialPosition[1] = Math.round(initialPosition[1]);
						
					/* set random value (in range 0 - 5.0) for trainingTime */
					initialPosition[2] = random.nextFloat() * (5.0f);
					/* make sure that trainingTime is integer value*/
					initialPosition[2] = Math.round(initialPosition[2] * 100.0f) / 100.0f;

					
					/* create random speed for all parameters*/
					initialSpeed[0] = -10.0f + random.nextFloat() * (10.0f + 10.0f);
					initialSpeed[0] = Math.round(initialSpeed[0]);
					
					initialSpeed[1] = -10.0f + random.nextFloat() * (10.0f + 10.0f);
					initialSpeed[1] = Math.round(initialSpeed[1]);
					
					initialSpeed[2] = -0.01f + random.nextFloat() * (0.01f + 0.01f);
					initialSpeed[2] = Math.round(initialSpeed[2] * 100.0f) / 100.0f;
					break;
					
				default:
					/* this will never happen*/
					break;
			}
			
			/* create particle*/
			particles[i] = new Particle(initialPosition, initialSpeed);
		}
	}
	
	public void getParticlesPositions()
	{
		for(int i = 0; i < this.particles.length; i++)
		{
			System.out.println(this.particles[i].toString());
		}
	}
	
	public Particle[] getParticles()
	{
		return this.particles;
	}
	
	public double getBestFitness()
	{
		return this.bestFitness;
	}
	
	public void setBestFitness(double fitness)
	{
		this.bestFitness = fitness;
	}

	public float[] getBestPosition() {
		return bestPosition;
	}

	public void setBestPosition(float[] bestPosition) 
	{
		for(int i = 0; i < this.bestPosition.length; i++)
		{
			this.bestPosition[i] = bestPosition[i];
		}
	}
}
