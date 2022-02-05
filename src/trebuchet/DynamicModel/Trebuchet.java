package trebuchet.DynamicModel;

public class Trebuchet {
	
// Instance Variables
	protected double throwingArmLength;
	protected double cwArmLength;
	protected double hangerLength;
	protected double slingLength;
	protected double mainArmCgLength;
	protected double hangerCgLength;
	protected double cwMass;
	protected double projectileMass;
	protected double mainArmMass;
	protected double mainArmMoment;
	protected double hangerMass;
	protected double hangerMoment;
	protected double mainArmAngle;
	protected double hangerAngle;
	protected double slingAngle;
	
	// convenient parameters for the magnitudes of position vectors. Correct values are automatically calculated based on the rest of the geometry, and are updated when setters are called.
	protected double r01;
	protected double r04;
	protected double r05;
	protected double r12;
	protected double r13;
	protected double r14;
	protected double r23;
	protected double r45;
	protected double r56;
	
	protected Vector2D testVector = new Vector2D(5,32,angleUnit.DEG);
	
// DEFAULT UNITS: 	Length - meters		Mass - kg 		Angle - radians		Moment - kg*m^2
	private static final double DEFAULT_THROWING_ARM_LENGTH = 1;
	private static final double DEFAULT_CW_ARM_LENGTH = 0.25;
	private static final double DEFAULT_CW_HANGER_LENGTH = 0.4;
	private static final double DEFAULT_SLING_LENGTH = 1;
	private static final double DEFAULT_THROWING_ARM_CG_LENGTH = 0.15;
	private static final double DEFAULT_CW_HANGER_CG_LENGTH = 0.2; 
	private static final double DEFAULT_COUNTER_WEIGHT_MASS = 20;
	private static final double DEFAULT_PROJECTILE_MASS = 0.15;
	private static final double DEFAULT_THROWWING_ARM_MASS = 1.0;
	private static final double DEFAULT_THROWING_ARM_MOMENT = 0.15;
	private static final double DEFAULT_CW_HANGER_MASS = 0.25;
	private static final double DEFAULT_CW_HANGER_MOMNENT = 0.03;
	private static final double DEFAULT_THROWING_ARM_ANGLE = -60*Math.PI/180;
	private static final double DEFAULT_CW_ARM_ANGLE = -90*Math.PI/180;
	private static final double DEFAULT_SLING_ANGLE = -180*Math.PI/180;
	
// CONSTRUCTOR METHOD - sets all parameters to default values	
	Trebuchet() {
		throwingArmLength = DEFAULT_THROWING_ARM_LENGTH;
		cwArmLength = DEFAULT_CW_ARM_LENGTH;
		hangerLength =DEFAULT_CW_HANGER_LENGTH;
		slingLength =DEFAULT_SLING_LENGTH;
		mainArmCgLength = DEFAULT_THROWING_ARM_CG_LENGTH;
		hangerCgLength = DEFAULT_CW_HANGER_CG_LENGTH;
		cwMass =DEFAULT_COUNTER_WEIGHT_MASS;
		projectileMass =DEFAULT_PROJECTILE_MASS;
		mainArmMass = DEFAULT_THROWWING_ARM_MASS;
		mainArmMoment =DEFAULT_THROWING_ARM_MOMENT;
		hangerMass = DEFAULT_CW_HANGER_MASS;
		hangerMoment =DEFAULT_CW_HANGER_MOMNENT;
		mainArmAngle =DEFAULT_THROWING_ARM_ANGLE;
		hangerAngle =DEFAULT_CW_ARM_ANGLE;
		slingAngle =DEFAULT_SLING_ANGLE;
		r01 = cwArmLength;
		r04 = mainArmCgLength;
		r05 = throwingArmLength;
		r12 = hangerCgLength;
		r13 = hangerLength;
		r14 = cwArmLength + mainArmCgLength;
		r23 = hangerLength - hangerCgLength;
		r45 = throwingArmLength - mainArmCgLength;
		r56 = slingLength;
	}
	
	
// Getters and Setters
	public double getThrowingArmLength() {
		return throwingArmLength;
	}
	public void setThrowingArmLength(double throwingArmLength) {
		if (throwingArmLength>0) {
		this.throwingArmLength = throwingArmLength;
		updatePositionVectorMagnitudes();
		} else {System.out.println("Opperation Failed: Length must be a positive numerical value."); }
	}
	public double getCwArmLength() {
		return cwArmLength;
	}
	public void setCwArmLength(double cwArmLength) {
		if (cwArmLength>0) {
			this.cwArmLength = cwArmLength;
			updatePositionVectorMagnitudes();
		} else {System.out.println("Opperation Failed: Length must be a positive numerical value."); }
	}
	public double getHangerLength() {
		return hangerLength;
	}
	public void setHangerLength(double hangerLength) {
		if (hangerLength>0) {
			this.hangerLength = hangerLength;
			updatePositionVectorMagnitudes();
		} else {System.out.println("Opperation Failed: Length must be a positive numerical value."); }
	}
	public double getSlingLength() {
		return slingLength;
	}
	public void setSlingLength(double slingLength) {
		if (slingLength>0) {
			this.slingLength = slingLength;
			updatePositionVectorMagnitudes();
		} else {System.out.println("Opperation Failed: Length must be a positive numerical value."); }
	}
	public double getMainArmCgLength() {
		return this.mainArmCgLength;
	}
	public void setMainArmCgLength(double mainArmCgLength) {
		if (mainArmCgLength < throwingArmLength && mainArmCgLength > (-cwArmLength)) {
			this.mainArmCgLength = mainArmCgLength;
			updatePositionVectorMagnitudes();
		} else {System.out.println("Opperation Failed: Length must be a numerical value in the range [ -cwArmLength +throwingArmLength]."); }
	}
	public double getHangerCgLength() {
		return this.hangerCgLength;
	}
	public void setHangerCgLength(double hangerCgLength) {
		if (hangerCgLength < hangerLength && hangerCgLength > 0) {
			this.hangerCgLength = hangerCgLength;
			updatePositionVectorMagnitudes();
		} else {System.out.println("Opperation Failed: Length must be a positive numerical value less than the length of the counter weight hanger arm.."); }
	}
	public double getCwMass() {
		return cwMass;
	}
	public void setCwMass(double cwMass) {
		if (cwMass>0) {
			this.cwMass = cwMass;
		} else {System.out.println("Opperation Failed: Mass must be a positive numerical value."); }
	}
	public double getProjectileMass() {
		return projectileMass;
	}
	public void setProjectileMass(double projectileMass) {
		if (projectileMass>0) {
			this.projectileMass = projectileMass;
		} else {System.out.println("Opperation Failed: Mass must be a positive numerical value."); }
	}
	public double getMainArmMass() {
		return mainArmMass;
	}
	public void setMainArmMass(double mainArmMass) {
		if (mainArmMass>0) {
			this.mainArmMass = mainArmMass;
		} else {System.out.println("Opperation Failed: Mass must be a positive numerical value."); }
	}
	public double getMainArmMoment() {
		return mainArmMoment;
	}
	public void setMainArmMoment(double mainArmMoment) {
		if (mainArmMoment>0) {
			this.mainArmMoment = mainArmMoment;
		} else {System.out.println("Opperation Failed: Moment must be a positive numerical value."); }
	}
	public double getHangerMass() {
		return hangerMass;
	}
	public void setHangerMass(double hangerMass) {
		if (hangerMass>0) {
			this.hangerMass = hangerMass;
		} else {System.out.println("Opperation Failed: Mass must be a positive numerical value."); }
	}
	public double getHangerMoment() {
		return hangerMoment;
	}
	public void setHangerMoment(double hangerMoment) {
		if (hangerMoment>0) {
			this.hangerMoment = hangerMoment;
		} else {System.out.println("Opperation Failed: Moment must be a positive numerical value."); }
	}
	public double getMainArmAngle() {
		return mainArmAngle;
	}
	public void setMainArmAngle(double mainArmAngle, angleUnit unit) {
		switch (unit) {
		case DEG:
			this.mainArmAngle = mainArmAngle*Math.PI/180;
			break;
		case RAD:
			this.mainArmAngle = mainArmAngle;
			break;
		}
	}
	public double getHangerAngle() {
		return hangerAngle;
	}
	public void setHangerAngle(double hangerAngle, angleUnit unit) {
		switch (unit) {
		case DEG:
			this.hangerAngle = hangerAngle*Math.PI/180;
			break;
		case RAD:
			this.hangerAngle = hangerAngle;
			break;
		}
	}
	public double getSlingAngle() {
		return slingAngle;
	}
	public void setSlingAngle(double slingAngle, angleUnit unit) {
		switch (unit) {
		case DEG:
			this.slingAngle = slingAngle*Math.PI/180;
			break;
		case RAD:
			this.slingAngle = slingAngle;
			break;
		}
	}

// any time a setter method is called that changes a parameter which impacts the position vectors, this method is called to update the position vectors
	public void updatePositionVectorMagnitudes() {
		r01 = cwArmLength;
		r04 = mainArmCgLength;
		r05 = throwingArmLength;
		r12 = hangerCgLength;
		r13 = hangerLength;
		r14 = cwArmLength + mainArmCgLength;
		r23 = hangerLength - hangerCgLength;
		r45 = throwingArmLength - mainArmCgLength;
		r56 = slingLength;
	}
	public double getR01() {
		return r01;
	}
	public double getR04() {
		return r04;
	}
	public double getR05() {
		return r05;
	}
	public double getR12() {
		return r12;
	}
	public double getR13() {
		return r13;
	}
	public double getR14() {
		return r14;
	}
	public double getR23() {
		return r23;
	}
	public double getR45() {
		return r45;
	}
	public double getR56() {
		return r56;
	}
}
