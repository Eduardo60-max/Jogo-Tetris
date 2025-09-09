package tetristela;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class jogo extends JPanel{
	
	private Timer tempo;
    
	public jogo() {
    	tempo = new Timer(600, new ActionListener() {
    	int t = 0;	
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			System.out.println(t++);
    			
    		}
    	});
    	tempo.start();	
    		
    	
    	
    	//sim
    	
    }
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		g.drawRect(10, 10, 200, 200);
	}

}
