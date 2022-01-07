import java.util.Random; 

public class GAFLC {

	public simulationIndividual[] population;
	public int populationSize;
	public double crossoverProbability;
	public double mutationProbability;
	public int parentSize;
	
	public GAFLC(int pop, double mutationProb, double crossoverProb, int parent) {
		populationSize = pop;
		crossoverProbability = crossoverProb;
		mutationProbability = mutationProb;
		parentSize = parent;
	}
	
	public void generatePopulation () {
		population = new simulationIndividual[populationSize];
		for(int i = 0; i < populationSize; i++) {
			//in the simSoftware, store a var called fitness
			population[i] = new simulationIndividual();
			population[i].run();
		}
	}
	
	public void runGeneration() {
		mutate();
		calculateDistances();
		sortIndividuals();
		crossover();
		//printPopulationFitness();
		//delete rubbish fuzzifier, replace them with the best 1/10th
	}
	
	//individuals have .getDistance() which will be used as fitness
	public void sortIndividuals() {
		int n = populationSize;
		for (int k=n; k >= 0; k--) {
			for (int i = 0; i < n-1; i++) {
				int j = i + 1;
				double elementI = population[i].getDistance();
				double elementJ = population[j].getDistance();
				if (elementI > elementJ) {
					swapElements(i, j, population);
				}
			}
		}
	}
	
	//for the sort method
	public static void swapElements(int i, int j, simulationIndividual[] array) {
		simulationIndividual temp;
		temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	public void calculateDistances() {
		for(int i = 0; i < populationSize; i++) {
			population[i].run();
		}
	}
	
	public simulationIndividual getBest() {
		simulationIndividual best = population[0];
		return best;
	}
	
	//this will have to be done in the indivdual 
	public void mutate() {
		for(int i = 0; i < populationSize; i++) {
			population[i].mutateFLC(mutationProbability);
			//System.out.println("Finished mutating this individual");
		}
	}
	
	//this is more involved, leave this until last
	public void crossover() {
		Random rand = new Random();
		int counter = 0;
		for(int i = 0; i < parentSize; i++) {
			double randomDouble = rand.nextDouble();
			if(randomDouble < crossoverProbability) { //do a crossover
				//System.out.println("Crossing over");
				int parent1 = 0;
				int parent2 = 0;
				while(parent1 == parent2) { //make sure the 2 parents arent the same, else waste of time
					parent1 = rand.nextInt(parentSize+1);
					parent2 = rand.nextInt(parentSize+1);
				}
				
				simulationIndividual child1 = new simulationIndividual();
				simulationIndividual child2 = new simulationIndividual();
				
				//System.out.println("Parent 1 distance: " + population[parent1].getDistance());
				//System.out.println("Parent 2 distance: " + population[parent2].getDistance());
				
				double[] parent1FuzzifierSlip = population[parent1].getFuzzifierS();
				double[] parent1FuzzifierRateOfSlip = population[parent1].getFuzzifierRofS();
				
				double[] parent2FuzzifierSlip = population[parent2].getFuzzifierS();
				double[] parent2FuzzifierRateOfSlip = population[parent2].getFuzzifierRofS();
				//do i need this or this next line slowing down the program?
				child1.run();
				child2.run();
				
				//System.out.println("child 1 initial distance: " + child1.getDistance());
				//System.out.println("child 2 initial distance: " + child2.getDistance());
				
				child1.setFuzzifierRofS(parent1FuzzifierRateOfSlip);
				child1.setFuzzifierS(parent2FuzzifierSlip);
				child2.setFuzzifierRofS(parent2FuzzifierRateOfSlip);
				child2.setFuzzifierS(parent1FuzzifierSlip);
				
				child1.run();
				child2.run();
				
				//System.out.println("child 1 final distance: " + child1.getDistance());
				//System.out.println("child 2 final distance: " + child2.getDistance());
				
				population[populationSize-1-counter] = child1;
				population[populationSize-2-counter] = child2;
				counter = counter + 2;
			}
		}
	}
	
	//probably dont need this
	public void deletePopulation() {
		
	}
	
	public void printPopulationFitness() {
		for(int i = 0; i < populationSize; i++) {
			System.out.println(population[i].getDistance());
		}
	}
	
}
