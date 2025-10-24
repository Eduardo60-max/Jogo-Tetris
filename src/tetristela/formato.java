package tetristela;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static tetristela.jogo.*;

public class formato implements PecaDoJogo {

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

     // VARIÁVEIS DO DOMINÓ
    private int[][] numerosDomino;
    private Random random = new Random();

    public formato(int[][] coords, jogo board, Color color) {
        this.coords = coords;
        this.board = board;
        this.color = (color != null) ? color : Color.red;
        this.passarTempo = System.currentTimeMillis();
        
        // INICIALIZA NÚMEROS DO DOMINÓ
        this.numerosDomino = new int[coords.length][coords[0].length];
        inicializarNumerosDomino();
    }

    // MÉTODO PARA INICIALIZAR NÚMEROS DO DOMINÓ
    private void inicializarNumerosDomino() {
        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords[0].length; j++) {
                if (coords[i][j] != 0) {
                    numerosDomino[i][j] = random.nextInt(7); // 0-6
                }
            }
        }
    }

    // MÉTODOS DA INTERFACE PecaDoJogo - DOMINÓ
    @Override
    public int[][] getNumerosDomino() {
        return numerosDomino;
    }
    
    @Override
    public boolean temBucha() {
        Map<Integer, Integer> contagem = new HashMap<>();
        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords[0].length; j++) {
                if (coords[i][j] != 0) {
                    int num = numerosDomino[i][j];
                    contagem.put(num, contagem.getOrDefault(num, 0) + 1);
                    if (contagem.get(num) >= 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int[] getSequencia() {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords[0].length; j++) {
                if (coords[i][j] != 0) {
                    numeros.add(numerosDomino[i][j]);
                }
            }
        }
        return numeros.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public void setY(int newY) {
        this.y = newY;
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
    int[][] novosNumeros = new int[coords[0].length][coords.length]; // MATRIZ PARA NÚMEROS
    
    // TRANSPÕE A MATRIZ (rotação 90 graus)
    for (int i = 0; i < coords.length; i++) {
        for (int j = 0; j < coords[0].length; j++) {
            novaCoords[j][coords.length - 1 - i] = coords[i][j];
            // TRANSPÕE OS NÚMEROS TAMBÉM (MESMA ROTAÇÃO)
            novosNumeros[j][coords.length - 1 - i] = numerosDomino[i][j];
        }
    }
    
    // VERIFICA SE A ROTAÇÃO É VÁLIDA
    if (!verificarColisao(novaCoords, x, y)) {
        coords = novaCoords;
        numerosDomino = novosNumeros; // ATUALIZA NÚMEROS
    }
}
    // VERIFICA COLISÃO PARA ROTAÇÃO
   private boolean verificarColisao(int[][] coordsParaVerificar, int novoX, int novoY) {
    BlocoComNumero[][] tabuleiro = board.getBoard();
    
    for (int row = 0; row < coordsParaVerificar.length; row++) {
        for (int col = 0; col < coordsParaVerificar[0].length; col++) {
            if (coordsParaVerificar[row][col] != 0) {
                int boardX = novoX + col;
                int boardY = novoY + row;
                
                // VERIFICA LIMITES
                if (boardX < 0 || boardX >= BOARD_WIDTH || boardY >= BOARD_HEIGHT) {
                    return true;
                }
                
                // VERIFICA COLISÃO
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
        BlocoComNumero[][] tabuleiro = board.getBoard();
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    if (y + row >= 0 && y + row < BOARD_HEIGHT && x + col >= 0 && x + col < BOARD_WIDTH) {
                        // CRIA BlocoComNumero
                        tabuleiro[y + row][x + col] = new BlocoComNumero(color, numerosDomino[row][col]);
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
                // DESENHA BLOCO
                g.setColor(color);
                g.fillRect(
                    (x + col) * BLOCK_SIZE,
                    (y + row) * BLOCK_SIZE,
                    BLOCK_SIZE,
                    BLOCK_SIZE
                );
                
                // DESENHA NÚMERO PRETO POR CIMA
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                String numero = String.valueOf(numerosDomino[row][col]);
                
                int textoX = (x + col) * BLOCK_SIZE + BLOCK_SIZE/2 - 4;
                int textoY = (y + row) * BLOCK_SIZE + BLOCK_SIZE/2 + 5;
                g.drawString(numero, textoX, textoY);
                
                // BORDA
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


// HARD DROP: Cai até o fundo instantaneamente
    @Override
    public void hardDrop() {
        System.out.println("🚀 Hard Drop acionado!");
        
        // Encontra a posição Y mais baixa possível
        while (!colideAbaixo()) {
            y++;
        }
        
        // Força colisão imediata
        colisao = true;
        System.out.println("📍 Peça caiu para Y=" + y);
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

    @Override
    public void setX(int newX) {
        this.x = newX;
    }
}