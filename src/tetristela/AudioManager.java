package tetristela;

import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    
    private static AudioManager instance;
    private Map<String, Clip> soundClips;
    private Clip musicClip;
    private float volume = 0.7f;
    private boolean musicEnabled = true;
    
    // CONSTRUTOR PÚBLICO
    public AudioManager() {
        soundClips = new HashMap<>();
        carregarAudios();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    private void carregarAudios() {
        try {
            System.out.println("🎵 Iniciando carregamento de sons...");
            
            // CARREGA MÚSICA PRINCIPAL
            carregarMusica("Musica_Tema1.wav");
            
            // CARREGA EFEITOS SONOROS
            carregarSom("gameover", "Game_over.wav");
            
            System.out.println("✅ Sons carregados: " + soundClips.keySet());
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar sons: " + e.getMessage());
        }
    }
    
    private void carregarSom(String nome, String nomeArquivo) {
        try {
            // TENTA CARREGAR DO CLASSPATH (JAR)
            InputStream is = getClass().getResourceAsStream("/tetristela/" + nomeArquivo);
            
            if (is == null) {
                // FALLBACK: SISTEMA DE ARQUIVOS (DESENVOLVIMENTO)
                File arquivo = new File("src/tetristela/" + nomeArquivo);
                if (arquivo.exists()) {
                    is = new FileInputStream(arquivo);
                    System.out.println("📁 " + nome + " carregado do filesystem");
                } else {
                    System.err.println("❌ Arquivo não encontrado: " + nomeArquivo);
                    return;
                }
            } else {
                System.out.println("📦 " + nome + " carregado do JAR");
            }
            
            // CONVERTE PARA BYTE ARRAY
            byte[] buffer = lerTodosBytes(is);
            ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            ajustarVolume(clip, volume);
            soundClips.put(nome, clip);
            
            System.out.println("✅ " + nome + " carregado e pronto!");
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("❌ Formato não suportado para " + nome + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Erro de IO ao carregar " + nome + ": " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("❌ Linha de áudio indisponível para " + nome + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado em " + nome + ": " + e.getMessage());
        }
    }
    
    private void carregarMusica(String nomeArquivo) {
        try {
            // TENTA CARREGAR DO CLASSPATH (JAR)
            InputStream is = getClass().getResourceAsStream("/tetristela/" + nomeArquivo);
            
            if (is == null) {
                // FALLBACK: SISTEMA DE ARQUIVOS (DESENVOLVIMENTO)
                File arquivo = new File("src/tetristela/" + nomeArquivo);
                if (arquivo.exists()) {
                    is = new FileInputStream(arquivo);
                    System.out.println("📁 Música carregada do filesystem");
                } else {
                    System.err.println("❌ Música não encontrada: " + nomeArquivo);
                    return;
                }
            } else {
                System.out.println("📦 Música carregada do JAR");
            }
            
            // CONVERTE PARA BYTE ARRAY
            byte[] buffer = lerTodosBytes(is);
            ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            ajustarVolume(musicClip, volume * 0.8f);
            System.out.println("✅ Música carregada!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar música: " + e.getMessage());
        }
    }
    
    // MÉTODO AUXILIAR PARA LER INPUT STREAM
    private byte[] lerTodosBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
    
    private void ajustarVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));
            gainControl.setValue(dB);
        }
    }
    
    // MÉTODOS PÚBLICOS (MANTENDO OS NOMES ORIGINAIS)
    
    public void iniciarMusicaGameplay() {
        if (musicEnabled && volume > 0f && musicClip != null) {
            if (musicClip.isRunning()) {
                musicClip.stop();
            }
            musicClip.setFramePosition(0);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("🎵 Música iniciada!");
        }
    }
    
    public void tocarGameOver() {
        System.out.println("🎯 Tentando tocar Game Over...");
        System.out.println("   - musicEnabled: " + musicEnabled);
        System.out.println("   - volume: " + volume);
        System.out.println("   - soundClips tem gameover: " + soundClips.containsKey("gameover"));
        
        if (!musicEnabled || volume == 0f) {
            System.out.println("❌ Game Over bloqueado: música desabilitada ou volume zero");
            return;
        }
        
        Clip clip = soundClips.get("gameover");
        if (clip != null) {
            System.out.println("✅ Clip de Game Over encontrado!");
            
            // PARA A MÚSICA QUANDO TOCA GAME OVER
            pararMusica();
            
            if (clip.isRunning()) {
                clip.stop();
                System.out.println("🔄 Parando Game Over anterior...");
            }
            
            clip.setFramePosition(0);
            clip.start();
            System.out.println("💀 Game Over SFX tocado com sucesso!");
        } else {
            System.err.println("❌ Clip de Game Over é NULL!");
        }
    }
    
    // NOVO MÉTODO: PARAR SOM DO GAME OVER
    public void pararGameOver() {
        Clip clip = soundClips.get("gameover");
        if (clip != null && clip.isRunning()) {
            clip.stop();
            System.out.println("⏹️ Game Over SFX parado!");
        }
    }
    
    public void toggleMute() {
        musicEnabled = !musicEnabled;
        if (musicEnabled && volume > 0f) {
            iniciarMusicaGameplay();
        } else {
            pararMusica();
        }
        System.out.println(musicEnabled ? "🔊 Som ativado" : "🔇 Som desativado");
    }
    
    public void aumentarVolume() {
        float novoVolume = volume + 0.1f;
        if (novoVolume > 1.0f) {
            novoVolume = 1.0f;
            System.out.println("🔊 Volume máximo! (100%)");
        }
        setVolume(novoVolume);
    }
    
    public void diminuirVolume() {
        float novoVolume = volume - 0.1f;
        if (novoVolume < 0.0f) {
            novoVolume = 0.0f;
            System.out.println("🔇 Volume mínimo! (0%)");
        }
        setVolume(novoVolume);
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        // ATUALIZA MÚSICA
        if (musicClip != null) {
            ajustarVolume(musicClip, this.volume * 0.8f);
            if (this.volume > 0f && musicEnabled && !musicClip.isRunning()) {
                iniciarMusicaGameplay();
            } else if (this.volume == 0f) {
                pararMusica();
            }
        }
        
        // ATUALIZA EFFECTS
        for (Clip clip : soundClips.values()) {
            ajustarVolume(clip, this.volume);
        }
        
        System.out.println("🔊 Volume ajustado para: " + (int)(this.volume * 100) + "%");
    }

    
    public void pararMusica() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            System.out.println("⏹️ Música parada!");
        }
    }
    
    public float getVolume() {
        return volume;
    }
    
    public boolean isMuted() {
        return volume == 0.0f || !musicEnabled;
    }
    
    public void cleanup() {
        pararMusica();
        pararGameOver(); // PARA TUDO AO SAIR
        
        if (musicClip != null) {
            musicClip.close();
        }
        
        for (Clip clip : soundClips.values()) {
            clip.close();
        }
        
        soundClips.clear();
        System.out.println("🧹 AudioManager limpo!");
    }
    
    // MÉTODO DEBUG PARA VERIFICAR ESTADO
    public void debugEstado() {
        System.out.println("=== DEBUG AUDIO MANAGER ===");
        System.out.println("Música carregada: " + (musicClip != null));
        System.out.println("Game Over carregado: " + (soundClips.get("gameover") != null));
        System.out.println("Volume: " + volume);
        System.out.println("Music Enabled: " + musicEnabled);
        
        Clip gameOverClip = soundClips.get("gameover");
        if (gameOverClip != null) {
            System.out.println("Game Over clip status: " + 
                (gameOverClip.isRunning() ? "RUNNING" : "STOPPED") +
                ", frames: " + gameOverClip.getFrameLength());
        }
    }
}