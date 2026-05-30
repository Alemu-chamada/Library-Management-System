package library.gui.util;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Reusable UI factory methods, theme colors, and shared styling for the app.
 */
public final class UIHelper {

    public static final Color PRIMARY = new Color(52, 120, 246);
    public static final Color PRIMARY_HOVER = new Color(32, 98, 220);
    public static final Color DANGER = new Color(231, 76, 60);
    public static final Color DANGER_HOVER = new Color(192, 57, 43);
    public static final Color DANGER_LIGHT = new Color(255, 168, 168);
    public static final Color DANGER_LIGHT_HOVER = new Color(255, 134, 134);
    public static final Color SUCCESS = new Color(46, 204, 113);
    public static final Color AMBER = new Color(243, 156, 18);
    public static final Color PURPLE = new Color(155, 89, 182);

    public static Color SIDEBAR_BG;
    public static Color SIDEBAR_HOVER;
    public static Color SIDEBAR_ACTIVE;
    public static Color SIDEBAR_CARD_BG;
    public static Color HEADER_BG;
    public static Color CONTENT_BG;
    public static Color CARD_BG;
    public static Color CARD_HOVER_BG;
    public static Color PANEL_BG;
    public static Color FIELD_BG;
    public static Color TEXT_DARK;
    public static Color TEXT_GRAY;
    public static Color TEXT_LIGHT;
    public static Color BORDER;
    public static Color TABLE_ALT_ROW;
    public static Color TABLE_HEADER_BG;
    public static Color TABLE_SELECTION_BG;
    public static Color SELECTION_TEXT;
    public static Color LIST_ALT_ROW;
    public static Color LIST_SELECTION_BG;
    public static Color PLACEHOLDER_TEXT;
    public static Color STATUS_SUCCESS_BG;
    public static Color STATUS_DANGER_BG;
    public static Color STATUS_AMBER_BG;

    private static boolean darkMode = false;

    private UIHelper() {}

    static {
        applyLightPalette();
        applyOptionPaneTheme();
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
        if (enabled) {
            applyDarkPalette();
        } else {
            applyLightPalette();
        }
        applyOptionPaneTheme();
    }

    private static void applyLightPalette() {
        SIDEBAR_BG = new Color(14, 23, 45);
        SIDEBAR_HOVER = new Color(24, 38, 70);
        SIDEBAR_ACTIVE = PRIMARY;
        SIDEBAR_CARD_BG = new Color(20, 31, 56);
        HEADER_BG = new Color(14, 23, 45);
        CONTENT_BG = new Color(245, 247, 250);
        CARD_BG = Color.WHITE;
        CARD_HOVER_BG = new Color(248, 250, 253);
        PANEL_BG = new Color(248, 250, 253);
        FIELD_BG = Color.WHITE;
        TEXT_DARK = new Color(20, 28, 40);
        TEXT_GRAY = new Color(100, 110, 125);
        TEXT_LIGHT = new Color(200, 210, 220);
        BORDER = new Color(215, 220, 228);
        TABLE_ALT_ROW = new Color(248, 250, 253);
        TABLE_HEADER_BG = new Color(240, 242, 246);
        TABLE_SELECTION_BG = new Color(210, 228, 245);
        SELECTION_TEXT = TEXT_DARK;
        LIST_ALT_ROW = new Color(241, 247, 255);
        LIST_SELECTION_BG = new Color(210, 228, 245);
        PLACEHOLDER_TEXT = new Color(130, 140, 150);
        STATUS_SUCCESS_BG = new Color(230, 245, 235);
        STATUS_DANGER_BG = new Color(253, 232, 232);
        STATUS_AMBER_BG = new Color(253, 242, 230);
    }

