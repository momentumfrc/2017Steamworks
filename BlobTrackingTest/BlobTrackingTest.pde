/**
 * @author Fabio Colacio & Nate McConnell
 *
 * This  code looks for the color white in an image, and draws rectangles
 * around blobs which are white. If you click on part of the image, it
 * will track the color of the pixel that you clicked on instead of white.
 */

import processing.video.*;

Capture cam;
/** The color to look for blobs of (white by default) */
private color trackColor;
Float error;
/** 
 * Value determines how different a color can be from the 
 * track color and still be considered part of the same blob
 */
private float colorThresh = 5;

// Croshairs

private float crosshairX;
private float crosshairY;

/** Dynamically sized array that stores every blob */
private ArrayList<Blob> blobList = new ArrayList<Blob>();

void setup() {
  //error = cam.width/2 - crosshairX;
  size(160,120);
   trackColor = color(0xFFFFFFFF);
  /** Setup the camera object */
  cam = new Capture(this,160,120);
  cam.start();
}

/** This method gets called everytime you click the mouse */
void mouseClicked(){
  trackColor = cam.pixels[mouseX + mouseY*cam.width];
}

/** This method runs in a loop after setup(), just like loop() in arduino */
void draw(){
  /** refresh the image from the camera if it is ready */
  if(cam.available())
    cam.read();
  
  /** Load the pixel data into the cam.pixels array for later access */
  cam.loadPixels();
  image(cam, 0, 0);

  /** Empty the list of blobs with each new frame */
  blobList.clear();
  
  /** Loops through every pixel of the image */
  for(int x = 0; x < cam.width; x++){
    for(int y = 0; y < cam.height; y++){
      color currentColor = cam.pixels[x + (y * cam.width)];
      float currentRed = red(currentColor);
      float currentGreen = green(currentColor);
      float currentBlue = blue(currentColor);
      float trackRed = red(trackColor);
      float trackGreen = green(trackColor);
      float trackBlue = blue(trackColor);
      /** Calculates how different the current pixel is from the color we are looking for */
      float colorDiff = quadrance(currentRed,currentGreen,currentBlue, trackRed, trackGreen, trackBlue);
      /** 
       * If it is the right color, either add it to an existing blob
       * if it is close enough, otherwise, make a new one
       */
       
       // Feels Good Man!
       
      if(colorDiff < colorThresh){
        boolean blobFound = false;
        for(Blob b : blobList){
          if(b.isNear(x,y)){
            b.add(x,y);
            blobFound = true;
            break;
          } 
        }
        
        if(!blobFound)
          blobList.add(new Blob(x,y)); 
      }
    }
  }
  
  /** Draw the green rectangle for each blob */
  for(Blob blob : blobList)
    blob.show();
    // With every blob size change, this code will run and re-find the center. This is to save processing by not running the croshair code every frame. Just when the size of a blob changes. 
    for (int i=0; i<blobList.size(); i++){
    Blob currentBlob = blobList.get(i);
    // Getting The Current Blob to then get the center. **I know, this might not be the most efficient way to do this, but it worked for me.**
     crosshairX += (currentBlob.centerX() - crosshairX); // Processing Said To Do That. Not Sure Why
      crosshairY += (currentBlob.centerY() - crosshairY); // Processing Said To Do That. Not Sure Why
      // ERROR is unused right now. Im just stroing a seperate error for something i want to try later today.
      error = (currentBlob.centerX() - crosshairX) + (currentBlob.centerY() - crosshairY); 
    }
     //drawing crosshairs
      stroke(255,0,0);
      line(crosshairX, cam.height, crosshairX, 0);
      line(0, crosshairY, cam.width, crosshairY);
      println(crosshairX / cam.width + ", " + crosshairY / cam.width);
}

/**
 * Quadrance is distance squared. We can think of colors as coordinates in a 3Dimensional space.
 * Rather than having an x, y, and z component, we have red, green, and blue components.
 * The distance formula can be used to find the exact difference between two colors, but mathematicians
 * often used distance squared (quadrance) if the values are simply used for the sake of comparison, 
 * because it yields larger numbers which are often easier to work with.
 *
 * @param x1 The first x-coordinate to use in the quadrance calculation
 * @param y1 The first y-coordinate to use in the quadrance calculation
 * @param z1 The first z-coordinate to use in the quadrance calculation
 * @param x2 The second x-coordinate to use in the quadrance calculation
 * @param y2 The second y-coordinate to use in the quadrance calculation
 * @param z2 The second z-coordinate to use in the quadrance calculation
 */
public float quadrance(float x1, float y1, float z1, float x2, float y2, float z2){
  return ((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)) + ((z2 - z1) * (z2 - z1));
}

/**
 * @param x1 The first x-coordinate to use in the quadrance calculation
 * @param y1 The first y-coordinate to use in the quadrance calculation
 * @param x2 The second x-coordinate to use in the quadrance calculation
 * @param y2 The second y-coordinate to use in the quadrance calculation
 */
public float quadrance(float x1, float y1, float x2, float y2){
  return ((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1));
}