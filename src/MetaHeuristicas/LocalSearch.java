package MetaHeuristicas;

import Base.Instance;
import Base.MetaHeuristica;
import Base.Solution;

public class LocalSearch implements MetaHeuristica {

    protected long elapsedTime;

    protected Solution solve(Instance inst, Solution sol) {
        Solution best = sol.clone();
        for (int i = 0; i < 1000; i++) {
            sol.swap();
            if (sol.getMetric() < best.getMetric()) {
                best = sol.clone();
                //System.out.println(sol.getMetric() +" ("+ i+")");
                i = 0;
            } else {
                sol = best.clone();
            }
        }
        return best;
    }

    @Override
    public Solution execute() {
        long startTime = System.currentTimeMillis();

        System.out.println("--- LocalSearch ---");

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