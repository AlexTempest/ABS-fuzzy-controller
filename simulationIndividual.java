//Alex Tempest - 25/02/21
//Individual for GA for optimising fuzzifiers in an FLC for anti lock brakes

import java.lang.Math; 
import java.util.ArrayList;
public class simulationIndividual {
	
	double A = 0.9f;
	double B = 1.07f;
	double C = 0.2773f;
	double D = 0.0026f;
	double initialVelocity = 22.352;
	
	public double stoppingDistance;
	public SlipFuzzifierIndividual flc;
	
	public simulationIndividual() {
		flc = new SlipFuzzifierIndividual(0, 0); //initialise the FLC with 0 and 0 when the simulator is made
		flc.initialise();
	}
	
	public void mutateFLC(double prob) {
		flc.mutate(prob);
	}
	
	public double calcMu(double s, double a, double b, double c, double d) {
		double mu = a* (b * (1.0 - Math.exp(-c*s)) - d*s);
		return mu;
	}
	
	public void run() {
		//assume dry concrete
		double radiusOfWheel = 0.28; //m
		double g = 9.81f;//ms^-2
		int suspendedWeight = 1371;//kg
		int massOfWheel = 25; //kg
		double deltaTime = 0.001;
		double time; //s
		double frictionalForce; //n
		double slip;
		
		double brakeForce = 0; //N
		ArrayList<Double> linearVelocities = new ArrayList<Double>(); //set up arrays to store values
		ArrayList<Double> angularVelocities = new ArrayList<Double>();
		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Double> slips = new ArrayList<Double>();
		flc.setRateOfSlip(0); //initialise flc
		flc.setSlip(0);
		
		distances.add(0.0);
		slips.add(0.0);
		linearVelocities.add(initialVelocity); //30 mph in m/s
		angularVelocities.add(initialVelocity/radiusOfWheel); //initially we assume w = velocity / radius
		
		for(time = 0; time < 20; time=time+deltaTime) { //incrementing by 100th of a second, until 10 seconds
			if(linearVelocities.get(linearVelocities.size() - 1) <= 0) {
				break;
			}
			
			flc.run();
			brakeForce = flc.getBrakeForce();
			
			//caclulate distance
			double newX = distances.get(distances.size()-1) + deltaTime * linearVelocities.get(linearVelocities.size()-1);
			distances.add(newX);
			
			//calculate slip
			double rawSlip = Math.max(0.0, 1 - (angularVelocities.get(angularVelocities.size()-1)/(linearVelocities.get(linearVelocities.size()-1)/radiusOfWheel))); //raw slip is c[0,1]
			slip = rawSlip *100; //slip is measured as a %
			slips.add(rawSlip);
			
			//caclulate friction
			frictionalForce = -(0.25 * g * suspendedWeight * calcMu(slip, A, B, C, D)); //assuming dry concrete
			//System.out.println(frictionalForce);
			
			//calculate new linear velocity
			double linearAcceleration = frictionalForce / (suspendedWeight * 0.25 + 4*massOfWheel);
			double newV = linearVelocities.get(linearVelocities.size()-1) + deltaTime*(linearAcceleration);
			linearVelocities.add(newV);
			
			//caclulate new angular velocity;
			double angularAcceleration =  (- brakeForce - frictionalForce)/(massOfWheel*radiusOfWheel);
			double newW = angularVelocities.get(angularVelocities.size()-1) + (deltaTime*angularAcceleration);
			newW = Math.max(0.0, newW); //can't be -ve
			angularVelocities.add(newW);
			
			//rate of change of slip
			double slipRate = (linearAcceleration * (1 - rawSlip) - angularAcceleration*radiusOfWheel)/newV;	
			
			//System.out.println(slipRate);
			//System.out.println(slip);
			//System.out.println("\n");
			//System.out.println(time);
			flc.setRateOfSlip(slipRate);
			flc.setSlip(slip);
		}
		stoppingDistance = distances.get(distances.size()-1);
	}
	
	public double getDistance() {
		return stoppingDistance;
	}
	
	public double[] getFuzzifierRofS() {
		return flc.getFuzzifierRateOfSlip();
	}
	
	public double[] getFuzzifierS() {
		return flc.getFuzzifierSlip();
	}
	
	public void setFuzzifierRofS(double[] fuzz) {
		flc.setFuzzifierRateOfSlip(fuzz);
	}
	
	public void setFuzzifierS(double[] fuzz) {
		flc.setFuzzifierSlip(fuzz);
	}
	
