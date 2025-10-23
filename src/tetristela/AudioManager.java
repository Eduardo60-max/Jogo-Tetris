package tetristela;

import javax.sound.sampled.*;
import java.io.*;

public class AudioManager {
    private Clip gameplayMusic;
    private Clip gameOverSFX;
    private boolean musicEnabled = true;
    private float volume = 0.7f; // Volume padrão (70%)
    private FloatControl musicVolumeControl;
    private FloatControl sfxVolumeControl;
    
    public AudioManager() {
        System.out.println("🎵 AudioManager iniciado!");
        carregarAudios();
    }
    
    private void carregarAudios() {
        System.out.println("🔍 Procurando arquivos de áudio...");
    
        File musicaFile = new File("src/tetristela/Musica_Tema1.wav");
        File gameOverFile = new File("src/tetristela/Game_over.wav");
        
        System.out.println("📍 Caminho música: " + musicaFile.getAbsolutePath());
        System.out.println("📍 Música existe? " + musicaFile.exists());
        System.out.println("📍 Caminho game over: " + gameOverFile.getAbsolutePath());
        System.out.println("📍 Game over existe? " + gameOverFile.exists());
        
        try {
            // CARREGA MÚSICA
            if (musicaFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicaFile);
                gameplayMusic = AudioSystem.getClip();
                gameplayMusic.open(audioInput);
                
                // CONFIGURA CONTROLE DE VOLUME DA MÚSICA
                if (gameplayMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    musicVolumeControl = (FloatControl) gameplayMusic.getControl(FloatControl.Type.MASTER_GAIN);
                    setVolume(volume); // Aplica volume padrão
                }
                
                System.out.println("✅ Musica_Tema1.wav CARREGADA!");
            } else {
                System.err.println("❌ Musica_Tema1.wav NÃO encontrada!");
                // Tenta versão alternativa sem underline
                File musicaAlternativa = new File("src/tetristela/Musica Tema1.wav");
                if (musicaAlternativa.exists()) {
                    System.out.println("✅ Encontrada versão alternativa (sem underline)");
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicaAlternativa);
                    gameplayMusic = AudioSystem.getClip();
                    gameplayMusic.open(audioInput);
                    
                    // Configura volume para a alternativa também
                    if (gameplayMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        musicVolumeControl = (FloatControl) gameplayMusic.getControl(FloatControl.Type.MASTER_GAIN);
                        setVolume(volume);
                    }
                }
            }
            
            // CARREGA GAME OVER
            if (gameOverFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(gameOverFile);
                gameOverSFX = AudioSystem.getClip();
                gameOverSFX.open(audioInput);
                
                // CONFIGURA CONTROLE DE VOLUME DO SFX
                if (gameOverSFX.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    sfxVolumeControl = (FloatControl) gameOverSFX.getControl(FloatControl.Type.MASTER_GAIN);
                    // Aplica o volume atual aos SFX também
                    if (volume == 0.0f) {
                        sfxVolumeControl.setValue(-80.0f);
                    } else {
                        float minDB = -30.0f;
                        float maxDB = 0.0f;
                        float gain = minDB + (volume * (maxDB - minDB));
                        sfxVolumeControl.setValue(gain);
                    }
                }
                
                System.out.println("✅ Game_over.wav CARREGADO!");
            } else {
                System.err.println("❌ Game_over.wav NÃO encontrado!");
            }
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("❌ Formato de áudio não suportado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Erro de IO ao carregar áudio: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("❌ Linha de áudio indisponível: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ ERRO inesperado ao carregar áudios: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("📊 STATUS FINAL:");
        System.out.println("🎵 Música: " + (gameplayMusic != null ? "✅ PRONTA" : "❌ FALHOU"));
        System.out.println("💀 Game Over: " + (gameOverSFX != null ? "✅ PRONTO" : "❌ FALHOU"));
    }
    
