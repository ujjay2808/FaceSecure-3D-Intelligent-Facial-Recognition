package src;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class FeatureSpaceVisualizer extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private double[][] features;
    private String classification;
    
    public FeatureSpaceVisualizer(double[][] features, String classification) {
        this.features = features;
        this.classification = classification;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Face Recognition Results - Matched: " + classification);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create visualization panel

        SimpleChartPanel chartPanel = new SimpleChartPanel();
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        
        // Create info panel

        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(Color.LIGHT_GRAY);
        
        // Results summary

        JLabel resultLabel = new JLabel("RECOGNITION RESULT: Face matched to '" + classification + "'");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(Color.BLUE);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Statistics

        int trainingPoints = Math.max(0, features.length - 1);
        int totalPoints = features.length;
        
        JLabel statsLabel = new JLabel("Training Faces: " + trainingPoints + " | Probe Face: 1 | Total Points: " + totalPoints);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(resultLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(statsLabel);
        
        return infoPanel;
    }
    
    private class SimpleChartPanel extends JPanel {
        private static final int MARGIN = 80;
        private static final int POINT_SIZE = 8;
        private static final int PROBE_SIZE = 12;
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int plotWidth = width - 2 * MARGIN;
            int plotHeight = height - 2 * MARGIN;
            
            // Draw background

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Draw title

            drawTitle(g2d, width);
            
            // Draw chart area

            drawChartArea(g2d, width, height, plotWidth, plotHeight);
            
            // Draw data points

            drawDataPoints(g2d, width, height, plotWidth, plotHeight);
        }
        
        private void drawTitle(Graphics2D g2d, int width) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String title = "Face Recognition - Feature Space Visualization";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(title, (width - fm.stringWidth(title)) / 2, 30);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            String subtitle = "Each point represents a face in feature space";
            g2d.drawString(subtitle, (width - fm.stringWidth(subtitle)) / 2, 50);
        }
        
        private void drawChartArea(Graphics2D g2d, int width, int height, int plotWidth, int plotHeight) {
            
            // Draw bounding box

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(MARGIN, MARGIN, plotWidth, plotHeight);
            
            // Draw axis labels

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Feature Dimension 1", MARGIN + plotWidth/2 - 50, height - 20);
            
            // Draw Y-axis label (rotated)

            AffineTransform original = g2d.getTransform();
            g2d.rotate(-Math.PI/2, 30, MARGIN + plotHeight/2);
            g2d.drawString("Feature Dimension 2", 30, MARGIN + plotHeight/2);
            g2d.setTransform(original);
            
            // Draw legend

            drawLegend(g2d, width, height);
        }
        
        private void drawLegend(Graphics2D g2d, int width, int height) {
            int legendX = width - 150;
            int legendY = 70;
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Legend:", legendX, legendY);
            
            // Training data

            g2d.setColor(new Color(0, 100, 200));
            g2d.fill(new Ellipse2D.Double(legendX, legendY + 15, POINT_SIZE, POINT_SIZE));
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.drawString("Training Faces", legendX + 15, legendY + 22);
            
            // Probe face

            g2d.setColor(Color.RED);
            g2d.fill(new Ellipse2D.Double(legendX, legendY + 35, PROBE_SIZE, PROBE_SIZE));
            g2d.setColor(Color.BLACK);
            g2d.drawString("Probe Face", legendX + 15, legendY + 42);
        }
        
        private void drawDataPoints(Graphics2D g2d, int width, int height, int plotWidth, int plotHeight) {
            if (features == null || features.length == 0) return;
            
            // Draw training points (blue)

            g2d.setColor(new Color(0, 100, 200));
            for (int i = 0; i < features.length - 1; i++) {
                if (features[i].length >= 2) {
                    int x = MARGIN + (int)(features[i][0] * plotWidth / 100.0);
                    int y = MARGIN + (int)((100 - features[i][1]) * plotHeight / 100.0); // Invert Y for better display
                    g2d.fill(new Ellipse2D.Double(x - POINT_SIZE/2, y - POINT_SIZE/2, POINT_SIZE, POINT_SIZE));
                }
            }
            
            // Draw probe point (red) - last point

            if (features.length > 0) {
                double[] probe = features[features.length - 1];
                if (probe.length >= 2) {
                    int x = MARGIN + (int)(probe[0] * plotWidth / 100.0);
                    int y = MARGIN + (int)((100 - probe[1]) * plotHeight / 100.0);
                    
                    g2d.setColor(Color.RED);
                    g2d.fill(new Ellipse2D.Double(x - PROBE_SIZE/2, y - PROBE_SIZE/2, PROBE_SIZE, PROBE_SIZE));
                    
                    // Highlight probe point
                    
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new Line2D.Double(x - PROBE_SIZE, y, x + PROBE_SIZE, y));
                    g2d.draw(new Line2D.Double(x, y - PROBE_SIZE, x, y + PROBE_SIZE));
                }
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(700, 500);
        }
    }
}