//Alex Tempest 25/10/20
//V 1.2
//Computer Science project 2020/2021 - 3rd year MSci Maths and Computer Science undergaduate - University of Birmingham
//Simulation software for fuzzy logic controller
//Anti lock braking system for Peugeot 308 (2008)
import java.lang.Math; 
import java.util.ArrayList;
public class Simulation_Software {
	
	public static double stoppingDistance;
	//assume dry concrete
	public static double A = 0.9f;
	public static double B = 1.07f;
	public static double C = 0.2773f;
	public static double D = 0.0026f;
	
	public static double initialVelocity = 22.352; //m/s - change this to change initial velocity
	
	public static double calcMu(double s, double a, double b, double c, double d) {
		double mu = a* (b * (1.0 - Math.exp(-c*s)) - d*s); //calculates mu depending on abcd depending on weather
		return mu;
	}
	
	public static void main(String[] args) {
		
		double brakeForce = 10876; //N		
		
		double radiusOfWheel = 0.28; //m
		double g = 9.81f;//ms^-2
		int suspendedMass = 1371;//kg
		int massOfWheel = 25; //kg
		double deltaTime = 0.001;
		double time; //s
		double frictionalForce; //N
		double slip;

		ArrayList<Double> linearVelocities = new ArrayList<Double>(); //keep track of car velocities
		ArrayList<Double> angularVelocities = new ArrayList<Double>(); 
		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Double> slips = new ArrayList<Double>();
		ArrayList<Double> rateOfSlips = new ArrayList<Double>();
		ArrayList<Double> brakeForces = new ArrayList<Double>();
		
		distances.add(0.0); //initially distance travelled is 0
		slips.add(0.0); //initially slip is 0%
		linearVelocities.add(initialVelocity); //50 mph in m/s
		angularVelocities.add(initialVelocity/radiusOfWheel); //initially we assume w = velocity / radius
		fuzzyLogicController flc = new fuzzyLogicController(0, 0); //initialise the FLC with 0 slip and 0 rOfs
		
		for(time = 0; time < 20; time=time+deltaTime) { //incrementing by 100th of a second, until 10 seconds
			if(linearVelocities.get(linearVelocities.size() - 1) <= 0) {
				break; //if speed is 0, break
			}
			flc.run(); //run the FLC and get the calculated brake force
			brakeForce = flc.getBrakeForce(); //comment this out to run without any ABS
			brakeForces.add(brakeForce);
			
			//calculate distance given velocity and delta time
			double newX = distances.get(distances.size()-1) + deltaTime * linearVelocities.get(linearVelocities.size()-1);
			distances.add(newX);
			
			//calculate slip given velocity and angular velocity (and R_w)
			double rawSlip = Math.max(0.0, 1 - (angularVelocities.get(angularVelocities.size()-1)/(linearVelocities.get(linearVelocities.size()-1)/radiusOfWheel))); //raw slip is c[0,1]
			slip = rawSlip *100; //slip is measured as a %
			slips.add(slip);
			
			//calculate friction
			frictionalForce = -(0.25 * g * (suspendedMass) * calcMu(slip, A, B, C, D)); //assuming dry concrete
			
			//calculate new linear velocity given friction
			double linearAcceleration = frictionalForce / (suspendedMass * 0.25 + 4*massOfWheel);
			double newV = linearVelocities.get(linearVelocities.size()-1) + deltaTime*linearAcceleration;
			linearVelocities.add(newV);
			
			//calculate new angular velocity given brake force and friction force
			double angularAcceleration =  (- brakeForce - frictionalForce)/(massOfWheel*radiusOfWheel);
			double newW = angularVelocities.get(angularVelocities.size()-1) + (deltaTime*angularAcceleration);
			newW = Math.max(0.0, newW); //can't be -ve
			angularVelocities.add(newW);
			
			//rate of change of slip
			double slipRate = (linearAcceleration * (1 - rawSlip) - angularAcceleration*radiusOfWheel)/newV;
			//double rateOfSlip = (rawSlip - slips.get(slips.size()-2))/deltaTime;
			rateOfSlips.add(slipRate);
			flc.setRateOfSlip(slipRate); //pass slip and rate of slip values back to FLC
			flc.setSlip(slip);
		}
		//total distance travelled
		stoppingDistance = distances.get(distances.size()-1);
		
		//System.out.println("Distances: \n");
		for(int i = 0; i < distances.size(); i=i+100) {
			//System.out.println("(" + i/1000f + "," + distances.get(i) + ")");
		} 
		
		//System.out.println("Speeds: \n");
		for(int i = 0; i < linearVelocities.size(); i=i+100) {   
			//System.out.println("(" + i/1000f + "," + linearVelocities.get(i) + ")");
		}
		
		//System.out.println("Wheel Speeds: \n");
		for(int i = 0; i < angularVelocities.size(); i=i+100) {   
			//System.out.println("(" + i/1000f + "," + angularVelocities.get(i) + ")");
		}
		
		//System.out.println("Slips: \n");
		for(int i = 0; i < slips.size(); i=i+100) {
			//System.out.println(slips.get(i));
		}
		
		//System.out.println("Rate of slips: \n");
		for(int i = 0; i < rateOfSlips.size(); i=i+100) {
			//System.out.println("(" + i/1000f + "," + rateOfSlips.get(i) + ")");
		}
		
		//System.out.println("Brake Force: ");
		for(int i = 0; i < brakeForces.size(); i=i+100) {
			//System.out.println("(" + i/1000f + "," + brakeForces.get(i) + ")");
		}
		
		System.out.println("Final Distance: " + distances.get(distances.size()-1));
		System.out.println("Time: " + time);
		System.out.println("Final Speed: " + linearVelocities.get(linearVelocities.size()-1));
	}
	
	public static double getDistance() {
		return stoppingDistance;
	}
}

