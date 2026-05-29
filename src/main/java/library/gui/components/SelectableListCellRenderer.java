package library.gui.components;

import library.gui.UIHelper;

import javax.swing.*;
import java.awt.*;

public class SelectableListCellRenderer extends JPanel implements ListCellRenderer<String> {

    private final JLabel label = new JLabel();

    public SelectableListCellRenderer() {
        setLayout(new BorderLayout());
        setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        add(label, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        boolean dark = UIHelper.isDarkMode();

        Color bg;
        Color fg;

        Integer hover = (Integer) list.getClientProperty("hoverIndex");
        boolean isHover = hover != null && hover == index;

        if (isSelected) {
            bg = UIHelper.LIST_SELECTION_BG;
            fg = UIHelper.SELECTION_TEXT;
        } else if (isHover) {
            bg = UIHelper.CARD_HOVER_BG;
            fg = dark ? UIHelper.TEXT_LIGHT : UIHelper.TEXT_DARK;
        } else {
            bg = (index % 2 == 0) ? UIHelper.LIST_ALT_ROW : UIHelper.CARD_BG;
            fg = UIHelper.PRIMARY;
        }

        label.setText(value);
        label.setForeground(fg);
        label.setOpaque(false);

        // panel background used for rounded box painting
        setBackground(bg);
        setToolTipText(value);

        // set preferred height consistent with existing lists
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = getBackground();
        if (bg != null) {
            g2.setColor(bg);
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
            g2.setColor(UIHelper.BORDER);
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
