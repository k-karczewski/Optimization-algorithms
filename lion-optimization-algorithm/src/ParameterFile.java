public class ParameterFile
{
    private final int PERCENT_TO_BE_NOMAD = 10;
    private final int NUMBER_OF_PRIDES = 3;
    private final int NUMBER_OF_GROUPS = 3;
    private final int PERCENT_OF_MALES_IN_PRIDE = 15;
    private final int PERCENT_OF_FEMALES_IN_PRIDE = 100 - PERCENT_OF_MALES_IN_PRIDE;
    private final int FEMALE_IMMIGRATE_RATE = 10;
    /* has to be the same as males in pride */
    private final int PERCENT_OF_FEMALES_IN_NOMADS = 15;
    private final int PERCENT_OF_MALES_IN_NOMADS = 100 - PERCENT_OF_FEMALES_IN_NOMADS;
    private final int NUMBER_OF_FOLDS = 10;
    private final int PERCENT_OF_WEAKEST_MALES_TO_BECOME_NOMAD = 10;
    private  int TIME_TO_OPTIMIZATION = 10; /* not used yet */
    private float[][] PARAMETERS_RANGES;
    private String NAME_OF_CLASSIFIER;

    public ParameterFile(String nameOfClassifier)
    {
        this.NAME_OF_CLASSIFIER = nameOfClassifier;

        switch(nameOfClassifier)
		{
			case "J48":
				/* Particle ranges for J48*/
				PARAMETERS_RANGES = new float[2][2];
				PARAMETERS_RANGES[0][0] = 1.0f;
				PARAMETERS_RANGES[0][1] = 50.0f;
				PARAMETERS_RANGES[1][0] = 0.001f;
				PARAMETERS_RANGES[1][1] = 0.50f;
					
				/* time of optimization for J48 */
				TIME_TO_OPTIMIZATION = 5;
					
                break;
            
            case "JRip":
				/* Particle ranges for JRip */
				PARAMETERS_RANGES = new float[3][2];
				PARAMETERS_RANGES[0][0] = 2.0f;
				PARAMETERS_RANGES[0][1] = 50.0f;
				PARAMETERS_RANGES[1][0] = 1.0f;
				PARAMETERS_RANGES[1][1] = 50.0f;
				PARAMETERS_RANGES[2][0] = 1.0f;
				PARAMETERS_RANGES[2][1] = 50.0f;
					
				/* time of optimization for JRip */
				TIME_TO_OPTIMIZATION = 5;
					
                break;
            
            case "MLP":
				/* Particle ranges for MLP */
				PARAMETERS_RANGES = new float[4][2];
				PARAMETERS_RANGES[0][0] = 0.0f;
				PARAMETERS_RANGES[0][1] = 1.0f;
				PARAMETERS_RANGES[1][0] = 0.0f;
				PARAMETERS_RANGES[1][1] = 1.0f;
				PARAMETERS_RANGES[2][0] = 10.0f;
				PARAMETERS_RANGES[2][1] = 10000.0f;
				PARAMETERS_RANGES[3][0] = 0.0f;
				PARAMETERS_RANGES[3][1] = 99.0f;
					
				/* time of optimization for J48 */
				TIME_TO_OPTIMIZATION = 5;
					
                break;

            case "RT":
				/* Particle ranges for RT*/
				PARAMETERS_RANGES = new float[4][2];
				PARAMETERS_RANGES[0][0] = 0.0f;
				PARAMETERS_RANGES[0][1] = 50.0f;
				PARAMETERS_RANGES[1][0] = 0.0f;
				PARAMETERS_RANGES[1][1] = 50.0f;
				PARAMETERS_RANGES[2][0] = 0.0f;
				PARAMETERS_RANGES[2][1] = 50.0f;
				PARAMETERS_RANGES[3][0] = 2.0f;
				PARAMETERS_RANGES[3][1] = 50.0f;
					
				/* time of optimization for J48 */
				TIME_TO_OPTIMIZATION = 5;
					
                break;

            case "SL":
				/* Particle ranges for SL*/
				PARAMETERS_RANGES = new float[3][2];
				PARAMETERS_RANGES[0][0] = 0.0f;
				PARAMETERS_RANGES[0][1] = 10000.0f;
				PARAMETERS_RANGES[1][0] = 0.0f;
				PARAMETERS_RANGES[1][1] = 10000.0f;
				PARAMETERS_RANGES[2][0] = 0.0f;
				PARAMETERS_RANGES[2][1] = 5.0f;
					
				/* time of optimization for J48 */
				TIME_TO_OPTIMIZATION = 5;
					
                break;
            default:
            break;
        }
    }

    public int PERCENT_TO_BE_NOMAD()
    {
        return PERCENT_TO_BE_NOMAD;
    }

    public int NUMBER_OF_PRIDES()
    {
        return NUMBER_OF_PRIDES;
    }

    public int PERCENT_OF_MALES_IN_PRIDE()
    {
        return PERCENT_OF_MALES_IN_PRIDE;
    }

    public int PERCENT_OF_FEMALES_IN_NOMADS()
    {
        return PERCENT_OF_FEMALES_IN_NOMADS;
    }

    public String NAME_OF_CLASSIFIER()
    {
        return NAME_OF_CLASSIFIER;
    }

    public int NUMBER_OF_FOLDS()
    {
        return NUMBER_OF_FOLDS;
    }

    public int NUMBER_OF_GROUPS()
    {
        return NUMBER_OF_GROUPS;
    }

    public float[][] PARAMETERS_RANGES()
    {
        return this.PARAMETERS_RANGES;
    }

    public int PERCENT_OF_WEAKEST_MALES_TO_BECOME_NOMAD()
    {
        return PERCENT_OF_WEAKEST_MALES_TO_BECOME_NOMAD;
    }

    public int PERCENT_OF_FEMALES_IN_PRIDE()
    {
        return this.PERCENT_OF_FEMALES_IN_PRIDE;
    }

    public int FEMALE_IMMIGRATE_RATE()
    {
        return FEMALE_IMMIGRATE_RATE;
    }

    public int PERCENT_OF_MALES_IN_NOMADS()
    {
        return PERCENT_OF_MALES_IN_NOMADS;
    }
}