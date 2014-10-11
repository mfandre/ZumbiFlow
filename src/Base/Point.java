package Base;

public class Point implements java.io.Serializable{
	private double[] coord;
    private double overhead1;
    private double overhead2;

	public Point(int d) {
		this.coord = new double[d];
	}

	public double[] getCoord() {
		return coord;
	}

	public void setCoord(int index, double d) {
		this.coord[index] = d;
	}
	
	//somar dois pontos e atribuir no atributo
	public void add(Point p){
		if(this.coord.length==p.coord.length) {
			for(int i=0;i<this.coord.length;i++)
				coord[i]+=p.getCoord()[i];
		}
	}
	
	public double distance(Point p) {
		if(this.coord.length==p.coord.length) {
			int aux=0;
			for(int i=0;i<this.coord.length;i++)
				aux+=Math.pow(this.coord[i]-p.coord[i],2);
			return aux;
			//return Math.sqrt(aux); //sdds desacomplamento D:
		}
		return Double.MAX_VALUE;
	}

    public void setOverhead1(double val){
        this.overhead1 = val;
    }

    public void setOverhead2(double val){
        this.overhead2 = val;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Point))
            return false;

        for(int i = 0; i < coord.length; i++){

            if(((Point)o).coord[i] == coord[i])
                continue;
            else
                return false;
        }

        return true;
    }
}
