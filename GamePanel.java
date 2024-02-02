import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Padel padel1;
    Padel padel2;
    Ball ball;
    Score score;
    GamePanel(){
        newPadels();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall(){
        random = new Random();
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPadels(){

        padel1 = new Padel(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT,1);
        padel2 = new Padel(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT,2);


    }

    public void paint(Graphics g){
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);

    }

    public void draw(Graphics g){
        padel1.draw(g);
        padel2.draw(g);
        ball.draw(g);
        score.draw(g);
    }

    public void move(){
        padel1.move();
        padel2.move();
        ball.move();
    }

    public void checkCollision(){

        // bounce ball
        if(ball.y <= 0){
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.y >= GAME_HEIGHT-BALL_DIAMETER){
            ball.setYDirection(-ball.yVelocity);
        }
        // bounces ball off padels

        if(ball.intersects(padel1)){
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if(ball.yVelocity > 0)
                ball.yVelocity++;
            else
                ball.yVelocity--;
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if(ball.intersects(padel2)){
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if(ball.yVelocity > 0)
                ball.yVelocity++;
            else
                ball.yVelocity--;
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        //stops padels at window edges
        if(padel1.y <= 0)
            padel1.y = 0;
        if(padel1.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
            padel1.y = GAME_HEIGHT-PADDLE_HEIGHT;
        if(padel2.y <= 0)
            padel2.y = 0;
        if(padel2.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
            padel2.y = GAME_HEIGHT-PADDLE_HEIGHT;

        // give player 1 point and creates new padels and ball

        if(ball.x <= 0){
            score.player2++;
            newPadels();
            newBall();
            System.out.println("Player 2: " + score.player2);
        }

        if (ball.x >= GAME_WIDTH - BALL_DIAMETER){
            score.player1++;
            newPadels();
            newBall();
            System.out.println("Player 1: " + score.player1);

        }

    }

    public void run(){
        //un game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while(true){
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if(delta >=1){
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }

    }

    public class AL extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            padel1.KeyPressed(e);
            padel2.KeyPressed(e);
        }
        public void keyReleased(KeyEvent e){
            padel1.KeyReleased(e);
            padel2.KeyReleased(e);
        }
    }
}
