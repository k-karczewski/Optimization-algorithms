import java.util.Random;

public class Lion implements Comparable
{
    private float[] position;
    private float[] bestPosition;
    private double fitness;
    private double bestFitness = Double.NEGATIVE_INFINITY;
    private String gender;
    private boolean isNomad;
    private boolean isHunter;
    private boolean attacked;
    private int numberOfPride;
    private int successRate;

    public Lion(String nameOfClassifier, float[][] ranges)
    {
        this.isNomad = false;
        this.isHunter = false;
        this.attacked = false;
        this.successRate = 0;
        this.numberOfPride = -1; /* pride not initialized yet */
        this.gender = ""; /* gender not initialized yet */

        switch(nameOfClassifier)
		{ 
			case "J48":
                position = new float[2];
                bestPosition = new float[2];
                break;
            
            case "JRip":
                position = new float[3];
                bestPosition = new float[3];
                break;
            
            case "MLP":
                position = new float[4];
                bestPosition = new float[4];
                break;
            
            case "RT":
                position = new float[4];
                bestPosition = new float[4];
                break;
            
            case "SL":
                position = new float[3];
                bestPosition = new float[3];
                break;
				
			default:
				/* this will never happen*/
				break;
        }
        
        position = Lion.generateRandomPosition(nameOfClassifier, ranges);
    }
    
    /***********************************************************************************************
	 * Function: moveTowardRoamPoint
	 * Description: This function finds roaming place for male lion.
	 * @param destination
	 * @return New position of lion
	 ***********************************************************************************************/
    public float[] moveTowardRoamPoint(float[] destination)
    {
        float[] pointToMove = new float[destination.length];
        float[] currentPosition = this.getPosition();
        pointToMove = Helpers.calculatePointToImmigrate(this.getPosition(), destination);
        double theta = Helpers.getRandomAngle(30, -30);
        int[] parametersToDeviade = Helpers.findParametersToDeviade(pointToMove.length);
        
        float[] rotationCoords = new float[2];

        rotationCoords[0] = (float) (Math.cos(theta) * pointToMove[parametersToDeviade[0]] - Math.sin(theta) * pointToMove[parametersToDeviade[1]]);
        rotationCoords[1] = (float) (Math.sin(theta) * pointToMove[parametersToDeviade[0]] + Math.cos(theta) * pointToMove[parametersToDeviade[1]]);

        for(int i = 0; i < pointToMove.length; i++)
        {
            if(parametersToDeviade[0] == i)
            {
                if(this.getPosition()[i] > destination[i])
                {
                    currentPosition[i] -= rotationCoords[0];
                }
                else
                {
                    currentPosition[i] += rotationCoords[0];
                }
            }
            else if(parametersToDeviade[1] == i)
            {
                if(this.getPosition()[i] > destination[i])
                {
                    currentPosition[i] -= rotationCoords[1];
                }
                else
                {
                    currentPosition[i] += rotationCoords[1];
                }
            }
            else
            {
                if(this.getPosition()[i] > destination[i])
                {
                    currentPosition[i] -= pointToMove[i];
                }
                else
                {
                    currentPosition[i] += pointToMove[i];
                }
            }
        }

        return currentPosition;
    }

