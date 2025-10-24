package tetristela;

import java.awt.Color;
import java.awt.Graphics;

public interface PecaDoJogo {
    
    void update();
    void render(Graphics g);
    void rotacionar();

    // Métodos de controle
    void speedUp();
    void speedDown();
    void moverDi();
    void moverEs();
    void hardDrop();
    
    // Métodos de estado/info
    boolean hasColisao();
    int getX();
    int getY();
    int[][] getCoords();
    
    // Métodos adicionais essenciais para o jogo e decoradores
    Color getColor();
    void setY(int newY); // Usado para Hard Drop

    void setX(int newX);

     // Métodos para o Dominó
    int[][] getNumerosDomino();  // Matriz de números (0-6)
    boolean temBucha();          // Verifica se peça tem números iguais
    int[] getSequencia();        // Retorna sequência de números
}