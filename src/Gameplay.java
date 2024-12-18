import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {

    private final int[] snakexlength = new int[750];
    private final int[] snakeylength = new int[750];

    private boolean right = false, left = false, up = false, down = false;

    private final ImageIcon rightmouth, leftmouth, upmouth, downmouth, snakeimage, enemyimage, enemy2image, titleImage;

    private final int[] enemyxpos = {25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 
                                     450, 475, 500, 525, 550, 575, 600, 625, 650, 675, 700, 725, 750, 775, 800, 825, 850};

    private final int[] enemyypos = {75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 
                                     500, 525, 550, 575, 600, 625};

    private final Random random = new Random();
    private int xpos = random.nextInt(34);
    private int ypos = random.nextInt(23);
    
    // Special food position
    private int specialXpos, specialYpos;
    private boolean specialFoodVisible = false;

    private final Timer timer;
    private final int delay = 100;

    private int lengthofsnake = 3;
    private int moves = 0;
    private int scores = 0;
    private int foodCounter = 0; 

    public Gameplay() {
        setFocusable(true);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(true);
        right = true;

        // Load images
        rightmouth = loadImage("/resources/rightmouth.png");
        leftmouth = loadImage("/resources/leftmouth.png");
        upmouth = loadImage("/resources/upmouth.png");
        downmouth = loadImage("/resources/downmouth.png");
        snakeimage = loadImage("/resources/snakeimage.png");
        titleImage = loadImage("/resources/snaketitle.jpg");
        enemyimage = loadImage("/resources/enemy.png");
        
        ImageIcon originalEnemy2 = loadImage("/resources/enemy2.png");
        if (originalEnemy2 != null) {
            Image resizedEnemy2 = originalEnemy2.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            enemy2image = new ImageIcon(resizedEnemy2);
        } else {
            enemy2image = null;
        }

        timer = new Timer(delay, this);
        timer.start();
        
        generateSpecialFoodPosition();
        
        // Timer to hide special food after 10 seconds
        new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                specialFoodVisible = false; 
            }
        }).start();
    }

    private ImageIcon loadImage(String path) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(imgURL);
        } catch (Exception e) {
            System.out.println("Error loading image: " + path);
        }
        return null;
    }

    private void generateSpecialFoodPosition() {
        boolean validPosition = false;

        while (!validPosition) {
            specialXpos = random.nextInt(34);
            specialYpos = random.nextInt(23);
            validPosition = true;

           
            for (int i = 0; i < lengthofsnake; i++) {
                if (snakexlength[i] == enemyxpos[specialXpos] && snakeylength[i] == enemyypos[specialYpos]) {
                    validPosition = false; 
                    break;
                }
            }
        }

        specialFoodVisible = true;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void paint(Graphics g) {
        if (moves == 0) {
            snakexlength[0] = 100;
            snakexlength[1] = 75;
            snakexlength[2] = 50;

            snakeylength[0] = 100;
            snakeylength[1] = 100;
            snakeylength[2] = 100;
        }

        // Draw borders
        g.setColor(Color.white);
        g.drawRect(24, 10, 900, 55);
        if (titleImage != null) titleImage.paintIcon(this, g, 28, 1);

        g.setColor(Color.white);
        g.drawRect(24, 74, 851, 577);
        g.setColor(Color.BLACK);
        g.fillRect(25, 75, 850, 575);

        // Draw scores and length
        g.setColor(Color.WHITE);
        g.setFont(new Font("arial", Font.PLAIN, 14));
        g.drawString("Scores : " + scores, 780, 30);
        g.drawString("Length : " + lengthofsnake, 780, 50);

        // Draw snake
        for (int a = 0; a < lengthofsnake; a++) {
            if (a == 0) {
                if (right && rightmouth != null) rightmouth.paintIcon(this, g, snakexlength[a], snakeylength[a]);
                if (left && leftmouth != null) leftmouth.paintIcon(this, g, snakexlength[a], snakeylength[a]);
                if (up && upmouth != null) upmouth.paintIcon(this, g, snakexlength[a], snakeylength[a]);
                if (down && downmouth != null) downmouth.paintIcon(this, g, snakexlength[a], snakeylength[a]);
            } else {
                if (snakeimage != null) snakeimage.paintIcon(this, g, snakexlength[a], snakeylength[a]);
            }
        }

        // Draw regular food
        if (enemyimage != null) enemyimage.paintIcon(this, g, enemyxpos[xpos], enemyypos[ypos]);

        // Draw special food
        if (specialFoodVisible && enemy2image != null) {
            enemy2image.paintIcon(this, g, enemyxpos[specialXpos], enemyypos[specialYpos]);
        }

        // Game Over
        if (!timer.isRunning()) {
            g.setColor(Color.RED);
            g.setFont(new Font("arial", Font.BOLD, 50));
            g.drawString("Game Over", 300, 300);
            g.setFont(new Font("arial", Font.BOLD, 20));
            g.drawString("Press SPACE to Restart", 320, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moves++;
        moveSnake();
        checkCollision();
        repaint();
    }

    private void moveSnake() {
        for (int i = lengthofsnake - 1; i > 0; i--) {
            snakexlength[i] = snakexlength[i - 1];
            snakeylength[i] = snakeylength[i - 1];
        }

        if (right) snakexlength[0] += 25;
        else if (left) snakexlength[0] -= 25;
        else if (up) snakeylength[0] -= 25;
        else if (down) snakeylength[0] += 25;

        // Check wall collision
        if (snakexlength[0] > 850 || snakexlength[0] < 25 || snakeylength[0] > 625 || snakeylength[0] < 75) {
            timer.stop();
        }
    }

    private void checkCollision() {
        // Check self-collision
        for (int i = 1; i < lengthofsnake; i++) {
            if (snakexlength[0] == snakexlength[i] && snakeylength[0] == snakeylength[i]) {
                timer.stop();
            }
        }
    
        // Check regular food collision
        if (snakexlength[0] == enemyxpos[xpos] && snakeylength[0] == enemyypos[ypos]) {
            scores++;
            lengthofsnake++;
            foodCounter++;
            xpos = random.nextInt(34);
            ypos = random.nextInt(23);
    
            // Trigger special food after every 3 regular foods
            if (foodCounter % 3 == 0) {
                generateSpecialFoodPosition();
            }
        }
    
        // Check special food collision
        if (specialFoodVisible && snakexlength[0] == enemyxpos[specialXpos] && snakeylength[0] == enemyypos[specialYpos]) {
            scores += 3; 
            lengthofsnake++;
            specialFoodVisible = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && !left) {
            right = true; left = false; up = false; down = false;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && !right) {
            left = true; right = false; up = false; down = false;
        } else if (e.getKeyCode() == KeyEvent.VK_UP && !down) {
            up = true; down = false; left = false; right = false;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && !up) {
            down = true; up = false; left = false; right = false;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            resetGame();
        }
    }

    private void resetGame() {
        scores = 0;
        moves = 0;
        lengthofsnake = 3;
        foodCounter = 0;
        right = true; left = false; up = false; down = false;
        specialFoodVisible = false;
        xpos = random.nextInt(34);
        ypos = random.nextInt(23);
        timer.start();
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
