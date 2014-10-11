package MetaHeuristicas;

import Base.Instance;
import Base.MetaHeuristica;
import Base.Solution;

public class SA implements MetaHeuristica{
    private long elapsedTime;

	public Solution solve(Instance inst, Solution sol){
		Solution best=sol.clone();
		Solution current=sol.clone();
		Solution last=sol.clone();
		
		double t=200;
		int it=0;
		while(t>0.1||it<1000){
			current.swap();
			if(current.getMetric()<last.getMetric()) {
				last=current.clone();
				if(current.getMetric()<best.getMetric()){
					best=current.clone();
					it=0;
				}
			} else {
				if(Math.random()<Math.exp( (last.getMetric()-sol.getMetric())/t))
					last=current.clone();
				else
					current=last.clone();
			}
			//System.out.println(best.getMetric()+" "+t);
			t*=0.99996;	
			it++;
		}
		return best;
	}

    @Override
    public Solution execute() {
        long startTime = System.currentTimeMillis();

        System.out.println("--- SA ---");

        Instance inst = new Instance("/Users/fandre/IdeaProjects/ZumbiFlow/src/sample.csv");
        Solution sol = new Solution(20);
        sol.rndSolution(inst);
        System.out.println("Qualidade inicial:" + sol.getMetric());
        System.out.println(inst.getData().length + " points (d=" + inst.getData()[0].getCoord().length + ")");

        sol = this.solve(inst, sol);
        System.out.println("Qualidade final:" + sol.getMetric());

        long stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;

        return sol;
    }

    @Override
    public double elapsedTime() {
        return (double)elapsedTime/1000;
    }

}
