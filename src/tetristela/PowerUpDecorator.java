package tetristela;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Random;


public class PowerUpDecorator implements PecaDoJogo {
     
    protected PecaDoJogo pecaDecorada;

    private static final Random rand = new Random(); 

    
    private boolean inversoAtivo = false;
    private boolean superRotacaoAtivo = false;
    private boolean pesadeloNumericoAtivo = false;
    
    private boolean dominoDouradoAtivo = false;
    private boolean sequenciaPerfeitaAtivo = false;
    private boolean tetrisAbencoadoAtivo = false;
   


    public PecaDoJogo getPecaDecorada() {
    return pecaDecorada;
}

    // ADICIONA CORES ESPECIAIS PARA POWER-UPS
    private Color getCorComEfeito() {
        Color corOriginal = pecaDecorada.getColor();
        
        if (dominoDouradoAtivo) {
            // EFEITO DOURADO - Laranja flamejante
            return new Color(255, 100, 0); 
        }
        else if (sequenciaPerfeitaAtivo) {
            // EFEITO SEQUÊNCIA - rosa choque 
            return new Color(255, 105, 180); 
        }
        else if (inversoAtivo) {
            // EFEITO INVERSOR - vermelho
            return new Color(255, 0, 0); 
        }
        else if (superRotacaoAtivo) {
            // EFEITO SUPER ROTAÇÃO - azul
            return new Color(0, 0, 255); 
        }
        else if (tetrisAbencoadoAtivo) {
            // EFEITO TETRISABENCOADO - verde brilhante
            return new Color(0, 255, 0); 
        }
        else if (pesadeloNumericoAtivo) {
            // EFEITO PESADELO NUMERICO - roxo sombrio
            return new Color(128, 0, 128); 
        }
        
        return corOriginal; // Cor normal
    }

    public PowerUpDecorator(PecaDoJogo pecaDecorada, String tipoEfeito) {
        this.pecaDecorada = pecaDecorada;
        ativarEfeito(tipoEfeito);
    }
    

    
    private void ativarEfeito(String tipo) {
        switch (tipo) {
            case "INVERSOR" -> {
                this.inversoAtivo = true;
                System.out.println("🔄 Efeito Inversor de Controle Ativado!");
            }
            case "SUPER_ROTACAO" -> {
                this.superRotacaoAtivo = true;
                System.out.println("⚡ Efeito Super Rotação Ativado!");
            }
            case "DOMINO_DOURADO" -> {
                this.dominoDouradoAtivo = true;
                System.out.println("💰 Domino Dourado Ativado! Buchas valem o dobro!");
            }
            case "SEQUENCIA_PERFEITA" -> {
                this.sequenciaPerfeitaAtivo = true;
                System.out.println("🎯 Sequência Perfeita Ativado! Bônus por sequências!");
            }
            case "TETRIS_ABENÇOADO" -> {
                this.tetrisAbencoadoAtivo = true;
                System.out.println("🙏 Tetris Abençoado Ativado! Apenas peças I!");
            }
            case "PESADELO_NUMERICO" -> {
                this.pesadeloNumericoAtivo = true;
                System.out.println("😈 Pesadelo Numérico Ativado! Números aleatórios!");
            }
            default -> System.err.println("Tipo de efeito desconhecido: " + tipo);
        }
    }

    @Override 
    public void setX(int newX) { 
        pecaDecorada.setX(newX); 
    }

    // MÉTODOS DE DOMINÓ (DELEGAÇÃO ESPECIAL)
    @Override
    public int[][] getNumerosDomino() {
        return pecaDecorada.getNumerosDomino();
    }

    @Override
    public boolean temBucha() {
        boolean bucha = pecaDecorada.temBucha();
        if (dominoDouradoAtivo && bucha) {
            System.out.println("💰 BUCHA DOURADA! Pontuação extra!");
        }
        return bucha;
    }

    @Override
    public int[] getSequencia() {
        int[] seq = pecaDecorada.getSequencia();
        if (sequenciaPerfeitaAtivo && ehSequenciaPerfeita(seq)) {
            System.out.println("🎯 SEQUÊNCIA PERFEITA! Bônus máximo!");
        }
        return seq;
    }

    private boolean ehSequenciaPerfeita(int[] sequencia) {
        if (sequencia.length < 2) return false;
        int[] sorted = sequencia.clone();
        Arrays.sort(sorted);
        for (int i = 0; i < sorted.length - 1; i++) {
            if (sorted[i + 1] != sorted[i] + 1) return false;
        }
        return true;
    }

