package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class LogoUtil {

    private static ImageIcon logoIcon = null;

    // Load logo from various locations
    public static ImageIcon loadLogo() {
        if (logoIcon != null) return logoIcon;

        String[] paths = {
                "resources/logo.png",
                "resources/logo.jpg",
                "logo.png",
                "logo.jpg",
                "src/resources/logo.png",
                "images/logo.png",
                "images/logo.jpg",
                "../resources/logo.png"
        };

        for (String path : paths) {
            try {
                // Try as file
                File file = new File(path);
                if (file.exists()) {
                    logoIcon = new ImageIcon(file.getAbsolutePath());
                    if (logoIcon != null) return logoIcon;
                }

                // Try as resource
                URL url = LogoUtil.class.getClassLoader().getResource(path);
                if (url != null) {
                    logoIcon = new ImageIcon(url);
                    if (logoIcon != null) return logoIcon;
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }

        // Create default logo
        logoIcon = createDefaultLogo();
        return logoIcon;
    }

    // Create default logo with university initials
    public static ImageIcon createDefaultLogo() {
        int size = 64;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 102, 204), size, size, new Color(0, 51, 102));
        g2d.setPaint(gradient);
        g2d.fillOval(0, 0, size, size);

        // Border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(2, 2, size - 4, size - 4);

        // Draw text "FT" (Faculty of Technology)
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        String text = "FT";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2d.drawString(text, (size - textWidth) / 2, (size + textHeight) / 2 - 8);

        g2d.dispose();

        return new ImageIcon(image);
    }

    // Set logo for a JFrame
    public static void setFrameIcon(JFrame frame) {
        ImageIcon icon = loadLogo();
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }
    }

    // Set logo for a JDialog
    public static void setDialogIcon(JDialog dialog) {
        ImageIcon icon = loadLogo();
        if (icon != null) {
            dialog.setIconImage(icon.getImage());
        }
    }

    // Get scaled logo for buttons
    public static ImageIcon getScaledLogo(int width, int height) {
        ImageIcon icon = loadLogo();
        if (icon != null) {
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        }
        return null;
    }
}
