package Base;

import Utils.Tuple;

import java.util.ArrayList;
import java.util.Random;

public class Solution implements ISolution<Point>,java.io.Serializable {
    protected double metric;
    protected ArrayList<ArrayList<Point>> groups = new ArrayList<ArrayList<Point>>();
    protected Random rnd = new Random(System.nanoTime());

    public Solution(int k) {
        for (int i = 0; i < k; i++)
            groups.add(new ArrayList<Point>());
    }

    public int getNumGroups() {
        return this.groups.size();
    }

    //retorna o numero de elementos do grupo
    public int groupSize(int group) {
        return this.groups.get(group).size();
    }

    //retorna o elemento da posicao "index" do grupo "group"
    public Point getData(int group, int index) {
        return this.groups.get(group).get(index);
    }

    //remove elemento do grupo
    public void rmData(int group, int index) {
        this.groups.get(group).remove(index);
    }

    //adiciona o elemento "data" ao grupo "group"
    public void addData(int group, Point data) {
        this.groups.get(group).add(data);
    }

    //criar método para gerar a solucao inicial (aleatoria)
    public void rndSolution(Instance inst) {
        for (int i = 0; i < getNumGroups(); i++) //zera os grupos
            this.groups.get(i).clear();

        for (int i = 0; i < inst.getData().length; i++) //sorteia um grupo para cada ponto...
            addData(this.rnd.nextInt(groups.size()), inst.getData()[i]);

        this.metric = Metrics.MSSC(this); //calcula a qualidade da solucao
    }

    public boolean containsAtGroup(int group, Point p) {
        ArrayList<Point> points = groups.get(group);

        for (Point point : points) {
            if (p.equals(point))
                return true;
        }
        return false;
    }

    public Tuple<Integer, Integer> findData(Point p) {
        for (int i = 0; i < groups.size(); i++) {
            ArrayList<Point> points = groups.get(i);
            for (int j = 0; j < points.size(); j++) {
                Point pToCompare = points.get(j);
                if (pToCompare.equals(p)) {
                    return new Tuple<Integer, Integer>(i, j);
                }
            }
        }

        return new Tuple<Integer, Integer>(-1, -1);
    }

    //funcao de vizinhanca
    public void swap() {
        int group;
        do {
            group = this.rnd.nextInt(groups.size()); //sorteia 0 até grupos
        } while (groupSize(group) <= 2);

        int s = this.rnd.nextInt(groupSize(group)); //sorteia 0 até elementos no grupo sorteado
        addData(this.rnd.nextInt(groups.size()), getData(group, s));
        rmData(group, s);
        this.metric = Metrics.MSSC(this);
    }

    /**
     * Teste
     *
     * @return
     */
    public double getMetric() {
        return metric;
    }

    public void setMetric(double metric) {
        this.metric = metric;
    }

    public Solution clone() {
        Solution s = new Solution(this.getNumGroups());
        s.metric = this.metric;
        for (int i = 0; i < this.getNumGroups(); i++)
            s.groups.get(i).addAll(this.groups.get(i));
        return s;
    }

}
