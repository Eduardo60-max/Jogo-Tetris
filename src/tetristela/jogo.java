package tetristela;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class jogo extends JPanel implements KeyListener {

    private formato proximaShape;
    private final int INFO_X = BOARD_WIDTH * BLOCK_SIZE + 15;
    private final int PREVIEW_X = BOARD_WIDTH * BLOCK_SIZE + 25;
    private final int PREVIEW_Y = 180;

    private RankingManager rankingManager = new RankingManager();

    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int BLOCK_SIZE = 30;

    private static final int FPS = 60;
    private static final int delay = 1000 / FPS;

    private Timer tempo;
    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];

    // SISTEMA DE PONTUAÇÃO
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    private boolean gameOver = false;

    private Color[] colors = {
        Color.decode("#ed1c24"),
        Color.decode("#ff7f27"),
        Color.decode("#fff200"),
        Color.decode("#22b14c"),
        Color.decode("#00a2e8"),
        Color.decode("#a349a4"),
        Color.decode("#3f48cc")
    };

    private final Random random = new Random();
    private formato[] shapes = new formato[7];
    private formato currentShape;

    public jogo() {
        System.out.println("✅ Construtor do jogo chamado!");
        
        // cria as formas
        shapes[0] = new formato(new int[][]{
            {1,1,1},
            {0,1,0}
        }, this, colors[0]);

        shapes[1] = new formato(new int[][]{
            {1,1,1},
            {1,0,0}
        }, this, colors[1]);

        shapes[2] = new formato(new int[][]{
            {1,1,1},
            {0,0,1}
        }, this, colors[2]);

        shapes[3] = new formato(new int[][]{
            {0,1,1},
            {1,1,0}
        }, this, colors[3]);

        shapes[4] = new formato(new int[][]{
            {1,1,0},
            {0,1,1}
        }, this, colors[4]);

        shapes[5] = new formato(new int[][]{
            {1,1},
            {1,1}
        }, this, colors[5]);

        shapes[6] = new formato(new int[][]{
            {1,1,1,1}
        }, this, colors[6]);

        // sorteia a primeira peça
        currentShape = gerarNovaPeca();
        proximaShape = gerarNovaPeca();

        setFocusable(true);
        addKeyListener(this);

        tempo = new Timer(delay, e -> {
            update();
            repaint();
        });
        tempo.start();
    }

    // gera uma nova peça aleatória
    public formato gerarNovaPeca() {
        int i = random.nextInt(shapes.length);
        int[][] coords = shapes[i].getCoords();
        return new formato(coords, this, shapes[i].getColor());
	}

  private void update() {
    if (gameOver) {

        if (rankingManager.ehHighScore(score)) {
            mostrarTelaHighScore();
        }
        return;
    }

    if (currentShape == null) {
        return;
    }

    currentShape.update();
    
    checkGameOver();
}

private void mostrarTelaHighScore() {
    tempo.stop();
    
    String iniciais = (String) JOptionPane.showInputDialog(
        this,
        " NOVO HIGH SCORE! \n" +
        "Pontuação: " + score + "\n\n" +
        "Digite suas 3 iniciais:",
        "HIGH SCORE - NOVO RECORDE!",
        JOptionPane.INFORMATION_MESSAGE,
        null,
        null,
        "AAA" 
    );
    
    if (iniciais != null && !iniciais.trim().isEmpty()) {

        if (iniciais.length() > 3) {
            iniciais = iniciais.substring(0, 3);
        }
        iniciais = iniciais.toUpperCase();
        
        // Adiciona ao ranking
        rankingManager.adicionarScore(iniciais, score);
        
        System.out.println(" High score salvo: " + iniciais + " - " + score);
    }
    
    voltarAoMenu();
}


    // VERIFICA SE O JOGO ACABOU
