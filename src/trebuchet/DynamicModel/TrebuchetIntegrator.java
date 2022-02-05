package trebuchet.DynamicModel;

import Jama.*;
import java.io.*;

public class TrebuchetIntegrator extends Trebuchet {

	private static final double GRAV = 9.807;
	private static final double PI = Math.PI;
	
	private int timeStepCount;
	private double timeStep;
	private double currentTime;
	private double nextTime;
	
	private byte phase;
	
	private double mainArmAngVel;
	private double hangerAngVel;
	private double slingAngVel;
	
	private double mainArmAngAcc;
	private double hangerAngAcc;
	private double slingAngAcc;
	
	private double cwForceX;
	private double cwForceY;
	private double hangerForceX;
	private double hangerForceY;
	private double pivotForceX;
	private double pivotForceY;
	private double tensionForce;
	private double normalForce;
	
	private double cwAccX;
	private double cwAccY;
	private double hangerAccX;
	private double hangerAccY;
	private double mainArmAccX;
	private double mainArmAccY;
	private double projAccX;
	private double projAccY;
	
	protected Vector2D throwingArm = new Vector2D();
	protected Vector2D cwArm = new Vector2D();
	protected Vector2D cwHanger = new Vector2D();
	protected Vector2D sling = new Vector2D();
	protected Vector2D projPosition = new Vector2D();
	protected Vector2D cwPosition = new Vector2D();
	protected Vector2D projVel = new Vector2D();
	protected Vector2D cwVel = new Vector2D();
	
	private Matrix coefficients = new Matrix(18,18);
	private Matrix variables = new Matrix(18,1);
	private Matrix constants = new Matrix(18,1);
	
	private boolean printFirstIterationMatricies = false;
	
	TrebuchetIntegrator(){
		
		timeStepCount = 0;
		timeStep = 0.001;
		currentTime = 0;
		phase = 1;
		mainArmAngVel = 0.0;
		hangerAngVel = 0.0;
		slingAngVel = 0.0;
		mainArmAngAcc = 0.0;
		hangerAngAcc = 0.0;
		slingAngAcc = 0.0;
		cwForceX = 0.0;
		cwForceY = 0.0;
		hangerForceX = 0.0;
		hangerForceY = 0.0;
		pivotForceX = 0.0;
		pivotForceY = 0.0;
		tensionForce = 0.0;
		normalForce = getProjectileMass()*GRAV;
		cwAccX = 0.0;
		cwAccY = 0.0;
		hangerAccX = 0.0;
		hangerAccY = 0.0;
		mainArmAccX = 0.0;
		mainArmAccY = 0.0;
		projAccX = 0.0;
		projAccY = 0.0;
	}
	
	public void initialize(double timeStep) {
		
		this.timeStep = timeStep;
		nextTime = currentTime+timeStep;
		throwingArm.setMag(getR05());
		cwArm.setMag(getR01());
		cwHanger.setMag(getR13());
		sling.setMag(getR56());
		throwingArm.setAngle(getMainArmAngle(),angleUnit.RAD);
		cwArm.setAngle((getMainArmAngle()+PI),angleUnit.RAD);
		cwHanger.setAngle(getHangerAngle(),angleUnit.RAD);
		sling.setAngle(getSlingAngle(),angleUnit.RAD);
		projPosition = throwingArm.add(sling);
		cwPosition = cwArm.add(cwHanger);
	}
	
