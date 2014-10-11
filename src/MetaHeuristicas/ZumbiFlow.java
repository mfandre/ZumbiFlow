package MetaHeuristicas;

import Base.Instance;
import Base.MetaHeuristica;
import Base.Point;
import Base.Solution;
import Utils.Tuple;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by fandre on 08/10/14.
 */
public class ZumbiFlow implements MetaHeuristica {
    private Random rnd = new Random(System.nanoTime());

    /**
     * Numero de clusters que serao criados
     */
    private int k = 20;

    /**
     * Tempo inicial para os humanos caminharem por algumas solucoes
     */
    private int accomodationTime = 5;

    /**
     * Numero de pesosas (populacao inicial)
     */
    private int numberOfPeoples = 5;

    /**
     * Numero de zombies (populacao de zombies inicial)
     */
    private int numberOfZombies = 1;

    /**
     * Probabilidade do ataque ser com sucesso
     */
    private double successAttackChange = 0.3;

    /**
     * Tamanho de Janela usada pra determinar se uma pessoa pode receber um ataque de um zumbi proximo
     */
    private int neighborWindow = 10;

    /**
     * Burrice do zombie, quando a pessoa eh infectada o zombie resultante tem a chance de ficar parado (dumbZombie) ou buscar novos humanos
     */
    private double dumbZombie = 0.3;

    /**
     * Melhor solucao encontrada
     */
    private Solution betterThenAll = null;

    private Instance inst;

    private long elapsedTime;

    public ZumbiFlow(int k, int accomodationTime, int numberOfPeoples, int numberOfZombies, double successAttackChange, int neighborWindow, double dumbZombie, Instance instance){
        this.k = k;
        this.accomodationTime = accomodationTime;
        this.numberOfPeoples = numberOfPeoples;
        this.numberOfZombies = numberOfZombies;
        this.successAttackChange = successAttackChange;
        this.neighborWindow = neighborWindow;
        this.dumbZombie = dumbZombie;
        this.inst = instance;
    }

    @Override
    public Solution execute() {
        long startTime = System.currentTimeMillis();

        System.out.println("--- ZumbiFlow ---");

        ArrayList<Tuple<Solution, Double>> peoples = createPeoples();
        ArrayList<Solution> zombies = createZombies();

        System.out.println("Qualidade inicial:" + betterThenAll.getMetric());

        //Acomodando os humanos
        PeoplesAccommodation(peoples);

//        //Um humano vai virar zumbi (randomizacao para criar o zumbi)
//        int peoplesRandomizedToTurnZombie = rnd.nextInt(numberOfPeoples);
//        zombies.add(peoples.get(peoplesRandomizedToTurnZombie).x);
//
//        //removendo o humano que virou zumbi da populacao humana
//        peoples.remove(peoplesRandomizedToTurnZombie);

        int count = 0;
        while (!peoples.isEmpty()) {
            movePeoples(peoples, zombies);
            moveZombies(zombies, peoples);

            /*condicao para ir aumentado a janela de ataque pois quando o zumbi fica muito proximo do humano
             ficadificil igualar as duas solucoes (ataque) e com isso o zumbi fica sempre preso perto do humano e nunca chega a distancia minima
             para o ataque*/
            if(count %60000 == 0 && count != 0){
                neighborWindow++;
            }
            count++;
        }

        System.out.println("Qualidade final:" + betterThenAll.getMetric());
        System.out.println("Zombies dominated the world!!! " + count);

        long stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;

        return betterThenAll;
    }

    @Override
    public double elapsedTime() {
        return (double)elapsedTime/1000;
    }

    private ArrayList<Tuple<Solution, Double>> createPeoples() {
        ArrayList<Tuple<Solution, Double>> peoples = new ArrayList<Tuple<Solution, Double>>();

        //Instance inst = new Instance("/Users/fandre/IdeaProjects/ZumbiFlow/src/sample.csv");

        for (int i = 0; i < numberOfPeoples; i++) {
            Solution people = new Solution(k);
            people.rndSolution(inst);

            double fear = rnd.nextDouble() * 1000;
            peoples.add(new Tuple(people, fear));
        }

        betterThenAll = peoples.get(0).x.clone();

        return peoples;
    }

    private ArrayList<Solution> createZombies(){
        ArrayList<Solution> zombies = new ArrayList<Solution>();

        for (int i = 0; i < numberOfZombies; i++) {
            Solution zombie = new Solution(k);
            zombie.rndSolution(inst);

            zombies.add(zombie);
        }

        return zombies;
    }

    private void PeoplesAccommodation(ArrayList<Tuple<Solution, Double>> peoples) {
        for (Tuple<Solution, Double> people : peoples) {
            Solution best = people.x.clone();
            for (int i = 0; i < accomodationTime; i++) {
                people.x.swap();
                if (people.x.getMetric() < best.getMetric()) {
                    best = people.x.clone();
                    i = 0;
                } else {
                    people.x = best.clone();
                }
            }
        }
    }

