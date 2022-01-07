//Alex Tempest 07/12/20
//V 1.1
//Computer Science project 2020/2021 - 3rd year MSci Maths and Computer Science undergraduate - University of Birmingham
//fuzzy logic controller for Anti lock braking system for Peugeot 308 (2008)

import java.lang.Math;
public class fuzzyLogicController {
	
	public double slip;
	public double rateOfChangeOfSlip;
	public double brakeForce;

	//brakeFuzzySets
	double AZbrake;
	double VSbrake;
	double Sbrake;
	double Mbrake;
	double Lbrake;
	
	//initializer
	public fuzzyLogicController(double s, double cOfS) {
		slip = s;
		rateOfChangeOfSlip = cOfS;
		brakeForce = 0;
	}
	
	public double run () {
		double[] fuzzifiedSlips = fuzzifierSlip();
		double[] fuzzifiedRateOfSlips = fuzzifierRateOfSlip();
		
		double[] memberships = inferenceEngine(fuzzifiedSlips, fuzzifiedRateOfSlips);
		
		int defuzzified = defuzzifier(memberships);
		if(defuzzified != 0) {
			System.out.println("Something went wrong in the defuzzifier ");
		}
		
		//return brake value;
		return 0.0;
	}
	
	public double[] fuzzifierSlip() {
		//slip memberships
		double tooSmallSlip;
		double subOptimumSlip;
		double optimumSlip;
		double postOptimumSlip; 
		double tooBigSlip;
		
		//use membership function on slip to get values for slip
		tooSmallSlip = slipTooSmallMembership();
		subOptimumSlip = slipSubOptimumMembership();
		optimumSlip = slipOptimumMembership();
		postOptimumSlip = slipPastOptimumMembership();
		tooBigSlip = slipTooLargeMembership();
		double[] slips = {tooSmallSlip, subOptimumSlip, optimumSlip, postOptimumSlip, tooBigSlip};
		return slips;
	}
	
	public double[] fuzzifierRateOfSlip() {
		//change of slip memberships
		double BNrateOfSlip;
		double SNrateOfSlip;
		double ZrateOfSlip;
		double SPrateOfSlip;
		double BPrateOfSlip;
		
		//use membership functions on rate of change of slip to get membership values for slip
		BNrateOfSlip = rateOfSlipBNrateOfSlipBooleanMembership();
		SNrateOfSlip = rateOfSlipSNrateOfSlipBooleanMembership();
		ZrateOfSlip = rateOfSlipZrateOfSlipBooleanMembership();
		SPrateOfSlip = rateOfSlipSPrateOfSlipBooleanMembership();
		BPrateOfSlip = rateOfSlipBPrateOfSlipBooleanMembership();
		
		double[] rateOfSlips = {BNrateOfSlip, SNrateOfSlip, ZrateOfSlip, SPrateOfSlip, BPrateOfSlip};
		return rateOfSlips;
	}
	
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
	
	//--------------------Slip Membership Functions------------------------------------------------------------------
	
	public double slipTooSmallMembership() {
		double tooSmallMembership = 0;
		if(slip <= 8.0) {
			if(slip <= 4.0) {
				tooSmallMembership = 1;
			}
			else {
				tooSmallMembership = (2.0 - 0.25*slip);
				tooSmallMembership = Math.min(1.0, tooSmallMembership);
			}
		}
		return tooSmallMembership;
	}	
	public double slipSubOptimumMembership() {
		double subOptimumMembership = 0;
		if((slip >= 6.0) && (slip <= 16.0)) {
			if(slip <= 10.0) {
				subOptimumMembership = (0.25*slip - 1.5);
				subOptimumMembership = Math.min(1.0, subOptimumMembership);
			}
			else {
				subOptimumMembership = ((2.666666666666) - (0.1666666666666)*slip);
				subOptimumMembership = Math.min(1.0, subOptimumMembership);
			}
		} //if not in range, leave as 0
		return subOptimumMembership;
	}
	public double slipOptimumMembership() {
		double optimumMembership = 0;
		if((slip >= 9.083) && (slip <= 29.083)) {
			if(slip <= 17.083) {
				optimumMembership = 0.125*slip - 1.135375;
				optimumMembership = Math.min(1.0, optimumMembership);
			} 
			else {
				optimumMembership = 2.423583 - (0.0833333333333)*slip;
				optimumMembership = Math.min(1.0, optimumMembership);
			}
		}
		return optimumMembership;
	}
	public double slipPastOptimumMembership() {
		double pastOptimumMembership = 0;
		if((slip >= 18.0) && (slip <= 48.0)) {
			if(slip <= 33) {
				pastOptimumMembership = (0.06666666666666666666)*slip - 1.2;
				pastOptimumMembership = Math.min(1.0, pastOptimumMembership);
			}
			else {
				pastOptimumMembership = 3.2 - (0.0666666666666666666666)*slip;
				pastOptimumMembership = Math.min(1.0, pastOptimumMembership);
			}
		}
		return pastOptimumMembership;
	}
	public double slipTooLargeMembership() {
		double tooLargeMembership = 0;
		if((slip >= 45.0) && (slip <= 100.0)) {
			if(slip <= 70.0) {
				tooLargeMembership = 0.04*slip - 1.8;
				tooLargeMembership = Math.min(1.0, tooLargeMembership);
			}
			else {
				tooLargeMembership = 1;
			}
		}
		return tooLargeMembership;
	}
	
