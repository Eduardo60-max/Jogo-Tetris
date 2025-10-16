package tetristela;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RankingManager {
    private static final String ARQUIVO_RANKING = "ranking.txt";
    private static final int MAX_ENTRIES = 10;
    private List<ScoreEntry> ranking;
    
    public RankingManager() {
        ranking = new ArrayList<>();
        carregarRanking();
    }
    
    // Carrega ranking do arquivo
    private void carregarRanking() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_RANKING))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                ScoreEntry entry = parseLinha(linha);
                if (entry != null) {
                    ranking.add(entry);
                }
            }
            Collections.sort(ranking);
        } catch (IOException e) {
            System.out.println("Arquivo de ranking não encontrado. Criando novo.");
        }
    }
    
    // Salva ranking no arquivo
    private void salvarRanking() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_RANKING))) {
            for (ScoreEntry entry : ranking) {
                writer.println(entry.paraArquivo());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar ranking: " + e.getMessage());
        }
    }
    
    // Converte linha do arquivo para ScoreEntry
    private ScoreEntry parseLinha(String linha) {
        try {
            String[] partes = linha.split(" ");
            if (partes.length >= 3) {
                String iniciais = partes[0];
                int score = Integer.parseInt(partes[1]);
                
                // Reconstroi a data
                StringBuilder dataStr = new StringBuilder();
                for (int i = 2; i < partes.length; i++) {
                    if (i > 2) dataStr.append(" ");
                    dataStr.append(partes[i]);
                }
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date data = sdf.parse(dataStr.toString());
                
                return new ScoreEntry(iniciais, score, data);
            }
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha: " + linha);
        }
        return null;
    }
    
    // Adiciona novo score e retorna se entrou no ranking
    public boolean adicionarScore(String iniciais, int score) {
        ScoreEntry novoEntry = new ScoreEntry(iniciais, score);
        ranking.add(novoEntry);
        Collections.sort(ranking);
        
        // Mantém só os top 10
        if (ranking.size() > MAX_ENTRIES) {
            ranking = ranking.subList(0, MAX_ENTRIES);
        }
        
        salvarRanking();
        return estaNoRanking(novoEntry);
    }
    
    // Verifica se o score está no ranking
    private boolean estaNoRanking(ScoreEntry entry) {
        return ranking.contains(entry);
    }
    
    // Retorna o ranking completo
    public List<ScoreEntry> getRanking() {
        return new ArrayList<>(ranking);
    }
    
    // Verifica se o score é bom o suficiente para o ranking
    public boolean ehHighScore(int score) {
        if (ranking.size() < MAX_ENTRIES) return true;
        return score > ranking.get(ranking.size() - 1).getScore();
    }
}