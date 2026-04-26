package utils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonStyleUtil {

    // Bright Color constants
    public static final Color PRIMARY_BLUE = new Color(41, 128, 185);
    public static final Color BRIGHT_BLUE = new Color(52, 152, 219);
    public static final Color SUCCESS_GREEN = new Color(39, 174, 96);
    public static final Color BRIGHT_GREEN = new Color(46, 204, 113);
    public static final Color DANGER_RED = new Color(231, 76, 60);
    public static final Color BRIGHT_RED = new Color(241, 76, 60);
    public static final Color WARNING_ORANGE = new Color(230, 126, 34);
    public static final Color BRIGHT_ORANGE = new Color(243, 156, 18);
    public static final Color PURPLE = new Color(155, 89, 182);
    public static final Color BRIGHT_PURPLE = new Color(165, 105, 189);
    public static final Color DARK_GRAY = new Color(52, 73, 94);
    public static final Color BRIGHT_CYAN = new Color(26, 188, 156);
    public static final Color BRIGHT_PINK = new Color(232, 67, 147);
    public static final Color BRIGHT_YELLOW = new Color(241, 196, 15);
    public static final Color BRIGHT_TEAL = new Color(22, 160, 133);

    // Hover colors (brighter versions)
    public static final Color PRIMARY_BLUE_HOVER = new Color(31, 97, 141);
    public static final Color BRIGHT_BLUE_HOVER = new Color(41, 128, 185);
    public static final Color SUCCESS_GREEN_HOVER = new Color(29, 132, 73);
    public static final Color BRIGHT_GREEN_HOVER = new Color(39, 174, 96);
    public static final Color DANGER_RED_HOVER = new Color(192, 57, 43);
    public static final Color BRIGHT_RED_HOVER = new Color(231, 76, 60);
    public static final Color WARNING_ORANGE_HOVER = new Color(184, 101, 27);
    public static final Color BRIGHT_ORANGE_HOVER = new Color(230, 126, 34);
    public static final Color PURPLE_HOVER = new Color(124, 71, 145);
    public static final Color BRIGHT_PURPLE_HOVER = new Color(155, 89, 182);
    public static final Color BRIGHT_CYAN_HOVER = new Color(22, 160, 133);
    public static final Color BRIGHT_PINK_HOVER = new Color(200, 50, 120);
    public static final Color BRIGHT_YELLOW_HOVER = new Color(210, 170, 10);
    public static final Color BRIGHT_TEAL_HOVER = new Color(18, 140, 116);

    // Style any JButton with bright colors
    public static void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Remove default UI for flat look
        button.setUI(new BasicButtonUI());

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    // Style rounded button
    public static void styleRoundedButton(JButton button, Color bgColor, Color hoverColor) {
        styleButton(button, bgColor, hoverColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    // Different button styles
    public static void stylePrimaryButton(JButton button) {
        styleButton(button, PRIMARY_BLUE, PRIMARY_BLUE_HOVER);
    }

    public static void styleBrightBlueButton(JButton button) {
        styleButton(button, BRIGHT_BLUE, BRIGHT_BLUE_HOVER);
    }

    public static void styleSuccessButton(JButton button) {
        styleButton(button, SUCCESS_GREEN, SUCCESS_GREEN_HOVER);
    }

    public static void styleBrightGreenButton(JButton button) {
        styleButton(button, BRIGHT_GREEN, BRIGHT_GREEN_HOVER);
    }

    public static void styleDangerButton(JButton button) {
        styleButton(button, DANGER_RED, DANGER_RED_HOVER);
    }

    public static void styleBrightRedButton(JButton button) {
        styleButton(button, BRIGHT_RED, BRIGHT_RED_HOVER);
    }

    public static void styleWarningButton(JButton button) {
        styleButton(button, WARNING_ORANGE, WARNING_ORANGE_HOVER);
    }

    public static void styleBrightOrangeButton(JButton button) {
        styleButton(button, BRIGHT_ORANGE, BRIGHT_ORANGE_HOVER);
    }

    public static void stylePurpleButton(JButton button) {
        styleButton(button, PURPLE, PURPLE_HOVER);
    }

    public static void styleBrightPurpleButton(JButton button) {
        styleButton(button, BRIGHT_PURPLE, BRIGHT_PURPLE_HOVER);
    }

    public static void styleCyanButton(JButton button) {
        styleButton(button, BRIGHT_CYAN, BRIGHT_CYAN_HOVER);
    }

    public static void stylePinkButton(JButton button) {
        styleButton(button, BRIGHT_PINK, BRIGHT_PINK_HOVER);
    }

    public static void styleYellowButton(JButton button) {
        styleButton(button, BRIGHT_YELLOW, BRIGHT_YELLOW_HOVER);
        button.setForeground(Color.BLACK); // Yellow background needs black text
    }

    public static void styleTealButton(JButton button) {
        styleButton(button, BRIGHT_TEAL, BRIGHT_TEAL_HOVER);
    }

    // Create styled buttons directly
    public static JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        styleButton(button, bgColor, hoverColor);
        return button;
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_BLUE, PRIMARY_BLUE_HOVER);
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS_GREEN, SUCCESS_GREEN_HOVER);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER_RED, DANGER_RED_HOVER);
    }

    public static JButton createWarningButton(String text) {
        return createStyledButton(text, WARNING_ORANGE, WARNING_ORANGE_HOVER);
    }

    public static JButton createPurpleButton(String text) {
        return createStyledButton(text, PURPLE, PURPLE_HOVER);
    }

    public static JButton createInfoButton(String text) {
        return createStyledButton(text, BRIGHT_BLUE, BRIGHT_BLUE_HOVER);
    }

    public static JButton createCyanButton(String text) {
        return createStyledButton(text, BRIGHT_CYAN, BRIGHT_CYAN_HOVER);
    }

    public static JButton createPinkButton(String text) {
        return createStyledButton(text, BRIGHT_PINK, BRIGHT_PINK_HOVER);
    }

    public static JButton createTealButton(String text) {
        return createStyledButton(text, BRIGHT_TEAL, BRIGHT_TEAL_HOVER);
    }
}