	public void printEverything() {
			double radiusOfWheel = 0.28; //m
			double g = 9.81f;//ms^-2
			int suspendedWeight = 1371;//kg
			int massOfWheel = 25; //kg
			double deltaTime = 0.001;
			double time; //s
			double frictionalForce; //n
			double slip;
			
			double brakeForce = 0; //N
			ArrayList<Double> linearVelocities = new ArrayList<Double>();
			ArrayList<Double> angularVelocities = new ArrayList<Double>();
			ArrayList<Double> distances = new ArrayList<Double>();
			ArrayList<Double> slips = new ArrayList<Double>();
			ArrayList<Double> slipRates = new ArrayList<Double>();
			ArrayList<Double> brakeForces = new ArrayList<Double>();
			flc.setRateOfSlip(0);
			flc.setSlip(0);
			
			distances.add(0.0);
			slips.add(0.0);
			linearVelocities.add(initialVelocity); //30 mph in m/s
			angularVelocities.add(initialVelocity/radiusOfWheel); //initially we assume w = velocity / radius
			
			for(time = 0; time < 20; time=time+deltaTime) { //incrementing by 100th of a second, until 10 seconds
				if(linearVelocities.get(linearVelocities.size() - 1) <= 0) {
					break;
				}
				
				flc.run();
				brakeForce = flc.getBrakeForce();
				brakeForces.add(brakeForce);
				
				//caclulate distance
				double newX = distances.get(distances.size()-1) + deltaTime * linearVelocities.get(linearVelocities.size()-1);
				distances.add(newX);
				
				//calculate slip
				double rawSlip = Math.max(0.0, 1 - (angularVelocities.get(angularVelocities.size()-1)/(linearVelocities.get(linearVelocities.size()-1)/radiusOfWheel))); //raw slip is c[0,1]
				slip = rawSlip *100; //slip is measured as a %
				slips.add(rawSlip);
				
				//caclulate friction
				frictionalForce = -(0.25 * g * suspendedWeight * calcMu(slip, A,B,C,D)); //assuming dry concrete
				//System.out.println(frictionalForce);
				
				//calculate new linear velocity
				double linearAcceleration = frictionalForce / (suspendedWeight * 0.25 + 4*massOfWheel);
				double newV = linearVelocities.get(linearVelocities.size()-1) + deltaTime*(linearAcceleration);
				linearVelocities.add(newV);
				
				//caclulate new angular velocity;
				
				double angularAcceleration =  (- brakeForce - frictionalForce)/(massOfWheel*radiusOfWheel);
				double newW = angularVelocities.get(angularVelocities.size()-1) + (deltaTime*angularAcceleration);
				newW = Math.max(0.0, newW); //can't be -ve
				angularVelocities.add(newW);
				
				//rate of change of slip
				double slipRate = (linearAcceleration * (1 - rawSlip) - angularAcceleration*radiusOfWheel)/newV;	
				slipRates.add(slipRate);

				flc.setRateOfSlip(slipRate);
				flc.setSlip(slip);
			}
			
			stoppingDistance = distances.get(distances.size()-1);
			
			System.out.println("Distances: \n");
			for(int i = 0; i < distances.size(); i=i+100) {
				System.out.println("(" + i/1000f + "," + distances.get(i) + ")");
			} 
			
			System.out.println("Speeds: \n");
			for(int i = 0; i < linearVelocities.size(); i=i+100) {   
				System.out.println("(" + i/1000f + "," + linearVelocities.get(i) + ")");
			}
			
			System.out.println("Wheel Speeds: \n");
			for(int i = 0; i < angularVelocities.size(); i=i+100) {   
				System.out.println("(" + i/1000f + "," + angularVelocities.get(i) + ")");
			}
			
			System.out.println("Slips: \n");
			for(int i = 0; i < slips.size(); i=i+100) {
				System.out.println("(" + i/1000f + "," + slips.get(i)*100 + ")");
			}
			
			System.out.println("Rate of slips: \n");
			for(int i = 0; i < slipRates.size(); i=i+100) {
				System.out.println("(" + i/1000f + "," + slipRates.get(i) + ")");
			}
			
			System.out.println("Brake Force: ");
			for(int i = 0; i < brakeForces.size(); i=i+100) {
				System.out.println("(" + i/1000f + "," + brakeForces.get(i) + ")");
			}
			
			System.out.println(distances.get(distances.size()-1));
			System.out.println(time);
	}
}