	public void solve(String outputFileName,double timeStep, double endAngle) throws IOException {
		
		if (timeStepCount == 0) { initialize(timeStep);	}
		
		FileWriter fileWriter = new FileWriter(outputFileName);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    
	    printWriter.print("Time Step [frac],Time [sec],Phase Flag [frac],Main Arm Angle [rad],Main Arm Angular Velocity [Rad/Sec],Main Arm Angular Acceleration [rad/sec^2],"
	    		+ "Hanger Angle [rad],Hanger Angular Velocity [Rad/Sec],Hanger Angular Acceleration [rad/sec^2],"
	    		+ "Sling Angle [rad],Sling Angular Velocity [Rad/Sec],Sling Angular Acceleration [rad/sec^2],"
	    		+ "Throwing Arm End Position - X,Throwing Arm End Position - Y, Counter Weight Arm End Position - X, Counter Weight Arm End Position - Y,"
	    		+ "Counter Weight Possition - X,Counter Weight Position - Y,Projectile Positin - X,Projectile Position - Y,"
	    		+ "Projectile Velocity [m/sec],Projectile Direction [rad],Counter Weight Velocity [m/sec],Counter Weight Direction [rad]\n");
	    
	    printWriter.printf("%d,%f,%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\n",
	    		timeStepCount,currentTime,phase,getMainArmAngle(),mainArmAngVel,mainArmAngAcc,getHangerAngle(),hangerAngVel,hangerAngAcc,getSlingAngle(),slingAngVel,slingAngAcc,
	    		throwingArm.x(),throwingArm.y(),cwArm.x(),cwArm.y(),cwPosition.x(),cwPosition.y(),projPosition.x(),projPosition.y(),
	    		projVel.getMag(),projVel.getAngle(),cwVel.getMag(),cwVel.getAngle());
		
		boolean endCriteria = true;
		
		
		while (endCriteria){
			
			timeStep();
			timeStepCount++;
			
			printWriter.printf("%d,%f,%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\n",
		    		timeStepCount,currentTime,phase,getMainArmAngle(),mainArmAngVel,mainArmAngAcc,getHangerAngle(),hangerAngVel,hangerAngAcc,getSlingAngle(),slingAngVel,slingAngAcc,
		    		throwingArm.x(),throwingArm.y(),cwArm.x(),cwArm.y(),cwPosition.x(),cwPosition.y(),projPosition.x(),projPosition.y(),
		    		projVel.getMag(),projVel.getAngle(),cwVel.getMag(),cwVel.getAngle());
			
			
			if (projVel.getAngle()>(endAngle*PI/180)) {
				endCriteria = false;
				printWriter.close();
				System.out.println("Simulation Completed.");
				System.out.printf("Final Angle: %7.3f Degrees \n",projVel.getAngle()*180/PI);
				System.out.printf("Final Velocity: %7.3f m/sec \n",projVel.getMag());
			}
		}
	}
	
	public double[] solve(double timeStep, double endAngle){
		
		if (timeStepCount == 0) { initialize(timeStep);	}
		
		boolean endCriteria = true;
		
		while (endCriteria){
			
			timeStep();
			timeStepCount++;
			
			if (projVel.getAngle()>(endAngle*PI/180)) {
				endCriteria = false;
			}
		}
		
		double[] endValues = new double[2];
		endValues[0] = projVel.getMag();
		endValues[1] = projVel.getAngle();
		return endValues;
	}
	
