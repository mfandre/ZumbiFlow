package weka.clusterers;

import Base.Point;
import Base.Solution;
import MetaHeuristicas.ZumbiFlow;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;

/**
 * Created by fandre on 10/10/14.
 */
public class ZombieFlow  extends RandomizableClusterer {
    private static final long serialVersionUID = 1L;

    //# of clusters
    protected int clusters = 3;
    //# of humans
    protected int startHumans = 5;
    //# of zombies
    protected int startZombies = 1;
    //minimum probability of infection
    protected float successAttackChange = 0.3f;
    //chance to create a zombie that seeks humans
    protected float dumbZombie = 0.8f;
    //Tamanho de Janela usada pra determinar se uma pessoa pode receber um ataque de um zumbi proximo
    protected int neighborWindow = 1;
    //Tempo para os humanos vagarem pelo solucao sem o holocausto zumbi
    protected int accomodationTime = 20;

    //////////////////////////////           TipText           //////////////////////////////
    public String clusterTipText() { return "Number of clusters"; }
    public String startHumansTipText() { return "Starting human (population size)"; }
    public String startZombiesTipText() { return "Starting number of zombies (population size)"; }
    public String successAttackChangeTipText() { return "Zombie attack infection rate"; }
    public String dumbZombieTipText() { return "Zombie walking probability"; }
    public String neighborWindowTipText() { return "Window size used to determine whether a person can receive an attack from a zombie"; }
    public String accomodationTimeTipText() { return "Time to humans to walk solution without the zombie holocaust"; }

    //////////////////////////////           Getters / Setters           //////////////////////////////
    public int getClusters() { return clusters; }

    public void setClusters(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("Number of clusters must be > 0");
        }
        clusters = n;
    }


    public int getStartHumans() { return startHumans; }

    public void setStartHumans(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("Starting number of humans must be > 0");
        }
        startHumans = n;
    }


    public int getStartZombies() { return startZombies; }

    public void setStartZombies(int n) throws Exception {
        if (n < 1) {
            throw new Exception("Starting number of zombies must be >= 1");
        }
        startZombies = n;
    }


    public float getSuccessAttackChange() { return successAttackChange; }

    public void setSuccessAttackChange (float n) throws Exception {
        if (n <= 0 || n > 1) {
            throw new Exception("Infection rate minimum must be > 0 and <= 1");
        }
        successAttackChange = n;
    }


    public float getDumbZombie() { return dumbZombie; }

    public void setDumbZombie (float n) throws Exception {
        if (n < 0) {
            throw new Exception("Human fear of Zombies minimum must be >= 0");
        }
        dumbZombie = n;
    }


    public void setNeighborWindow (int n) throws Exception {
        if (n < 1) {
            throw new Exception("neighborWindow >= 1");
        }
        neighborWindow = n;
    }

    public int getNeighborWindow() { return neighborWindow; }


    public void setAccomodationTime (int n) throws Exception {
        if (n < 1) {
            throw new Exception("accomodationTime >= 1");
        }
        accomodationTime = n;
    }

    public int getAccomodationTime() { return accomodationTime; }
    ///////////////////////////////////////////////////////////////////////////

    //run in which datasets ?
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();
        result.enable(Capabilities.Capability.NO_CLASS); //can run without label data

        // attributes
        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES); //numerical attributes
        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES); //nominal attributes

        return result;
    }

    /**
     * Gets the current settings of ZombieCluster
     *
     * @return an array of strings suitable for passing to setOptions()
     */
    @Override
    public String[] getOptions() {
        int i;
        ArrayList<String> result;
        String[] options;

        result = new ArrayList<String>();

        result.add("-N");
        result.add("" + this.clusters);

        result.add("-SH");
        result.add(""+ this.startHumans);

        result.add("-SZ");
        result.add(""+ this.startZombies);

        result.add("-SAC");
        result.add(""+ this.successAttackChange);

        result.add("-DUMB");
        result.add(""+ this.dumbZombie);

        result.add("-WINDOW");
        result.add(""+ this.neighborWindow);

        result.add("-PEACE");
        result.add(""+ this.accomodationTime);

        options = super.getOptions();
        for (i = 0; i < options.length; i++) {
            result.add(options[i]);
        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    public void setOptions(String[] options) throws Exception {

        String temp = Utils.getOption("N", options);
        if (temp.length() > 0) {
            setClusters(Integer.parseInt(temp));
        }

        temp = Utils.getOption("SH", options);
        if (temp.length() > 0) {
            setStartHumans(Integer.parseInt(temp));
        }

        temp = Utils.getOption("SZ", options);
        if (temp.length() > 0) {
            setStartZombies(Integer.parseInt(temp));
        }

        temp = Utils.getOption("SAC", options);
        if (temp.length() > 0) {
            setSuccessAttackChange(Float.parseFloat(temp));
        }

        temp = Utils.getOption("DUMB", options);
        if (temp.length() > 0) {
            setDumbZombie(Float.parseFloat(temp));
        }

        temp = Utils.getOption("WINDOW", options);
        if (temp.length() > 0) {
            setNeighborWindow(Integer.parseInt(temp));
        }

        temp = Utils.getOption("PEACE", options);
        if (temp.length() > 0) {
            setAccomodationTime(Integer.parseInt(temp));
        }

    }

    public int numberOfClusters() throws Exception { return this.clusters; }

    /**
     * Generates a clusterer. Has to initialize all fields of the clusterer that
     * are not being set via options.
     *
     * @param data set of instances serving as training data
     * @exception Exception if the clusterer has not been generated successfully
     */

    private Solution solution = null;

    public void buildClusterer(Instances data) throws Exception {
        Base.Instance zumbiInstance = new Base.Instance(data);
        ZumbiFlow zf = new ZumbiFlow(this.clusters,
                this.accomodationTime,
                this.startHumans,
                this.startZombies,
                this.successAttackChange,
                this.neighborWindow,
                this.dumbZombie,
                zumbiInstance);
        solution = zf.execute();
    }

    /**
     * Classifies a given instance. Either this or distributionForInstance() needs
     * to be implemented by subclasses.
     *
     * @param instance the instance to be assigned to a cluster
     * @return the number of the assigned cluster as an integer
     * @exception Exception if instance could not be clustered successfully
     */
    public int clusterInstance(Instance instance) throws Exception {
        Point centroids[] = new Point[solution.getNumGroups()];

        // dimenção dos pontos
        int d = instance.numAttributes();

        //alocar os pontos dos centroids
        for(int i=0;i<solution.getNumGroups();i++)
            centroids[i] = new Point(d);

        // calcular os centroids
        for (int i = 0; i < solution.getNumGroups(); i++) { // grupos
            for (int j = 0; j < solution.groupSize(i); j++) //elementos
                centroids[i].add(solution.getData(i, j)); //soma as coords
            for (int j = 0; j < d; j++)
                centroids[i].setCoord(j,centroids[i].getCoord()[j] / solution.groupSize(i)); //divide cada coord pelo numero de elementos do grupo
        }


        Point toFind = new Point(instance.numAttributes());
        for(int i = 0; i < instance.numAttributes(); i++)
            toFind.setCoord(i,instance.value(i));

        double minimunDistance = Integer.MAX_VALUE;
        int group = -1;
        for (int i = 0; i < solution.getNumGroups(); i++) { //grupos
            for (int j = 0; j < solution.groupSize(i); j++) { //elementos
                double distance = centroids[i].distance(toFind);
                if (distance < minimunDistance) { //distancia quadratica do elemento para o centroid do grupo
                    minimunDistance = distance;
                    group = i;
                }
            }
        }

        return group;
    }


}