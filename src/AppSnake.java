import javax.swing.JFrame;

public class AppSnake {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Gameplay gameplay = new Gameplay();

        frame.setBounds(10, 10, 905, 700);
        frame.setTitle("Snake Game");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gameplay);
        gameplay.requestFocusInWindow(); 
    }
}
