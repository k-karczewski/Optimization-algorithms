
public class ParameterFile {
	private int NUMBER_OF_FOLDS;
	private  float[][] PARTICLE_RANGES;
	private double INERTIA_FACTOR;
	private double[] COGNITIVE_WEIGHT;
	private double[] SOCIAL_WEIGHT;
	
	/* time to optimization in seconds*/
	private float TIME_TO_OPTIMIZATION;
	private String CLASSIFIER_TO_OPTIMIZE;
	
	public ParameterFile(String nameOfClassifier)
	{
		CLASSIFIER_TO_OPTIMIZE = nameOfClassifier;
		NUMBER_OF_FOLDS = 10;
		
		switch(nameOfClassifier)
		{
			case "J48":
				/* Particle ranges for J48*/
				PARTICLE_RANGES = new float[2][2];
				PARTICLE_RANGES[0][0] = 1.0f;
				PARTICLE_RANGES[0][1] = 50.0f;
				PARTICLE_RANGES[1][0] = 0.001f;
				PARTICLE_RANGES[1][1] = 0.50f;
				
				/* INERTIA_FACTOR for J48 */
				INERTIA_FACTOR = 0.2;
				
				/* COGNITIVE_WEIGHT for J48 */
				COGNITIVE_WEIGHT = new double[2];
				COGNITIVE_WEIGHT[0] = 0.12;
				COGNITIVE_WEIGHT[1] = 0.12;
				
				/* COGNITIVE_WEIGHT for J48 */
				SOCIAL_WEIGHT = new double[2];
				SOCIAL_WEIGHT[0] = 0.36;
				SOCIAL_WEIGHT[1] = 0.36;
				
				/* time of optimization for J48 */
				TIME_TO_OPTIMIZATION = 0.1f;
				break;
			
			case "JRip": 
				/* Particle ranges for JRip*/
				PARTICLE_RANGES = new float[3][2];
				PARTICLE_RANGES[0][0] = 1.0f;
				PARTICLE_RANGES[0][1] = 50.0f;
				PARTICLE_RANGES[1][0] = 1.0f;
				PARTICLE_RANGES[1][1] = 50.0f;
				PARTICLE_RANGES[2][0] = 1.0f;
				PARTICLE_RANGES[2][1] = 50.0f;
				
				/* INERTIA_FACTOR for JRip */
				INERTIA_FACTOR = 0.2;

				
				/* COGNITIVE_WEIGHT for JRip */
				COGNITIVE_WEIGHT = new double[3];
				COGNITIVE_WEIGHT[0] = 0.64;
				COGNITIVE_WEIGHT[1] = 0.64;
				COGNITIVE_WEIGHT[2] = 0.64;
				
				/* COGNITIVE_WEIGHT for JRip */
				SOCIAL_WEIGHT = new double[3];
				SOCIAL_WEIGHT[0] = 0.8;
				SOCIAL_WEIGHT[1] = 0.8;
				SOCIAL_WEIGHT[2] = 0.8;
				
				/* time of optimization for JRip */
				TIME_TO_OPTIMIZATION = 0.1f;
				break;
			
			case "MLP": 
				/* Particle ranges for MLP */
				PARTICLE_RANGES = new float[4][2];
				PARTICLE_RANGES[0][0] = 0.0f;
				PARTICLE_RANGES[0][1] = 1.0f;
				PARTICLE_RANGES[1][0] = 0.0f;
				PARTICLE_RANGES[1][1] = 1.0f;
				PARTICLE_RANGES[2][0] = 10.0f;
				PARTICLE_RANGES[2][1] = 2500.0f;
				PARTICLE_RANGES[3][0] = 0.0f;
				PARTICLE_RANGES[3][1] = 99.0f;
				
				/* INERTIA_FACTOR for MLP */
				INERTIA_FACTOR = 0.2;

				
				/* COGNITIVE_WEIGHT for MLP */
				COGNITIVE_WEIGHT = new double[4];
				COGNITIVE_WEIGHT[0] = 0.2;
				COGNITIVE_WEIGHT[1] = 0.2;
				COGNITIVE_WEIGHT[2] = 0.45;
				COGNITIVE_WEIGHT[3] = 0.45;
				
				/* COGNITIVE_WEIGHT for MLP */
				SOCIAL_WEIGHT = new double[4];
				SOCIAL_WEIGHT[0] = 0.2;
				SOCIAL_WEIGHT[1] = 0.2;
				SOCIAL_WEIGHT[2] = 0.45;
				SOCIAL_WEIGHT[3] = 0.45;
				
				/* time of optimization for MLP */
				TIME_TO_OPTIMIZATION = 0.1f;
				break;
				
			case "RT": /* Random Tree*/
				/* Particle ranges for RT */
				PARTICLE_RANGES = new float[4][2];
				PARTICLE_RANGES[0][0] = 0.0f;
				PARTICLE_RANGES[0][1] = 50.0f;
				PARTICLE_RANGES[1][0] = 0.0f;
				PARTICLE_RANGES[1][1] = 50.0f;
				PARTICLE_RANGES[2][0] = 0.0f;
				PARTICLE_RANGES[2][1] = 50.0f;
				PARTICLE_RANGES[3][0] = 2.0f;
				PARTICLE_RANGES[3][1] = 50.0f;
				
				/* INERTIA_FACTOR for RT */
				INERTIA_FACTOR = 0.2;

				
				/* COGNITIVE_WEIGHT for RT */
				COGNITIVE_WEIGHT = new double[4];
				COGNITIVE_WEIGHT[0] = 0.74;
				COGNITIVE_WEIGHT[1] = 0.74;
				COGNITIVE_WEIGHT[2] = 0.74;
				COGNITIVE_WEIGHT[3] = 0.74;
				
				/* COGNITIVE_WEIGHT for RT */
				SOCIAL_WEIGHT = new double[4];
				SOCIAL_WEIGHT[0] = 0.7;
				SOCIAL_WEIGHT[1] = 0.7;
				SOCIAL_WEIGHT[2] = 0.7;
				SOCIAL_WEIGHT[3] = 0.7;
				
				/* time of optimization for RT */
				TIME_TO_OPTIMIZATION = 0.1f;
				break;
			
			case "SL": /* Simple Logistic */
				/* Particle ranges for RT */
				PARTICLE_RANGES = new float[3][2];
				PARTICLE_RANGES[0][0] = 0.0f;
				PARTICLE_RANGES[0][1] = 10000.0f;
				PARTICLE_RANGES[1][0] = 0.0f;
				PARTICLE_RANGES[1][1] = 10000.0f;
				PARTICLE_RANGES[2][0] = 0.0f;
				PARTICLE_RANGES[2][1] = 5.0f;
				
				/* INERTIA_FACTOR for RT */
				INERTIA_FACTOR = 0.2;

	
				/* COGNITIVE_WEIGHT for SL */
				COGNITIVE_WEIGHT = new double[3];
				COGNITIVE_WEIGHT[0] = 1.74;
				COGNITIVE_WEIGHT[1] = 1.74;
				COGNITIVE_WEIGHT[2] = 1.74;
				
				/* COGNITIVE_WEIGHT for SL */
				SOCIAL_WEIGHT = new double[3];
				SOCIAL_WEIGHT[0] = 1.7;
				SOCIAL_WEIGHT[1] = 1.7;
				SOCIAL_WEIGHT[2] = 1.7;
				
				/* time of optimization for SL */
				TIME_TO_OPTIMIZATION = 0.1f;
				break;
			
			default:
				/* this will never happen */
				break;
		}
	}
	
	public String CLASSIFIER_TO_OPTIMIZE()
	{
		return this.CLASSIFIER_TO_OPTIMIZE;
	}

	public int NUMBER_OF_FOLDS() 
	{
		return this.NUMBER_OF_FOLDS;
	}
	
	public double INERTIA_FACTOR() 
	{
		return this.INERTIA_FACTOR;
	}
	
	public double[] COGNITIVE_WEIGHT() 
	{
		return this.COGNITIVE_WEIGHT;
	}
	
	public double[] SOCIAL_WEIGHT() 
	{
		return this.SOCIAL_WEIGHT;
	}
	
	public float TIME_TO_OPTIMIZATION()
	{
		return this.TIME_TO_OPTIMIZATION;
	}
	
	public float[][] PARTICLE_RANGES()
	{
		return this.PARTICLE_RANGES;
	}
}
