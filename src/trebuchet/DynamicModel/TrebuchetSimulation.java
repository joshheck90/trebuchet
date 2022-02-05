package trebuchet.DynamicModel;

import java.io.IOException;

public class TrebuchetSimulation {

	public static void main(String[] args) throws IOException {
		
		String outputFile = "C:\\Users\\joshh\\Documents\\Trebuchet\\TrebuchetFileOutput_NewMatchingTest.csv";
		double timeStep = 0.0001;
		double finalAngle = 180; // any final angle equal to or greater than 270 degrees breaks things. Tries to set a negative vector magnitude somewhere.
		
		TrebuchetIntegrator sim = new TrebuchetIntegrator();
		
		sim.setThrowingArmLength(1.0);  		// meter
		sim.setCwArmLength(0.25);				// meter
		sim.setHangerLength(0.4);				// meter
		sim.setSlingLength(1.0);				// meter
		sim.setMainArmCgLength(0.2);			// meter
		sim.setHangerCgLength(0.2);				// meter
		sim.setCwMass(12);						// kg
		sim.setProjectileMass(0.15);			// kg
		sim.setMainArmMass(2);     				// kg
		sim.setMainArmMoment(0.5); 	            // kg-m^2
		sim.setHangerMass(0.5);					// kg
		sim.setHangerMoment(0.25);				// kg-m^2
		sim.setMainArmAngle(-60,angleUnit.DEG);	// 
		sim.setHangerAngle(-90,angleUnit.DEG);	// 
		sim.setSlingAngle(-180,angleUnit.DEG);	// 
		
		sim.solve(outputFile,timeStep,finalAngle);
		
		System.out.println("Sanity Check:");
		
		double velX = sim.getMainArmAngVel()*sim.getR05()*Math.cos(sim.getMainArmAngle() + Math.PI/2) + sim.getSlingAngVel()*sim.getR56()*Math.cos(sim.getSlingAngle() + Math.PI/2);
		double velY = sim.getMainArmAngVel()*sim.getR05()*Math.sin(sim.getMainArmAngle() + Math.PI/2) + sim.getSlingAngVel()*sim.getR56()*Math.sin(sim.getSlingAngle() + Math.PI/2);
		double velMag = Math.sqrt(Math.pow(velY,2) + Math.pow(velX,2));
		System.out.printf("Final Velocity: %7.3f m/s \n",velMag);
		
	}
}