package Base;

/**
 * Created by fandre on 08/10/14.
 */
public interface MetaHeuristica {
    public Solution execute();

    /**
     * Tempo de execucao da metaheuristica
     * @return
     */
    public double elapsedTime();
}
