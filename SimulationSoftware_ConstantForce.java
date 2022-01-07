import java.lang.Math; 
import java.util.ArrayList;
public class SimulationSoftware_ConstantForce {
	
	public static double calcMu(double s, double a, double b, double c, double d) {
		double mu = a* (b * (1.0 - Math.exp(-c*s)) - d*s);
		return mu;
	}
	
	public static void main(String[] args) {
		
		//assume dry concrete
		//double A = 0.9f;
		//double B = 1.07f;
		//double C = 0.2773f;
		//double D = 0.0026f;
		double radiusOfWheel = 0.28; //m
		double g = 9.81f;//ms^-2
		int suspendedWeight = 1371;//kg
		int massOfWheel = 25; //kg
		double deltaTime = 0.001;
		double time; //s
		double frictionalForce; //n
		double slip;
		
		double brakeForce = 0; //N
		double initialVelocity = 22.352; //22.352;
		ArrayList<Double> linearVelocities = new ArrayList<Double>();
		ArrayList<Double> angularVelocities = new ArrayList<Double>();
		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Double> slips = new ArrayList<Double>();
		
		distances.add(0.0);
		slips.add(0.0);
		linearVelocities.add(initialVelocity); //30 mph in m/s
		angularVelocities.add(initialVelocity/radiusOfWheel); //initially we assume w = velocity / radius
		
		for(time = 0; time < 5; time=time+deltaTime) { //incrementing by 100th of a second, until 10 seconds
			if(linearVelocities.get(linearVelocities.size() - 1) <= 0) {
				break;
			}
			
			brakeForce = 5000;
			//System.out.println("brake force: " + brakeForce);
			
			//caclulate distance
			double newX = distances.get(distances.size()-1) + deltaTime * linearVelocities.get(linearVelocities.size()-1);
			distances.add(newX);
			
			//calculate slip
			double rawSlip = Math.max(0.0, 1 - (angularVelocities.get(angularVelocities.size()-1)/(linearVelocities.get(linearVelocities.size()-1)/radiusOfWheel))); //raw slip is c[0,1]
			slip = rawSlip *100; //slip is measured as a %
			slips.add(rawSlip);
			
			//caclulate friction
			frictionalForce = -(0.25 * g * suspendedWeight * calcMu(slip, 0.9, 1.07, 0.2773, 0.0026)); //assuming dry concrete
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
			
			double slipRate = (linearAcceleration * (1 - rawSlip) - angularAcceleration*radiusOfWheel)/newV;

			//System.out.println(angularAcceleration);
			//System.out.println(slipRate);
			//System.out.println(slip);
			//System.out.println("\n");
		}
		
		System.out.println("Wheel Speeds: \n");
		for(int i = 0; i < angularVelocities.size(); i++) {   
			//System.out.print(angularVelocities.get(i) + "\n");
		}
		System.out.println(angularVelocities.get(angularVelocities.size()-1));
		
		System.out.println("Speeds: \n");
		for(int i = 0; i < linearVelocities.size(); i++) {   
			//System.out.print(linearVelocities.get(i) + "\n");
		}
		System.out.println(linearVelocities.get(linearVelocities.size()-1));
		
		System.out.println("Distances: \n");
		for(int i = 0; i < distances.size(); i=i+100) {
			System.out.print(distances.get(i) + "\n");
		} 
		//System.out.println(distances.get(distances.size()-1));
			
	}	
}

