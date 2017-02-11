package org.usfirst.frc.team4999.robot;

public class Vector2D {
	private double x;
	private double y;
	
	/**
	 * Creates a new Vector2D object.
	 *
	 * @param x The X scalar.
	 * @param y The Y scalar.
	 */
	public Vector2D(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the vector's X value.
	 */
	public double getX(){
		return x;
	}
	/**
	 * Returns the vector's Y value.
	 */
	public double getY(){
		return y;
	}
	/**
	 * Sets the vector's X value.
	 * 
	 * @param x The new X scalar.
	 */
	public void setX(double x){
		this.x = x;
	}
	/**
	 * Sets the vector's Y value.
	 * 
	 * @param y The new Y scalar.
	 */
	public void setY(double y){
		this.y = y;
	}
	/**
	 * Sets both the vector's X and Y value.
	 * 
	 * @param x The new X scalar.
	 * @param y The new Y scalar.
	 */
	public void setXY(double x, double y){
		this.x = x;
		this.y = y;
	}
	/**
	 * Adds another specified vector2d object, otherVector, to this vector.
	 * 
	 * @param otherVector The other vector to be added.
	 */
	public void add(Vector2D otherVector){
		x += otherVector.getX();
		y += otherVector.getY();
	}
	/**
	 * Subtracts another specified vector2d object, otherVector, from this vector.
	 * 
	 * @param otherVector The other vector to be subtracted.
	 */
	public void subtract(Vector2D otherVector){
		x -= otherVector.getX();
		y -= otherVector.getY();
	}
	/**
	 * Multiplies this vector's X and Y scalars with another specified scalar.
	 * 
	 * @param scalar The multiplication scalar.
	 */
	public void multiply(double scalar){
		x *= scalar;
		y *= scalar;
	}
	/**
	 * Divides this vector's X and Y scalars with another specified scalar.
	 * 
	 * @param scalar The division scalar.
	 */
	public void divide(double scalar){
		x /= scalar;
		y /= scalar;
	}
	/**
	 * Returns the distance of this vector from (0,0).
	 */
	public double getMagnitude(){
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	public void addToXY(double x, double y) {
		this.x += x;
		this.y += y;
	}
	public void addVectorWithTime(Vector2D otherVector, long t) {
		this.x += otherVector.getX() * t;
		this.y += otherVector.getY() * t;
	}
}