	private void buildMatricies() {
		
		coefficients.set(0,0, -getR01()*Math.sin(getMainArmAngle()+PI) );
		coefficients.set(0,1, -getR13()*Math.sin(getHangerAngle()) );
		coefficients.set(0,7, -1 );
				
		coefficients.set(1,0, getR01()*Math.cos(getMainArmAngle()+PI) );
		coefficients.set(1,1, getR13()*Math.cos(getHangerAngle()) );
		coefficients.set(1,8, -1 );
				
		coefficients.set(2,0, -getR01()*Math.sin(getMainArmAngle()+PI) );
		coefficients.set(2,1, -getR12()*Math.sin(getHangerAngle()) );
		coefficients.set(2,5, 1 );
		
		coefficients.set(3,0, getR01()*Math.cos(getMainArmAngle()+PI) );
		coefficients.set(3,1, getR12()*Math.cos(getHangerAngle()) );
		coefficients.set(3,6, -1 );
		
		coefficients.set(4,0, -getR04()*Math.sin(getMainArmAngle()) );
		coefficients.set(4,3, -1 );
		
		coefficients.set(5,0, getR04()*Math.cos(getMainArmAngle()) );
		coefficients.set(5,4, -1 );
		
		coefficients.set(6,0, -getR05()*Math.sin(getMainArmAngle()) );
		coefficients.set(6,2, -getR56()*Math.sin(getSlingAngle()) );
		coefficients.set(6,9, -1 );
		
		coefficients.set(7,0, getR05()*Math.cos(getMainArmAngle()) );
		coefficients.set(7,2, getR56()*Math.cos(getSlingAngle()) );
		
		coefficients.set(8,3, -getMainArmMass() );
		coefficients.set(8,11, Math.cos(getSlingAngle()) );
		coefficients.set(8,14, -1 );
		coefficients.set(8,16, 1 );
		
		coefficients.set(9,4, -getMainArmMass() );
		coefficients.set(9,11, Math.sin(getSlingAngle()) );
		coefficients.set(9,15, -1 );
		coefficients.set(9,17, 1 );
		
		coefficients.set(10,0, -getMainArmMoment() );
		coefficients.set(10,11, getR45()*(Math.cos(getMainArmAngle())*Math.sin(getSlingAngle()) - Math.sin(getMainArmAngle())*Math.cos(getSlingAngle())) );
		coefficients.set(10,15, getR14()*Math.sin(getMainArmAngle()+PI) );
		coefficients.set(10,15, -getR14()*Math.cos(getMainArmAngle()+PI) );
		coefficients.set(10,16, -getR04()*Math.sin(getMainArmAngle()+PI) );
		coefficients.set(10,17, getR04()*Math.cos(getMainArmAngle()+PI) );
		
		coefficients.set(11,5 ,-getHangerMass() );
		coefficients.set(11,12, -1 );
		coefficients.set(11,14, 1 );
		
		coefficients.set(12,6, -getHangerMass() );
		coefficients.set(12,13, -1 );
		coefficients.set(12,15, 1 );
		
		coefficients.set(13,1, -getHangerMoment() );
		coefficients.set(13,12, getR23()*Math.sin(getHangerAngle()) );
		coefficients.set(13,13, -getR23()*Math.cos(getHangerAngle()) );
		coefficients.set(13,14, -getR12()*Math.sin(getHangerAngle()+PI) );
		coefficients.set(13,15, getR12()*Math.cos(getHangerAngle()+PI) );
		
		coefficients.set(14,7, -getCwMass() );
		coefficients.set(14,12, 1 );
		
		coefficients.set(15,8, -getCwMass() );
		coefficients.set(15,13, 1 );
		
		coefficients.set(16,9, -getProjectileMass() );
		coefficients.set(16,11, Math.cos(getSlingAngle()+PI) );
		
		coefficients.set(17,11, Math.sin(getSlingAngle()+PI) );
		
		if (phase == 1) {
			coefficients.set(7,10, 0 );
			coefficients.set(17,10, 1 );
		} else {
			coefficients.set(7,10, -1 );
			coefficients.set(17,10, -getProjectileMass() );
		}
		
		constants.set(0,0, getR01()*Math.pow(mainArmAngVel,2)*Math.cos(getMainArmAngle()+PI) + getR13()*Math.pow(hangerAngVel,2)*Math.cos(getHangerAngle()) );
		constants.set(1,0, getR01()*Math.pow(mainArmAngVel,2)*Math.sin(getMainArmAngle()+PI) + getR13()*Math.pow(hangerAngVel,2)*Math.sin(getHangerAngle()) );
		constants.set(2,0, getR01()*Math.pow(mainArmAngVel,2)*Math.cos(getMainArmAngle()+PI) + getR12()*Math.pow(hangerAngVel,2)*Math.cos(getHangerAngle()) );
		constants.set(3,0, getR01()*Math.pow(mainArmAngVel,2)*Math.sin(getMainArmAngle()+PI) + getR12()*Math.pow(hangerAngVel,2)*Math.sin(getHangerAngle()) );
		constants.set(4,0, getR04()*Math.pow(mainArmAngVel,2)*Math.cos(getMainArmAngle()) );
		constants.set(5,0, getR04()*Math.pow(mainArmAngVel,2)*Math.sin(getMainArmAngle()) );
		constants.set(6,0, getR05()*Math.pow(mainArmAngVel,2)*Math.cos(getMainArmAngle()) + getR56()*Math.pow(slingAngVel,2)*Math.cos(getSlingAngle()) );
		constants.set(7,0, getR05()*Math.pow(mainArmAngVel,2)*Math.sin(getMainArmAngle()) + getR56()*Math.pow(slingAngVel,2)*Math.sin(getSlingAngle()) );
		constants.set(8,0, 0.0 );
		constants.set(9,0, getMainArmMass()*GRAV );
		constants.set(10,0, 0.0 );
		constants.set(11,0, 0.0 );
		constants.set(12,0, getHangerMass()*GRAV );
		constants.set(13,0, 0.0 );
		constants.set(14,0, 0.0 );
		constants.set(15,0, getCwMass()*GRAV );
		constants.set(16,0, 0.0 );
		constants.set(17,0, getProjectileMass()*GRAV );
	}
	
	
	
