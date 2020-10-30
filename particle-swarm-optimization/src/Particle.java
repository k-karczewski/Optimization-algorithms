import java.util.Arrays;

public class Particle {
	private float[] position;
	private float[] speed;
	private double fitness;
	private float[] bestPosition;
	private double bestFitness = Double.NEGATIVE_INFINITY;
	
	public Particle(float[] initialPosition, float[] initialSpeed)
	{
		this.bestPosition = new float[initialPosition.length];
		this.setPosition(initialPosition);
		this.setSpeed(initialSpeed);
	}
	
	public boolean isPositionInCorrectRange(float[][] particleRanges)
	{
		for(int i = 0; i < this.position.length; i++)
		{
			if(this.position[i] < particleRanges[i][0] || this.position[i] > particleRanges[i][1])
			{
				return false;
			}
		}
		return true;
	}
	
	public void repairParticlePosition(float[][] particleRanges)
	{
		for(int i = 0; i < this.position.length; i++)
		{
			if(this.position[i] < particleRanges[i][0])
			{
				this.position[i] = particleRanges[i][0];
			}
			
			if(this.position[i] > particleRanges[i][1])
			{
				this.position[i] = particleRanges[i][1];
			}
		}
	}
	
	public float[] getPosition() {
		return position;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public float[] getSpeed() {
		return speed;
	}

	public void setSpeed(float[] speed) {
		this.speed = speed;
	}

	public float[] getBestPosition() {
		return bestPosition;
	}

	public void setBestPosition(float[] bestPosition) {
		for(int i = 0; i < this.bestPosition.length; i++)
		{
			this.bestPosition[i] = bestPosition[i];
		}
	}
	
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double getBestFitness() {
		return bestFitness;
	}

	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}
	
	public static boolean arePositionsTheSame(float[] currentPosition, float[] bestPosition)
	{
		for(int i = 0; i < currentPosition.length; i++)
		{
			if(currentPosition[i] != bestPosition[i])
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return "Particle [position=" + Arrays.toString(position) + ", speed=" + Arrays.toString(speed) + ", fitness="
				+ fitness + ", bestPosition=" + Arrays.toString(bestPosition) + ", bestFitness=" + bestFitness + "]";
	}
}
