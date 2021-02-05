import java.util.Random;

public class Cuckoo implements Comparable {
	/* position in other algorithms */
	private float[] habitat;
	/* fitness in other algorithms */
	private double profit;
	/* number of eggs to lay by Cuckoo in range (5-20)*/
	private int numberOfEggs;
	Egg[] eggs;
	private int group = -1;
	private boolean isYoung;
	
	private float[] eggLayingRadius;
	
	public Cuckoo(String nameOfClassifier)
	{
		Random random = new Random();		
		
		switch(nameOfClassifier)
		{
			case "J48":
				habitat = new float[2];
				/* set random value (in range 1 - 20) for learningRate */
				habitat[0] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				habitat[0] = Math.round(habitat[0]);
				
				/* set random value (in range 0.001 - 0.49f) for momentum */
				habitat[1] = 0.001f + random.nextFloat() * (0.4999f - 0.001f);
				habitat[1] = Math.round(habitat[1]*1000.0f)/1000.0f;
				break;
				
			case "JRip":
				habitat = new float[3];				
				/* set random value (in range 1 - 50) for Folds */
				habitat[0] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				habitat[0] = Math.round(habitat[0]);
				
				/* set random value (in range 1 - 50) for minNo */
				habitat[1] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				habitat[1] = Math.round(habitat[1] * 100.0f) / 100.0f;
					
				/* set random value (in range 1 - 50) for Optimizations */
				habitat[2] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				/* make sure that trainingTime is integer value*/
				habitat[2] = Math.round(habitat[2]);
				break;
				
			case "MLP":	
				habitat = new float[4];
				/* set random value (in range 0 - 1) for learningRate */
				habitat[0] = random.nextFloat();
				habitat[0] = Math.round(habitat[0]*100.0f)/100.0f;
				/* set random value (in range 0 - 1) for momentum */
				habitat[1] = random.nextFloat();
				habitat[1] = Math.round(habitat[1]*100.0f)/100.0f;
				/* set random value (in range 10 - 2500) for trainingTime */
				habitat[2] = 10.0f + random.nextFloat() * (2500.0f - 10.0f);
				/* make sure that trainingTime is integer value*/
				habitat[2] = Math.round(habitat[2]);
				/* set random value (in range 0 - 99) for validationSetSize */
				habitat[3] = random.nextFloat() * (99.0f);
				/* make sure that validationSetSize is integer value*/
				habitat[3] = Math.round(habitat[3]);
				
				break;
				
			case "RT":
				habitat = new float[4];
				/* set random value (in range 0 - 50) for KValue */
				habitat[0] = random.nextFloat() * (50.0f);
				habitat[0] = Math.round(habitat[0]);
				
				/* set random value (in range 0 - 50) for maxDepth */
				habitat[1] = random.nextFloat() * (50.0f);
				habitat[1] = Math.round(habitat[1]);
					
				/* set random value (in range 0 - 50.0) for minNum */
				habitat[2] = random.nextFloat() * (50.0f);
				/* make sure that trainingTime is integer value*/
				habitat[2] = Math.round(habitat[2] * 100.0f) / 100.0f;
				
				/* set random value (in range 1 - 50) for numFolds */
				habitat[3] = 1.0f + random.nextFloat() * (50.0f - 1.0f);
				habitat[3] = Math.round(habitat[3]);
				
				break;
				
			case "SL":	
				habitat = new float[3];				
				/* set random value (in range 0 - 10 000) for maxBoostingIterations */
				habitat[0] = random.nextFloat() * (10000.0f);
				habitat[0] = Math.round(habitat[0]);
				
				/* set random value (in range 0 - 10 000) for numBoostingIterations */
				habitat[1] = random.nextFloat() * (10000.0f);
				habitat[1] = Math.round(habitat[1]);
					
				/* set random value (in range 0 - 5.0) for trainingTime */
				habitat[2] = random.nextFloat() * (5.0f);
				/* make sure that trainingTime is integer value*/
				habitat[2] = Math.round(habitat[2] * 100.0f) / 100.0f;
				break;
				
			default:
				/* this will never happen*/
				break;
		}
				
		this.setYoung(false);
	}
	
