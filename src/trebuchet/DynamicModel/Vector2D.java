package trebuchet.DynamicModel;
/**
 * 
 * Vector class has parameters for vector magnitude, angle, and unit vectors, and methods to return the magnitudes of the component (x,y) vectors, and performe vector addition. 
 * 
 * @author JHECK7
 *
 */
public class Vector2D {
	
	private double mag;
	private double i;
	private double j;
	private double angle;
	
// CONSTRUCTORS
	Vector2D(){
		mag = 0;
		i = 1;
		j = 0;
		angle = 0;
	}
	Vector2D(double mag, double i, double j){
		this.mag = mag;
		
		if (Math.pow(i,2)+Math.pow(j,2)>0.9999 && Math.pow(i,2)+Math.pow(j,2)<1.0001) {
			this.i = i;
			this.j = j;
			} else {System.out.println(" Opperation Failed. Unit Vector must have a magnitude of 1."); }
		updateAngle();
	}

	Vector2D(double mag, double angle, angleUnit unit){
		this.mag = mag;
		switch (unit) {
		case DEG:
			this.angle = angle*Math.PI/180;
			break;
		case RAD:
			this.angle = angle;
			break;
		}
		this.i = Math.cos(this.angle);
		this.j = Math.sin(this.angle);
	}
	
// RETURN MAGNITUDE OF CARTISIAN COMPONENTS OF VECTOR
	public double x() {
		return this.mag*this.i;
	}
	public double y() {
		return this.mag*this.j;
	}
	
	
	public Vector2D add(Vector2D vec) {
		
		double resultX = this.x() + vec.x();
		double resultY = this.y() + vec.y();
		double resultMag = Math.sqrt(Math.pow(resultX,2) + Math.pow(resultY,2));
		
		Vector2D output = new Vector2D(resultMag,(resultX/resultMag),(resultY/resultMag));
		
		return output;
	}
	
	
	public Vector2D scalarMultiply(double val) {
		Vector2D output = new Vector2D(this.mag*val,this.angle,angleUnit.RAD);
		return output;
	}
	
	
// GETTERS AND SETTERS
	public double getMag() {
		return mag;
	}
	public void setMag(double mag) {
		if (mag>0) {
		this.mag = mag;
		} else {System.out.println(" Opperation Failed. Cannot set a negative Vector Magnitude."); }
	}
	public double getI() {
		return i;
	}
	public void setUnitVectors(double i,double j) {
		if (Math.pow(i,2)+Math.pow(j,2)>0.9999 && Math.pow(i,2)+Math.pow(j,2)<1.0001) {
		this.i = i;
		this.j = j;
		updateAngle();
		} else {System.out.println(" Opperation Failed. Unit Vector must have a magnitude of 1."); }
	}
	public double getJ() {
		return j;
	}
	
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle, angleUnit unit) {
		switch (unit) {
		case DEG:
			this.angle = angle*Math.PI/180;
			break;
		case RAD:
			this.angle = angle;
			break;
		}
		this.i = Math.cos(this.angle);
		this.j = Math.sin(this.angle);
	}
	public void updateAngle() {
		if (i<0) {
			angle = Math.atan(j/i)+Math.PI;
			} else {
				angle = Math.atan(j/i);
			}
	}
}
