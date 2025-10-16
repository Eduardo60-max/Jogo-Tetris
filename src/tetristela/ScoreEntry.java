package tetristela;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private String iniciais;
    private int score;
    private Date data;
    
    public ScoreEntry(String iniciais, int score) {
        this.iniciais = iniciais.toUpperCase();
        this.score = score;
        this.data = new Date();
    }
    
    public ScoreEntry(String iniciais, int score, Date data) {
        this.iniciais = iniciais.toUpperCase();
        this.score = score;
        this.data = data;
    }
    
    // Getters
    public String getIniciais() { return iniciais; }
    public int getScore() { return score; }
    public Date getData() { return data; }
    
    // Para ordenar do maior score para o menor
    @Override
    public int compareTo(ScoreEntry outro) {
        return Integer.compare(outro.score, this.score);
    }
    
    // Formatação para salvar no arquivo
    public String paraArquivo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return iniciais + " " + score + " " + sdf.format(data);
    }
    
    // Formatação para exibir
    public String paraDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return String.format("%-4s %-8d %s", iniciais, score, sdf.format(data));
    }
}
