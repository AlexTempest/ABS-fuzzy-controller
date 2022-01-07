//Alex Tempest
//Feb 2020
//slip fuzzifier individual for GA
import java.util.Random;

public class SlipFuzzifierIndividual {

	public double TSpeak;
	public double TSx2;
	public double SOx1;
	public double SOpeak;
	public double SOx2;
	public double Ox1;
	public double Opeak = 17.1;
	public double Ox2;
	public double POx1;
	public double POpeak;
	public double POx2;
	public double TLx1;
	public double TLpeak;
	
	public double BNpeak;
	public double BNx2;
	public double SNx1;
	public double SNpeak;
	public double SNx2;
	public double Zx1;
	public final double Zpeak = 0;
	public double Zx2;
	public double SPx1;
	public double SPpeak;
	public double SPx2;
	public double BPx1;
	public double BPpeak;
	
	public double slip;
	public double rateOfChangeOfSlip;
	public double brakeForce;
	
	public SlipFuzzifierIndividual(double s, double ros) {
		slip = s;
		rateOfChangeOfSlip = ros;
	}
	
	public int initialise() {
		generateSlipFuzzifier();
		generateRateOfSlipFuzzifier();
		return 0;
	}
	
	public int run() {
		//use the generated values to assign memberships to classes
		double[] fuzzySlipClasses = fuzzifierSlip();
		//assign values to memberships for rate of change of slip classes
		double[] fuzzyRateOfSlipClasses = fuzzifierRateOfSlip();
		//call the inference engine
		double[] memberships = inferenceEngine(fuzzySlipClasses, fuzzyRateOfSlipClasses);
		//call the defuzzifier
		int defuzzified = defuzzifier(memberships);
		//check the defuzzifier worked
		if(defuzzified != 0) {
			System.out.println("Something went wrong in the defuzzifier ");
		}
		//return brake value;
		return 0;
	}
	
	//Generate the fuzzifier for slip
	public void generateSlipFuzzifier() {
		//create randomiser
		Random rand = new Random();
		//Optimum peak value is only fixed value, use it to randomly select upper and lower bounds for optimum class
		
		Ox1 = Opeak*rand.nextDouble(); //in (0, Opeak)
		Ox2 = Opeak + (100-Opeak) * rand.nextDouble(); //in (Opeak, 100)
		
		SOx2 = Ox1 + (Opeak - Ox1) * rand.nextDouble();
		SOpeak = Ox1 * rand.nextDouble(); //left of Ox1
		SOx1 = SOpeak * rand.nextDouble(); //has to be < SOpeak
		
		TSx2 = SOx1 + (SOpeak - SOx1) * rand.nextDouble();
		TSpeak = SOx1 * rand.nextDouble(); //has to be < SOx1
		
		POx1 = Opeak + (Ox2 - Opeak) * rand.nextDouble(); //has to be in (Opeak, Ox2)
		POpeak = Ox2 + (100 - Ox2) * rand.nextDouble(); //has to be > Ox2
		POx2 = POpeak + (100 - POpeak) * rand.nextDouble(); //has to be > POpeak
		
		TLx1 = POpeak + (POx2 - POpeak) * rand.nextDouble(); //has to be in (POpeak, POx2)
		TLpeak =  POx2 + (100 - POx2) * rand.nextDouble(); //has to be > POx2
	}
	
	//Generate the fuzzifier for rate of change of slip
	public void generateRateOfSlipFuzzifier() {
		Random rand = new Random();
		Zx2 = rand.nextDouble();
		Zx1 = -rand.nextDouble();
		
		SPx1 = Zx2*rand.nextDouble();
		SPpeak = Zx2 + (2 - Zx2)*rand.nextDouble();
		SPx2 = SPpeak + (2 - SPpeak)*rand.nextDouble();
		
		BPx1 = SPpeak + (SPx2 - SPpeak)*rand.nextDouble();
		BPpeak = SPx2 + (2 - SPx2)*rand.nextDouble();
		
		SNx2 = Zx1*rand.nextDouble();
		SNpeak = -2 + (Zx1 + 2)*rand.nextDouble();
		SNx1 = -2 + (SNpeak + 2)*rand.nextDouble();
		
		BNx2 = SNx1 + (SNpeak - SNx1)*rand.nextDouble();
		BNpeak = -2 + (SNx1 + 2)*rand.nextDouble();
	}
	
