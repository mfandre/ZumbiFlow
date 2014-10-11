package MetaHeuristicas;

import Base.Instance;
import Base.MetaHeuristica;
import Base.Solution;

public class MultiStart extends LocalSearch {

    private Solution solve(Instance inst, Solution sol, int starts) {
        Solution best = sol.clone();
        for (int i = 0; i < starts; i++) {
            sol = super.solve(inst, sol);
            //System.out.println(sol.getMetric() + " (" + i + ")");
            if (sol.getMetric() < best.getMetric()) {
                best = sol.clone();
            }
            sol.rndSolution(inst);
        }
        return best;
    }

    @Override
    public Solution execute() {
        long startTime = System.currentTimeMillis();

        System.out.println("--- MultiStart ---");

        Instance inst = new Instance("/Users/fandre/IdeaProjects/ZumbiFlow/src/sample.csv");
        Solution sol = new Solution(20);
        sol.rndSolution(inst);
        System.out.println("Qualidade inicial:" + sol.getMetric());
        System.out.println(inst.getData().length + " points (d=" + inst.getData()[0].getCoord().length + ")");

        sol = this.solve(inst, sol, 5);
        System.out.println("Qualidade final:" + sol.getMetric());

        long stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;

        return sol;
    }

}
