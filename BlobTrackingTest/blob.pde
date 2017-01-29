/**
 * @author Fabio Colacio & Nate McConnell
 *
 * This is the code for a blob. It is simply two sets of coordinates
 * defining a rectangle, with a means to expand itself to include extra pixels. 
 */
public class Blob{

  /** 
   * Coordinates for the rectangle
   * left defines the x-value for the top-left coorinate
   * top defines the y-value for the top-left coordinate
   * right defines the x-value for the bottom-right coordinate
   * bottom defines the y-value for the bottom-right coordinate
   */
  private float left, top, right, bottom;
  
  /** 
   * This value determines how far something can be from the
   * CENTER of the blob, to still be considered part of that blob.
   * See the isNear() method below, and its applications in the draw() function.
   */
  private float distanceThresh;
  
  /** Defines the minimum size a blob can be to still be shown on the screen. */
  private float minShowSize = 100;

  /**
   * Each blob starts off as a single pixel, and more pixels are added to it
   * as the draw() method loops through every pixel of the image.
   * distanceThresh defaults to 5000
   *
   * @param x The x-value of the initial blob pixel
   * @param y The y-value of the initial blob pixel
   */
  public Blob(float x, float y){
    left = x;
    top = y;
    right = x;
    bottom = y;
    distanceThresh = 5000;
  }
  
  /**
   * @return The area of the rectangular blob.
   */
  public float getArea(){
    return (right - left) * (bottom - top);
  }
  
  /**
   * This method takes and x,y coordinate pair, and expands the
   * bounds of the blob to include that coordinate pair if it does not already.
   *
   * The min() function returns the smaller of the two parameters.
   * The max() function returns the larger of the two parameters.
   *
   * @param x The x-value of the pixel to add to this blob.
   * @param y The y-value of the pixel to add to this blob.
   */
  public void add(float x, float y){
    left = min(left, x);
    top = min(top, y);
    right = max(right, x);
    bottom = max(bottom, y);
  }
  
  /**
   * @return the x-value of the center of the rectangle
   */
  public float centerX(){
    return (left + right) / 2;
  }
  
  /**
   * @return the y-value of the center of the rectangle
   */
  public float centerY(){
    return (top + bottom) / 2;
  }
  
  /**
   * @param x The x-value of the pixel to check for proximity
   * @param y The y-value of the pixel to check for proximity
   * @return True if the coordinate is less than the distance thresh or false otherwise.
   */
  public boolean isNear(float x, float y){
    return quadrance(centerX(), centerY(), x, y) < distanceThresh;
  }
  
  /**
   * Draws a rectangular representation of the blob.
   */
  public void show(){
    if(getArea() > minShowSize){
      stroke(#551A8B);
      fill(#551A8B);
      strokeWeight(2);
      rectMode(CORNERS);
      rect(left, top, right, bottom);
    }
  }
  
}