private void checkGameOver() {
    for (int col = 0; col < BOARD_WIDTH; col++) {
        if (board[0][col] != null) {
            gameOver = true;
            tempo.stop();
            System.out.println(" GAME OVER! Pontuação: " + score);
            
            System.out.println(" Verificando se é high score...");
            boolean ehHigh = rankingManager.ehHighScore(score);
            System.out.println(" É high score? " + ehHigh);
            
            if (ehHigh) {
                System.out.println(" Chamando tela de high score...");
                mostrarTelaHighScore();
            } else {
                System.out.println(" Não é high score, voltando ao menu...");
                JOptionPane.showMessageDialog(this, 
                    "Game Over!\nPontuação: " + score, 
                    "Fim de Jogo", 
                    JOptionPane.INFORMATION_MESSAGE);
                voltarAoMenu();
            }
            break;
        }
    }
}

    // LIMPA LINHAS COMPLETAS
    public void clearLines() {
        int linesRemoved = 0;
        
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            boolean lineComplete = true;
            
            // VERIFICA SE A LINHA ESTÁ COMPLETA
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == null) {
                    lineComplete = false;
                    break;
                }
            }
            
            // SE COMPLETA, REMOVE A LINHA
            if (lineComplete) {
                linesRemoved++;
                
                // MOVIMENTA TODAS AS LINHAS ACIMA PARA BAIXO
                for (int r = row; r > 0; r--) {
                    for (int col = 0; col < BOARD_WIDTH; col++) {
                        board[r][col] = board[r-1][col];
                    }
                }
                
                // LIMPA A LINHA DO TOPO
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    board[0][col] = null;
                }
                
                // VOLTA UMA LINHA PARA VERIFICAR NOVAMENTE (pois as linhas desceram)
                row++;
            }
        }
        
        // ATUALIZA PONTUAÇÃO
        if (linesRemoved > 0) {
            updateScore(linesRemoved);
            linesCleared += linesRemoved;
            level = (linesCleared / 10) + 1; // A CADA 10 LINHAS, SOBE NIVEL
        }
    }

    // ATUALIZA PONTUAÇÃO
    private void updateScore(int lines) {
        switch (lines) {
            case 1 -> score += 100 * level;
            case 2 -> score += 300 * level;
            case 3 -> score += 500 * level;
            case 4 -> score += 800 * level; // TETRIS!
        }
    }

    // desenha o tabuleiro e a peça atual
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // fundo preto
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        // INFORMAÇÕES DO JOGO
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Pontuação: " + score, INFO_X, 30);
            g.drawString("Linhas: " + linesCleared, INFO_X, 60);
            g.drawString("Nível: " + level, INFO_X, 90);
        
        // PRÉ-VISUALIZAÇÃO
        if (proximaShape != null && !gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("PRÓXIMA:", PREVIEW_X, PREVIEW_Y - 20);
            desenharPreview(g);
        }
        
        // DESENHA PEÇA FANTASMA
        if (currentShape != null && !gameOver) {
            desenharFantasma(g);
        }

        // MENSAGEM DE GAME OVER
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", 50, BOARD_HEIGHT * BLOCK_SIZE / 2);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Pontuação Final: " + score, 70, BOARD_HEIGHT * BLOCK_SIZE / 2 + 40);
            g.drawString("Pressione ESC para voltar", 40, BOARD_HEIGHT * BLOCK_SIZE / 2 + 80);
            return;
        }

        // desenha peças que ta no tabuleiro
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != null) {
                    g.setColor(board[row][col]);
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    
                    // BORDA NAS PEÇAS
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        // desenha a peça atual
        if (currentShape != null){ 
			currentShape.render(g);
		 }

        // desenha a grade
        g.setColor(Color.GRAY);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            g.drawLine(0, row * BLOCK_SIZE, BOARD_WIDTH * BLOCK_SIZE, row * BLOCK_SIZE);
        }
        for (int col = 0; col <= BOARD_WIDTH; col++) {
            g.drawLine(col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE);
        }
    }

    // MÉTODO PARA DESENHAR FANTASMA
    private void desenharFantasma(Graphics g) {
        int fantasmaY = calcularPosicaoFantasma();
        int[][] coords = currentShape.getCoords();
        Color cor = currentShape.getColor();
        
        // Cria cor fantasma (transparente)
        Color corFantasma = new Color(
            cor.getRed(), cor.getGreen(), cor.getBlue(), 80 // 80 = 31% de opacidade
        );
        
        g.setColor(corFantasma);
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    g.fillRect(
                        (currentShape.getX() + col) * BLOCK_SIZE,
                        (fantasmaY + row) * BLOCK_SIZE,
                        BLOCK_SIZE,
                        BLOCK_SIZE
                    );
                    
                    // Borda fantasma
                    g.setColor(new Color(255, 255, 255, 50));
                    g.drawRect(
                        (currentShape.getX() + col) * BLOCK_SIZE,
                        (fantasmaY + row) * BLOCK_SIZE,
                        BLOCK_SIZE,
                        BLOCK_SIZE
                    );
                }
            }
        }
    }


    private void desenharPreview(Graphics g) {
    int[][] coords = proximaShape.getCoords();
    Color cor = proximaShape.getColor();
    
    int tamanhoBlocoPreview = 20;
    int offsetX = PREVIEW_X + (50 - coords[0].length * tamanhoBlocoPreview) / 2;
    int offsetY = PREVIEW_Y + (50 - coords.length * tamanhoBlocoPreview) / 2;
    
    g.setColor(cor);
    for (int row = 0; row < coords.length; row++) {
        for (int col = 0; col < coords[0].length; col++) {
            if (coords[row][col] != 0) {
                g.fillRect(
                    offsetX + col * tamanhoBlocoPreview,
                    offsetY + row * tamanhoBlocoPreview, 
                    tamanhoBlocoPreview, 
                    tamanhoBlocoPreview
                );
                
                // Borda
                g.setColor(Color.WHITE);
                g.drawRect(
                    offsetX + col * tamanhoBlocoPreview,
                    offsetY + row * tamanhoBlocoPreview,
                    tamanhoBlocoPreview,
                    tamanhoBlocoPreview
                );
                g.setColor(cor);
            }
        }
    }
}

    //CALCULA POSIÇÃO DA PEÇA FANTASMA
    private int calcularPosicaoFantasma() {
        if (currentShape == null) return currentShape.getY();
        
        int fantasmaY = currentShape.getY();
        int[][] coords = currentShape.getCoords();
        
        // Encontra a posição Y onde a peça vai cair
        while (fantasmaY + coords.length < BOARD_HEIGHT) {
            if (verificarColisaoFantasma(coords, currentShape.getX(), fantasmaY + 1)) {
                break;
            }
            fantasmaY++;
        }
        return fantasmaY;
    }

    //VERIFICA COLISÃO PARA FANTASMA
    private boolean verificarColisaoFantasma(int[][] coords, int x, int y) {
        Color[][] tabuleiro = board;
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    int boardX = x + col;
                    int boardY = y + row;
                    
                    if (boardY >= BOARD_HEIGHT || boardX < 0 || boardX >= BOARD_WIDTH) {
                        return true;
                    }
                    
                    if (boardY >= 0 && tabuleiro[boardY][boardX] != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Color[][] getBoard() {
        return board;
    }

    // cria nova peça no topo
public void setCurrentShape() {
    currentShape = proximaShape;  
    proximaShape = gerarNovaPeca(); 
    
    // VERIFICA SE NOVA PEÇA PODE SER COLOCADA (GAME OVER)
    if (currentShape.hasColisao()) {
        gameOver = true;
    }
}
    // VOLTAR AO MENU
public void voltarAoMenu() {
    tempo.stop();
    
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
    if (frame != null) {
        frame.getContentPane().removeAll();
        frame.setContentPane(new menuPrincipal(frame));
        frame.revalidate();
        frame.repaint();
        
        frame.removeKeyListener(this);
    }
}

    // CONTROLES DA PEÇA
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                voltarAoMenu();
            }
            return;
        }

        if (currentShape == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN -> currentShape.speedUp();
            case KeyEvent.VK_RIGHT -> currentShape.moverDi();
            case KeyEvent.VK_LEFT -> currentShape.moverEs();
            case KeyEvent.VK_UP -> currentShape.rotacionar();
            case KeyEvent.VK_ESCAPE -> voltarAoMenu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (currentShape == null) return;

        if (e.getKeyCode() == KeyEvent.VK_DOWN) currentShape.speedDown();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Não usado
    }
}