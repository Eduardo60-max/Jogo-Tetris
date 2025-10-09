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

    // ROTACIONA A PEÇA
    public void rotacionar() {
        int[][] novaCoords = new int[coords[0].length][coords.length];
        
        // TRANSPÕE A MATRIZ (rotação 90 graus)
        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords[0].length; j++) {
                novaCoords[j][coords.length - 1 - i] = coords[i][j];
            }
        }
        
        // VERIFICA SE A ROTAÇÃO É VÁLIDA
        if (!verificarColisao(novaCoords, x, y)) {
            coords = novaCoords;
        }
    }

    // VERIFICA COLISÃO PARA ROTAÇÃO
    private boolean verificarColisao(int[][] coordsParaVerificar, int novoX, int novoY) {
        Color[][] tabuleiro = board.getBoard();
        for (int row = 0; row < coordsParaVerificar.length; row++) {
            for (int col = 0; col < coordsParaVerificar[0].length; col++) {
                if (coordsParaVerificar[row][col] != 0) {
                    int boardX = novoX + col;
                    int boardY = novoY + row;
                    
                    // VERIFICA LIMITES
                    if (boardX < 0 || boardX >= BOARD_WIDTH || boardY >= BOARD_HEIGHT) {
                        return true;
                    }
                    
                    // VERIFICA COLISÃO COM OUTRAS PEÇAS
                    if (boardY >= 0 && tabuleiro[boardY][boardX] != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // atualiza a posição da peça
    public void update() {
        if (colisao) {
            // FIXA A PEÇA NO TABULEIRO
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
            
            // LIMPA LINHAS COMPLETAS
            board.clearLines();
            
            // CRIA NOVA PEÇA
            board.setCurrentShape();
            return;
        }

        // MOVIMENTO HORIZONTAL
        if (!(x + deltaX + coords[0].length > BOARD_WIDTH) && !(x + deltaX < 0)) {
            if (!verificarColisao(coords, x + deltaX, y)) {
                x += deltaX;
            }
        }
        deltaX = 0;

        // QUEDA AUTOMÁTICA E VERIFICA COLISÃO
        if (System.currentTimeMillis() - passarTempo > delayTempoDeMovimento) {
            if (!colideAbaixo()) {
                y++;
            } else {
                colisao = true;
            }
            passarTempo = System.currentTimeMillis();
        }
    }

    // VERIFICA COLISÃO ABAIXO
    private boolean colideAbaixo() {
        return verificarColisao(coords, x, y + 1);
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
                    
                    // BORDA NA PEÇA ATUAL
                    g.setColor(Color.WHITE);
                    g.drawRect(
                        (x + col) * BLOCK_SIZE,
                        (y + row) * BLOCK_SIZE,
                        BLOCK_SIZE,
                        BLOCK_SIZE
                    );
                    g.setColor(color);
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