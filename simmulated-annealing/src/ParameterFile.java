
public class ParameterFile {
	private int NUMBER_OF_FOLDS;
	private  float[][] PARTICLE_RANGES;
	
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
	
	public float TIME_TO_OPTIMIZATION()
	{
		return this.TIME_TO_OPTIMIZATION;
	}
	
	public float[][] PARTICLE_RANGES()
	{
		return this.PARTICLE_RANGES;
	}
}
