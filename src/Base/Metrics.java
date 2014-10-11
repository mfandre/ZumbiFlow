package Base;

public class Metrics {

	// Minimum sum-square clustering
	public static double MSSC(Solution s) {
		double total = 0;
		Point centroids[] = new Point[s.getNumGroups()];
	
		// dimenção dos pontos
		int d = s.getData(0, 0).getCoord().length;

		//alocar os pontos dos centroids
		for(int i=0;i<s.getNumGroups();i++)
			centroids[i] = new Point(d);
		
		// calcular os centroids
		for (int i = 0; i < s.getNumGroups(); i++) { // grupos
			for (int j = 0; j < s.groupSize(i); j++) //elementos
				centroids[i].add(s.getData(i, j)); //soma as coords
			for (int j = 0; j < d; j++)
				centroids[i].setCoord(j,centroids[i].getCoord()[j] / s.groupSize(i)); //divide cada coord pelo numero de elementos do grupo
		}

		// somar as distancias
		for (int i = 0; i < s.getNumGroups(); i++) { //grupos
			for (int j = 0; j < s.groupSize(i); j++) //elementos
				total += centroids[i].distance(s.getData(i, j)); //distancia quadratica do elemento para o centroid do grupo
		}
		return total;
	}

    public static int HammingDistance(Solution s){

        return 0;
    }
}
