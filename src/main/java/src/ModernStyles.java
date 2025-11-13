package src;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ModernStyles {
    
    // Modern color palette
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Light Blue
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);      // Red
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);    // Green
    public static final Color WARNING_COLOR = new Color(241, 196, 15);    // Yellow
    public static final Color DARK_BG = new Color(44, 62, 80);            // Dark Blue
    public static final Color LIGHT_BG = new Color(236, 240, 241);        // Light Gray
    public static final Color CARD_BG = Color.WHITE;
    
    // Modern fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Modern borders
    public static Border createModernBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }
    
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }
    
    public static Border createRoundedBorder(Color color, int thickness) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, thickness),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        );
    }
    
    // Modern button styling
    public static void styleModernButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    public static void stylePrimaryButton(JButton button) {
        styleModernButton(button, PRIMARY_COLOR, Color.WHITE);
    }
    
    public static void styleSecondaryButton(JButton button) {
        styleModernButton(button, Color.WHITE, PRIMARY_COLOR);
    }
    
    public static void styleSuccessButton(JButton button) {
        styleModernButton(button, SUCCESS_COLOR, Color.WHITE);
    }
    
    // Modern panel styling
    public static void styleModernPanel(JPanel panel) {
        panel.setBackground(LIGHT_BG);
        panel.setBorder(createModernBorder());
    }
    
    public static void styleCardPanel(JPanel panel) {
        panel.setBackground(CARD_BG);
        panel.setBorder(createCardBorder());
    }
    
    // Modern label styling
    public static void styleTitleLabel(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(DARK_BG);
    }
    
    public static void styleHeaderLabel(JLabel label) {
        label.setFont(HEADER_FONT);
        label.setForeground(DARK_BG);
    }
    
    public static void styleBodyLabel(JLabel label) {
        label.setFont(BODY_FONT);
        label.setForeground(DARK_BG);
    }
}