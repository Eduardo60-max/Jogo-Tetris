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

	public jogo() {
    	tempo = new Timer(delay, new ActionListener() {
    	int t = 0;	
    		@Override
    		public void actionPerformed(ActionEvent e) {
				if(System.currentTimeMillis() - passarTempo > delayTempoDeMovimento){
    		y++;
			passarTempo = System.currentTimeMillis();
				}
    		repaint();
    		}
    	});
    	tempo.start();	
    		
    	
    	
    	//sim
    	
    }
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.white);
		
		//desenhei a tela
		for(int row = 0; row < BOARD_HEIGHT; row++) {
			g.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
		
		}
		for(int col = 0; col < BOARD_WIDTH + 1; col++) {
			g.drawLine( col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
		
		}
		
		//DESENHO DO SHAPE
		for(int row = 0; row < shape.length; row++) {
			for(int col = 0; col < shape[0].length; col++) {
				if(shape[row][col] != null) {
				g.setColor(shape[row][col]);
			    g.fillRect(row * BLOCK_SIZE + x * BLOCK_SIZE, col * BLOCK_SIZE + y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}
		}	
		}
	}

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == KeyEvent.VK_SPACE){
			delayTempoDeMovimento = rapido;
		}
    }
 
    @Override
    public void keyReleased(KeyEvent e) {
       if(e.getKeyChar() == KeyEvent.VK_SPACE){
			delayTempoDeMovimento = normal; 
    }

}
}