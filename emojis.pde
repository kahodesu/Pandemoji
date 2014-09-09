//used the ball object array list example to make lots of emojis

ArrayList<Ball> balls;
int ballWidth = 48;
int[] posArray= {
  int(512/7), int(4*512/7), int(6*512/7), int(2*512/7), int(5*512/7), int(3*512/7)
};
int posNum = 0;
int lastBall = 0;


// Simple bouncing ball class
void setupBall() {
  // Create an empty ArrayList (will store Ball objects)
  balls = new ArrayList<Ball>();
  // Start by adding one element
  balls.add(new Ball(width/2));
}

void drawBall() {
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
    gravity = random(.05, 0.1);
    ranNum = int(random(10));
    //  println(ranNum);
    photo = loadImage (images[ranNum]);
    position = new PVector(x, y);
  }

  void move() {
    // Add gravity to speed
    speed = (speed + gravity);
    // Add speed to y location
    y = y + speed;
    position.y=y;
  }

  boolean finished() {
    if (y>height+photo.height) {
      return true;
    } else {
      return false;
    }
  }

  void display() {
    // Display the circle
    fill(0);
    image(photo, x, y-photo.height);
  }

  boolean checkCollision() {
    //   println(position.x, facePos.x, position.y, facePos.y );
    //   println(position.dist(facePos));

    if (counter >10) {
      if (position.dist(facePos)< 50.0) {
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