    /***********************************************************************************************
	 * Function: moveTowardSafePosition
	 * Description: This function finds correct place for lioness to move.
	 * @param safePlace
	 * @param nameOfClassifier
	 * @return New position of lioness
	 ***********************************************************************************************/
    public float[] moveTowardSafePosition(float[] safePlace, String nameOfClassifier)
    {
        /* Euclidean distance between current position of lioness and safe place */
        double distance = Helpers.euclideanDistance(this.getPosition(), safePlace);
        float[] finalPlace = new float[this.getPosition().length];

        if(distance > 0)
        {
            /* random deviation of movement */
            double theta = 0;
            /* angle between current position and safe place */
            double angleOfPoints = Helpers.calculateAngleOfPoints(this.getPosition(), safePlace);
            /* temporary place used to calculations */
            float[] tmpPlace = new float[safePlace.length];
            /* final new place of lioness */
            
            double height = 0;
            int[] parametersToDeviade = null;
            float[] vector = null;
            float[] deviationVector = null;
            float[] differences = null;
            /* copy values from safe place */
            for(int i = 0; i < tmpPlace.length; i++)
            {
                tmpPlace[i] = safePlace[i];
            }
            /* for each classifier calculations will be a bit different */
            if(nameOfClassifier.equals("J48"))
            {
                /* angle between 5 and -5 degrees ONLY FOR J48 */
                theta = Helpers.getRandomAngle(5, -5);
                /* length of deviation by theta angle */
                height = Math.tan(theta) * distance;
                /* add length to y coordinate of safe place */
                tmpPlace[1] += height;
                /* move safe place to (0, 0) coordinates to make possible rotation */
                vector = Helpers.moveVectorToOrigin(safePlace, tmpPlace);
                /* differences between original point and rotated one */
                differences = Helpers.rotatePointCounterClockwise(vector, angleOfPoints);
                /* use all variables in folmula for each coordinate */
                for(int i = 0; i < tmpPlace.length; i++)
                {
                    finalPlace[i] = (float) (this.getPosition()[i] + 
                                    (2 * Helpers.calculateDiff(this.getPosition(), safePlace)[i] * Math.random()) + 
                                    (Helpers.getRandomNumber(-1, 1) * (tmpPlace[i] + differences[i])));          
                }
            }
            else /* for JRip, MLP, RT and SL */
            {
                /* angle between 30 and -30 degrees */
                theta = Helpers.getRandomAngle(30, -30);
                /* length of deviation by theta angle */
                height = Math.tan(theta) * distance;
                /* get indexes of parameters that will be deviaded */
                parametersToDeviade = Helpers.findParametersToDeviade(safePlace.length);
                /* add length to second coordinate of safe place */
                tmpPlace[parametersToDeviade[1]] += height;
                vector = Helpers.moveVectorToOrigin(safePlace, tmpPlace);
                deviationVector = new float [parametersToDeviade.length];
                
                for(int i = 0; i < vector.length; i++)
                {
                    if(i == parametersToDeviade[0])
                    {
                        deviationVector[0] = vector[i];
                    }
                    if(i == parametersToDeviade[1])
                    {
                        deviationVector[1] = vector[i];
                    }
                }
                differences = Helpers.rotatePointCounterClockwise(deviationVector, angleOfPoints);
                /* use all variables in folmula for each coordinate */
                for(int i = 0; i < safePlace.length; i++)
                {
                    if(parametersToDeviade[0] == i)
                    {
                        finalPlace[i] = (float) (this.getPosition()[i] + 
                                        (2 * Helpers.calculateDiff(this.getPosition(), safePlace)[i] * Math.random()) + 
                                        (Helpers.getRandomNumber(-1, 1) * (tmpPlace[i] + differences[0])));    
                    }
                    else if(parametersToDeviade[1] == i)
                    {
                        finalPlace[i] = (float) (this.getPosition()[i] + 
                                        (2 * Helpers.calculateDiff(this.getPosition(), safePlace)[i] * Math.random()) + 
                                        (Helpers.getRandomNumber(-1, 1) * (tmpPlace[i] + differences[1])));    
                    }
                    else
                    {
                        if(this.getPosition()[i] > tmpPlace[i])
                        {
                            finalPlace[i] += Math.random() * (this.getPosition()[i] - tmpPlace[i]); 
                        }
                        else
                        {
                            finalPlace[i] += Math.random() * (tmpPlace[i] - this.getPosition()[i]); 
                        }
                    }     
                }
            }
            /* return new place */
            return finalPlace;
        }
        else
        {
            /* no movement for lion in the best place */
            return safePlace;
        }
    }

    public static void setRandomLionsAsNomad(Lion[] lions, int countOfNomads)
    {
        int numOfNomad = 0;
        while(numOfNomad < countOfNomads)
        {
            Random random = new Random();
            int randomIndex = random.nextInt(lions.length);
            
            if(lions[randomIndex].isNomad == false)
            {
                lions[randomIndex].isNomad = true;
                numOfNomad++;
            }
        }
    }

    public boolean isPositionInCorrectRange(float[][] parameters)
	{
		for(int i = 0; i < position.length; i++)
		{
			if(this.position[i] < parameters[i][0] || this.position[i] > parameters[i][1])
			{
				return false;
			}
		}
		return true;
    }
    
    public void repairLionPosition(float[][] cuckooRanges)
	{
		for(int i = 0; i < this.position.length; i++)
		{
			if(this.position[i] < cuckooRanges[i][0])
			{
				this.position[i] = cuckooRanges[i][0];
			}
			
			if(this.position[i] > cuckooRanges[i][1])
			{
				this.position[i] = cuckooRanges[i][1];
			}
		}
    }
    
