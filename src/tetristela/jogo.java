package tetristela;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class jogo extends JPanel implements KeyListener{
    private static int FPS = 60;
    private static int delay = FPS / 1000;
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int BLOCK_SIZE = 30;
    private Timer tempo;
    private Color[][] jogo = new Color[BOARD_WIDTH][BOARD_HEIGHT];
    
    private Color[][] shape = {
            {Color.RED, Color.RED, Color.RED},
            {null, Color.RED, null}
    };

    private int x = 4, y = 0;
    private int normal = 600;
    private int rapido = 50;
    private int delayTempoDeMovimento = normal;
    private long passarTempo;
    
    private int deltaX = 0;
    private boolean colisao = false;
    
    public jogo() {
        tempo = new Timer(delay, new ActionListener() {
        int t = 0;    
            @Override
            public void actionPerformed(ActionEvent e) {
                if(colisao){
                    return;
                }
                if(!(x + deltaX + shape[0].length > 10) && !(x + deltaX < 0)){
                x+= deltaX;
                }
                    deltaX = 0;
                if(System.currentTimeMillis() - passarTempo > delayTempoDeMovimento){
                    if(!(y+ 1 + shape.length > BOARD_HEIGHT)){
            y++;
                    }else {
                        colisao = true;
                    }
            passarTempo = System.currentTimeMillis();
                }
                repaint();
            }
        });
        tempo.start();    
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        //DESENHO DO SHAPE
        for(int row = 0; row < shape.length; row++) {
            for(int col = 0; col < shape[0].length; col++) {
                if(shape[row][col] != null) {
                g.setColor(shape[row][col]);
                g.fillRect(col * BLOCK_SIZE + x * BLOCK_SIZE, row * BLOCK_SIZE + y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            }
        }    
        }
        g.setColor(Color.white);
        //desenhei a tela
        for(int row = 0; row < BOARD_HEIGHT; row++) {
            g.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
        
        }
        for(int col = 0; col < BOARD_WIDTH + 1; col++) {
            g.drawLine( col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
        
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            delayTempoDeMovimento = rapido;
        }else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
             deltaX = 1;
        } else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            deltaX = -1;
        }
    }
 
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyChar() == KeyEvent.VK_SPACE){  
            delayTempoDeMovimento = normal; 
        }
    } 
}
