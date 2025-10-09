package tetristela;

import javax.swing.*;

public class janelaJogo {
    private JFrame janela;
    public static final int largura = 445, altura = 629;
    
    public janelaJogo() {
        janela = new JFrame("Tetris");
        janela.setSize(largura, altura);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setResizable(false);
        janela.setLocationRelativeTo(null);
        
        // MOSTRA MENU PRINCIPAL PRIMEIRO
        mostrarMenu();
        
        janela.setVisible(true);
    }
    
    private void mostrarMenu() {
        menuPrincipal menu = new menuPrincipal(janela);
        janela.setContentPane(menu);
        janela.revalidate();
    }
    
    public void iniciarJogo() {
        jogo game = new jogo();
        janela.setContentPane(game);
        janela.addKeyListener(game);
        game.requestFocusInWindow();
        janela.revalidate();
    }

    public static void main(String[] args) {
        new janelaJogo();
    }
}