    private static void applyDarkPalette() {
        SIDEBAR_BG = new Color(12, 18, 34);
        SIDEBAR_HOVER = new Color(22, 30, 50);
        SIDEBAR_ACTIVE = new Color(59, 130, 246);
        SIDEBAR_CARD_BG = new Color(26, 35, 54);
        HEADER_BG = new Color(18, 24, 40);
        CONTENT_BG = new Color(28, 34, 52);
        CARD_BG = new Color(34, 42, 56);
        CARD_HOVER_BG = new Color(44, 52, 68);
        PANEL_BG = new Color(32, 38, 50);
        FIELD_BG = new Color(34, 42, 54);
        TEXT_DARK = new Color(235, 241, 250);
        TEXT_GRAY = new Color(164, 176, 190);
        TEXT_LIGHT = new Color(215, 224, 236);
        BORDER = new Color(68, 79, 101);
        TABLE_ALT_ROW = new Color(34, 40, 53);
        TABLE_HEADER_BG = new Color(36, 44, 58);
        TABLE_SELECTION_BG = new Color(58, 96, 185);
        SELECTION_TEXT = new Color(242, 248, 255);
        LIST_ALT_ROW = new Color(34, 40, 53);
        LIST_SELECTION_BG = new Color(56, 101, 212);
        PLACEHOLDER_TEXT = new Color(140, 151, 169);
        STATUS_SUCCESS_BG = new Color(31, 97, 69);
        STATUS_DANGER_BG = new Color(110, 51, 59);
        STATUS_AMBER_BG = new Color(115, 84, 43);
    }

    private static void applyOptionPaneTheme() {
        UIManager.put("OptionPane.background", CARD_BG);
        UIManager.put("Panel.background", CARD_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        UIManager.put("Button.background", dialogPrimaryBackground());
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", dialogPrimaryHoverBackground());
        UIManager.put("Button.focus", dialogFocusBorder());
        UIManager.put("Button.disabledText", dialogDisabledForeground());
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Button.margin", new Insets(8, 16, 8, 16));
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text,
                dangerButtonBackground(),
                dangerButtonHoverBackground(),
                dangerButtonText(),
                dangerButtonText());
    }

    public static Color dangerButtonBackground() {
        return darkMode ? DANGER : DANGER_LIGHT;
    }

    public static Color dangerButtonHoverBackground() {
        return darkMode ? DANGER_HOVER : DANGER_LIGHT_HOVER;
    }

    public static Color dangerButtonText() {
        return darkMode ? Color.WHITE : new Color(20, 28, 40);
    }

    private static JButton createStyledButton(String text, Color bg, Color hover) {
        return createStyledButton(text, bg, hover, readableTextColor(darkMode ? softenForDarkMode(bg) : bg), readableTextColor(darkMode ? softenForDarkMode(hover) : hover));
    }