	public Cuckoo(Egg egg)
	{
		this.habitat = new float[egg.getHabitat().length];
		for(int i = 0; i < egg.getHabitat().length; i++)
		{
			this.habitat[i] = egg.getHabitat()[i];
		}
		this.profit = egg.getProfit();
		this.setYoung(true);
	}
	
	public void initializeEggs()
	{
		Random random = new Random();
		this.setNumberOfEggs(5 + random.nextInt(15));
		this.eggs = new Egg[this.getNumberOfEggs()];
		
		for(int j = 0; j < this.eggs.length; j++)
		{
			this.eggs[j] = new Egg(this.habitat.length);
		}
	}

	public static float[] repairPosition(float[] habitat, float[][] cuckooRanges)
	{
		for(int i = 0; i < habitat.length; i++)
		{
			if(habitat[i] < cuckooRanges[i][0])
			{
				habitat[i] = cuckooRanges[i][0];
			}
			
			if(habitat[i] > cuckooRanges[i][1])
			{
				habitat[i] = cuckooRanges[i][1];
			}
		}

		return habitat;
	}
	
	public void countEggLayingRadius(float totalNumberOfEggs, float[] maxValOfELR, float[] varHi, float[] varLow) 
	{
		this.eggLayingRadius = new float[this.habitat.length];
		for(int i = 0; i < this.eggLayingRadius.length; i++)
		{
			this.eggLayingRadius[i] = (float) maxValOfELR[i] * (this.getNumberOfEggs() / totalNumberOfEggs) * (varHi[i] - varLow[i]);
		}
	}
	
	public Egg getEgg(int index)
	{
		return this.eggs[index];
	}
	
	public void layEggsWithinELR(float[][] ranges)
	{
		Random random = new Random();		
		float[] eggPosition = new float[this.habitat.length];
		int i = 0;
	
		while(i < this.eggs.length)
		{
			for(int j = 0; j < eggPosition.length; j++)
			{
				float lower = -this.getEggLayingRadius()[j];
				eggPosition[j] = this.habitat[j] + (lower + random.nextFloat() * (this.getEggLayingRadius()[j] - lower));
			}

			if(!isPositionInCorrectRange(eggPosition, ranges))
			{
				eggPosition = repairPosition(eggPosition, ranges);
			}

			this.eggs[i].setHabitat(eggPosition);
			i++;		
		}		
	}
	
	public void killEgg(int index)
	{
		Egg[] tmp = new Egg[this.eggs.length-1];
		
		/* make a copy*/
		for(int i = 0; i < tmp.length; i++)
		{
			tmp[i] = this.eggs[i];
		}
		
		if(index < this.eggs.length-1)
		{
			for(int i = index; i < tmp.length; i++)
			{
				tmp[i] = this.eggs[i+1];
			}
		}		
		this.eggs = tmp;
		this.numberOfEggs--;
	}
	
	public void killAllEggs()
	{
		this.eggs = null;
	}
	
	public boolean isPositionInCorrectRange(float[] position, float[][] parameters)
	{
		for(int i = 0; i < position.length; i++)
		{
			if(position[i] < parameters[i][0] || position[i] > parameters[i][1])
			{
				return false;
			}
		}
		return true;
	}
	
	public int getGroup() {
		return group;
	}

	public void setGroups(int group) {
		this.group = group;
	}

	public boolean isYoung() {
		return isYoung;
	}

	public void setYoung(boolean isYoung) {
		this.isYoung = isYoung;
	}

	public float[] getHabitat() {
		return habitat;
	}
	
	public void setHabitat(float[] habitat) 
	{
		for(int i = 0; i < this.habitat.length; i++)
		{
			this.habitat[i] = habitat[i];
		}
	}
	
	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public int getNumberOfEggs() {
		return numberOfEggs;
	}

	public void setNumberOfEggs(int numberOfEggs) {
		this.numberOfEggs = numberOfEggs;
	}

	public float[] getEggLayingRadius() {
		return eggLayingRadius;
	}

	@Override
    public int compareTo(Object cuckoo) {
        double profit = ((Cuckoo)cuckoo).getProfit();
        
        return Double.valueOf(this.profit).compareTo(profit);
    }
}
