import java.util.ArrayList;

public class KMeans {
	private Cuckoo[] data;
	private int numberOfClusters;
	private float[][] centroids;
	private int ndim;
	private int[] labels;
	
	public KMeans(Cuckoo[] cuckoos)
	{
		this.data = cuckoos;
		this.ndim = cuckoos[0].getHabitat().length;
	}
	
	public void clustering(int numberOfClusters, int numberOfIterations, float[][] centroids)
	{
		this.numberOfClusters = numberOfClusters;
		
		if(centroids != null)
		{
			this.centroids = centroids;
		}
		else
		{
			/* select centroids randomly */
			this.centroids = new float[this.numberOfClusters][];
			
			ArrayList<Integer> idx = new ArrayList<Integer>();
			
			for(int i = 0; i < this.numberOfClusters; i++)
			{
				int c;
				do {
					c = (int) (Math.random() * data.length); // chyba
				}while(idx.contains(c));
				idx.add(c);
				
				this.centroids[i] = new float[this.ndim];
				
				for(int j = 0; j < this.ndim; j++)
				{
					this.centroids[i][j] = this.data[c].getHabitat()[j];
				}	
			}
		}
		
		float [][] c1 = this.centroids;
		double threshold = 0.001;
		int iteration = 0;
		
		while(true)
		{
			this.centroids = c1;
			this.labels = new int[this.data.length];
			
			for(int i = 0; i < this.labels.length; i++)
			{
				this.labels[i] = findClosest(this.data[i]);
			}
			
			c1 = updateCentroids();
			iteration++;
			
			if(numberOfIterations > 0 && iteration >= numberOfIterations || converge(c1, threshold))
			{
				break;
			}
		}
	}
	
	private boolean converge(float[][] c1, double threshold)
	{
		double maxv = 0;
	    for (int i=0; i< this.numberOfClusters; i++)
	    {
	        double d= countMinDistance(this.centroids[i], c1[i]);
	        if (maxv<d)
	        {
	            maxv = d;
	        }
	    }
	    if (maxv < threshold)
	    {
	        return true;
	    }
	    else
	    {
	    	return false;
	    }
	}
	
	private float[][] updateCentroids()
	{
		float[][] newCentroid = new float[this.numberOfClusters][];
		int[] counts = new int[this.numberOfClusters];
		
		for(int i = 0; i < counts.length; i++)
		{
			counts[i] = 0;
			newCentroid[i] = new float[this.ndim];
			
			for(int j = 0; j < this.ndim; j++)
			{
				newCentroid[i][j] = 0;
			}
		}
		
		for(int i = 0; i < this.data.length; i++)
		{
			int cn = this.labels[i];
			
			for(int j = 0; j < this.ndim; j++)
			{
				newCentroid[cn][j] += this.data[i].getHabitat()[j];
			}
			counts[cn]++;
		}
		
		for(int i = 0; i < this.numberOfClusters; i++)
		{
			for(int j = 0; j < this.ndim; j++)
			{
				newCentroid[i][j] /= counts[i];
			}
		}
		
		return newCentroid;
	}
	
	private int findClosest(Cuckoo cuckoo)
	{
		double minDistance  = countMinDistance(cuckoo.getHabitat(), this.centroids[0]);
		int label = 0;
		
		for(int i = 0; i < this.numberOfClusters; i++)
		{
			double a = countMinDistance(cuckoo.getHabitat(), this.centroids[i]);
			
			if(a < minDistance)
			{
				minDistance = a;
				label = i;
			}
		}
		return label;
	}
	
	private double countMinDistance(float[] habitat, float[] centroid)
	{
		double sum = 0;
		
		for(int i = 0; i < habitat.length; i++)
		{
			double a = habitat[i] - centroid[i];
			sum += a*a;
		}
		return Math.sqrt(sum);
	}
	
	public void printResults()
	{	      
		System.out.println("Label:");
	     
		for (int i=0; i<this.data.length; i++)
	    {
	       System.out.println(this.labels[i]);
	    }
	    System.out.println("Centroids:");
	    for (int i=0; i< this.numberOfClusters; i++)
	    {
	    	for(int j=0; j < this.ndim; j++)
	    	{
	          System.out.print(this.centroids[i][j] + " ");
	    	}
	        System.out.println(); 
	    }
	}
	
	public int[] getLabels()
	{
		return this.labels;
	}
	
	public float[] getCentroid(int index)
	{
		return this.centroids[index];
	}
}
