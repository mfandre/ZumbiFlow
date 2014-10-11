import Base.Instance;
import MetaHeuristicas.LocalSearch;
import MetaHeuristicas.MultiStart;
import MetaHeuristicas.SA;
import Base.Solution;
import MetaHeuristicas.ZumbiFlow;
import Utils.Tuple;

import java.util.*;

public class Main {

    public static void mainRelatorio(String[] args) {
        Instance inst = new Instance("/Users/fandre/IdeaProjects/ZumbiFlow/src/sample.csv");

        ArrayList<Tuple<Solution, Double>> execucoesLocalSearch = new ArrayList<Tuple<Solution, Double>>();
        ArrayList<Tuple<Solution, Double>> execucoesMultiStart = new ArrayList<Tuple<Solution, Double>>();
        ArrayList<Tuple<Solution, Double>> execucoesSA = new ArrayList<Tuple<Solution, Double>>();
        ArrayList<Tuple<Solution, Double>> execucoesZumbiFlow = new ArrayList<Tuple<Solution, Double>>();

        for(int i = 0; i < 10; i++) {
            LocalSearch ls = new LocalSearch();
            Solution bestLS = ls.execute();
            System.out.println("Tempo de execucao (s):" + ls.elapsedTime());
            execucoesLocalSearch.add(new Tuple<Solution, Double>(bestLS,ls.elapsedTime()));

            MultiStart ms = new MultiStart();
            Solution bestMS = ms.execute();
            System.out.println("Tempo de execucao (s):" + ms.elapsedTime());
            execucoesMultiStart.add(new Tuple<Solution, Double>(bestMS,ms.elapsedTime()));

            SA sa = new SA();
            Solution bestSA = sa.execute();
            System.out.println("Tempo de execucao (s):" + sa.elapsedTime());
            execucoesSA.add(new Tuple<Solution, Double>(bestSA,sa.elapsedTime()));

            ZumbiFlow zf = new ZumbiFlow(2,20,20,5,0.3,1,0.8,inst);
            Solution bestZF = zf.execute();
            System.out.println("Tempo de execucao:" + zf.elapsedTime());
            execucoesZumbiFlow.add(new Tuple<Solution, Double>(bestZF,zf.elapsedTime()));
        }

        System.out.println("\n --- Relatorio ---");
        System.out.println("Tempo medio de execucao LocalSearch:\t"   + averageExecutionTime(execucoesLocalSearch));
        System.out.println("Solucao media LocalSearch:\t\t\t"   + averageSolution(execucoesLocalSearch));
        System.out.println("Mediana LocalSearch:\t\t\t"   + medianSolution(execucoesLocalSearch));
        System.out.println("Moda LocalSearch:\t\t\t"   + modeSolution(execucoesLocalSearch));
        System.out.println("Melhor solucao LocalSearch:\t\t\t"   + bestSolution(execucoesLocalSearch));

        System.out.println();

        System.out.println("Tempo medio de execucao MultiStart:\t"    + averageExecutionTime(execucoesMultiStart));
        System.out.println("Solucao media MultiStart:\t\t\t"   + averageSolution(execucoesMultiStart));
        System.out.println("Mediana MultiStart:\t\t\t"   + medianSolution(execucoesMultiStart));
        System.out.println("Moda MultiStart:\t\t\t"   + modeSolution(execucoesMultiStart));
        System.out.println("Melhor solucao MultiStart:\t\t\t"   + bestSolution(execucoesMultiStart));

        System.out.println();

        System.out.println("Tempo medio de execucao SA:\t"            + averageExecutionTime(execucoesSA));
        System.out.println("Solucao media SA:\t\t\t"   + averageSolution(execucoesSA));
        System.out.println("Mediana SA:\t\t\t"   + medianSolution(execucoesSA));
        System.out.println("Moda SA:\t\t\t"   + modeSolution(execucoesSA));
        System.out.println("Melhor solucao SA:\t\t\t"   + bestSolution(execucoesSA));

        System.out.println();

        System.out.println("Tempo medio de execucao ZumbiFlow:\t"     + averageExecutionTime(execucoesZumbiFlow));
        System.out.println("Solucao media ZumbiFlow:\t\t\t"   + averageSolution(execucoesZumbiFlow));
        System.out.println("Mediana ZumbiFlow:\t\t\t"   + medianSolution(execucoesZumbiFlow));
        System.out.println("Moda ZumbiFlow:\t\t\t"   + modeSolution(execucoesZumbiFlow));
        System.out.println("Melhor solucao ZumbiFlow:\t\t\t"   + bestSolution(execucoesZumbiFlow));

    }

    public static void main(String[] args){
        Instance inst = new Instance("/Users/fandre/IdeaProjects/ZumbiFlow/src/sample.csv");
        ZumbiFlow zf = new ZumbiFlow(20,20,20,5,0.3,10,0.8,inst);
        Solution bestZF = zf.execute();
        System.out.println("Tempo de execucao:" + zf.elapsedTime());
    }

    private static double averageExecutionTime(ArrayList<Tuple<Solution, Double>> execucoes){
        double sumTime = 0;
        for(Tuple<Solution, Double> execucao : execucoes){
            sumTime += execucao.y;
        }

        return sumTime/execucoes.size();
    }

    private static double averageSolution(ArrayList<Tuple<Solution, Double>> execucoes){
        double sumMetric = 0;
        for(Tuple<Solution, Double> execucao : execucoes){
            sumMetric += execucao.x.getMetric();
        }

        return sumMetric/execucoes.size();
    }

    private static double bestSolution(ArrayList<Tuple<Solution, Double>> execucoes){
        double best = 9999999;
        for(Tuple<Solution, Double> execucao : execucoes){
            if(execucao.x.getMetric() < best)
                best = execucao.x.getMetric();
        }

        return best;
    }

    private static double medianSolution(ArrayList<Tuple<Solution, Double>> execucoes){
        Collections.sort(execucoes, new Comparator<Tuple<Solution,Double>>() {
            @Override
            public int compare(Tuple<Solution, Double>  t1, Tuple<Solution, Double>  t2)
            {
                if(t1.x.getMetric() < t2.x.getMetric())
                    return 1;
                else if (t1.x.getMetric() == t2.x.getMetric())
                    return 0;
                else
                    return -1;
            }
        });

        return execucoes.get(execucoes.size()/2).x.getMetric();
    }

    private static double modeSolution(ArrayList<Tuple<Solution, Double>> execucoes){
        ArrayList<Tuple<Double, Integer>> occurrences = new ArrayList<Tuple<Double, Integer>>();

        for(Tuple<Solution, Double> execucao : execucoes){
            for(Tuple<Double, Integer> ocurrence : occurrences){
                if(ocurrence.x == execucao.x.getMetric()){
                    ocurrence.y++;
                }else{
                    occurrences.add(new Tuple(execucao.x.getMetric(),0));
                }
            }
        }

        double moreRepeatedValue = 0;
        int countRepeated = 0;
        for(Tuple<Double, Integer> ocurrence : occurrences){
            if(ocurrence.y > countRepeated) {
                countRepeated = ocurrence.y;
                moreRepeatedValue = ocurrence.x;
            }
        }

        return moreRepeatedValue;
    }
}
