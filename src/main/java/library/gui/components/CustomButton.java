package library.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static library.gui.UIHelper.PRIMARY;
import static library.gui.UIHelper.PRIMARY_HOVER;
import static library.gui.UIHelper.isDarkMode;
import static library.gui.UIHelper.BORDER;

/**
 * A professional, flat-styled button with modern hover effects.
 * Eliminates the outdated look of default Swing buttons.
 */
public class CustomButton extends JButton {

    private final Color defaultColor;
    private final Color hoverColor;

    public CustomButton(String text, Color defaultColor, Color hoverColor) {
        super(text);
        this.defaultColor = isDarkMode() ? softenForDarkMode(defaultColor) : defaultColor;
        this.hoverColor = isDarkMode() ? softenForDarkMode(hoverColor) : hoverColor;

        setFocusPainted(false);
        setBorderPainted(true);
        setForeground(readableTextColor(this.defaultColor));
        setBackground(this.defaultColor);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 36));
        setMaximumSize(new Dimension(200, 42));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        setOpaque(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(CustomButton.this.hoverColor);
                    setForeground(readableTextColor(CustomButton.this.hoverColor));
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(105, 145, 220), 1),
                            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(CustomButton.this.defaultColor);
                    setForeground(readableTextColor(CustomButton.this.defaultColor));
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER, 1),
                            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
                }
            }
        });

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(isDarkMode()
                        ? new Color(105, 145, 220)
                        : new Color(45, 105, 220), 2),
                    BorderFactory.createEmptyBorder(7, 13, 7, 13)));
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            }
        });

        addPropertyChangeListener("enabled", evt -> {
            if (isEnabled()) {
                setBackground(this.defaultColor);
                setForeground(readableTextColor(this.defaultColor));
            } else {
                setBackground(isDarkMode() ? new Color(43, 49, 62) : new Color(226, 231, 238));
                setForeground(isDarkMode() ? new Color(126, 139, 156) : new Color(135, 145, 158));
            }
        });
    }

    public CustomButton(String text) {
        // Default to the requested blue theme
        this(text, new Color(52, 120, 246), new Color(32, 98, 220));
    }

    private static Color softenForDarkMode(Color color) {
        if (color.equals(PRIMARY) || color.equals(PRIMARY_HOVER)) {
            return color.equals(PRIMARY) ? new Color(49, 73, 116) : new Color(62, 90, 142);
        }
        // in dark mode, avoid overly darkening button accents — make them slightly brighter
        int r = Math.min(255, color.getRed() + 26);
        int g = Math.min(255, color.getGreen() + 26);
        int b = Math.min(255, color.getBlue() + 26);
        return new Color(r, g, b);
    }

    private static Color readableTextColor(Color background) {
        double luminance = relativeLuminance(background);
        double whiteContrast = (1.0 + 0.05) / (luminance + 0.05);
        double darkContrast = (luminance + 0.05) / 0.05;
        return darkContrast >= whiteContrast ? new Color(20, 28, 40) : Color.WHITE;
    }

    private static double relativeLuminance(Color color) {
        double r = linearColor(color.getRed());
        double g = linearColor(color.getGreen());
        double b = linearColor(color.getBlue());
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    private static double linearColor(int value) {
        double channel = value / 255.0;
        return channel <= 0.03928
                ? channel / 12.92
                : Math.pow((channel + 0.055) / 1.055, 2.4);
    }
}
