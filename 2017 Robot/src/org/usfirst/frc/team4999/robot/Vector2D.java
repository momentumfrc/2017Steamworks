package org.usfirst.frc.team4999.robot;

public class Vector2D {
	private double x;
	private double y;
	public Vector2D(double x, double y){
		this.x = x;
		this.y = y;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	public void setXY(double x, double y){
		this.x = x;
		this.y = y;
	}
	public void add(Vector2D otherVector){
		x += otherVector.getX();
		y += otherVector.getY();
	}
	public void subtract(Vector2D otherVector){
		x -= otherVector.getX();
		y -= otherVector.getY();
	}
	public void multiply(double scalar){
		x *= scalar;
		y *= scalar;
	}
	public void divide(double scalar){
		x /= scalar;
		y /= scalar;
	}
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