    private static JButton createStyledButton(String text, Color bg, Color hover, Color fg, Color hoverFg) {
        Color normalBg = darkMode ? softenForDarkMode(bg) : bg;
        Color hoverBg = darkMode ? softenForDarkMode(hover) : hover;
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(normalBg);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                    btn.setForeground(hoverFg);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(dialogFocusBorder(), 1),
                            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(normalBg);
                    btn.setForeground(fg);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER, 1),
                            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
                }
            }
        });
        btn.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(dialogFocusBorder(), 2),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
            }
            public void focusLost(FocusEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
            }
        });
        btn.addPropertyChangeListener("enabled", evt -> {
            if (btn.isEnabled()) {
                btn.setBackground(normalBg);
                btn.setForeground(fg);
            } else {
                btn.setBackground(dialogDisabledBackground());
                btn.setForeground(dialogDisabledForeground());
            }
        });
        return btn;
    }

    public static JTextField createField(String placeholder) {
        JTextField field = new JTextField(22);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setToolTipText(placeholder);
        styleField(field);
        field.setForeground(PLACEHOLDER_TEXT);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(PLACEHOLDER_TEXT);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(22);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setToolTipText(placeholder);
        styleField(field);
        field.setForeground(PLACEHOLDER_TEXT);
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                    field.setEchoChar('\u2022');
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setForeground(PLACEHOLDER_TEXT);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    public static String getPasswordFieldText(JPasswordField field, String placeholder) {
        String text = String.valueOf(field.getPassword()).trim();
        if (text.isEmpty() || text.equals(placeholder)) {
            return "";
        }
        return text;
    }

    public static void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setCaretColor(TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    public static String getFieldText(JTextField field) {
        String text = field.getText().trim();
        Color fg = field.getForeground();
        if (fg.equals(PLACEHOLDER_TEXT)) return "";
        return text;
    }

    public static JPanel createFormPanel(String[] labels, JComponent[] fields) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 14);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(TEXT_DARK);
            lbl.setPreferredSize(new Dimension(90, 28));
            form.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            form.add(fields[i], gbc);
        }
        return form;
    }

    public static JTable styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setSelectionForeground(SELECTION_TEXT);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setRowMargin(0);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBackground(TABLE_HEADER_BG);
                setForeground(TEXT_DARK);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
                return this;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String header = t.getColumnName(col);
                if (header.equals("ID") || header.equals("Title") || header.equals("Author")
                        || header.equals("Book") || header.equals("User")) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
                if (sel) {
                    setBackground(TABLE_SELECTION_BG);
                    setForeground(SELECTION_TEXT);
                    return this;
                }
                if (!sel) {
                    setBackground(row % 2 == 0 ? CARD_BG : TABLE_ALT_ROW);
                }
                if (val instanceof String s) {
                    if (s.equals("Available")) setForeground(SUCCESS);
                    else if (s.equals("Out of stock")) setForeground(DANGER);
                    else if (s.equals("Issued")) setForeground(AMBER);
                    else if (s.equals("ACTIVE")) setForeground(AMBER);
                    else if (s.equals("Returned")) setForeground(SUCCESS);
                    else if (s.matches(".*\\([^()]+\\)$")) setForeground(PRIMARY);
                    else setForeground(TEXT_DARK);
                } else {
                    setForeground(TEXT_DARK);
                }
                return this;
            }
        });
        return table;
    }

    public static JScrollPane styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(CARD_BG);
        sp.setBackground(CARD_BG);
        return sp;
    }

    public static Color statusBackground(Color accentColor) {
        if (accentColor.equals(SUCCESS)) return STATUS_SUCCESS_BG;
        if (accentColor.equals(DANGER)) return STATUS_DANGER_BG;
        if (accentColor.equals(AMBER)) return STATUS_AMBER_BG;
        return darkMode ? new Color(35, 59, 93) : new Color(232, 241, 255);
    }

    public static JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(175, 82));

        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_GRAY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valLbl.setForeground(accentColor);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valLbl);
        return card;
    }

    public static void showSuccess(Component parent, String msg) {
        showStyledMessage(parent, "Success", msg, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        showStyledMessage(parent, "Error", msg, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        JOptionPane op = createStyledOptionPane(createStyledPanel(msg),
                JOptionPane.QUESTION_MESSAGE, "Confirm", "Cancel");
        op.createDialog(parent, "Confirm").setVisible(true);
        return "Confirm".equals(op.getValue());
    }

    public static boolean confirmWarning(Component parent, String title, String msg) {
        JOptionPane op = createStyledOptionPane(createWarningPanel(msg, title),
                JOptionPane.WARNING_MESSAGE, "Confirm", "Cancel");
        op.createDialog(parent, title).setVisible(true);
        return "Confirm".equals(op.getValue());
    }

    private static void showStyledMessage(Component parent, String title, String msg, int messageType) {
        JOptionPane op = createStyledOptionPane(createStyledPanel(msg), messageType, "OK");
        op.createDialog(parent, title).setVisible(true);
    }

    private static JOptionPane createStyledOptionPane(Object message, int messageType, String... options) {
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION);
        pane.setBackground(CARD_BG);
        pane.setOpaque(true);

        JButton[] buttons = new JButton[options.length];
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            JButton btn = new JButton(option);
            styleDialogButton(btn);
            btn.addActionListener(e -> pane.setValue(option));
            buttons[i] = btn;
        }
        pane.setOptions(buttons);
        if (buttons.length > 0) {
            pane.setInitialValue(buttons[0]);
        }
        return pane;
    }

    private static JPanel createStyledPanel(String msg) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        JLabel label = createDialogLabel(msg);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_DARK);
        label.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }

    private static JPanel createWarningPanel(String msg, String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        JLabel iconLabel = new JLabel("!");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        iconLabel.setForeground(AMBER);
        
        JLabel label = createDialogLabel(msg);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_DARK);
        label.setVerticalAlignment(SwingConstants.TOP);
        
        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setOpaque(false);
        content.add(iconLabel, BorderLayout.WEST);
        content.add(label, BorderLayout.CENTER);
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private static JLabel createDialogLabel(String msg) {
        String escaped = msg
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
        return new JLabel("<html><body style='width:320px'>" + escaped + "</body></html>");
    }

    private static void styleDialogButton(JButton btn) {
        Color normalBg = isNeutralDialogButton(btn.getText())
                ? dialogNeutralBackground()
                : dialogPrimaryBackground();
        Color hoverBg = isNeutralDialogButton(btn.getText())
                ? dialogNeutralHoverBackground()
                : dialogPrimaryHoverBackground();
        Color normalFg = isNeutralDialogButton(btn.getText())
                ? dialogNeutralForeground()
                : readableTextColor(normalBg);
        Color hoverFg = isNeutralDialogButton(btn.getText())
                ? dialogNeutralForeground()
                : readableTextColor(hoverBg);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(normalFg);
        btn.setBackground(normalBg);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                    btn.setForeground(hoverFg);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(dialogFocusBorder(), 1),
                            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(normalBg);
                    btn.setForeground(normalFg);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER, 1),
                            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
                }
            }
        });
        btn.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(dialogFocusBorder(), 2),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
            }
            public void focusLost(FocusEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
            }
        });
        btn.addPropertyChangeListener("enabled", evt -> {
            if (btn.isEnabled()) {
                btn.setBackground(normalBg);
                btn.setForeground(normalFg);
            } else {
                btn.setBackground(dialogDisabledBackground());
                btn.setForeground(dialogDisabledForeground());
            }
        });
    }

    public static void showWarning(Component parent, String title, String msg) {
        JOptionPane op = createStyledOptionPane(createWarningPanel(msg, title),
                JOptionPane.WARNING_MESSAGE, "OK");
        op.createDialog(parent, title).setVisible(true);
    }

    private static boolean isNeutralDialogButton(String text) {
        return "Cancel".equalsIgnoreCase(text)
                || "No".equalsIgnoreCase(text)
                || "Close".equalsIgnoreCase(text);
    }

    private static Color dialogPrimaryBackground() {
        return darkMode ? new Color(49, 73, 116) : PRIMARY;
    }

    private static Color dialogPrimaryHoverBackground() {
        return darkMode ? new Color(62, 90, 142) : PRIMARY_HOVER;
    }

    private static Color dialogNeutralBackground() {
        return darkMode ? new Color(55, 65, 81) : new Color(234, 238, 244);
    }

    private static Color dialogNeutralHoverBackground() {
        return darkMode ? new Color(70, 82, 100) : new Color(220, 226, 236);
    }

    private static Color dialogNeutralForeground() {
        return darkMode ? new Color(235, 241, 250) : TEXT_DARK;
    }

    private static Color dialogDisabledBackground() {
        return darkMode ? new Color(43, 49, 62) : new Color(226, 231, 238);
    }

    private static Color dialogDisabledForeground() {
        return darkMode ? new Color(126, 139, 156) : new Color(135, 145, 158);
    }

    private static Color dialogFocusBorder() {
        return darkMode ? new Color(105, 145, 220) : new Color(45, 105, 220);
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