	//------------------Rate of change of Slip Membership Functions -------------------------------------------------
	
	public double rateOfSlipBNrateOfSlipBooleanMembership() {
		double BNrateOfSlipBooleanMembership = 0;
		if(rateOfChangeOfSlip <= -1) {
			if(rateOfChangeOfSlip <= -1.6666666666666) {
				BNrateOfSlipBooleanMembership = 1;
			}
			else {
				BNrateOfSlipBooleanMembership = (-1.5 - 1.5*rateOfChangeOfSlip);
				BNrateOfSlipBooleanMembership = Math.min(1.0, BNrateOfSlipBooleanMembership);
			}
		}
		return BNrateOfSlipBooleanMembership;
	}
	public double rateOfSlipSNrateOfSlipBooleanMembership() {
		double SNrateOfSlipBooleanMembership = 0;
		if((rateOfChangeOfSlip <= -0.1333333333) && (rateOfChangeOfSlip >= -1.4666666666)) {
			if(rateOfChangeOfSlip <= -0.8) {
				SNrateOfSlipBooleanMembership = (1.5*rateOfChangeOfSlip + 2.2);
				SNrateOfSlipBooleanMembership = Math.min(1.0, SNrateOfSlipBooleanMembership);
			}
			else {
				SNrateOfSlipBooleanMembership = (- 0.2 - 1.5*rateOfChangeOfSlip);
				SNrateOfSlipBooleanMembership = Math.min(1.0, SNrateOfSlipBooleanMembership);
			}
		}
		return SNrateOfSlipBooleanMembership;
	}
	public double rateOfSlipZrateOfSlipBooleanMembership() {
		double ZrateOfSlipBooleanMembership = 0;
		if((rateOfChangeOfSlip <= 0.333333333) && (rateOfChangeOfSlip >= -0.3333333333)) {
			if(rateOfChangeOfSlip <= 0) {
				ZrateOfSlipBooleanMembership = (1.0 + 3*rateOfChangeOfSlip);
				ZrateOfSlipBooleanMembership = Math.min(1.0, ZrateOfSlipBooleanMembership);
			}
			else {
				ZrateOfSlipBooleanMembership = (1.0 - 3*rateOfChangeOfSlip);
				ZrateOfSlipBooleanMembership = Math.min(1.0, ZrateOfSlipBooleanMembership);
			}
		}
		return ZrateOfSlipBooleanMembership;
	}
	public double rateOfSlipSPrateOfSlipBooleanMembership() {
		double SPrateOfSlipBooleanMembership = 0;
		if((rateOfChangeOfSlip <= 1.46666666) && (rateOfChangeOfSlip >= 0.13333333333)) {
			if(rateOfChangeOfSlip <= 0.8) {
				SPrateOfSlipBooleanMembership = ((-0.2) + 1.5*rateOfChangeOfSlip);
				SPrateOfSlipBooleanMembership = Math.min(1.0, SPrateOfSlipBooleanMembership);
			}
			else {
				SPrateOfSlipBooleanMembership = (2.2 - 1.5*rateOfChangeOfSlip);
				SPrateOfSlipBooleanMembership = Math.min(1.0, SPrateOfSlipBooleanMembership);
			}
		}
		return SPrateOfSlipBooleanMembership;
	}
	public double rateOfSlipBPrateOfSlipBooleanMembership() {
		double BPrateOfSlipBooleanMembership = 0;
		if(rateOfChangeOfSlip >= 1) {
			if(rateOfChangeOfSlip <= 1.6666666666) {
				BPrateOfSlipBooleanMembership = (-1.5 + 1.5*rateOfChangeOfSlip);
				BPrateOfSlipBooleanMembership = Math.min(1.0, BPrateOfSlipBooleanMembership);
			}
			else {
				BPrateOfSlipBooleanMembership = 1;
			}
		}
		return BPrateOfSlipBooleanMembership;
	}

	//---------------------------------------------------------------------------------------------------------------

	public void setSlip(double s) {
		slip = s;
	}
	
	public void setRateOfSlip(double cOfs) {
		rateOfChangeOfSlip = cOfs;
	}

	public double getBrakeForce() {
		return brakeForce;
	}
}
