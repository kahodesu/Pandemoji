import ddf.minim.*;

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

void setup() {
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

void draw() {
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

