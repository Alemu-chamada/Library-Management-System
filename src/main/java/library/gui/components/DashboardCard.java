package library.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static library.gui.UIHelper.*;

/**
 * A highly professional dashboard card with an icon, title, count, and description.
 * Includes interactive hover effects and click navigation.
 */
public class DashboardCard extends JPanel {

    private final Color accentColor;
    private final JLabel titleLbl;
    private final JLabel descLbl;
    private boolean isHovered = false;

    public DashboardCard(String title, String count, String desc, Color accentColor, Runnable onClick) {
        this.accentColor = accentColor;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setPreferredSize(new Dimension(150, 96));
        setMinimumSize(new Dimension(110, 96));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        titleLbl = new JLabel(" " + title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_DARK);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valLbl = new JLabel(count);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLbl.setForeground(accentColor);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLbl.setForeground(TEXT_GRAY);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(titleLbl);
        add(Box.createVerticalStrut(3));
        add(valLbl);
        add(Box.createVerticalStrut(3));
        add(descLbl);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        titleLbl.setForeground(TEXT_DARK);
        descLbl.setForeground(TEXT_GRAY);

        g2.setColor(isHovered ? CARD_HOVER_BG : CARD_BG);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

        // Draw accent bar on the left
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 5, getHeight() - 1, 14, 14);
        
        // Fix corner overlap to keep the right side sharp if needed, 
        // but round rectangles look softer and better.
        g2.fillRect(5, 0, 5, getHeight() - 1); 

        // Draw a soft border
        g2.setColor(BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

        g2.dispose();
    }
}
