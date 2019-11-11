package Mars;

public class ClusterGenerator 
{
	static Location[] generateClusters(int numOfClusters, int numOfLocations, int fieldWidth, int fieldDepth, double std)
	{
		boolean hasRock[][] = new boolean[fieldWidth][fieldDepth];
		for(int i=0;i<fieldWidth;i++)
			for(int j=0; j<fieldDepth; j++)
				hasRock[i][j]=false;
						
		Location locations[] = new Location[numOfLocations];
		Location clusters[] = new Location[numOfLocations];
		
		for(int i=0; i<numOfClusters; i++)
		{
			int x= ModelConstants.random.nextInt(fieldWidth);
			int y= ModelConstants.random.nextInt(fieldDepth);
			clusters[i] = new Location(x,y);
		}
		for(int i=0; i<numOfLocations; i++)
		{
			int c = ModelConstants.random.nextInt(numOfClusters);
			int x = clusters[c].getCol() + (int)(std*ModelConstants.random.nextGaussian());
			int y = clusters[c].getRow() + (int)(std*ModelConstants.random.nextGaussian());
			x = (x+10*fieldWidth) % fieldWidth;
			y = (y+10*fieldDepth) % fieldDepth;
			if(!hasRock[x][y]){
				locations[i] = new Location(x,y);
				hasRock[x][y] = true;
			}
			else
				i--;
		}
		return locations;
	}
}
