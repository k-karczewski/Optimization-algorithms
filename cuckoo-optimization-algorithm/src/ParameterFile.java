
public class ParameterFile {
	
	private int NUMBER_OF_FOLDS;
	private float[][] CUCKOO_RANGES;
	
	/* maximal number of cuckoos at one moment */
	private int MAX_NUM_OF_CUCKOOS;
	
	private float[] MAX_VAL_OF_ELR;
	/* maximal distance to lay egg used formula as varhi */
	private float[] VAR_HI;
	/* minimal distance to lay egg used formula as varlow */
	private float[] VAR_LOW;
	
	private final int NUMBER_OF_CLUSTERS = 5;
	
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
				CUCKOO_RANGES = new float[2][2];
				CUCKOO_RANGES[0][0] = 1.0f;
				CUCKOO_RANGES[0][1] = 50.0f;
				CUCKOO_RANGES[1][0] = 0.001f;
				CUCKOO_RANGES[1][1] = 0.50f;
				
				VAR_HI = new float[2];
				VAR_HI[0] = 5.0f;
				VAR_HI[1] = 0.5f;
				
				VAR_LOW = new float[2];
				VAR_LOW[0] = 1.0f;
				VAR_LOW[1] = 0.0f;
				
				MAX_VAL_OF_ELR = new float[2];
				MAX_VAL_OF_ELR[0] = 3.0f;
				MAX_VAL_OF_ELR[1] = 0.5f;
				
				MAX_NUM_OF_CUCKOOS = 20;
				
				/* time of optimization for J48 */
				TIME_TO_OPTIMIZATION = 0.2f;
					
				break;
			
			case "JRip": 
				/* Particle ranges for JRip*/
				CUCKOO_RANGES = new float[3][2];
				CUCKOO_RANGES[0][0] = 1.0f;
				CUCKOO_RANGES[0][1] = 50.0f;
				CUCKOO_RANGES[1][0] = 1.0f;
				CUCKOO_RANGES[1][1] = 50.0f;
				CUCKOO_RANGES[2][0] = 1.0f;
				CUCKOO_RANGES[2][1] = 50.0f;
				
				VAR_HI = new float[3];
				VAR_HI[0] = 5.0f;
				VAR_HI[1] = 5.0f;
				VAR_HI[2] = 5.0f;
				
				VAR_LOW = new float[3];
				VAR_LOW[0] = 2.0f;
				VAR_LOW[1] = 2.0f;
				VAR_LOW[2] = 2.0f;
				
				MAX_VAL_OF_ELR = new float[3];
				MAX_VAL_OF_ELR[0] = 10.0f;
				MAX_VAL_OF_ELR[1] = 10.0f;
				MAX_VAL_OF_ELR[2] = 10.0f;
				
				MAX_NUM_OF_CUCKOOS = 20;
				
				/* time of optimization for JRip */
				TIME_TO_OPTIMIZATION = 0.2f;
				break;
			
			case "MLP": 
				/* Particle ranges for MLP */
				CUCKOO_RANGES = new float[4][2];
				CUCKOO_RANGES[0][0] = 0.0f;
				CUCKOO_RANGES[0][1] = 1.0f;
				CUCKOO_RANGES[1][0] = 0.0f;
				CUCKOO_RANGES[1][1] = 1.0f;
				CUCKOO_RANGES[2][0] = 10.0f;
				CUCKOO_RANGES[2][1] = 2500.0f;
				CUCKOO_RANGES[3][0] = 0.0f;
				CUCKOO_RANGES[3][1] = 99.0f;
				
				VAR_HI = new float[4];
				VAR_HI[0] = 0.5f;
				VAR_HI[1] = 0.5f;
				VAR_HI[2] = 500.0f;
				VAR_HI[3] = 5.0f;
				
				VAR_LOW = new float[4];
				VAR_LOW[0] = 0.0005f;
				VAR_LOW[1] = 0.0005f;
				VAR_LOW[2] = 20.0f;
				VAR_LOW[3] = 0.05f;
				
