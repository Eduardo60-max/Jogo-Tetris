package tetristela;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class jogo extends JPanel implements KeyListener {

     // SISTEMA DE DURAÇÃO PERSONALIZADA
    private int pecasComPowerUpRestantes = 0;
    private String powerUpAtivo = null;
    private Map<String, Integer> duracaoPowerUps = new HashMap<>();

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
    private BlocoComNumero[][] board = new BlocoComNumero[BOARD_HEIGHT][BOARD_WIDTH];

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

    private boolean animandoLinha = false;
    private int frameAnimacao = 0;

    private List<Integer> linhasParaLimpar = new ArrayList<>();

    private final Random random = new Random();
    private PecaDoJogo[] shapes = new PecaDoJogo[7];
    private PecaDoJogo currentShape;
    private PecaDoJogo proximaShape;

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

        // CONFIGURA DURAÇÃO DE CADA POWER-UP (BALANCEAMENTO)
        duracaoPowerUps.put("DOMINO_DOURADO", 4);      // 🟡 MÉDIO - buchas são boas
        duracaoPowerUps.put("SEQUENCIA_PERFEITA", 5);  // 🟢 FRACO - sequências são situacionais  
        duracaoPowerUps.put("TETRIS_ABENÇOADO", 2);    // 🔴 FORTE - peças I são OP!
        duracaoPowerUps.put("INVERSOR", 3);            // 🟡 MÉDIO - debuff irritante
        duracaoPowerUps.put("SUPER_ROTACAO", 4);       // 🟡 MÉDIO - debuff controlável
        duracaoPowerUps.put("PESADELO_NUMERICO", 3);   // 🔴 FORTE - debuff muito caótico
        
        System.out.println("⚖️ Sistema de balanceamento carregado!");


        // sorteia a primeira peça
        currentShape = shapes[random.nextInt(shapes.length)];
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
    public PecaDoJogo gerarNovaPeca() {
        int i = random.nextInt(shapes.length);
        formato shapeOriginal = (formato) shapes[i];
        return new formato(shapeOriginal.getCoords(), this, shapeOriginal.getColor());
	}

    // MÉTODO PARA APLICAR POWER-UP COM DURAÇÃO PERSONALIZADA
    public void aplicarPowerUp(String tipoPowerUp) {
        if (currentShape != null && duracaoPowerUps.containsKey(tipoPowerUp)) {
            int duracao = duracaoPowerUps.get(tipoPowerUp);
            System.out.println("🔮 Aplicando " + tipoPowerUp + " por " + duracao + " peças!");
            
            // CONFIGURA SISTEMA DE DURAÇÃO (INCLUI PEÇA ATUAL!)
            pecasComPowerUpRestantes = duracao;
            powerUpAtivo = tipoPowerUp;
            
            try {
                // APLICA NA PEÇA ATUAL IMEDIATAMENTE
                if (currentShape instanceof PowerUpDecorator) {
                    // Se já tem power-up, substitui
                    PowerUpDecorator decoratorAntigo = (PowerUpDecorator) currentShape;
                    currentShape = new PowerUpDecorator(decoratorAntigo.pecaDecorada, tipoPowerUp);
                } else {
                    // Aplica diretamente
                    currentShape = new PowerUpDecorator(currentShape, tipoPowerUp);
                }
                
                // PREPARA PRÓXIMA PEÇA COM POWER-UP
                proximaShape = gerarPecaComPowerUp();
                
                System.out.println("✅ " + tipoPowerUp + " ativo! Incluindo peça atual + " + (duracao-1) + " adicionais");
                
            } catch (Exception e) {
                System.err.println("❌ ERRO: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // GERA PEÇAS ESPECIAIS BASEADAS NO POWER-UP ATIVO
    private PecaDoJogo gerarPecaComPowerUp() {
        if (powerUpAtivo == null) return gerarNovaPeca();
        
        switch (powerUpAtivo) {
            case "DOMINO_DOURADO":
                return gerarPecaBucha();
                
            case "SEQUENCIA_PERFEITA":
                return gerarPecaSequencia();
                
            case "TETRIS_ABENÇOADO":
                // GERA APENAS PEÇAS I
                return new formato(new int[][]{{1,1,1,1}}, this, colors[6]);
                
            case "PESADELO_NUMERICO":
                // PARA PESADELO, GERA PEÇA NORMAL (o efeito aplica durante o movimento)
                return gerarNovaPeca();
                
            default:
                // INVERSOR, SUPER_ROTACAO - peça normal com comportamento alterado
                return gerarNovaPeca();
        }
    }
    // GERA PEÇA COM BUCHA FORÇADA
    private PecaDoJogo gerarPecaBucha() {
        int i = random.nextInt(shapes.length);
        formato shapeOriginal = (formato) shapes[i];
        formato novaPeca = new formato(shapeOriginal.getCoords(), this, shapeOriginal.getColor());
        
        // FORÇA BUCHA
        int numeroBucha = random.nextInt(7);
        int[][] numeros = novaPeca.getNumerosDomino();
        
        for (int row = 0; row < numeros.length; row++) {
            for (int col = 0; col < numeros[0].length; col++) {
                if (numeros[row][col] != 0) {
                    numeros[row][col] = numeroBucha;
                }
            }
        }
        
        return novaPeca;
    }

    // GERA PEÇA COM SEQUÊNCIA FORÇADA
    private PecaDoJogo gerarPecaSequencia() {
        int i = random.nextInt(shapes.length);
        formato shapeOriginal = (formato) shapes[i];
        formato novaPeca = new formato(shapeOriginal.getCoords(), this, shapeOriginal.getColor());
        
        // FORÇA SEQUÊNCIA
        int[][] numeros = novaPeca.getNumerosDomino();
        int numeroInicial = random.nextInt(4); // 0-3 para caber sequência
        
        int contador = 0;
        for (int row = 0; row < numeros.length; row++) {
            for (int col = 0; col < numeros[0].length; col++) {
                if (numeros[row][col] != 0) {
                    numeros[row][col] = (numeroInicial + contador) % 7; // Garante 0-6
                    contador++;
                }
            }
        }
        
        return novaPeca;
    }

    // CALCULAR BÔNUS DE UMA LINHA ESPECÍFICA
    private int calcularBonusLinha(int linha) {
        int bonus = 0;
        
        // VERIFICA BUCHAS NA LINHA
        Map<Integer, Integer> contagemNumeros = new HashMap<>();
        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[linha][col] != null) {
                int numero = board[linha][col].numero;
                contagemNumeros.put(numero, contagemNumeros.getOrDefault(numero, 0) + 1);
            }
        }
        
        // SE TEM BUCHA (número aparece 2+ vezes)
        for (Map.Entry<Integer, Integer> entry : contagemNumeros.entrySet()) {
            if (entry.getValue() >= 2) {
                bonus += 50 * level;
                System.out.println("🎲 Bucha na linha " + linha + "! Número " + entry.getKey() + " aparece " + entry.getValue() + " vezes! +" + (50 * level));
            }
        }
        
        // VERIFICA SEQUÊNCIAS NA LINHA
        List<Integer> numerosLinha = new ArrayList<>();
        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[linha][col] != null) {
                numerosLinha.add(board[linha][col].numero);
            }
        }
        
        if (numerosLinha.size() >= 2) {
            Collections.sort(numerosLinha);
            boolean ehSequencia = true;
            for (int i = 0; i < numerosLinha.size() - 1; i++) {
                if (numerosLinha.get(i + 1) != numerosLinha.get(i) + 1) {
                    ehSequencia = false;
                    break;
                }
            }
            
            if (ehSequencia) {
                bonus += 75 * level;
                System.out.println("🎯 Sequência na linha " + linha + "! " + numerosLinha + " +" + (75 * level));
            }
        }
        
        return bonus;
    }

    // VERIFICAR SEQUÊNCIA PERFEITA
    private boolean ehSequenciaPerfeita(int[] sequencia) {
        if (sequencia.length < 2) return false;
        
        // Faz uma cópia e ordena para verificar
        int[] sorted = sequencia.clone();
        Arrays.sort(sorted);
        
        // Verifica se os números estão em ordem crescente
        for (int i = 0; i < sorted.length - 1; i++) {
            if (sorted[i + 1] != sorted[i] + 1) {
                return false;
            }
        }
        return true;
    }

  private void update() {
    if (gameOver) {

        if (rankingManager.ehHighScore(score)) {
            mostrarTelaHighScore();
        }
        return;
    }

    // DEBUG: Verifica se currentShape é PowerUpDecorator
    if (currentShape instanceof PowerUpDecorator) {
        System.out.println("⚡ PowerUpDecorator ativo no update!");
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
    linhasParaLimpar.clear();
    
    // DETECTA LINHAS COMPLETAS
    for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
        boolean lineComplete = true;
        
        for (int col = 0; col < BOARD_WIDTH; col++) {
            // VERIFICA SE TEM BLOCO (independente de ter número ou não)
            if (board[row][col] == null) {
                lineComplete = false;
                break;
            }
        }
        
        if (lineComplete) {
            linhasParaLimpar.add(row);
            System.out.println("✅ Linha " + row + " completa! Adicionada para limpeza.");
        }
    }
    
    // SE TEM LINHAS, PROCESSAR
    if (!linhasParaLimpar.isEmpty()) {
        System.out.println("🧹 Processando " + linhasParaLimpar.size() + " linhas...");
        processarRemocaoLinhas();
    } else {
        System.out.println("❌ Nenhuma linha completa encontrada.");
        calcularBonusDominoBasico();
    }
}

    // PROCESSAR REMOÇÃO DEPOIS DA ANIMAÇÃO
    
    private void processarRemocaoLinhas() {
        if (linhasParaLimpar.isEmpty()) {
            System.out.println("❌ Nada para processar - linhasParaLimpar vazia");
            return;
        }
        
        System.out.println("🔧 Iniciando processarRemocaoLinhas...");
        
        int bonusDominoTotal = 0;
        
        // ANTES DE REMOVER: VERIFICA BUCHAS/SEQUÊNCIAS NAS LINHAS
        for (int linha : linhasParaLimpar) {
            System.out.println("🔍 Analisando linha " + linha + " para buchas/sequências...");
            bonusDominoTotal += calcularBonusLinha(linha);
        }
        
        // ORDENA LINHAS PARA REMOVER DE BAIXO PARA CIMA
        Collections.sort(linhasParaLimpar);
        int linesRemoved = linhasParaLimpar.size();
        
        System.out.println("📊 Linhas para remover: " + linhasParaLimpar);
        
        for (int linha : linhasParaLimpar) {
            System.out.println("🗑️ Removendo linha " + linha);
            
            // MOVIMENTA TODAS AS LINHAS ACIMA PARA BAIXO
            for (int r = linha; r > 0; r--) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    board[r][col] = board[r-1][col];
                }
            }
            
            // LIMPA A LINHA DO TOPO
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[0][col] = null;
            }
        }
        
        // ADICIONA BÔNUS DAS LINHAS + PONTUAÇÃO NORMAL
        updateScore(linesRemoved);
        
        if (bonusDominoTotal > 0) {
            score += bonusDominoTotal;
            System.out.println("🎯 Bônus dominó das linhas: +" + bonusDominoTotal + " pontos!");
        }
        
        linesCleared += linesRemoved;
        level = (linesCleared / 10) + 1;
        
        System.out.println("🎯 Pontuação atualizada: " + score + ", Linhas: " + linesCleared);
        
        // BÔNUS DA PEÇA ATUAL (como está)
        calcularBonusDominoCompleto();
        
        linhasParaLimpar.clear();
        System.out.println("🎉 Limpeza concluída!");
    }
    
    // BÔNUS DOMINÓ COMPLETO
    private void calcularBonusDominoCompleto() {
        int bonus = 0;
        
        if (currentShape != null) {
            // BUCHA NA PEÇA ATUAL = +100 por nível
            if (currentShape.temBucha()) {
                bonus += 100 * level;
                System.out.println("🎲 BUCHA na peça atual! +" + (100 * level) + " pontos!");
            }
            
            // SEQUÊNCIA PERFEITA NA PEÇA ATUAL = +150 por nível
            int[] sequencia = currentShape.getSequencia();
            if (ehSequenciaPerfeita(sequencia)) {
                bonus += 150 * level;
                System.out.println("🎯 SEQUÊNCIA PERFEITA na peça atual! +" + (150 * level) + " pontos!");
            }
            
            // POWER-UP DOMINO DOURADO DOBRA O BÔNUS
            if (currentShape instanceof PowerUpDecorator) {
                PowerUpDecorator decorator = (PowerUpDecorator) currentShape;
                if (decorator.temBucha()) {
                    bonus *= 2;
                    System.out.println("💰 DOMINO DOURADO! Bônus dobrado!");
                }
            }
        }
        
        if (bonus > 0) {
            score += bonus;
            System.out.println("🎯 Total bônus dominó da peça atual: +" + bonus + " pontos!");
        }
    }

    // BÔNUS BÁSICO (quando não tem linhas para limpar)
    private void calcularBonusDominoBasico() {
        if (currentShape != null) {
            int bonus = 0;
            
            if (currentShape.temBucha()) {
                bonus += 50 * level;
                System.out.println("🎲 Bucha básica! +" + (50 * level) + " pontos!");
            }
            
            // SEQUÊNCIA BÁSICA TAMBÉM
            int[] sequencia = currentShape.getSequencia();
            if (ehSequenciaPerfeita(sequencia)) {
                bonus += 75 * level;
                System.out.println("🎯 Sequência básica! +" + (75 * level) + " pontos!");
            }
            
            if (bonus > 0) {
                score += bonus;
                System.out.println("🎯 Bônus dominó básico: +" + bonus + " pontos!");
            }
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
            // USA BlocoComNumero
            g.setColor(board[row][col].cor);
            g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            
            // DESENHA NÚMERO NO BLOCO FIXADO
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String numero = String.valueOf(board[row][col].numero);
            g.drawString(numero, 
                col * BLOCK_SIZE + BLOCK_SIZE/2 - 4, 
                row * BLOCK_SIZE + BLOCK_SIZE/2 + 4
            );
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
        BlocoComNumero[][] tabuleiro = board;
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

    public BlocoComNumero[][] getBoard() {
        return board;
    }

    // cria nova peça no topo
public void setCurrentShape() {
    // Processa a peça atual (que já tem power-up se aplicável)
    currentShape = proximaShape;
    
    // VERIFICA SE TEM POWER-UP ATIVO
    if (pecasComPowerUpRestantes > 0) {
        System.out.println("🔮 Power-up ativo: " + powerUpAtivo + " | Restantes: " + pecasComPowerUpRestantes);
        
        // SE A PEÇA ATUAL NÃO TEM POWER-UP, APLICA
        if (!(currentShape instanceof PowerUpDecorator)) {
            currentShape = new PowerUpDecorator(currentShape, powerUpAtivo);
            System.out.println("🎯 Power-up aplicado na peça atual!");
        }
        
        // REDUZ CONTADOR APÓS USAR ESTA PEÇA
        pecasComPowerUpRestantes--;
        System.out.println("📉 Peças restantes: " + pecasComPowerUpRestantes);
        
        // PREPARA PRÓXIMA PEÇA (com ou sem power-up)
        if (pecasComPowerUpRestantes > 0) {
            // Ainda tem power-up ativo - gera peça especial
            proximaShape = gerarPecaComPowerUp();
            System.out.println("🔮 Próxima peça COM power-up");
        } else {
            // Power-up acabou - volta ao normal
            proximaShape = gerarNovaPeca();
            powerUpAtivo = null;
            System.out.println("⏰ Power-up acabou! Voltando ao normal.");
        }
    } else {
        // SEM POWER-UP - GERA PEÇA NORMAL
        proximaShape = gerarNovaPeca();
    }
    
    // VERIFICA GAME OVER
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
            // TECLAS DE TESTE PARA POWER-UPS (Remover depois)
        case KeyEvent.VK_1 -> aplicarPowerUp("DOMINO_DOURADO");
        case KeyEvent.VK_2 -> aplicarPowerUp("SEQUENCIA_PERFEITA");
        case KeyEvent.VK_3 -> aplicarPowerUp("TETRIS_ABENÇOADO");
        case KeyEvent.VK_4 -> aplicarPowerUp("INVERSOR");
        case KeyEvent.VK_5 -> aplicarPowerUp("SUPER_ROTACAO");
        case KeyEvent.VK_6 -> aplicarPowerUp("PESADELO_NUMERICO");
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