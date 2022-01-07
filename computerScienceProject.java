
public class computerScienceProject {
	public static void main(String[] args) {
		fuzzyLogicController flc = new fuzzyLogicController(10, 1);
		//for(int i = 0; i < 100; i++) {
			flc.run();
			double bf = flc.getBrakeForce();
			System.out.println(bf);
			flc.setSlip(0);
		//}
		System.out.println("END");
	}
}
