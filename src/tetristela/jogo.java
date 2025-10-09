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

public class jogo extends JPanel implements KeyListener {

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
            return;
        }

        if (currentShape == null) {
			return;
		}

        currentShape.update();
        
        // VERIFICA GAME OVER
        checkGameOver();
    }

    // VERIFICA SE O JOGO ACABOU
    private void checkGameOver() {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[0][col] != null) {
                gameOver = true;
                tempo.stop();
                System.out.println("🎮 GAME OVER! Pontuação: " + score);
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

        // DESENHA INFORMAÇÕES DO JOGO
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Pontuação: " + score, BOARD_WIDTH * BLOCK_SIZE + 10, 30);
        g.drawString("Linhas: " + linesCleared, BOARD_WIDTH * BLOCK_SIZE + 10, 60);
        g.drawString("Nível: " + level, BOARD_WIDTH * BLOCK_SIZE + 10, 90);
        
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

    public Color[][] getBoard() {
        return board;
    }

    // cria nova peça no topo
    public void setCurrentShape() {
        currentShape = gerarNovaPeca();
        
        // VERIFICA SE NOVA PEÇA PODE SER COLOCADA (GAME OVER)
        if (currentShape.hasColisao()) {
            gameOver = true;
        }
    }
    
    // VOLTAR AO MENU
    public void voltarAoMenu() {
        tempo.stop();
        
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.add(new menuPrincipal(frame));
        frame.revalidate();
        frame.repaint();
        
        frame.removeKeyListener(this);
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