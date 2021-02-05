
public class Egg {
	private float[] habitat;
	private double profit = Double.NEGATIVE_INFINITY;
	
	public Egg(int numOfDim)
	{
		this.habitat = new float[numOfDim];
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

	public void repairEggPosition(float[][] cuckooRanges)
	{
		for(int i = 0; i < this.habitat.length; i++)
		{
			if(this.habitat[i] < cuckooRanges[i][0])
			{
				this.habitat[i] = cuckooRanges[i][0];
			}
			
			if(this.habitat[i] > cuckooRanges[i][1])
			{
				this.habitat[i] = cuckooRanges[i][1];
			}
		}
	}
	
	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}
}
