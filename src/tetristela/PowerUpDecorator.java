package tetristela;

import java.awt.Color;
import java.awt.Graphics;


public class PowerUpDecorator implements PecaDoJogo {
    
    protected PecaDoJogo pecaDecorada;

    // Flags para gerenciar os diferentes Power-Ups ativos
    private boolean inversoAtivo = false;
    private boolean superRotacaoAtivo = false;
    // Adicione mais flags aqui, se necessário

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
            // Adicione novos efeitos aqui!
            default -> System.err.println("Tipo de efeito desconhecido: " + tipo);
        }
    }

    //DECORAÇÃO DOS MÉTODOS DE CONTROLE
    
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
    public void rotacionar() {
        pecaDecorada.rotacionar();
        if (superRotacaoAtivo) {
            pecaDecorada.rotacionar(); 
        }
    }
    
    //  DELEGAÇÃO DE OUTROS MÉTODOS
    
    @Override public void update() { pecaDecorada.update(); }
    @Override public void render(Graphics g) { pecaDecorada.render(g); }
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