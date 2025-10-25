package tetristela;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ResourceManager {
    
    private static ResourceManager instance;
    private Image backgroundImage;
    private boolean imagemCarregada = false;
    
    private ResourceManager() {
        System.out.println("🖼️ ResourceManager iniciado!");
        carregarImagens();
    }
    
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    private void carregarImagens() {
        carregarBackground();
    }
    
    private void carregarBackground() {
        try {
            System.out.println("🔍 Buscando background...");
            
            // TENTA CARREGAR DO CLASSPATH (JAR) - MESMO PADRÃO DO AUDIOMANAGER
            InputStream stream = getClass().getResourceAsStream("/tetristela/fullbackground.png");
            
            if (stream == null) {
                System.out.println("❌ Não encontrado no JAR, tentando filesystem...");
                
                // FALLBACK: SISTEMA DE ARQUIVOS (DESENVOLVIMENTO)
                File file = new File("src/tetristela/fullbackground.png");
                if (file.exists()) {
                    System.out.println("✅ Encontrado no filesystem do src");
                    backgroundImage = new ImageIcon(file.getAbsolutePath()).getImage();
                    imagemCarregada = true;
                    System.out.println("✅ Background carregado do filesystem!");
                    return;
                }
                
                // FALLBACK 2: Pasta resources original
                file = new File("resources/sprites/fullbackground.png");
                if (file.exists()) {
                    System.out.println("✅ Encontrado no resources original");
                    backgroundImage = new ImageIcon(file.getAbsolutePath()).getImage();
                    imagemCarregada = true;
                    System.out.println("✅ Background carregado do resources!");
                    return;
                }
                
                // SE NÃO ENCONTROU, CRIA AUTOMÁTICO
                System.out.println("🎨 Nenhum arquivo encontrado, criando background automático...");
                criarBackgroundAutomatico();
                
            } else {
                // ENCONTROU NO JAR!
                System.out.println("📦 Background encontrado no JAR!");
                backgroundImage = ImageIO.read(stream);
                imagemCarregada = true;
                stream.close();
                System.out.println("✅ Background carregado do JAR com sucesso!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar background: " + e.getMessage());
            e.printStackTrace();
            criarBackgroundAutomatico();
        }
    }
    
    private void criarBackgroundAutomatico() {
        try {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g2d = img.createGraphics();
            
            // Gradiente bonito
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(25, 25, 112),      // Azul escuro
                800, 600, new Color(0, 0, 0)        // Preto
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, 800, 600);
            
            // Texto principal
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 54));
            g2d.drawString("DOMINOTRIS", 150, 150);
            
            // Subtítulo
            g2d.setColor(Color.CYAN);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Tetris com Dominó", 220, 200);
            
            // Detalhes decorativos
            g2d.setColor(new Color(255, 255, 255, 100));
            for (int i = 0; i < 50; i++) {
                int x = (int) (Math.random() * 800);
                int y = (int) (Math.random() * 600);
                int size = (int) (Math.random() * 3) + 1;
                g2d.fillOval(x, y, size, size);
            }
            
            g2d.dispose();
            backgroundImage = img;
            imagemCarregada = true;
            System.out.println("✅ Background automático criado!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar background automático: " + e.getMessage());
        }
    }
    
    public Image getBackgroundImage() {
        return backgroundImage;
    }
    
    public boolean isImagemCarregada() {
        return imagemCarregada;
    }
    
    public void debugRecursos() {
        System.out.println("=== DEBUG RESOURCE MANAGER ===");
        System.out.println("Imagem carregada: " + imagemCarregada);
        System.out.println("Background: " + (backgroundImage != null ? "✅ OK" : "❌ NULL"));
        
        if (backgroundImage != null) {
            System.out.println("Dimensões: " + backgroundImage.getWidth(null) + "x" + backgroundImage.getHeight(null));
        }
        
        debugCaminhos();
    }
    
    private void debugCaminhos() {
        System.out.println("🔍 Verificando caminhos:");
        
        // CAMINHO DO JAR (igual ao AudioManager)
        InputStream stream = getClass().getResourceAsStream("/tetristela/fullbackground.png");
        System.out.println("  /tetristela/fullbackground.png (JAR): " + (stream != null ? "✅" : "❌"));
        if (stream != null) {
            try { stream.close(); } catch (Exception e) {}
        }
        
        // FILESYSTEM - SRC
        File file = new File("src/tetristela/fullbackground.png");
        System.out.println("  src/tetristela/fullbackground.png: " + (file.exists() ? "✅" : "❌"));
        
        // FILESYSTEM - RESOURCES ORIGINAL
        file = new File("resources/sprites/fullbackground.png");
        System.out.println("  resources/sprites/fullbackground.png: " + (file.exists() ? "✅" : "❌"));
    }
    
    public void cleanup() {
        if (backgroundImage != null) {
            backgroundImage.flush();
            backgroundImage = null;
        }
        imagemCarregada = false;
        instance = null;
        System.out.println("🧹 ResourceManager limpo!");
    }
}