    public static float[] generateRandomPosition(String nameOfClassifier, float[][] parametersRanges)
    {
        float[] randomPosition = null;
        Random random = new Random();

        switch(nameOfClassifier)
        {
            case "J48":
                randomPosition = new float[2];
        		/* set random value (in range 1 - 50) for learningRate */
				randomPosition[0] = parametersRanges[0][0] + random.nextFloat() * (parametersRanges[0][1] - parametersRanges[0][0]);
				randomPosition[0] = Math.round(randomPosition[0]);
				
				/* set random value (in range 0.001 - 0.49f) for momentum */
				randomPosition[1] = parametersRanges[1][0] + random.nextFloat() * (parametersRanges[1][1]- parametersRanges[1][0]);
                randomPosition[1] = Math.round(randomPosition[1]*1000.0f)/1000.0f;
                break;
            
            case "JRip":
                randomPosition = new float[3];				
				/* set random value (in range 2 - 50) for Folds */
				randomPosition[0] = parametersRanges[0][0] + random.nextFloat() * (parametersRanges[0][1] - parametersRanges[0][0]);
				randomPosition[0] = Math.round(randomPosition[0]);
				
				/* set random value (in range 1 - 50) for minNo */
				randomPosition[1] = parametersRanges[1][0] + random.nextFloat() * (parametersRanges[1][1] - parametersRanges[1][0]);
				randomPosition[1] = Math.round(randomPosition[1] * 100.0f) / 100.0f;
					
				/* set random value (in range 1 - 50) for Optimizations */
				randomPosition[2] = parametersRanges[2][0] + random.nextFloat() * (parametersRanges[2][1] - parametersRanges[2][0]);
				/* make sure that trainingTime is integer value*/
				randomPosition[2] = Math.round(randomPosition[2]);
				break;
				
			case "MLP":	
                randomPosition = new float[4];
				/* set random value (in range 0 - 1) for learningRate */
				randomPosition[0] = random.nextFloat();
				randomPosition[0] = Math.round(randomPosition[0]*100.0f)/100.0f;
				/* set random value (in range 0 - 1) for momentum */
				randomPosition[1] = random.nextFloat();
				randomPosition[1] = Math.round(randomPosition[1]*100.0f)/100.0f;
				/* set random value (in range 10 - 100000) for trainingTime */
				randomPosition[2] = parametersRanges[2][0] + random.nextFloat() * (parametersRanges[2][1] - parametersRanges[2][0]);
				/* make sure that trainingTime is integer value*/
				randomPosition[2] = Math.round(randomPosition[2]);
				/* set random value (in range 0 - 99) for validationSetSize */
				randomPosition[3] = random.nextFloat() * (parametersRanges[3][1]);
				/* make sure that validationSetSize is integer value*/
				randomPosition[3] = Math.round(randomPosition[3]);
				break;
				
			case "RT":
                randomPosition = new float[4];
				/* set random value (in range 0 - 50) for KValue */
				randomPosition[0] = random.nextFloat() * (parametersRanges[0][1]);
				randomPosition[0] = Math.round(randomPosition[0]);
				
				/* set random value (in range 0 - 50) for maxDepth */
				randomPosition[1] = random.nextFloat() * (parametersRanges[1][1]);
				randomPosition[1] = Math.round(randomPosition[1]);
					
				/* set random value (in range 0 - 50.0) for minNum */
				randomPosition[2] = random.nextFloat() * (parametersRanges[2][1]);
				/* make sure that trainingTime is integer value*/
				randomPosition[2] = Math.round(randomPosition[2] * 100.0f) / 100.0f;
				
				/* set random value (in range 2 - 50) for numFolds */
				randomPosition[3] = parametersRanges[3][0] + random.nextFloat() * (parametersRanges[3][1] - parametersRanges[3][0]);
				randomPosition[3] = Math.round(randomPosition[3]);
				break;
				
			case "SL":	
                randomPosition = new float[3];				
				/* set random value (in range 0 - 10 000) for maxBoostingIterations */
				randomPosition[0] = random.nextFloat() * parametersRanges[0][1];
				randomPosition[0] = Math.round(randomPosition[0]);
				
				/* set random value (in range 0 - 10 000) for numBoostingIterations */
				randomPosition[1] = random.nextFloat() * parametersRanges[1][1];
				randomPosition[1] = Math.round(randomPosition[1]);
					
				/* set random value (in range 0 - 5.0) for trainingTime */
				randomPosition[2] = random.nextFloat() * parametersRanges[2][1];
				/* make sure that trainingTime is integer value*/
				randomPosition[2] = Math.round(randomPosition[2] * 100.0f) / 100.0f;
                break;
                
            default:
                /* default case */
                break;
        }

        return randomPosition;
    }

    public void setIsNomad(boolean value)
    {
        this.isNomad = value;
    }

    public void setNumberOfPride(int value)
    {
        this.numberOfPride = value;
    }

    public void setGender(String value)
    {
        this.gender = value;
    }

    public void setFitness(double value)
    {
        this.fitness = value;
    }

    public void setBestFitness(double value)
    {
        this.bestFitness = value;
    }

    public void setPosition(float[] values)
    {
        for(int i = 0; i < values.length; i++)
        {
            this.position[i] = values[i];
        }
    }

    public void setBestPosition(float[] values)
    {
        for(int i = 0; i < values.length; i++)
        {
            this.bestPosition[i] = values[i];
        }
    }

    public void setHunter(boolean value)
    {
        this.isHunter = value;
    }

    public void setAttacked(boolean value)
    {
        this.attacked = value;
    }

    public void setSuccessRate(int value)
    {
        this.successRate = value;
    }

    public double getFitness()
    {
        return this.fitness;
    }

    public double getBestFitness()
    {
        return this.bestFitness;
    }

    public String getGender()
    {
        return this.gender;
    }

    public float[] getPosition()
    {
        return this.position;
    }

    public boolean getAttacked()
    {
        return this.attacked;
    }

    public boolean isHunter()
    {
        return this.isHunter;
    }

    public boolean isNomad()
    {
        return this.isNomad;
    }
    public float[] getBestPosition()
    {
        return this.bestPosition;
    }
    
    public int getSuccessRate()
    {
        return this.successRate;
    }

  @Override
  public int compareTo(Object lion) 
  {
      double fitness = ((Lion)lion).getFitness();
      
      return Double.valueOf(this.fitness).compareTo(fitness);
  }
}

