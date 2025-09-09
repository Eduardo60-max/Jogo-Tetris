package tetristela;

import javax.swing.JFrame;

public class janelaJogo {
private JFrame janela;
private jogo jogo;
public static final int largura = 445, altura = 629;
  public janelaJogo(){
	  janela = new JFrame("Tetris");
	  janela.setSize(largura, altura);
	  janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  janela.setResizable(false);
	  
	  janela.setLocationRelativeTo(null);
	  
	  jogo = new jogo();
	  janela.add(jogo);
	  
	  
	  
	  janela.setVisible(true);
  }
//sim
 

public static void main(String[] args) {
 new janelaJogo();
}
}