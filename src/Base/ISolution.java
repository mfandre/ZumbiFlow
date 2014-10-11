package Base;

import Base.Instance;
import Utils.Tuple;

/**
 * Created by fandre on 08/10/14.
 */
public interface ISolution<T> {

    public int getNumGroups();

    //retorna o numero de elementos do grupo
    public int groupSize(int group);

    //retorna o elemento da posicao "index" do grupo "group"
    public T getData(int group, int index);

    public void rndSolution(Instance inst);

    public boolean containsAtGroup(int group, T p);

    public Tuple<Integer, Integer> findData(T p);

    public void swap();

    public double getMetric();

    public void setMetric(double metric);
}