				MAX_VAL_OF_ELR = new float[4];
				MAX_VAL_OF_ELR[0] = 0.5f;
				MAX_VAL_OF_ELR[1] = 0.5f;
				MAX_VAL_OF_ELR[2] = 200.0f;
				MAX_VAL_OF_ELR[3] = 5.0f;
				
				MAX_NUM_OF_CUCKOOS = 20;
				
				/* time of optimization for MLP */
				TIME_TO_OPTIMIZATION = 0.2f;
				break;
				
			case "RT": /* Random Tree*/
				/* Particle ranges for RT */
				CUCKOO_RANGES = new float[4][2];
				CUCKOO_RANGES[0][0] = 0.0f;
				CUCKOO_RANGES[0][1] = 50.0f;
				CUCKOO_RANGES[1][0] = 0.0f;
				CUCKOO_RANGES[1][1] = 50.0f;
				CUCKOO_RANGES[2][0] = 0.0f;
				CUCKOO_RANGES[2][1] = 50.0f;
				CUCKOO_RANGES[3][0] = 2.0f;
				CUCKOO_RANGES[3][1] = 50.0f;
				
				VAR_HI = new float[4];
				VAR_HI[0] = 5.0f;
				VAR_HI[1] = 5.0f;
				VAR_HI[2] = 5.0f;
				VAR_HI[3] = 5.0f;
				
				VAR_LOW = new float[4];
				VAR_LOW[0] = 2.0f;
				VAR_LOW[1] = 3.0f;
				VAR_LOW[2] = 2.0f;
				VAR_LOW[3] = 3.0f;
				
				MAX_VAL_OF_ELR = new float[4];
				MAX_VAL_OF_ELR[0] = 10.0f;
				MAX_VAL_OF_ELR[1] = 10.0f;
				MAX_VAL_OF_ELR[2] = 10.0f;
				MAX_VAL_OF_ELR[3] = 10.0f;
				
				MAX_NUM_OF_CUCKOOS = 20;
				
				/* time of optimization for RT */
				TIME_TO_OPTIMIZATION = 0.2f;
				break;
			
			case "SL": /* Simple Logistic */
				/* Particle ranges for RT */
				CUCKOO_RANGES = new float[3][2];
				CUCKOO_RANGES[0][0] = 0.0f;
				CUCKOO_RANGES[0][1] = 10000.0f;
				CUCKOO_RANGES[1][0] = 0.0f;
				CUCKOO_RANGES[1][1] = 10000.0f;
				CUCKOO_RANGES[2][0] = 0.0f;
				CUCKOO_RANGES[2][1] = 5.0f;
				
				VAR_HI = new float[3];
				VAR_HI[0] = 100.0f;
				VAR_HI[1] = 100.0f;
				VAR_HI[2] = 5.0f;
				
				VAR_LOW = new float[3];
				VAR_LOW[0] = 0.0f;
				VAR_LOW[1] = 0.0f;
				VAR_LOW[2] = 0.0f;
	
				MAX_VAL_OF_ELR = new float[3];
				MAX_VAL_OF_ELR[0] = 100.0f;
				MAX_VAL_OF_ELR[1] = 100.0f;
				MAX_VAL_OF_ELR[2] = 5.0f;
				
				MAX_NUM_OF_CUCKOOS = 20;
				
				/* time of optimization for SL */
				TIME_TO_OPTIMIZATION = 0.2f;
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
	
	public float[][] CUCKOO_RANGES()
	{
		return this.CUCKOO_RANGES;
	}
	
	public float[] VAR_HI()
	{
		return this.VAR_HI;
	}
	
	public float[] VAR_LOW()
	{
		return this.VAR_LOW;
	}
	
	public float[] MAX_VAL_OF_ELR()
	{
		return this.MAX_VAL_OF_ELR;
	}
	
	public int NUMBER_OF_CLUSTERS()
	{
		return this.NUMBER_OF_CLUSTERS;
	}
	
	public int MAX_NUM_OF_CUCKOOS()
	{
		return this.MAX_NUM_OF_CUCKOOS;
	}
}