	//Calculate the memberships to each fuzzy class
	public double[] fuzzifierSlip() {
		//slip memberships
		double tooSmallSlip = slipTooSmallMembership();
		double subOptimumSlip = slipSubOptimumMembership();
		double optimumSlip = slipOptimumMembership();
		double postOptimumSlip = slipPostOptimumMembership();
		double tooLargeSlip = slipTooLargeMembership();
		double[] slips = {tooSmallSlip, subOptimumSlip, optimumSlip, postOptimumSlip, tooLargeSlip};
		return slips;
	}
	
	//Calculate the memberships to each fuzzy class
	public double[] fuzzifierRateOfSlip() {
		//change of slip memberships
		//use membership functions on rate of change of slip to get membership values for slip
		double BNrateOfSlip = rateOfSlipBNrateOfSlipBooleanMembership();
		double SNrateOfSlip = rateOfSlipSNrateOfSlipBooleanMembership();
		double ZrateOfSlip = rateOfSlipZrateOfSlipBooleanMembership();
		double SPrateOfSlip = rateOfSlipSPrateOfSlipBooleanMembership();
		double BPrateOfSlip = rateOfSlipBPrateOfSlipBooleanMembership();
		double[] rateOfSlips = {BNrateOfSlip, SNrateOfSlip, ZrateOfSlip, SPrateOfSlip, BPrateOfSlip};
		return rateOfSlips;
	}
	
