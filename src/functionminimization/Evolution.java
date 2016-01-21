package functionminimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author oguzcagiran
 */
public class Evolution {

    private static final Random random = new Random();

    private int minimumValue = 9999999;
    private int iterationNumber = 0;
    private Chromosome bestChromosome;
    
    private double crossoverRate;
    private double mutationRate;
    
    public Evolution(double crossoverRate, double mutationRate) {
        
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    public void start(int iteration) {

        List<Chromosome> chromosomeList = new ArrayList<>();
        List<Double> fitnessList = new ArrayList<>();
        List<Double> rouletteWheelTable = new ArrayList<>();
        List<Chromosome> selectedChromosomeList = new ArrayList<>();
        List<Double> crossoverRateList = new ArrayList<>();
        List<Chromosome> crossoverChromosomeList = new ArrayList<>();
        
        int sumIteration = 0;

        createPopulation(chromosomeList);
        
        for (int z = 0; z < 100; z++) {

            for (int i = 0; i < iteration; i++) {

                createFitnessList(chromosomeList, fitnessList);
                double sumOfFitness = calculateSumOfFitness(chromosomeList, fitnessList);
                createRouletteWheelTable(chromosomeList, rouletteWheelTable, fitnessList, sumOfFitness);
                selectFromPopulation(chromosomeList, selectedChromosomeList, rouletteWheelTable);
                createCrossoverRateList(crossoverRateList);
                createCrossoverChromosomeList(selectedChromosomeList, crossoverChromosomeList, crossoverRateList);
                makeCrossover(crossoverChromosomeList, selectedChromosomeList);
                makeMutation(selectedChromosomeList);

                crossoverChromosomeList.clear();
                crossoverRateList.clear();
                rouletteWheelTable.clear();
                fitnessList.clear();
                chromosomeList.clear();

                for (int j = 0; j < selectedChromosomeList.size(); j++) {

                    Chromosome c = selectedChromosomeList.get(j);
                    chromosomeList.add(c);
                    int result = c.calculateFunctionValue();

                    if (result < minimumValue) {
                        minimumValue = result;
                        List<String> chromosomeSolution = new ArrayList<>();

                        for (int k = 0; k < c.getChromosome().size(); k++) {
                            chromosomeSolution.add(c.getChromosome().get(k));
                        }

                        bestChromosome = new Chromosome(chromosomeSolution);
                        iterationNumber = i;
                        sumIteration += iterationNumber;
                    }

                }

                selectedChromosomeList.clear();
            }

            System.out.println("Solution = " + bestChromosome.getChromosome());
            System.out.println("Min Value = " + bestChromosome.calculateFunctionValue());
            System.out.println("Iteration found = " + iterationNumber);

            bestChromosome = null;
            iterationNumber = 0;
            minimumValue = 99999;
        }

        System.out.println("Average Iteration = " + sumIteration / 100);

    }

    /**
     * Makes mutation on genes
     */
    private void makeMutation(List<Chromosome> selectedChromosomeList) {

        double[] mutationRate = new double[48];

        for (int i = 0; i < 48; i++) {
            double rate = random.nextDouble();
            mutationRate[i] = rate;
        }

        for (int i = 0; i < 48; i++) {
            if (mutationRate[i] <= this.mutationRate) {

                int chromosomeNumber = i / 8;
                int genNumber = i % 8;

                List<String> chromosome = selectedChromosomeList.get(chromosomeNumber).getChromosome();

                String gen = chromosome.get(genNumber);

                if (gen.equals("1")) {
                    gen = "0";
                } else {
                    gen = "1";
                }

                chromosome.set(genNumber, gen);

            }
        }

    }

    /**
     * Makes crossover and add
     * new Chromosomes to population
     *
     */
    private void makeCrossover(List<Chromosome> crossoverChromosomeList,
                               List<Chromosome> selectedChromosomeList) {

        int size = crossoverChromosomeList.size();

        /**
         * If size is odd delete 
         * an element for crossover pair
         */
        if (size % 2 != 0) {
            crossoverChromosomeList.remove(0);
        }

        for (int i = 0; i < crossoverChromosomeList.size(); i = i + 2) {

            List<String> firstParent = crossoverChromosomeList.get(i).getChromosome();
            List<String> secondParent = crossoverChromosomeList.get(i + 1).getChromosome();
            List<String> firstnewChromosome = new ArrayList<>();
            List<String> secondnewChromosome = new ArrayList<>();

            int crossoverPoint = random.nextInt(7) + 1;

            for (int j = 0; j < crossoverPoint; j++) {
                firstnewChromosome.add(firstParent.get(j));
            }

            for (int j = crossoverPoint; j < secondParent.size(); j++) {
                firstnewChromosome.add(secondParent.get(j));
            }

            for (int j = 0; j < crossoverPoint; j++) {
                secondnewChromosome.add(secondParent.get(j));
            }

            for (int j = crossoverPoint; j < firstParent.size(); j++) {
                secondnewChromosome.add(firstParent.get(j));
            }

            selectedChromosomeList.remove(crossoverChromosomeList.get(i));
            selectedChromosomeList.remove(crossoverChromosomeList.get(i + 1));
            selectedChromosomeList.add(new Chromosome(firstnewChromosome));
            selectedChromosomeList.add(new Chromosome(secondnewChromosome));

        }
    }

    /**
     * Creates list which contains 
     * chromosomes for crossover
     */
    private void createCrossoverChromosomeList(List<Chromosome> selectedChromosomeList, 
                                               List<Chromosome> crossoverChromosomeList, 
                                               List<Double> crossoverRateList) {
        
        for (int j = 0; j < crossoverRateList.size(); j++) {
            if (crossoverRateList.get(j) <= crossoverRate) {
                Chromosome chromosome = selectedChromosomeList.get(j);
                crossoverChromosomeList.add(chromosome);
            }
        }
    }

    /**
     * Creates crossover rate 
     * of each chromosome
     */
    private void createCrossoverRateList(List<Double> crossoverRateList) {

        for (int j = 0; j < 6; j++) {
            double probability = random.nextDouble();
            crossoverRateList.add(probability);
        }

    }

    /**
     * Selects chromosome from 
     * population using roulette wheel
     */
    private void selectFromPopulation(List<Chromosome> chromosomeList, 
                                      List<Chromosome> selectedChromosomeList,
                                      List<Double> rouletteWheelTable) {

        for (int chromosome = 0; chromosome < 6; chromosome++) {

            double rand = random.nextDouble();

            for (int j = 0; j < 6; j++) {

                double probabilityLower = rouletteWheelTable.get(j);
                double probabilityUpper = rouletteWheelTable.get(j + 1);

                if (rand >= probabilityLower && rand < probabilityUpper) {
                    Chromosome chromosomeSelected = chromosomeList.get(j);
                    selectedChromosomeList.add(chromosomeSelected);
                }

            }
        }
    }

    /**
     * Calculates each chromosome roulette 
     * wheel probability and add them
     * probability list
     *
     * @param sumOfFitness
     */
    private void createRouletteWheelTable(List<Chromosome> chromosomeList, 
                                          List<Double> rouletteWheelTable, 
                                          List<Double> fitnessList, 
                                          double sumOfFitness) {

        double sum = 0;
        rouletteWheelTable.add(0d);

        for (int chromosome = 0; chromosome < chromosomeList.size(); chromosome++) {
            double probability = (double) fitnessList.get(chromosome) / (double) sumOfFitness;
            sum += probability;
            rouletteWheelTable.add(sum);
        }

    }

    /**
     * Add all Fitness values
     *
     * @return sumAllFitness
     */
    private double calculateSumOfFitness(List<Chromosome> chromosomeList, List<Double> fitnessList) {

        double sumOfFitness = 0;

        for (int chromosome = 0; chromosome < chromosomeList.size(); chromosome++) {
            sumOfFitness += fitnessList.get(chromosome);
        }

        return sumOfFitness;
    }

    /**
     * Gets fitness of each chromosome 
     * Add them fitnessList Fitness function
     * f(x) = 1 / (value + 128)
     */
    private void createFitnessList(List<Chromosome> chromosomeList, List<Double> fitnessList) {

        for (int chromosome = 0; chromosome < chromosomeList.size(); chromosome++) {

            double fitness = chromosomeList.get(chromosome).calculateFunctionValue();
            fitness = fitness + 128;
            fitness = 1 / fitness;
            fitnessList.add(fitness);
        }

    }

    /**
     * Creates initial population
     */
    private void createPopulation(List<Chromosome> chromosomeList) {
        for (int i = 0; i < 6; i++) {
            chromosomeList.add(new Chromosome());
        }
    }

}