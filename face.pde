//off of OPENCV face detection example

import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture video;
OpenCV opencv;

float faceArea;
float highestArea;
int highestFace;
PVector facePos;

void setupFace() {

  video = new Capture(this, width/2, height/2);
  opencv = new OpenCV(this, width/2, height/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
  facePos = new PVector(0, 0);
  video.start();
}

void face() {
  scale(2);
  opencv.loadImage(video);
  pushMatrix();
  scale(-1, 1);
  translate(-video.width, 0); 

  image(video, 0, 0 );
  popMatrix(); 
  noFill();
  stroke(0, 255, 0);
  strokeWeight(3);
  Rectangle[] faces = opencv.detect();
  int[] faceArea = new int[faces.length];  
  // println("faces.length = "+ faces.length);
  //picks largest square (face) in camera image cause sometimes camera finds little faces in the brick wall
  if (faces.length >1) {
    float oldFaceArea = 0;
    for (int i = 0; i < faces.length; i++) {
      faceArea[i] = faces[i].width * faces[i].height;
      if ( faceArea[i] > oldFaceArea) {
        highestArea = faceArea[i];
        highestFace = i;
      }
      oldFaceArea = faceArea[i];
    }
    if (mode == rainMode) {
      //this works on the camera image
      pushMatrix();
      scale(-1, 1);
      translate(-video.width, 0); 
      //draws a nice green box around face
      rect(faces[highestFace].x, faces[highestFace].y, faces[highestFace].width, faces[highestFace].height);
      popMatrix();
    }

    //facePos is for the rest of the program in relation to the screen 
    //this part was confusing... probaby a much better way to do this
    //but this is where the vector is recorded so other parts of the code can use it

    facePos = new PVector(
    .5*map(video.width-(faces[highestFace].x + faces[highestFace].width/2), 0, video.width, 0, 1028), 
    .5*map((faces[highestFace].y+ faces[highestFace].height/2), 0, video.height, 0, 768)
      );
  }
}

void captureEvent(Capture c) {
  c.read();
}

