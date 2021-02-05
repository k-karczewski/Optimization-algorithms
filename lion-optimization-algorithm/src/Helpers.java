import java.util.ArrayList;
import java.util.Random;
import weka.core.Instances;

public class Helpers
{
    /***********************************************************************************************
	 * Function: euclideanDistance
	 * Description: Counts Euclidean Distance between two points.
	 * @param pointA
	 * @param pointB
	 * @return Returns Euclidean Distance
	 ***********************************************************************************************/
    public static double euclideanDistance(float[] pointA, float[] pointB)
    {
        double distance = 0;

        for(int i = 0; i < pointA.length; i++)
        {
            distance += Math.pow(pointA[i] - pointB[i], 2);
        }

        distance = Math.sqrt(distance);

        return distance;
    }

    /***********************************************************************************************
	 * Function: getRandomNumber
	 * Description: Returns randomly value of parameter1 or parameter2
	 * @param number1
	 * @param number2
	 * @return Value of parameter1 or parameter2
	 ***********************************************************************************************/
    public static int getRandomNumber(int number1, int number2)
    {
        Random random = new Random();

        if(random.nextBoolean())
        {
            return number1;
        }
        else
        {
            return number2;
        }
    }

    /***********************************************************************************************
	 * Function: getRandomAngle
	 * Description: Counts random value of angle between boundaries
	 * @param topBoundary
	 * @param downBoundary
	 * @return Value of angle in radians
	 ***********************************************************************************************/
    public static double getRandomAngle(int topBoundary, int downBoundary)
    {
        Random random = new Random();
        int degrees = downBoundary + random.nextInt(topBoundary - downBoundary);
        
        /* convert degrees to radians */
        double radians = Math.toRadians(degrees);
        
        return radians;
    }

    /***********************************************************************************************
	 * Function: calculateAngleOfPoints
	 * Description: This function counts the angle of deviation between two points
	 * @param origin
	 * @param destination
	 * @return Value of angle in radians
	 ***********************************************************************************************/
    public static double calculateAngleOfPoints(float[] origin, float[] destination)
    {
        float[] vector = moveVectorToOrigin(origin, destination);
        float[] tmpDest = vector;
        double tangent = Math.abs(tmpDest[1]) / Math.abs(tmpDest[0]);
        double angle = Math.atan(tangent);

        return angle;
    }

    /***********************************************************************************************
	 * Function: moveVectorToOrigin
	 * Description: This function moves point A to position (0,0) and point B according to Point A offset
	 * @param pointA
	 * @param pointB
	 * @return Position of PointB
	 ***********************************************************************************************/
    public static float[] moveVectorToOrigin(float[] pointA, float[] pointB)
    {
        float[] tmpOrigin = new float[pointA.length];
        float[] tmpDest = new float[pointA.length];

        for(int i = 0; i < pointA.length; i++)
        {
            tmpOrigin[i] = pointA[i];
            tmpDest[i] = pointB[i];
        }

        for(int i = 0; i < pointA.length; i++)
        {
            float value = pointA[i];

            if(value > 0)
            {
                tmpOrigin[i] = 0;
                tmpDest[i] -= value;
            }
            else if(value < 0)
            {
                tmpOrigin[i] = 0;
                tmpDest[i] += value;
            }
            else
            {
                /* no calculation needed */
            }
        }

        return tmpDest;
    }

    /***********************************************************************************************
	 * Function: rotatePointClockwise
	 * Description: This function rotates point in counter clockwise direction by the angle
	 * @param point
	 * @param angle
	 * @return Differences of coordinates of the original point and rotated point
	 ***********************************************************************************************/
    public static float[] rotatePointCounterClockwise(float[] point, double angle)
    {
        float[] differences = new float[point.length];
        /* x coordinate */
        differences[0] = (float)(Math.cos(angle) * point[0] - Math.sin(angle) * point[1]);
        /* y coordinate */
        differences[1] = (float)(Math.sin(angle) * point[0] + Math.cos(angle) * point[1]);

        return differences;
    }

    /***********************************************************************************************
	 * Function: calculateDiff
	 * Description: This function returns subtraction of each coordinate 
	 * @param pointA
	 * @param pointB
	 * @return Differences of coordinates of two points
	 ***********************************************************************************************/
    public static float[] calculateDiff(float[] pointA, float[] pointB)
    {
        float[] differences = new float[pointA.length];

        for(int i = 0; i < differences.length; i++)
        {
            if(pointA[i] > pointB[i])
            {
                differences[i] = pointA[i] - pointB[i];
            }
            else
            {
                differences[i] = pointB[i] - pointA[i];
            }
        }

        return differences;
    }

    /***********************************************************************************************
	 * Function: createIndexes
	 * Description: This function creates list of values from 0 to size-1 
	 * @param size
	 * @return List of values from 0 to size-1
	 ***********************************************************************************************/

    public static ArrayList<Integer> createIndexes(int size)
    {
        ArrayList<Integer> indexes = new ArrayList<Integer>();

        for(int i = 0; i < size; i++)
        {
            indexes.add(i);
        }

        return indexes;
    }

    public static int[] findParametersToDeviade(int numOfParameters)
	{
		Random random = new Random();
		int[] returnValues = new int [2];
		for(int i = 0; i < 2; i++)
		{
			returnValues[i] = random.nextInt(numOfParameters);
		}

		/* check if both values are different */
		boolean paramsTheSame = true;
		while(paramsTheSame)
		{
			if(returnValues[0] == returnValues[1])
			{
				returnValues[1] = random.nextInt(numOfParameters);
			}
			else
			{
				paramsTheSame = false;
			}
		}

		/* if elements not sorted ascending - sort them */
		if(returnValues[0] > returnValues[1])
		{
			int tmp = returnValues[0];
			returnValues[0] = returnValues[1];
			returnValues[1] = tmp;
		}

		return returnValues;
    }
    
    public static float[] calculatePointToImmigrate(float[] habitat, float[] bestHabitat) 
	{	
		float random = (float) (Math.random() * 100.0f / 100.0f);
		float[] newHabitat = new float[habitat.length];
		
		for(int i = 0; i < habitat.length; i++)
		{
			float diff;
			if(bestHabitat[i] > habitat[i])
			{
				diff = bestHabitat[i] - habitat[i];
			}
			else
			{
				diff = habitat[i] - bestHabitat[i];
			}

			diff *= random;
			newHabitat[i] = diff;
		}

		return newHabitat;
    }
    
    /* Creates a specified number of pride lions/nomads */
    public static void addNewLions(int numberOfLionsToAdd, String gender, ArrayList<Lion> destination, int prideNumber,
        String nameOfClassifier, float[][] parameters_Ranges, Instances[][] split, boolean isNomad) throws Exception
    {
        for(int i = 0; i < numberOfLionsToAdd; i++)
        {
            Lion newLion = new Lion(nameOfClassifier, parameters_Ranges);
            newLion.setFitness(Math.round(ClassifierClass.classify(newLion.getPosition(), split, nameOfClassifier)*100)/100);
            newLion.setBestFitness(newLion.getFitness());
            newLion.setGender(gender);
            destination.add(newLion);

            if(isNomad)
            {
                newLion.setIsNomad(true);
                newLion.setNumberOfPride(-1);
            }
            else
            {
                newLion.setIsNomad(false);
                newLion.setNumberOfPride(prideNumber);
            }
        }
    }
}