    // DECORAÇÃO DOS MÉTODOS DE CONTROLE
    
    @Override
    public void moverDi() {
        if (inversoAtivo) {
            pecaDecorada.moverEs(); // INVERSÃO
        } else {
            pecaDecorada.moverDi(); // Comportamento normal
        }
    }

    @Override
    public void moverEs() {
        if (inversoAtivo) {
            pecaDecorada.moverDi(); // INVERSÃO
        } else {
            pecaDecorada.moverEs(); // Comportamento normal
        }
    }

    @Override
    public void hardDrop() {
        pecaDecorada.hardDrop();
    }
    
    @Override
    public void rotacionar() {
        // PESADELO NUMÉRICO: Aleatoriza ANTES da rotação
        if (pesadeloNumericoAtivo && pecaDecorada instanceof formato) {
            formato forma = (formato) pecaDecorada;
            int[][] numeros = forma.getNumerosDomino();
            
            // GARANTIR QUE NÃO VIRA TUDO 0
            boolean todosZeros = true;
            for (int i = 0; i < numeros.length; i++) {
                for (int j = 0; j < numeros[0].length; j++) {
                    if (numeros[i][j] != 0) {
                        // GERA NÚMERO ENTRE 1-6 (NUNCA 0)
                        int novoNumero = rand.nextInt(6) + 1; // 1-6
                        numeros[i][j] = novoNumero;
                        if (novoNumero != 0) {
                            todosZeros = false;
                        }
                    }
                }
            }
            
            // SE ACABOU TUDO 0, FORÇA PELO MENOS UM NÚMERO DIFERENTE
            if (todosZeros) {
                for (int i = 0; i < numeros.length; i++) {
                    for (int j = 0; j < numeros[0].length; j++) {
                        if (numeros[i][j] != 0) {
                            numeros[i][j] = rand.nextInt(6) + 1; // 1-6
                            break;
                        }
                    }
                }
            }
            
            System.out.println("😈 Números aleatorizados!");
            
        }
        
        // ROTAÇÃO NORMAL
        pecaDecorada.rotacionar();
        
        // SUPER ROTAÇÃO
        if (superRotacaoAtivo) {
            pecaDecorada.rotacionar(); 
        }
    }
    
    //  DELEGAÇÃO DE OUTROS MÉTODOS
    @Override public void update() { pecaDecorada.update(); }
    @Override
    public void render(Graphics g) {
        // PEGA COR COM EFEITO DO POWER-UP
        Color corEfeito = getCorComEfeito();
        int[][] coords = getCoords();
        
        // DESENHA BLOCO COM COR DO POWER-UP
        g.setColor(corEfeito);
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    g.fillRect(
                        (getX() + col) * 30, // BLOCK_SIZE (30)
                        (getY() + row) * 30,
                        30,
                        30
                    );
                    
                    // DESENHA NÚMERO PRETO
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 14));
                    int[][] numeros = getNumerosDomino();
                    String numero = String.valueOf(numeros[row][col]);
                    
                    int textoX = (getX() + col) * 30 + 15 - 4; // CENTRALIZA
                    int textoY = (getY() + row) * 30 + 15 + 5;
                    g.drawString(numero, textoX, textoY);
                    
                    // VOLTA PARA COR DO POWER-UP
                    g.setColor(corEfeito);
                    
                    // BORDA BRANCA
                    g.setColor(Color.WHITE);
                    g.drawRect(
                        (getX() + col) * 30,
                        (getY() + row) * 30,
                        30,
                        30
                    );
                    
                    // VOLTA PARA COR DO POWER-UP
                    g.setColor(corEfeito);
                }
            }
        }
    }
    @Override public void speedUp() { pecaDecorada.speedUp(); }
    @Override public void speedDown() { pecaDecorada.speedDown(); }
    
    //DELEGAÇÃO DE ESTADO
    
    @Override public boolean hasColisao() { return pecaDecorada.hasColisao(); }
    @Override public int getX() { return pecaDecorada.getX(); }
    @Override public int getY() { return pecaDecorada.getY(); }
    @Override public int[][] getCoords() { return pecaDecorada.getCoords(); }
    @Override public Color getColor() { return pecaDecorada.getColor(); } 
    @Override public void setY(int newY) { pecaDecorada.setY(newY); }
}