	private void solveMatricies() {
		
		Matrix aInv = coefficients.inverse();
		variables = aInv.times(constants);
		
		if (timeStepCount == 0 && printFirstIterationMatricies) {
			printCurrentMatricies();
		}
		
		mainArmAngAcc = variables.get(0,0);
		hangerAngAcc = variables.get(1,0);
		slingAngAcc = variables.get(2,0);
		mainArmAccX = variables.get(3,0);
		mainArmAccY = variables.get(4,0);
		hangerAccX = variables.get(5,0);
		hangerAccY = variables.get(6,0);
		cwAccX = variables.get(7,0);
		cwAccY = variables.get(8,0);
		projAccX = variables.get(9,0);
		tensionForce = variables.get(11,0);
		cwForceX = variables.get(12,0);
		cwForceY = variables.get(13,0);
		hangerForceX = variables.get(14,0);
		hangerForceY = variables.get(15,0);
		pivotForceX = variables.get(16,0);
		pivotForceY = variables.get(17,0);
		
		if (phase == 1) {
			normalForce = variables.get(10,0);
		} else {
			projAccY = variables.get(10,0);
		}
		
		if (normalForce <= 0) {
			phase = 2;
		}
		
	}
	
	
	public void timeStep() {
		
		// Solve Matrices for current time step
		buildMatricies();
		solveMatricies();
		
		// calculate next time step values
		double mainArmAngleNext = getMainArmAngle() + mainArmAngVel*timeStep + 0.5*mainArmAngAcc*Math.pow(timeStep,2);
		double mainArmOmegaNext = mainArmAngVel + mainArmAngAcc*timeStep;
		
		double hangerAngleNext = getHangerAngle() + hangerAngVel*timeStep + 0.5*hangerAngAcc*Math.pow(timeStep,2);
		double hangerOmegaNext = hangerAngVel + hangerAngAcc*timeStep;
		
		double slingAngleNext = getSlingAngle() + slingAngVel*timeStep + 0.5*slingAngAcc*Math.pow(timeStep,2);
		double slingOmegaNext = slingAngVel + slingAngAcc*timeStep;
		
		double projVelX = projVel.x() + projAccX*timeStep;
		double projVelY = projVel.y() + projAccY*timeStep;
		double projVelMag = Math.sqrt(Math.pow(projVelX,2) + Math.pow(projVelY,2));
		
		double cwVelX = cwVel.x() + cwAccX*timeStep;
		double cwVelY = cwVel.y() + cwAccY*timeStep;
		double cwVelMag = Math.sqrt(Math.pow(cwVelX,2) + Math.pow(cwVelY,2));
		
		
		setMainArmAngle(mainArmAngleNext,angleUnit.RAD);
		mainArmAngVel = mainArmOmegaNext;
		
		setHangerAngle(hangerAngleNext,angleUnit.RAD);
		hangerAngVel = hangerOmegaNext;
		
		setSlingAngle(slingAngleNext,angleUnit.RAD);
		slingAngVel = slingOmegaNext;
		
		throwingArm.setAngle(getMainArmAngle(),angleUnit.RAD);
		cwArm.setAngle((getMainArmAngle()+PI),angleUnit.RAD);
		cwHanger.setAngle(getHangerAngle(),angleUnit.RAD);
		sling.setAngle(getSlingAngle(),angleUnit.RAD);
		projVel.setMag(projVelMag);
		projVel.setUnitVectors((projVelX/projVelMag),(projVelY/projVelMag));
		cwVel.setMag(cwVelMag);
		cwVel.setUnitVectors((cwVelX/cwVelMag),(cwVelY/cwVelMag));
		
		projPosition = throwingArm.add(sling);
		cwPosition = cwArm.add(cwHanger);
		
		// update Time
		currentTime = nextTime;
		nextTime = currentTime + timeStep;
	}

	public void printCurrentMatricies() {
		System.out.println("Coefficients matrix: ");
		PrintMatrix.print(coefficients);
		System.out.println("Constants matrix: ");
		PrintMatrix.print(constants);
		System.out.println("Variables matrix: ");
		PrintMatrix.print(variables);
	}
	
	
// getters for all variables, setter only for time step.
	public int getTimeStepCount() {
		return timeStepCount;
	}
	public double getTimeStep() {
		return timeStep;
	}
	public double getCurrentTime() {
		return currentTime;
	}
	public double getNextTime() {
		return nextTime;
	}
	public double getMainArmAngVel() {
		return mainArmAngVel;
	}
	public double getHangerAngVel() {
		return hangerAngVel;
	}
	public double getSlingAngVel() {
		return slingAngVel;
	}
	public double getMainArmAngAcc() {
		return mainArmAngAcc;
	}
	public double getHangerAngAcc() {
		return hangerAngAcc;
	}
	public double getSlingAngAcc() {
		return slingAngAcc;
	}
	public double getCwForceX() {
		return cwForceX;
	}
	public double getCwForceY() {
		return cwForceY;
	}
	public double getHangerForceX() {
		return hangerForceX;
	}
	public double getHangerForceY() {
		return hangerForceY;
	}
	public double getPivotForceX() {
		return pivotForceX;
	}
	public double getPivotForceY() {
		return pivotForceY;
	}
	public double getTensionForce() {
		return tensionForce;
	}
	public double getNormalForce() {
		return normalForce;
	}
	public double getCwAccX() {
		return cwAccX;
	}
	public double getCwAccY() {
		return cwAccY;
	}
	public double getHangerAccX() {
		return hangerAccX;
	}
	public double getHangerAccY() {
		return hangerAccY;
	}
	public double getMainArmAccX() {
		return mainArmAccX;
	}
	public double getMainArmAccY() {
		return mainArmAccY;
	}
	public double getProjAccX() {
		return projAccX;
	}
	public double getProjAccY() {
		return projAccY;
	}
	public static double getGrav() {
		return GRAV;
	}
	public Matrix getA() {
		return coefficients;
	}
	public Matrix getvariables() {
		return variables;
	}
	public Matrix getConstants() {
		return constants;
	}
}