import ui.LoginUI;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Use cross-platform L&F so custom button colours are NOT overridden
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Global UI defaults – ensures buttons always honour setBackground / setForeground
            UIManager.put("Button.background",          new Color(25, 118, 210));
            UIManager.put("Button.foreground",          Color.WHITE);
            UIManager.put("Button.select",              new Color(13, 71, 161));
            UIManager.put("Button.focus",               new Color(25, 118, 210));
            UIManager.put("Button.border",              BorderFactory.createEmptyBorder(6,14,6,14));
            UIManager.put("Button.font",                new Font("Arial", Font.BOLD, 12));

            UIManager.put("ComboBox.background",        Color.WHITE);
            UIManager.put("ComboBox.foreground",        new Color(13, 71, 161));
            UIManager.put("ComboBox.font",              new Font("Arial", Font.PLAIN, 12));

            UIManager.put("TextField.background",       Color.WHITE);
            UIManager.put("TextField.foreground",       new Color(13, 71, 161));
            UIManager.put("TextField.caretForeground",  new Color(13, 71, 161));
            UIManager.put("TextField.font",             new Font("Arial", Font.PLAIN, 12));

            UIManager.put("TextArea.background",        Color.WHITE);
            UIManager.put("TextArea.foreground",        new Color(13, 71, 161));
            UIManager.put("TextArea.font",              new Font("Arial", Font.PLAIN, 12));

            UIManager.put("Label.font",                 new Font("Arial", Font.PLAIN, 12));
            UIManager.put("Label.foreground",           new Color(13, 71, 161));

            UIManager.put("Panel.background",           new Color(227, 242, 253));

            UIManager.put("TabbedPane.background",      new Color(227, 242, 253));
            UIManager.put("TabbedPane.foreground",      new Color(13, 71, 161));
            UIManager.put("TabbedPane.font",            new Font("Arial", Font.BOLD, 12));
            UIManager.put("TabbedPane.selected",        Color.WHITE);
            UIManager.put("TabbedPane.selectedForeground", new Color(13, 71, 161));

            UIManager.put("Table.font",                 new Font("Arial", Font.PLAIN, 12));
            UIManager.put("TableHeader.font",           new Font("Arial", Font.BOLD, 12));
            UIManager.put("TableHeader.background",     new Color(13, 71, 161));
            UIManager.put("TableHeader.foreground",     Color.WHITE);

            UIManager.put("ScrollPane.border",          BorderFactory.createEmptyBorder());

            UIManager.put("OptionPane.background",      new Color(227, 242, 253));
            UIManager.put("OptionPane.messageForeground", new Color(13, 71, 161));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}