    private int distanceBetweenPeopleToZombie(Solution people, Solution zombie) {
        int distance = 0;
        for (int i = 0; i < people.getNumGroups(); i++) {
            for (int j = 0; j < people.groupSize(i); j++) {
                Point peopleToCompare = people.getData(i, j);

                if (!zombie.containsAtGroup(i, peopleToCompare)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    private void movePeoples(ArrayList<Tuple<Solution, Double>> peoples, ArrayList<Solution> zombies) {
        for (Tuple<Solution, Double> people : peoples) {
            Solution lastPosition = people.x.clone();
            people.x.swap();

            if (betterThenAll.getMetric() > people.x.getMetric()) {
                betterThenAll = people.x.clone();
            }

            int nearestZombies = checkZombiesNear(people.x, zombies);
            if (nearestZombies > 0)
                people.x.setMetric(people.x.getMetric() + people.y * nearestZombies); //penalizo a solucao quando tem zombies mais proximos

            if (lastPosition.getMetric() < people.x.getMetric())
                people.x = lastPosition;

        }
    }

    private void moveZombies(ArrayList<Solution> zombies, ArrayList<Tuple<Solution, Double>> peoples) {
        for (int i = 0; i < zombies.size(); i++) {
            Solution zombie = zombies.get(i);

            //pega a pessoa mais proxima do zombie para move-lo na direcao dela
            Tuple<Solution, Double> people = checkPeopleNear(zombie, peoples);

            if (peoples.size() == 0) { //quando a populacao de humanos acaba os zoombies ficam parados (termino do movimento)
                return;
            } else if (peoples == null) {
                return;
            } else if (distanceBetweenPeopleToZombie(people.x, zombie) <= 1*neighborWindow) { //se a distancia for == 0 ATACA
                //atack chance
                if (rnd.nextDouble() <= successAttackChange) {
                    //attack => people vira zombie e removo da lista dos humanos

                    //se o zombie for burro ele ficará parado para sempre
                    //if(rnd.nextDouble() <= dumbZombie)
                    //todo
                    Solution transformedPeople = people.x.clone();
                    transformedPeople.swap();
                    transformedPeople.swap();
                    transformedPeople.swap();
                    zombies.add(transformedPeople);
                    peoples.remove(people);
                }
                else
                {
                    //a pessoa corre pra longe do zumbi
                    people.x.swap();
                    people.x.swap();
                    people.x.swap();
                }
            } else {
                //movendo o zombie na direcao de um humano
                //for(k = 0; k < 10; k++) {
                    int peopleGroup = rnd.nextInt(people.x.getNumGroups());
                    int peopleGroupIndex1 = rnd.nextInt(people.x.groupSize(peopleGroup));

                    // pego um parte da solucao dos humanos e injeto no zumbi, aproximando o zumbi do humano
                    Tuple<Integer, Integer> positionInZombie = zombie.findData(people.x.getData(peopleGroup, peopleGroupIndex1));
                    zombie.rmData(positionInZombie.x, positionInZombie.y);
                    zombie.addData(peopleGroup, people.x.clone().getData(peopleGroup, peopleGroupIndex1));
                //}
//                int range = people.x.groupSize(peopleGroup) - peopleGroupIndex1 + 1;
//                int peopleGroupIndex2 =  rnd.nextInt(range) + peopleGroupIndex1;
//                int peopleGroupIndex1 = 0;
//                int peopleGroupIndex2 = people.x.groupSize(peopleGroup);

//                for(int w = peopleGroupIndex1; w < peopleGroupIndex2; w++) {
//                    // pego um parte da solucao dos humanos e injeto no zumbi, aproximando o zumbi do humano
//                    Tuple<Integer, Integer> positionInZombie = zombie.findData(people.x.getData(peopleGroup, w));
//                    zombie.rmData(positionInZombie.x, positionInZombie.y);
//                    zombie.addData(peopleGroup, people.x.clone().getData(peopleGroup, w));
//                }
            }
        }
    }

    /**
     * @param people
     * @param zombies
     * @return retorna a quantidade de zombies que estão a uma vizinhanca proximidade do humano
     */
    private int checkZombiesNear(Solution people, ArrayList<Solution> zombies) {
        int count = 0;

        for (Solution zombie : zombies) {
            if (distanceBetweenPeopleToZombie(people, zombie) > 2*neighborWindow)
                continue;
            else
                count++;
        }

        return count;
    }

    /**
     * @param zombie
     * @param peoples
     * @return retorna a pessoa mais perto do zombie
     */
    private Tuple<Solution, Double> checkPeopleNear(Solution zombie, ArrayList<Tuple<Solution, Double>> peoples) {
        Tuple<Solution, Double> peopleNear = null;
        int mininumDistance = 999999999;
        for (Tuple<Solution, Double> people : peoples) {
            int distance = distanceBetweenPeopleToZombie(people.x, zombie);
            if (distance < mininumDistance) {
                peopleNear = people;
                mininumDistance = distance;
            }
        }

        return peopleNear;
    }
}
