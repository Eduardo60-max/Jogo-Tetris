package tetristela;

import java.awt.Color;
import java.awt.Graphics;
import static tetristela.jogo.*;

public class formato {

    private int x = 4, y = 0;
    private int normal = 600;
    private int rapido = 50;
    private int delayTempoDeMovimento = normal;
    private long passarTempo;

    private int deltaX = 0;
    private boolean colisao = false;

    private jogo board; 
    private int[][] coords;
    private Color color;

   
    public formato(int[][] coords, jogo board, Color color) {
        this.coords = coords;
        this.board = board;
        this.color = (color != null) ? color : Color.red;
        this.passarTempo = System.currentTimeMillis();
    }


    public void setx(int x) {
        this.x = x;
    }

    public void sety(int y) {
        this.y = y;
    }
   
public int[][] getCoords() {
    return coords;
}


public Color getColor() {
    return color;
}


public int getX() {
    return x;
}


public int getY() {
    return y;
}

    // atualiza a posição da peça
    public void update() {
        if (colisao) {
           
            Color[][] tabuleiro = board.getBoard();
            for (int row = 0; row < coords.length; row++) {
                for (int col = 0; col < coords[0].length; col++) {
                    if (coords[row][col] != 0) {
                        if (y + row >= 0 && y + row < BOARD_HEIGHT && x + col >= 0 && x + col < BOARD_WIDTH) {
                            tabuleiro[y + row][x + col] = color;
                        }
                    }
                }
            }
            // cria nova peça
            board.setCurrentShape();
            return;
        }

        // movimento horizontal
        if (!(x + deltaX + coords[0].length > BOARD_WIDTH) && !(x + deltaX < 0)) {
            x += deltaX;
        }
        deltaX = 0;

        // queda automática e ve colisao
        if (System.currentTimeMillis() - passarTempo > delayTempoDeMovimento) {
            if (!colideAbaixo()) {
                y++;
            } else {
                colisao = true;
            }
            passarTempo = System.currentTimeMillis();
        }
    }

    // verifica colisão abaixo
    private boolean colideAbaixo() {
        Color[][] tabuleiro = board.getBoard();
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    int novoY = y + row + 1;
                    int novoX = x + col;
                    if (novoY >= BOARD_HEIGHT) {
                        return true; 
                    }
                    if (novoY >= 0 && tabuleiro[novoY][novoX] != null) {
                        return true; 
                    }
                }
            }
        }
        return false;
    }

    
    public void render(Graphics g) {
        g.setColor(color);
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    g.fillRect(
                        (x + col) * BLOCK_SIZE,
                        (y + row) * BLOCK_SIZE,
                        BLOCK_SIZE,
                        BLOCK_SIZE
                    );
                }
            }
        }
    }

    // controles de velocidade e movimento
    public void speedUp() { 
        delayTempoDeMovimento = rapido;
     }
    public void speedDown() { 
        delayTempoDeMovimento = normal;
     }
    public void moverDi() {
         deltaX = 1; 
        }
    public void moverEs() { 
        deltaX = -1; 
    }

    // verifica se colidiu
    public boolean hasColisao() {
        return colisao;
    }
}