    // MÉTODOS DE CONTROLE DE VOLUME CORRIGIDOS
    public void setVolume(float volume) {
        // Garante que volume fique entre 0% e 100%
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        if (this.volume == 0.0f) {
            // Volume 0% = MUTE COMPLETO
            if (musicVolumeControl != null) {
                musicVolumeControl.setValue(-80.0f); // Volume mínimo (praticamente mudo)
            }
            if (sfxVolumeControl != null) {
                sfxVolumeControl.setValue(-80.0f);
            }
        } else {
            float minDB = -30.0f;   // Volume baixo mas audível
            float maxDB = 0.0f;     // Volume máximo
            float gain = minDB + (this.volume * (maxDB - minDB));
            
            // Aplica à música
            if (musicVolumeControl != null) {
                musicVolumeControl.setValue(gain);
            }
            
            // Aplica aos SFX
            if (sfxVolumeControl != null) {
                sfxVolumeControl.setValue(gain);
            }
        }
        
        System.out.println("🔊 Volume ajustado para: " + (int)(this.volume * 100) + "%");
    }
    
    public float getVolume() {
        return volume;
    }
    
    public void aumentarVolume() {
        // Aumenta em passos de 10% e para no máximo 100%
        float novoVolume = volume + 0.1f;
        if (novoVolume > 1.0f) {
            novoVolume = 1.0f;
            System.out.println("🔊 Volume já está no máximo! (100%)");
        }
        setVolume(novoVolume);
    }
    
    public void diminuirVolume() {
        // Diminui em passos de 10% e para no mínimo 0% (mudo)
        float novoVolume = volume - 0.1f;
        if (novoVolume < 0.0f) {
            novoVolume = 0.0f;
            System.out.println("🔇 Volume já está no mínimo! (0%)");
        }
        setVolume(novoVolume);
    }
    
    public void toggleMute() {
        if (volume > 0.0f) {
            // Se não está mudo, salva o volume atual e muta
            setVolume(0.0f);
        } else {
            // Se está mudo, volta para 50% (ou poderia salvar o volume anterior)
            setVolume(0.5f);
        }
        System.out.println(volume > 0 ? "🔊 Som ativado" : "🔇 Som desativado");
    }
    
    public boolean isMuted() {
        // Considera mudo quando volume é 0%
        return volume == 0.0f;
    }
    
    public void iniciarMusicaGameplay() {
        System.out.println("🎵 Iniciando música...");
        // Só toca se volume não for 0%
        if (musicEnabled && gameplayMusic != null && volume > 0.0f) {
            try {
                gameplayMusic.setFramePosition(0);
                gameplayMusic.loop(Clip.LOOP_CONTINUOUSLY);
                gameplayMusic.start();
                System.out.println("✅ Música INICIADA!");
            } catch (Exception e) {
                System.err.println("❌ Erro ao iniciar música: " + e.getMessage());
            }
        } else {
            System.err.println("❌ Música NÃO disponível para iniciar (volume: " + (int)(volume * 100) + "%)");
        }
    }
    
        public void tocarGameOver() {
        System.out.println("💀 Tocando game over...");
        // Só toca se volume não for 0%
        if (musicEnabled && gameOverSFX != null && volume > 0.0f) {
            try {
                pararMusica();
                
                gameOverSFX.setFramePosition(0);
                gameOverSFX.start();
                System.out.println("✅ Game Over tocado!");
            } catch (Exception e) {
                System.err.println("❌ Erro ao tocar game over: " + e.getMessage());
            }
        } else {
            System.err.println("❌ Game Over NÃO disponível (volume: " + (int)(volume * 100) + "%)");
        }
    }
    
    public void pararMusica() {
        if (gameplayMusic != null && gameplayMusic.isRunning()) {
            gameplayMusic.stop();
            System.out.println("⏹️ Música parada!");
        }
    }
    
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            pararMusica();
        } else if (gameplayMusic != null && !gameplayMusic.isRunning() && volume > 0.0f) {
            iniciarMusicaGameplay();
        }
    }
}