package src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

/**
 * A JPanel that displays a background image.
 * The image is scaled to fit the panel's dimensions.
 * Provides a customizable background for the application interface.
 */

public class ImageBackgroundPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private BufferedImage backgroundImage;

    public ImageBackgroundPanel() {
        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {

            // Try multiple resource locations
            InputStream stream = getClass().getResourceAsStream("/bkd.png");
            if (stream == null) {
                stream = getClass().getResourceAsStream("bkd.png");
            }
            if (stream == null) {

                // Try from classpath root
                stream = getClass().getClassLoader().getResourceAsStream("bkd.png");
            }
            
            if (stream != null) {
                backgroundImage = ImageIO.read(stream);
                stream.close();
            } else {

                // Try from file system as last resort
                File file = new File("src/main/resources/bkd.png");
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                } else {
                    
                    // Create default background
                    createDefaultBackground();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());
            createDefaultBackground();
        }
    }

    private void createDefaultBackground() {
        backgroundImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = backgroundImage.createGraphics();
        
        // Create a gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), 
                                                  getWidth(), getHeight(), new Color(176, 224, 230));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 800, 600);
        
        // Add text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("3D Face Recognition System", 200, 300);
        
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}