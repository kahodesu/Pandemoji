//this is the archive mode function where the last 3 photos are shown

String[] gamePics; 
PImage a;
PImage b;
PImage c;

void  displayPics() {// display max 7?

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

