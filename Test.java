
public class Test {
	public static void main(String args[]) {
		final long startTime = System.nanoTime();
		
		GAFLC ga = new GAFLC(50, 0.5, 0.5, 25);
		//pass the GA population size, crossover prob, mutation prob, parent size
		
		ga.generatePopulation();
		ga.sortIndividuals();
		double best = ga.getBest().getDistance();
		System.out.println("initial best: " + best);
		//ga.printPopulationFitness();

		for(int i = 0; i < 50; i++) { //number of generations
			ga.runGeneration();
			best = ga.getBest().getDistance();
			System.out.println("best after individual generation " + i + " is: " + best);
			//ga.printPopulationFitness();
		}
		
		final long duration = System.nanoTime() - startTime;
		
		simulationIndividual finalBest = ga.getBest();
		double[] fSlip = finalBest.getFuzzifierS();
		double[] fRofS = finalBest.getFuzzifierRofS();
		
		//System.out.println("Slip fuzzifier: ");
		
		for(int i = 0; i < fSlip.length; i++) {
			//System.out.println(fSlip[i]);
		}	
		
		//System.out.println("Rate of slip fuzzifier: ");
		
		for(int i = 0; i < fRofS.length; i++) {
			//System.out.println(fRofS[i]);
		}	
		
		//ga.getBest().printEverything(); 
		//Uncomment this to show velocities, angular velocities, brakeforce, distances, slips, rate of slips and the fuzzifier
		
		System.out.println("Total time take to optimise: " + duration/1000000000f);
	}
}