	//Use fuzzy memberships to assign memberships to defuzzify classes
	public double[] inferenceEngine(double[] fuzzySlips, double[] fuzzyRateOfSlips) {
		double[] brakeForceMemberships = {0, 0, 0, 0, 0};
		double[] slips = fuzzySlips;
		double[] rateOfSlips = fuzzyRateOfSlips;
		
		//-------------------Rule Base - IF THENS----------------------------------------------------------------------
		
		//-----------too small slips------------
		
		if(slips[0] != 0 && rateOfSlips[0] != 0) { //BN rate
			brakeForceMemberships[4] = Math.max(brakeForceMemberships[4], Math.min(slips[0], rateOfSlips[0])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[0] != 0 && rateOfSlips[1] != 0) { //SN rate
			brakeForceMemberships[4] = Math.max(brakeForceMemberships[4], Math.min(slips[0], rateOfSlips[1])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[0] != 0 && rateOfSlips[2] != 0) { //Z rate
			brakeForceMemberships[4] = Math.max(brakeForceMemberships[4], Math.min(slips[0], rateOfSlips[2])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[0] != 0 && rateOfSlips[3] != 0) { //SP rate
			brakeForceMemberships[4] = Math.max(brakeForceMemberships[4], Math.min(slips[0], rateOfSlips[3])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[0] != 0 && rateOfSlips[4] != 0) { //BP rate
			brakeForceMemberships[3] = Math.max(brakeForceMemberships[3], Math.min(slips[0], rateOfSlips[4])); //if the membership is already assigned, then pick biggest membership
		}
		
		//-----------sub optimum slips---------------
		
		if(slips[1] != 0 && rateOfSlips[0] != 0) { //BN rate
			brakeForceMemberships[4] = Math.max(brakeForceMemberships[4], Math.min(slips[1], rateOfSlips[0])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[1] != 0 && rateOfSlips[1] != 0) { //SN rate
			brakeForceMemberships[3] = Math.max(brakeForceMemberships[3], Math.min(slips[1], rateOfSlips[1])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[1] != 0 && rateOfSlips[2] != 0) { //Z rate
			brakeForceMemberships[3] = Math.max(brakeForceMemberships[3], Math.min(slips[1], rateOfSlips[2])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[1] != 0 && rateOfSlips[3] != 0) { //SP rate
			brakeForceMemberships[2] = Math.max(brakeForceMemberships[2], Math.min(slips[1], rateOfSlips[3])); //if the membership is already assigned, then pick biggest membership
		}
		
		if(slips[1] != 0 && rateOfSlips[4] != 0) { //BP rate
			brakeForceMemberships[2] = Math.max(brakeForceMemberships[2], Math.min(slips[1], rateOfSlips[4])); //if the membership is already assigned, then pick biggest membership
		}
		//--------- optimum slips---------------
		
		if(slips[2] != 0 && rateOfSlips[0] != 0) { //BN rate
			brakeForceMemberships[3] = Math.max(brakeForceMemberships[3], Math.min(slips[2], rateOfSlips[0])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[2] != 0 && rateOfSlips[1] != 0) { //SN rate
			brakeForceMemberships[3] = Math.max(brakeForceMemberships[3], Math.min(slips[2], rateOfSlips[1])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[2] != 0 && rateOfSlips[2] != 0) { //Z rate
			brakeForceMemberships[2] = Math.max(brakeForceMemberships[2], Math.min(slips[2], rateOfSlips[2])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[2] != 0 && rateOfSlips[3] != 0) { //SP rate
			brakeForceMemberships[2] = Math.max(brakeForceMemberships[2], Math.min(slips[2], rateOfSlips[3])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[2] != 0 && rateOfSlips[4] != 0) { //BP rate
			brakeForceMemberships[2] = Math.max(brakeForceMemberships[2], Math.min(slips[2], rateOfSlips[4])); //if the membership is already assigned, then pick biggest membership
		}
		
		//------------post optimum slips---------------
		
		if(slips[3] != 0 && rateOfSlips[0] != 0) { //BN rate
			brakeForceMemberships[2] = Math.max(brakeForceMemberships[2], Math.min(slips[3], rateOfSlips[0])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[3] != 0 && rateOfSlips[1] != 0) { //SN rate
			brakeForceMemberships[2] = Math.max(brakeForceMemberships[2], Math.min(slips[3], rateOfSlips[1])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[3] != 0 && rateOfSlips[2] != 0) { //Z rate
			brakeForceMemberships[1] = Math.max(brakeForceMemberships[1], Math.min(slips[3], rateOfSlips[2])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[3] != 0 && rateOfSlips[3] != 0) { //SP rate
			brakeForceMemberships[1] = Math.max(brakeForceMemberships[1], Math.min(slips[3], rateOfSlips[3])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[3] != 0 && rateOfSlips[4] != 0) { //BP rate
			brakeForceMemberships[0] = Math.max(brakeForceMemberships[0], Math.min(slips[3], rateOfSlips[4])); //if the membership is already assigned, then pick biggest membership
		}
		
		//-----------too big slips-----------------
		
		if(slips[4] != 0 && rateOfSlips[0] != 0) { //BN rate
			brakeForceMemberships[1] = Math.max(brakeForceMemberships[1], Math.min(slips[4], rateOfSlips[0])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[4] != 0 && rateOfSlips[1] != 0) { //SN rate
			brakeForceMemberships[0] = Math.max(brakeForceMemberships[0], Math.min(slips[4], rateOfSlips[1])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[4] != 0 && rateOfSlips[2] != 0) { //Z rate
			brakeForceMemberships[0] = Math.max(brakeForceMemberships[0], Math.min(slips[4], rateOfSlips[2])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[4] != 0 && rateOfSlips[3] != 0) { //SP rate
			brakeForceMemberships[0] = Math.max(brakeForceMemberships[0], Math.min(slips[4], rateOfSlips[3])); //if the membership is already assigned, then pick biggest membership
		}
		if(slips[4] != 0 && rateOfSlips[4] != 0) { //BP rate
			brakeForceMemberships[0] = Math.max(brakeForceMemberships[0], Math.min(slips[4], rateOfSlips[4])); //if the membership is already assigned, then pick biggest membership
		}
		/*
		System.out.println("Almost Zero: " + brakeForceMemberships[0]);
		System.out.println("Very Small: " + brakeForceMemberships[1]);
		System.out.println("Small: " + brakeForceMemberships[2]);
		System.out.println("Medium: " + brakeForceMemberships[3]);
		System.out.println("Large: " + brakeForceMemberships[4]);
		*/
		return brakeForceMemberships;
	}
	
	//Use memberships to classes to get a crisp brake value
	public int defuzzifier(double[] memberships) { 
		int returnValue = 0;

		double[] centreOfAreas = {0, 2719, 5438, 8157, 10876};
		double[] areas = new double[5];
		
		areas[0] = getZeroArea(memberships[0]);
		areas[1] = getVerySmallArea(memberships[1]);
		areas[2] = getSmallArea(memberships[2]);
		areas[3] = getMediumArea(memberships[3]);
		areas[4] = getLargeArea(memberships[4]);
		/*
		System.out.println("area of almost zero: " + areas[0]);
		System.out.println("area of v small: " + areas[1]);
		System.out.println("area of small: " + areas[2]);
		System.out.println("area of medium: " + areas[3]);
		System.out.println("area of large: " + areas[4]);
		*/
		brakeForce = (areas[0]*centreOfAreas[0] + areas[1]*centreOfAreas[1] + areas[2]*centreOfAreas[2] + areas[3]*centreOfAreas[3] + areas[4]*centreOfAreas[4])/(areas[0] + areas[1] + areas[2] + areas[3] + areas[4]);
		return returnValue;
	}
	
	public double getZeroArea(double peak) {
		int z_end = 2719;
		
		double x = (peak - 1)*(-2719);
		double area1 = ((z_end - x)*peak)/2;
		double area2 = x*peak;
		
		return area1+area2;
	}
	public double getVerySmallArea(double peak) {
		int start = 0;
		int end = 5438;
		
		double x1 = peak*2719;
		double x2 = (peak - 2)*(-2719);
		
		double area1 = (x1 - start)*peak/2;
		double area2 = (x2 - x1)*peak;
		double area3 = (end - x2)*peak/2;
		return area1+area2+area3;
	}
	public double getSmallArea(double peak) {
		int start = 2719;
		int end = 8157;
		double x1 = (peak + 1)*2719;
		double x2 = (peak - 3)*(-2719);
		
		double area1 = (x1 - start)*peak/2;
		double area2 = (x2 - x1)*peak;
		double area3 = (end - x2)*peak/2;
		return area1+area2+area3;
	}
	public double getMediumArea(double peak) {
		int start = 5438;
		int end = 10876;
		double x1 = (peak + 2)*2719;
		double x2 = (peak - 4)*(-2719);
		
		double area1 = (x1 - start)*peak/2;
		double area2 = (x2 - x1)*peak;
		double area3 = (end - x2)*peak/2;
		return area1+area2+area3;
	}
	public double getLargeArea(double peak) {
		int start = 8157;
		int end = 10876;
		double x1 = (peak + 3)*2719;
		
		double area1 = (x1 - start)*peak/2;
		double area2 = (end - x1)*peak;
		return area1 + area2;
	}
	
	//Mutate Fn
	public void mutate(double prob) {
		mutateSlip(prob);
		mutateRateOfSlip(prob);
	}
	
	//Mutate Slip
	public void mutateSlip(double prob) {
		Random rand = new Random();
		//if decides to mutate, generate a number in (-1,1) 
		if(mutateBoolean(prob)) {
			//System.out.println("TSpeak is mutating ");
			double change = 1 - 2*rand.nextDouble();
			double filler = TSpeak + change;
			while(filler > SOx1) {
				change = 1 - 2*rand.nextDouble();
				filler = TSpeak + change;
			}
			TSpeak = filler; //update value
			TSpeak = Math.max(0, TSpeak); //make sure it is still valid
		}
		
		if(mutateBoolean(prob)) {
			//System.out.println("TSx2 is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = TSx2 + change; //calculate new value
			while((filler > SOpeak) || (filler < SOx1)) {  //while new value doesn't make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = TSx2 + change;
			}
			TSx2 = filler; //update value
		}
		
		if(mutateBoolean(prob)) {
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = SOx1 + change; //calculate new value
			while((filler > TSx2) || (filler < TSpeak)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = SOx1 + change;
			}
			SOx1 = filler; //update value
		}
		
		if(mutateBoolean(prob)) {
			//System.out.println("SOpeak is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = SOpeak + change; //calculate new value
			while((filler < TSx2) || (filler > Ox1)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = SOpeak + change;
			}
			SOpeak = filler; //update value
		}
		
		if(mutateBoolean(prob)) {	
			//System.out.println("SOx2 is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = SOx2 + change; //calculate new value
			while((filler > Opeak) || (filler < Ox1)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = SOx2 + change;
			}
			SOx2 = filler; //update value
		}
		
		if(mutateBoolean(prob)) { //Ox1
			//System.out.println("Ox1 is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = Ox1 + change; //calculate new value
			while((filler < SOpeak) || (filler > SOx2)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = Ox1 + change;
			}
			Ox1 = filler; //update value
		}
		
		if(mutateBoolean(prob)) { //Ox2
			//System.out.println("Ox2 is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = Ox2 + change; //calculate new value
			while((filler < POx1) || (filler > POpeak)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = Ox2 + change;
			}
			Ox2 = filler; //update value
		}
		
		if(mutateBoolean(prob)) { //POx1
			//System.out.println("POx1 is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = POx1 + change; //calculate new value
			while(filler < Opeak || (filler > Ox2)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = POx1 + change;
			}
			POx1 = filler; //update value
		}
		
		if(mutateBoolean(prob)) { //POpeak
			//System.out.println("POpeak is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = POpeak + change; //calculate new value
			while((filler > TLx1) || (filler < Ox2)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = POpeak + change;
			}
			POpeak = filler; //update value
		}
		
		if(mutateBoolean(prob)) { //POx2
			//System.out.println("POx2 is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = POx2 + change; //calculate new value
			while((filler < TLx1) || (filler > TLpeak)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = POx2 + change;
			}
			POx2 = filler; //update value
		}
		
		if(mutateBoolean(prob)) { //TLx1
			//System.out.println("TLx1 is mutating ");
			double change = 1 - 2*rand.nextDouble(); //generate new value in (-1,1)
			double filler = TLx1 + change; //calculate new value
			while((filler > POx2) || (filler < POpeak)) {  //while new value doesnt make sense, recalculate until it does
				change = 1 - 2*rand.nextDouble();
				filler = TLx1 + change;
			}
			TLx1 = filler; //update value
		}
		
		if(mutateBoolean(prob)) {
			//System.out.println("TSpeak is mutating ");
			double change = 1 - 2*rand.nextDouble();
			double filler = TLpeak + change;
			while(filler < POx2) {
				change = 1 - 2*rand.nextDouble();
				filler = TLpeak + change;
			}
			TLpeak = filler;
			TLpeak = Math.min(100, TLpeak);
		}
	}
	
	//Mutate Rate of Slip
	public void mutateRateOfSlip(double prob) {
		Random rand = new Random();
		if(mutateBoolean(prob)) { //mutate BNpeak
			//System.out.println("BNPeak is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = BNpeak + change;
			while(temp > SNx1) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = BNpeak + change;
			}
			BNpeak = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate BNx2
			//System.out.println("BNx2 is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = BNx2 + change;
			while((temp < SNx1) || (temp > SNpeak)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = BNx2 + change;
			}
			BNx2 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate SNx1

			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = SNx1 + change;
			while((temp < BNpeak) || (temp > BNx2)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = SNx1 + change;
			}
			SNx1 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate SNpeak
			//System.out.println("SNPeak is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = SNpeak + change;
			while((temp < BNx2) || (temp > Zx1)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = SNpeak + change;
			}
			SNpeak = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate SNx2
			//System.out.println("SNx2 is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = SNx2 + change;
			while((temp < Zx1) || (temp > Zpeak)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = SNx2 + change;
			}
			SNx2 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate Zx1
			//System.out.println("Zx1 is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = Zx1 + change;
			while((temp > SNx2) || (temp < SNpeak)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = Zx1 + change;
			}
			Zx1 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate Zx2
			//System.out.println("Zx2 is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = Zx2 + change;
			while((temp > SPpeak) || (temp < SPx1)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = Zx2 + change;
			}
			Zx2 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate SPx1
			//System.out.println("SPx1 is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = SPx1 + change;
			while((temp > Zx2) || (temp < Zpeak)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = SPx1 + change;
			}
			SPx1 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate SPpeak
			//System.out.println("SPpeak is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = SPpeak + change;
			while((temp < Zx2) || (temp > BPx1)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = SPpeak + change;
			}
			SPpeak = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate SPx2
			//System.out.println("SPx2 is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = SPx2 + change;
			while((temp < BPx1) || (temp > BPpeak)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = SPx2 + change;
			}
			SPx2 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate BPx1
			//System.out.println("BPx1 is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = BPx1 + change;
			while((temp > SPx2) || (temp < SPpeak)) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = BPx1 + change;
			}
			BPx1 = temp; //update value
		}
		
		if(mutateBoolean(prob)) { //mutate BPpeak
			//System.out.println("BPpeak is mutating ");
			double change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
			double temp = BPpeak + change;
			while(temp < SPx2) {
				change = 0.1 - 0.2*rand.nextDouble(); //generate random in (-0.1, 0.1)
				temp = BPpeak + change;
			}
			BPpeak = temp; //update value
		}
	}
	
	//Boolean to decide to mutate or not
	public boolean mutateBoolean(double prob) {
		double mutationProbability = prob;
		Random rand = new Random();
		double a = rand.nextDouble();
		if(a < mutationProbability) {
			//System.out.println("True");
			return true;
		}
		//System.out.println("False");
		return false;
	}
	
	//-----------------Rate of change of Slip Membership Functions -------------------------------------------------
	
	public double rateOfSlipBNrateOfSlipBooleanMembership() {
		double bigNegativeMembership = 0;
		if(rateOfChangeOfSlip <= BNx2) {
			if(rateOfChangeOfSlip <= BNpeak) {
				bigNegativeMembership = 1;
			}
			else {
				double gradient = getGradient(BNpeak, BNx2, 1, 0);
				double yIntercept = 1 - gradient*BNpeak;
				bigNegativeMembership = (yIntercept + gradient*rateOfChangeOfSlip);
				bigNegativeMembership = Math.min(1.0, bigNegativeMembership);
			}
		}
		return bigNegativeMembership;
	}
	public double rateOfSlipSNrateOfSlipBooleanMembership() {
		double SNrateOfSlipBooleanMembership = 0;
		if((rateOfChangeOfSlip <= SNx2) && (rateOfChangeOfSlip >= SNx1)) {
			if(rateOfChangeOfSlip <= SNpeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(SNx1, SNpeak, 0, 1);
				double yIntercept = 1 - gradient*SNpeak;
				SNrateOfSlipBooleanMembership = (gradient*rateOfChangeOfSlip + yIntercept);
				//cant be more than 1
				SNrateOfSlipBooleanMembership = Math.min(1.0, SNrateOfSlipBooleanMembership);
			}
			else {
				//if right of peak, then calculate different gradient and y intercepts
				double gradient = getGradient(SNpeak, SNx2, 1, 0);
				double yIntercept = 1 - gradient*SNpeak;
				SNrateOfSlipBooleanMembership = (yIntercept + gradient*rateOfChangeOfSlip);
				SNrateOfSlipBooleanMembership = Math.min(1.0, SNrateOfSlipBooleanMembership);
			}
		}
		return SNrateOfSlipBooleanMembership;
	}
	public double rateOfSlipZrateOfSlipBooleanMembership() {
		double ZrateOfSlipBooleanMembership = 0;
		if((rateOfChangeOfSlip <= Zx2) && (rateOfChangeOfSlip >= Zx1)) {
			if(rateOfChangeOfSlip <= Zpeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(Zx1, Zpeak, 0, 1);
				double yIntercept = 1 - gradient*Zpeak;
				ZrateOfSlipBooleanMembership = (gradient*rateOfChangeOfSlip + yIntercept);
				//cant be more than 1
				ZrateOfSlipBooleanMembership = Math.min(1.0, ZrateOfSlipBooleanMembership);
			}
			else {
				//if right of peak, then calculate different gradient and y intercepts
				double gradient = getGradient(Zpeak, Zx2, 1, 0);
				double yIntercept = 1 - gradient*Zpeak;
				ZrateOfSlipBooleanMembership = (yIntercept + gradient*rateOfChangeOfSlip);
				ZrateOfSlipBooleanMembership = Math.min(1.0, ZrateOfSlipBooleanMembership);
			}
		}
		return ZrateOfSlipBooleanMembership;
	}
	public double rateOfSlipSPrateOfSlipBooleanMembership() {
		double SPrateOfSlipBooleanMembership = 0;
		if((rateOfChangeOfSlip <= SPx2) && (rateOfChangeOfSlip >= SPx1)) {
			if(rateOfChangeOfSlip <= SPpeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(SPx1, SPpeak, 0, 1);
				double yIntercept = 1 - gradient*SPpeak;
				SPrateOfSlipBooleanMembership = (gradient*rateOfChangeOfSlip + yIntercept);
				//cant be more than 1
				SPrateOfSlipBooleanMembership = Math.min(1.0, SPrateOfSlipBooleanMembership);
			}
			else {
				//if right of peak, then calculate different gradient and y intercepts
				double gradient = getGradient(SPpeak, SPx2, 1, 0);
				double yIntercept = 1 - gradient*SPpeak;
				SPrateOfSlipBooleanMembership = (yIntercept + gradient*rateOfChangeOfSlip);
				SPrateOfSlipBooleanMembership = Math.min(1.0, SPrateOfSlipBooleanMembership);
			}
		}
		return SPrateOfSlipBooleanMembership;
	}
	public double rateOfSlipBPrateOfSlipBooleanMembership() {
		double BPrateOfSlipBooleanMembership = 0;
		if(rateOfChangeOfSlip >= BPx1) {
			if(rateOfChangeOfSlip <= BPpeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(BPx1, BPpeak, 0, 1);
				double yIntercept = 1 - gradient*BPpeak;
				BPrateOfSlipBooleanMembership = (gradient*rateOfChangeOfSlip + yIntercept);
				//cant be more than 1
				BPrateOfSlipBooleanMembership = Math.min(1.0, BPrateOfSlipBooleanMembership);
			}
			else {
				BPrateOfSlipBooleanMembership = 1;
			}
		}
		return BPrateOfSlipBooleanMembership;
	}
	
	
	//-----------------Slip Membership Functions---------------------------------------------------------------------
	
	public double slipTooSmallMembership() {
		double tooSmallMembership = 0;
		if(slip <= TSx2) {
			if(slip <= TSpeak) {
				tooSmallMembership = 1;
			}
			else {
				double gradient = getGradient(TSpeak, TSx2, 1, 0);
				double yIntercept = 1 - gradient*TSpeak;
				tooSmallMembership = (yIntercept + gradient*slip);
				tooSmallMembership = Math.min(1.0, tooSmallMembership);
			}
		}
		return tooSmallMembership;
	}	
	public double slipSubOptimumMembership() {
		//initialise variable
		double subOptimumMembership = 0;
		//if it is in range then calc, else ignore
		if((slip >= SOx1) && (slip <= SOx2)) {
			//decide if left or right of peak
			if(slip <= SOpeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(SOx1, SOpeak, 0, 1);
				double yIntercept = 1 - gradient*SOpeak;
				subOptimumMembership = (gradient*slip + yIntercept);
				//cant be more than 1
				subOptimumMembership = Math.min(1.0, subOptimumMembership);
			}
			else {
				//if right of peak, then calculate different gradient and y intercepts
				double gradient = getGradient(SOpeak, SOx2, 1, 0);
				double yIntercept = 1 - gradient*SOpeak;
				subOptimumMembership = (yIntercept + gradient*slip);
				subOptimumMembership = Math.min(1.0, subOptimumMembership);
			}
		} //if not in range, leave as 0
		return subOptimumMembership;
	}
	public double slipOptimumMembership() {
		//initialise variable
		double optimumMembership = 0;
		//if it is in range then calc, else ignore
		if((slip >= Ox1) && (slip <= Ox2)) {
			//decide if left or right of peak
			if(slip <= Opeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(Ox1, Opeak, 0, 1);
				double yIntercept = 1 - gradient*Opeak;
				optimumMembership = (gradient*slip + yIntercept);
				//cant be more than 1
				optimumMembership = Math.min(1.0, optimumMembership);
			}
			else {
				//if right of peak, then calculate different gradient and y intercepts
				double gradient = getGradient(Opeak, Ox2, 1, 0);
				double yIntercept = 1 - gradient*Opeak;
				optimumMembership = (yIntercept + gradient*slip);
				optimumMembership = Math.min(1.0, optimumMembership);
			}
		} //if not in range, leave as 0
		return optimumMembership;
	}
	public double slipPostOptimumMembership() {
		//initialise variable
		double postOptimumMembership = 0;
		//if it is in range then calc, else ignore
		if((slip >= POx1) && (slip <= POx2)) {
			//decide if left or right of peak
			if(slip <= POpeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(POx1, POpeak, 0, 1);
				double yIntercept = 1 - gradient*POpeak;
				postOptimumMembership = (gradient*slip + yIntercept);
				//cant be more than 1
				postOptimumMembership = Math.min(1.0, postOptimumMembership);
			}
			else {
				//if right of peak, then calculate different gradient and y intercepts
				double gradient = getGradient(POpeak, POx2, 1, 0);
				double yIntercept = 1 - gradient*POpeak;
				postOptimumMembership = (yIntercept + gradient*slip);
				postOptimumMembership = Math.min(1.0, postOptimumMembership);
			}
		} //if not in range, leave as 0
		return postOptimumMembership;
	}
	public double slipTooLargeMembership() {
		//initialise variable
		double tooBigMembership = 0;
		//if it is in range then calc, else ignore
		if(slip >= TLx1) {
			//decide if left or right of peak
			if(slip < TLpeak) {
				//if left of peak, get gradient and y intercept to get the membership value
				double gradient = getGradient(TLx1, TLpeak, 0, 1);
				double yIntercept = 1 - gradient*TLpeak;
				tooBigMembership = (gradient*slip + yIntercept);
				//cant be more than 1
				tooBigMembership = Math.min(1.0, tooBigMembership);
			}
			else {
				//if right of peak, then membership is 1
				tooBigMembership = 1;
			}
		} //if not in range, leave as 0
		return tooBigMembership;
	}

	//------------------Brake Force Membership Functions-------------------------------------------------------------
	

		
	public double getGradient(double x1, double x2, int y1, int y2) {
		double gradient = (y2 - y1)/(x2 - x1);
		return gradient;
	}
	
	public void setSlip(double s) {
		slip = s;
	}
	
	public void setRateOfSlip(double cOfs) {
		rateOfChangeOfSlip = cOfs;
	}

	public double getBrakeForce() {
		return brakeForce;
	}
	
	public double[] getFuzzifierSlip() {
		double[] fuzzifier = {TSpeak, TSx2, SOx1, SOpeak, SOx2, Ox1, Ox2, POx1, POpeak, POx2, TLx1, TLpeak};
		return fuzzifier;
	}

	public double[] getFuzzifierRateOfSlip() {
		double[] fuzzifier = {BNpeak, BNx2, SNx1, SNpeak, SNx2, Zx1, Zx2, SPx1, SPpeak, SPx2, BPx1, BPpeak};
		return fuzzifier;
	}
	
	public void setFuzzifierSlip(double[] fuzz) {
		//has to be size 12 else fail
		if(fuzz.length != 12) {
			System.out.println("Bad input for fuzzifier");
			return;
		}
		TSpeak = fuzz[0];
		TSx2 = fuzz[1];
		SOx1 = fuzz[2];
		SOpeak = fuzz[3];
		SOx2 = fuzz[4];
		Ox1 = fuzz[5];
		Ox2 = fuzz[6];
		POx1 = fuzz[7];
		POpeak = fuzz[8];
		POx2 = fuzz[9];
		TLx1 = fuzz[10];
		TLpeak = fuzz[11];
	}
	
	public void setFuzzifierRateOfSlip(double[] fuzz) {
		//has to be size 12 else fail
		if(fuzz.length != 12) {
			System.out.println("Bad input for fuzzifier");
			return;
		}
		BNpeak = fuzz[0];
		BNx2 = fuzz[1];
		SNx1 = fuzz[2];
		SNpeak = fuzz[3];
		SNx2 = fuzz[4];
		Zx1 = fuzz[5];
		Zx2 = fuzz[6];
		SPx1 = fuzz[7];
		SPpeak = fuzz[8];
		SPx2 = fuzz[9];
		BPx1 = fuzz[10];
		BPpeak = fuzz[11];
	}
	
	public void printSlipClasses() {
		System.out.println("TSpeak: " + TSpeak);
		System.out.println("TSx2: " + TSx2);
		
		System.out.println("SOx1: " + SOx1);
		System.out.println("SOpeak: " + SOpeak);
		System.out.println("SOx2: " + SOx2);
		
		System.out.println("Ox1: " + Ox1);
		System.out.println("Opeak: " + Opeak);
		System.out.println("Ox2: " + Ox2);
		
		System.out.println("POx1: " + POx1);
		System.out.println("POpeak: " + POpeak);
		System.out.println("POx2: " + POx2);
		
		System.out.println("TLx1: " + TLx1);
		System.out.println("TLpeak: " + TLpeak);
	}

	public void printRateOfSlipClasses() {
		System.out.println("BNpeak: " + BNpeak);
		System.out.println("BNx2: " + BNx2);
		
		System.out.println("SNx1: " + SNx1);
		System.out.println("SNpeak: " + SNpeak);
		System.out.println("SNx2: " + SNx2);
		
		System.out.println("Zx1: " + Zx1);
		System.out.println("Zpeak: " + Zpeak);
		System.out.println("Zx2: " + Zx2);
		
		System.out.println("SPx1: " + SPx1);
		System.out.println("SPpeak: " + SPpeak);
		System.out.println("SPx2: " + SPx2);
		
		System.out.println("BPx1: " + BPx1);
		System.out.println("BPpeak: " + BPpeak);
	}
	
}
