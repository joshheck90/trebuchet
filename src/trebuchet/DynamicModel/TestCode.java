package trebuchet.DynamicModel;

public class TestCode {
	
	public static void main(String[] args){
			
		TrebuchetIntegrator sim = new TrebuchetIntegrator();
		
		double[] vals = sim.solve(0.001,135);
		
		System.out.println("Velocity " + vals[0] + " angle " + vals[1]*180/Math.PI);
		
		System.out.println("Time Step: " + sim.getCurrentTime());
		System.out.println("Projectile Possition: X = " + sim.projPosition.x() + " Y = " + sim.projPosition.y());
		
		
		
	}
}