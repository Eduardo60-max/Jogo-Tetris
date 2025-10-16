package tetristela;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaRanking extends JPanel {
    private RankingManager rankingManager;
    private JButton btnVoltar;
    
    public TelaRanking(JFrame janela) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        rankingManager = new RankingManager();
        
        // Título
        JLabel titulo = new JLabel("RANKING TOP 10", JLabel.CENTER);
        titulo.setFont(new Font("Courier New", Font.BOLD, 36));
        titulo.setForeground(Color.YELLOW);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);
        
        // Painel do ranking
        JPanel painelRanking = new JPanel();
        painelRanking.setLayout(new GridLayout(11, 1));
        painelRanking.setBackground(Color.BLACK);
        painelRanking.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        
        // Cabeçalho
        JLabel cabecalho = new JLabel("INIC  SCORE    DATA", JLabel.CENTER);
        cabecalho.setFont(new Font("Courier New", Font.BOLD, 18));
        cabecalho.setForeground(Color.CYAN);
        painelRanking.add(cabecalho);
        
        // Entradas do ranking
        List<ScoreEntry> ranking = rankingManager.getRanking();
        for (int i = 0; i < 10; i++) {
            JLabel entryLabel;
            if (i < ranking.size()) {
                ScoreEntry entry = ranking.get(i);
                entryLabel = new JLabel((i + 1) + ". " + entry.paraDisplay(), JLabel.CENTER);
            } else {
                entryLabel = new JLabel((i + 1) + ". ---    ---       --/--/----", JLabel.CENTER);
            }
            
            entryLabel.setFont(new Font("Courier New", Font.PLAIN, 16));
            entryLabel.setForeground(Color.WHITE);
            entryLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            painelRanking.add(entryLabel);
        }
        
        add(painelRanking, BorderLayout.CENTER);
        
        // Botão voltar
        btnVoltar = new JButton("VOLTAR");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltar.setBackground(Color.RED);
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.addActionListener(e -> voltarAoMenu(janela));
        
        JPanel painelBotao = new JPanel();
        painelBotao.setBackground(Color.BLACK);
        painelBotao.add(btnVoltar);
        add(painelBotao, BorderLayout.SOUTH);
    }
    
    private void voltarAoMenu(JFrame janela) {
    janela.getContentPane().removeAll();
    janela.setContentPane(new menuPrincipal(janela));
    janela.revalidate();
    janela.repaint();
    }
}