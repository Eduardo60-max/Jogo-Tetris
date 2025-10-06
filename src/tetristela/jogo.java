package tetristela;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class jogo extends JPanel implements KeyListener {

    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int BLOCK_SIZE = 30;

    private static final int FPS = 60;
    private static final int delay = 1000 / FPS;

    private Timer tempo;
    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];

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
        if (currentShape == null) {
			return;
		}

        currentShape.update();
    }

    // desenha o tabuleiro e a peça atual
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // fundo preto
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        // desenha peças que ta no tabuleiro
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != null) {
                    g.setColor(board[row][col]);
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        // desenha a peça atual
        if (currentShape != null){ 
			currentShape.render(g);
		 }

        // desenha a grade
        g.setColor(Color.white);
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
    }

    // controles da peça
    @Override
    public void keyPressed(KeyEvent e) {
        if (currentShape == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN -> currentShape.speedUp();
            case KeyEvent.VK_RIGHT -> currentShape.moverDi();
            case KeyEvent.VK_LEFT -> currentShape.moverEs();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (currentShape == null) return;

        if (e.getKeyCode() == KeyEvent.VK_DOWN) currentShape.speedDown();
    }

    @Override
    @Override
public void keyTyped(KeyEvent e) {
    
}

}