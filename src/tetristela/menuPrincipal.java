package tetristela;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class menuPrincipal extends JPanel {
    private JButton btnIniciar, btnSair, btnInstrucoes, btnRanking;
    private Image backgroundImage;
    private boolean imagemCarregada = false;

    public menuPrincipal(JFrame janela) {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        // CARREGA BACKGROUND DO RESOURCE MANAGER
        ResourceManager resourceManager = ResourceManager.getInstance();
        backgroundImage = resourceManager.getBackgroundImage();
        imagemCarregada = resourceManager.isImagemCarregada();
        
        resourceManager.debugRecursos();
        
        // Configura layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);
        
        // ESPAÇO PARA O TÍTULO
        JLabel espacoTitulo = new JLabel(" ");
        espacoTitulo.setPreferredSize(new Dimension(1, 200));
        gbc.insets = new Insets(100, 50, 50, 50);
        add(espacoTitulo, gbc);

        // Botão Iniciar
        btnIniciar = criarBotao8Bit("INICIAR", Color.GREEN);
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(menuPrincipal.this);
                if (frame != null) {
                    frame.getContentPane().removeAll();
                    jogo game = new jogo();
                    frame.setContentPane(game);
                    frame.addKeyListener(game);
                    frame.revalidate();
                    frame.repaint();
                    game.requestFocusInWindow();
                }
            }
        });
        gbc.insets = new Insets(10, 50, 10, 50);
        add(btnIniciar, gbc);

        // Botão Ranking
        btnRanking = criarBotao8Bit("RANKING", Color.CYAN);
        btnRanking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(menuPrincipal.this);
                if (frame != null) {
                    frame.getContentPane().removeAll();
                    frame.setContentPane(new TelaRanking(frame));
                    frame.revalidate();
                    frame.repaint();
                }
            }
        });
        add(btnRanking, gbc);
        
        // Botão Instruções
        btnInstrucoes = criarBotao8Bit("INSTRUÇÕES", Color.YELLOW);
        btnInstrucoes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarInstrucoes();
            }
        });
        add(btnInstrucoes, gbc);

        // Botão Sair
        btnSair = criarBotao8Bit("SAIR", Color.RED);
        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resposta = JOptionPane.showConfirmDialog(menuPrincipal.this,
                    "Você tem certeza que quer sair do Tetris?",
                    "Sair",
                    JOptionPane.YES_NO_OPTION);
                if (resposta == JOptionPane.YES_OPTION) {
                    // SÓ NO SAIR DO JOGO USA CLEANUP (fecha tudo)
                    AudioManager.getInstance().cleanup();
                    System.exit(0);
                }
            }
        });
        add(btnSair, gbc);
    }

    
    private JButton criarBotao8Bit(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(load8BitFont(18));
        botao.setBackground(cor);
        botao.setForeground(Color.BLACK);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createRaisedBevelBorder());
        botao.setPreferredSize(new Dimension(200, 50));
        
        // Efeito hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor.brighter());
                botao.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor);
                botao.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
        
        return botao;
    }
    
    private Font load8BitFont(int size) {
        try {
            Font font = new Font("Press Start 2P", Font.BOLD, size);
            return font;
        } catch (Exception e) {
            return new Font("Courier New", Font.BOLD, size);
        }
    }

    private void mostrarInstrucoes() {
        String instrucoes = 
            "🎮 CONTROLES DO TETRIS:\n\n" +
            "← → SETAS: Mover para esquerda/direita\n" +
            "↑ SETA CIMA: Rotacionar peça\n" + 
            "↓ SETA BAIXO: Acelerar queda\n" +
            "SHIFT: Hold Piece (Guardar peça)\n" +
            "BARRA DE ESPAÇO: Hard Drop (queda instantânea)\n" +
            "ESC: Voltar ao menu\n\n" +

            "🔊 CONTROLES DE ÁUDIO:\n" +
            "M: Mute/Unmute durante o jogo\n" +
            "+: Aumentar volume\n" +
            "-: Diminuir volume\n\n" +
            
            "🎲 SISTEMA DE DOMINÓ:\n" +
            "• 🎲 BUCHA: Números iguais = +50-100 pontos\n" +
            "• 🎯 SEQUÊNCIA: Números em ordem = +75-150 pontos\n" +
            "• 💰 COMBO: Bucha + Sequência = BÔNUS MÁXIMO!\n\n" +
            
            "🔮 POWER-UPS (Ativação no jogo):\n" +
            "• Diferentes efeitos especiais serão\n" +
            "• Ativados misteriosamente durante o jogo!\n\n" +
            "• Podem tanto ajudar como atrapalhar o jogador!\n" +
            
            "🎯 OBJETIVO:\n" +
            "• Complete linhas horizontais\n" +
            "• Use buchas e sequências para pontos extras\n" +
            "• Evite que as peças cheguem ao topo!" +
            "• Consiga o máximo de pontos e entre no Ranking!\n";
        
        JOptionPane.showMessageDialog(this, instrucoes, "Instruções", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // DESENHA BACKGROUND
    if (imagemCarregada && backgroundImage != null) {
        System.out.println("🎨 Paint: Desenhando background carregado");
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    } else {
        System.out.println("🎨 Paint: Usando fallback - imagemCarregada: " + imagemCarregada + ", backgroundImage: " + (backgroundImage != null));
        // Fallback
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.WHITE);
        g.setFont(load8BitFont(36));
        g.drawString("TETRIS", 150, 100);
        g.drawString("(Sem Imagem)", 120, 150);
    }
}
}