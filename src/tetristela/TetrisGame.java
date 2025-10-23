package tetristela;

import javax.swing.*;
import java.awt.*;

public class TetrisGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 700);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            
            menuPrincipal menu = new menuPrincipal(frame);
            frame.setContentPane(menu);
            frame.setVisible(true);
        });
    }
}