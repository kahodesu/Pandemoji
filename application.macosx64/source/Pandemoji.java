import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import gab.opencv.*; 
import processing.video.*; 
import java.awt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Pandemoji extends PApplet {



//SOUND VARS
Minim minim;
AudioPlayer countDown, song;
AudioSample feelings, pandas, choose;

//TIME VARS: should've used millis()
int counter; 
Integer poseStart; //when pose mode starts
String timestamp = "";

//MODE/STATE VARS
int startMode = 0;
int rainMode = 1;
int poseMode = 2;
int photoMode = 3;
int archiveMode = 4;
int mode;

//IMAGE VARS
PImage poseEmoji, face, instructions1, instructions2, bg, gamePhoto; 
PImage[] emojis = new PImage[11];
String[] images= {
  "pandemojis_WEB-01_SMALL.png", 
  "pandemojis_WEB-02_SMALL.png", 
  "pandemojis_WEB-03_SMALL.png", 
  "pandemojis_WEB-04_SMALL.png", 
  "pandemojis_WEB-05_SMALL.png", 
  "pandemojis_WEB-06_SMALL.png", 
  "pandemojis_WEB-07_SMALL.png", 
  "pandemojis_WEB-08_SMALL.png", 
  "pandemojis_WEB-09_SMALL.png", 
  "pandemojis_WEB-10_SMALL.png", 
  "pandemojis_WEB-11_SMALL.png"
};

public void setup() {
  size(1024, 768);
  frameRate(60);

  setupBall();
  setupFace();

  minim = new Minim(this);
  song = minim.loadFile("Finders Keepers.mp3");
  countDown = minim.loadFile("321_TWENTY.wav");
  feelings = minim.loadSample( "Feelings.mp3");
  pandas= minim.loadSample( "Panda a.mp3");
  choose= minim.loadSample( "Choose A Feeling.mp3");
  choose.trigger();

  bg = loadImage ("BACKGROUND_SMALL.png");
  instructions1 = loadImage("instructions1.png");
  instructions2 = loadImage("instructions2.png");
  gamePics = new String[0];
  for (int i=0; i<images.length; i++) {
    emojis[i] = loadImage (images[i]);
  }

  //song.loop();
  counter = 0;
  mode = rainMode;
}

public void draw() {
  counter++;
  background(0);

  if (mode == rainMode) {
    face();
    drawBall();
    image(bg, 0, 0);
  } else if (mode == poseMode) {

    face();
    imageMode(CENTER);
    image(poseEmoji, facePos.x, facePos.y, 200, 200);
    imageMode(CORNER);
    //println(counter); 
    if (counter >10 && counter<20) {
      image(instructions2, 0, 0);
    }

    if (!countDown.isPlaying()) {
      ///take a screen shot and name it
      timestamp = "";
      timestamp = (hour() +"_"+ minute()+"_"+second()+"_"+month()+"_"+day()+"_"+ year());
      saveFrame(timestamp+".png"); 
      saveFrame("gamephoto.png"); 
      gamePhoto= loadImage("gamephoto.png");
      gamePics = append(gamePics, timestamp+".png");
      counter = 0;
      mode = photoMode;
    } 
    image(bg, 0, 0);
  } else if (mode == photoMode) {
    if (counter <250) {
      image(gamePhoto, 0, 0); //show the game photo only
    } else {

      a = loadImage(gamePics[gamePics.length-1]);
      if (gamePics.length >1) {//in case there is only one pic so far
        b = loadImage(gamePics[gamePics.length-2]);
      }  
      if (gamePics.length >2) {
        c = loadImage(gamePics[gamePics.length-3]);
      }  
      counter= 0; 
      feelings.trigger();     
      mode = archiveMode;
    }
  } else if (mode == archiveMode) {
    if (counter<250) {
      displayPics();//show the archive
    } else { //This part resets everything to start, might have some redundant stuff in it
      choose.trigger();
      println(gamePics[gamePics.length-1]);
      setupBall();
      setupFace();
      mode = rainMode;
      for (int i=0; i<images.length; i++) {
        emojis[i] = loadImage (images[i]);
      }
      bg = loadImage ("BACKGROUND_SMALL.png");
      instructions1 = loadImage("instructions1.png");
      instructions2 = loadImage("instructions2.png");

      minim = new Minim(this);
      countDown = minim.loadFile("321_TWENTY.wav");
      counter = 0;
    }
  }
}

//this is the archive mode function where the last 3 photos are shown

String[] gamePics; 
PImage a;
PImage b;
PImage c;

public void  displayPics() {// display max 7?

  //println(gamePics);
   imageMode(CENTER);
  background(0);
  image(a, 2*width/5-200, 368/2, 400, 300);

  if (gamePics.length >1) {
    image(b, 4*width/5-200, 368/2, 400, 300);
  }
  if (gamePics.length >2) {
    image(c, width/2-200, 2*368/3, 400, 300);
  }
  imageMode(CORNER);
}

//used the ball object array list example to make lots of emojis

ArrayList<Ball> balls;
int ballWidth = 48;
int[] posArray= {
  PApplet.parseInt(512/7), PApplet.parseInt(4*512/7), PApplet.parseInt(6*512/7), PApplet.parseInt(2*512/7), PApplet.parseInt(5*512/7), PApplet.parseInt(3*512/7)
};
int posNum = 0;
int lastBall = 0;


// Simple bouncing ball class
public void setupBall() {
  // Create an empty ArrayList (will store Ball objects)
  balls = new ArrayList<Ball>();
  // Start by adding one element
  balls.add(new Ball(width/2));
}

public void drawBall() {
  // This is because we are deleting elements from the list  
  for (int i = balls.size ()-1; i >= 0; i--) { 
    // An ArrayList doesn't know what it is storing so we have to cast the object coming out
    Ball ball = balls.get(i);
    ball.move();
    ball.display();
    if (ball.checkCollision()) {
      poseStart= millis();
      countDown.play();
      counter = 0;
      mode = poseMode;
    }
    if (ball.finished()) {
      // Items can be deleted with remove()
      balls.remove(i);
    }
  }

  if (balls.size()<10 && millis()-lastBall>2000) {
    posNum++;
    if (posNum>5)
      posNum = 0;
    balls.add(new Ball(posArray[posNum]));
    lastBall = millis();
  }
}


class Ball {

  float x;
  float y;
  float speed;
  float gravity;

  float life = 255;
  String imageName;
  PImage photo;
  PVector position;
  float distance;
  int ranNum;

  Ball(float tempX) {
    x = tempX;
    y = 0;
    speed = 15;
    gravity = random(.05f, 0.1f);
    ranNum = PApplet.parseInt(random(10));
    //  println(ranNum);
    photo = loadImage (images[ranNum]);
    position = new PVector(x, y);
  }

  public void move() {
    // Add gravity to speed
    speed = (speed + gravity);
    // Add speed to y location
    y = y + speed;
    position.y=y;
  }

  public boolean finished() {
    if (y>height+photo.height) {
      return true;
    } else {
      return false;
    }
  }

  public void display() {
    // Display the circle
    fill(0);
    image(photo, x, y-photo.height);
  }

  public boolean checkCollision() {
    //   println(position.x, facePos.x, position.y, facePos.y );
    //   println(position.dist(facePos));

    if (counter >10) {
      if (position.dist(facePos)< 50.0f) {
        //println("HIT!!");
        pandas.trigger();
        poseEmoji = emojis[ranNum];
        return true;
      } else {
        return false;
      }
    }
    return false;
  }
}  

//off of OPENCV face detection example





Capture video;
OpenCV opencv;

float faceArea;
float highestArea;
int highestFace;
PVector facePos;

public void setupFace() {

  video = new Capture(this, width/2, height/2);
  opencv = new OpenCV(this, width/2, height/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);  
  facePos = new PVector(0, 0);
  video.start();
}

public void face() {
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
    .5f*map(video.width-(faces[highestFace].x + faces[highestFace].width/2), 0, video.width, 0, 1028), 
    .5f*map((faces[highestFace].y+ faces[highestFace].height/2), 0, video.height, 0, 768)
      );
  }
}

public void captureEvent(Capture c) {
  c.read();
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "Pandemoji" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
