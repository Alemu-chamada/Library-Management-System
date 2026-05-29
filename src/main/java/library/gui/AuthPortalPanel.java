package library.gui;

import library.exception.InvalidInputException;
import library.gui.components.CustomButton;
import library.gui.components.VectorIcon;
import library.model.AuthAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

import static library.gui.UIHelper.*;
import static library.gui.components.VectorIcon.IconType.*;

/**
 * A highly polished, modern, and professional authentication portal (Login + Register).
 * Designed with a centered visual auth card, dynamic light/dark mode support,
 * custom glassmorphism backgrounds, and responsive layouts.
 */
public class AuthPortalPanel extends JPanel {

    private final LibrarySwingApp parentApp;
    private final LibraryController controller;
    private final Consumer<AuthAccount> onLoginSuccess;

    private JPanel cardContainer;
    private JPanel formCard;
    private CardLayout formLayout;
    private JPanel cornerPanel;
    private JPanel wrapper;

    // Login fields
    private JTextField loginUserField;
    private JPasswordField loginPassField;

    // Register fields
    private JTextField regUserField;
    private JTextField regNameField;
    private JTextField regEmailField;
    private JTextField regPhoneField;
    private JComboBox<String> regRoleCombo;
    private JPasswordField regPassField;
    private JPasswordField regConfirmPassField;

    public AuthPortalPanel(LibrarySwingApp parentApp, LibraryController controller, Consumer<AuthAccount> onLoginSuccess) {
        this.parentApp = parentApp;
        this.controller = controller;
        this.onLoginSuccess = onLoginSuccess;

        setLayout(new GridBagLayout());
        setOpaque(true);

        initUI();
    }

    private void initUI() {
        removeAll();
        setLayout(null); // absolute layout for responsive floats

        // 850x520 px rectangular auth card size
        Dimension cardSize = new Dimension(850, 530);

        // Centered Auth Card Container
        cardContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw drop shadow
                g2.setColor(isDarkMode() ? new Color(0, 0, 0, 80) : new Color(0, 0, 0, 24));
                for (int i = 0; i < 8; i++) {
                    g2.drawRoundRect(i, i, getWidth() - 2 * i - 1, getHeight() - 2 * i - 1, 20, 20);
                }

                // Fill card background
                g2.setColor(CARD_BG);
                g2.fillRoundRect(4, 4, getWidth() - 9, getHeight() - 9, 20, 20);

                // Draw border
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(4, 4, getWidth() - 9, getHeight() - 9, 20, 20);

                g2.dispose();
            }
        };
        cardContainer.setOpaque(false);
        cardContainer.setPreferredSize(cardSize);
        cardContainer.setMinimumSize(cardSize);
        cardContainer.setMaximumSize(cardSize);

        // Split the card into Left (Brand Panel) and Right (Auth Forms Panel)
        JPanel leftBrandPanel = buildBrandPanel();
        JPanel rightFormsPanel = buildFormsPanel();

        cardContainer.add(leftBrandPanel, BorderLayout.WEST);
        cardContainer.add(rightFormsPanel, BorderLayout.CENTER);

        // Center card wrapper using GridBagLayout
        wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(cardContainer, new GridBagConstraints());
        add(wrapper);

        // Quick dark/light mode toggle in the corner of the portal
        cornerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        cornerPanel.setOpaque(false);
        JButton modeToggle = new JButton(isDarkMode() ? "Light Mode" : "Dark Mode");
        modeToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        modeToggle.setForeground(TEXT_DARK);
        modeToggle.setBackground(CARD_BG);
        modeToggle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        modeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        modeToggle.setFocusPainted(false);
        modeToggle.addActionListener(e -> {
            setDarkMode(!isDarkMode());
            initUI();
            revalidate();
            repaint();
        });
        cornerPanel.add(modeToggle);
        add(cornerPanel);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (wrapper != null) {
            wrapper.setBounds(0, 0, getWidth(), getHeight());
        }
        if (cornerPanel != null) {
            Dimension d = cornerPanel.getPreferredSize();
            cornerPanel.setBounds(getWidth() - d.width - 16, getHeight() - d.height - 16, d.width, d.height);
        }
    }

    private JPasswordField createAuthPasswordField(String placeholder, JCheckBox toggleCheck) {
        JPasswordField field = new JPasswordField(22);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setToolTipText(placeholder);
        styleField(field);
        field.setForeground(PLACEHOLDER_TEXT);
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                    field.setEchoChar(toggleCheck.isSelected() ? (char) 0 : '\u2022');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setForeground(PLACEHOLDER_TEXT);
                    field.setText(placeholder);
                }
            }
        });
        
        toggleCheck.addActionListener(e -> {
            boolean show = toggleCheck.isSelected();
            if (!String.valueOf(field.getPassword()).equals(placeholder)) {
                field.setEchoChar(show ? (char) 0 : '\u2022');
            }
        });
        
        return field;
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Use premium primary colors for the brand panel
                Color startColor = new Color(14, 26, 56);
                Color endColor = new Color(34, 76, 154);
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2.setPaint(gp);

                // Clip to rounded corners on the left side
                g2.setClip(new RoundRectangle2D.Float(4, 4, getWidth() + 10, getHeight() - 9, 20, 20));
                g2.fillRect(4, 4, getWidth() + 10, getHeight() - 9);

                // Draw decorative minimal grid lines overlay
                g2.setColor(new Color(255, 255, 255, 12));
                int gridSpacing = 40;
                for (int x = 0; x < getWidth(); x += gridSpacing) {
                    g2.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += gridSpacing) {
                    g2.drawLine(0, y, getWidth(), y);
                }

                // Programmatically draw abstract glowing bubbles
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillOval(-80, -80, 240, 240);
                g2.fillOval(getWidth() - 120, getHeight() - 160, 200, 200);

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(380, 520));
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(36, 36, 36, 36));

        // Center content wrap
        JPanel textWrap = new JPanel();
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        textWrap.setOpaque(false);

        // Logo
        JLabel logoLabel = new JLabel(new VectorIcon(LIBRARY, 60, Color.WHITE));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("SmartLibrary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline
        JLabel tagLabel = new JLabel("Knowledge. Growth. Innovation.");
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagLabel.setForeground(new Color(210, 222, 245));
        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textWrap.add(Box.createVerticalGlue());
        textWrap.add(logoLabel);
        textWrap.add(Box.createVerticalStrut(16));
        textWrap.add(titleLabel);
        textWrap.add(Box.createVerticalStrut(6));
        textWrap.add(tagLabel);
        textWrap.add(Box.createVerticalGlue());

        // Footer in left panel
        JLabel footerLabel = new JLabel("ASTU Library Services © 2026");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(175, 195, 230));
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(textWrap, BorderLayout.CENTER);
        panel.add(footerLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildFormsPanel() {
        formLayout = new CardLayout();
        formCard = new JPanel(formLayout);
        formCard.setOpaque(false);

        // Build login view
        JPanel loginPanel = buildLoginPanel();
        // Build register view
        JPanel registerPanel = buildRegisterPanel();

        formCard.add(loginPanel, "LOGIN");
        formCard.add(registerPanel, "REGISTER");

        formLayout.show(formCard, "LOGIN");

        return formCard;
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 44, 40, 44));

        // Header Title
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Sign in to your library account to continue");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(TEXT_GRAY);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(24));

        // Inputs Layout Panel
        JPanel inputsPanel = new JPanel();
        inputsPanel.setLayout(new BoxLayout(inputsPanel, BoxLayout.Y_AXIS));
        inputsPanel.setOpaque(false);
        inputsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(TEXT_DARK);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginUserField = createField("Enter username");
        loginUserField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginUserField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(TEXT_DARK);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox loginShowPass = new JCheckBox("Show Password");
        loginShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginShowPass.setForeground(TEXT_GRAY);
        loginShowPass.setOpaque(false);
        loginShowPass.setFocusPainted(false);
        loginShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginPassField = createAuthPasswordField("Enter password", loginShowPass);
        loginPassField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        inputsPanel.add(userLabel);
        inputsPanel.add(Box.createVerticalStrut(4));
        inputsPanel.add(loginUserField);
        inputsPanel.add(Box.createVerticalStrut(10));
        inputsPanel.add(passLabel);
        inputsPanel.add(Box.createVerticalStrut(4));
        inputsPanel.add(loginPassField);
        inputsPanel.add(Box.createVerticalStrut(4));
        inputsPanel.add(loginShowPass);

        panel.add(inputsPanel);
        panel.add(Box.createVerticalStrut(24));

        // Action buttons
        CustomButton loginBtn = new CustomButton("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> performLogin());

        // Keyboard Action: Enter on username / password submit
        ActionListener enterSubmit = e -> performLogin();
        loginUserField.addActionListener(enterSubmit);
        loginPassField.addActionListener(enterSubmit);

        panel.add(loginBtn);
        panel.add(Box.createVerticalStrut(20));

        // Footer Register Link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setOpaque(false);
        linkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noAccLabel = new JLabel("Don't have an account?");
        noAccLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noAccLabel.setForeground(TEXT_GRAY);

        JLabel regLinkLabel = new JLabel("Register here");
        regLinkLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regLinkLabel.setForeground(PRIMARY);
        regLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                formLayout.show(formCard, "REGISTER");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                regLinkLabel.setText("<html><u>Register here</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                regLinkLabel.setText("Register here");
            }
        });

        linkPanel.add(noAccLabel);
        linkPanel.add(regLinkLabel);

        panel.add(linkPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(22, 44, 22, 44));

        // Header Title
        JLabel registerLabel = new JLabel("Create Account");
        registerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        registerLabel.setForeground(TEXT_DARK);
        registerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Sign up for student or staff portal access");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(TEXT_GRAY);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(registerLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(14));

        // Forms inside a scrollpane because there are many fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 3, 0);
        gbc.weightx = 1.0;

        regUserField = createField("Username");
        regNameField = createField("Full Name");
        regEmailField = createField("Email Address");
        regPhoneField = createField("Phone Number");

        // Role select combobox
        String[] roles = {"MEMBER", "LIBRARIAN", "ADMIN"};
        regRoleCombo = new JComboBox<>(roles);
        regRoleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        regRoleCombo.setBackground(FIELD_BG);
        regRoleCombo.setForeground(TEXT_DARK);
        regRoleCombo.setBorder(BorderFactory.createLineBorder(BORDER));

        JCheckBox regShowPassCheck = new JCheckBox("Show Passwords");
        regShowPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regShowPassCheck.setForeground(TEXT_GRAY);
        regShowPassCheck.setOpaque(false);
        regShowPassCheck.setFocusPainted(false);
        regShowPassCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));

        regPassField = createAuthPasswordField("Password", regShowPassCheck);
        regConfirmPassField = createAuthPasswordField("Confirm Password", regShowPassCheck);

        // Labels and Inputs
        addFormRow(fieldsPanel, "Username", regUserField, gbc, 0);
        addFormRow(fieldsPanel, "Full Name", regNameField, gbc, 1);
        addFormRow(fieldsPanel, "Email", regEmailField, gbc, 2);
        addFormRow(fieldsPanel, "Phone", regPhoneField, gbc, 3);
        addFormRow(fieldsPanel, "Portal Role", regRoleCombo, gbc, 4);
        addFormRow(fieldsPanel, "Password", regPassField, gbc, 5);
        addFormRow(fieldsPanel, "Confirm Password", regConfirmPassField, gbc, 6);
        addFormRow(fieldsPanel, "", regShowPassCheck, gbc, 7);

        JScrollPane scroll = new JScrollPane(fieldsPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(scroll);
        panel.add(Box.createVerticalStrut(14));

        // Submit Button
        CustomButton regBtn = new CustomButton("Register Account");
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        regBtn.addActionListener(e -> performRegister());

        panel.add(regBtn);
        panel.add(Box.createVerticalStrut(14));

        // Back to Login Link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setOpaque(false);
        linkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel haveAccLabel = new JLabel("Already have an account?");
        haveAccLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        haveAccLabel.setForeground(TEXT_GRAY);

        JLabel loginLinkLabel = new JLabel("Sign In");
        loginLinkLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginLinkLabel.setForeground(PRIMARY);
        loginLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                formLayout.show(formCard, "LOGIN");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLinkLabel.setText("<html><u>Sign In</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginLinkLabel.setText("Sign In");
            }
        });

        linkPanel.add(haveAccLabel);
        linkPanel.add(loginLinkLabel);

        panel.add(linkPanel);

        return panel;
    }

    private void addFormRow(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int gridy) {
        gbc.gridy = gridy;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_DARK);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void performLogin() {
        String username = getFieldText(loginUserField);
        String password = getPasswordFieldText(loginPassField, "Enter password");

        if (username.isEmpty() || password.isEmpty()) {
            showError(this, "Please enter both Username and Password.");
            return;
        }

        try {
            AuthAccount account = controller.authenticate(username, password);
            onLoginSuccess.accept(account);
        } catch (InvalidInputException e) {
            showError(this, e.getMessage());
        }
    }

    private void performRegister() {
        String username = getFieldText(regUserField);
        String name = getFieldText(regNameField);
        String email = getFieldText(regEmailField);
        String phone = getFieldText(regPhoneField);
        String role = (String) regRoleCombo.getSelectedItem();
        String password = getPasswordFieldText(regPassField, "Password");
        String confirmPassword = getPasswordFieldText(regConfirmPassField, "Confirm Password");

        if (username.isEmpty()) { showError(this, "Username is required."); return; }
        if (name.isEmpty()) { showError(this, "Full Name is required."); return; }
        if (password.isEmpty()) { showError(this, "Password is required."); return; }
        if (!password.equals(confirmPassword)) {
            showError(this, "Passwords do not match.");
            return;
        }

        try {
            AuthAccount newAccount = new AuthAccount(username, name, email, phone, role, password);
            controller.registerAccount(newAccount);
            showSuccess(this, "Account registered successfully! Please login.");
            formLayout.show(formCard, "LOGIN");

            // Populate login field with newly registered username
            loginUserField.setText(username);
            loginUserField.setForeground(TEXT_DARK);
            loginPassField.setText("");
        } catch (InvalidInputException e) {
            showError(this, e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw linear gradient background
        int w = getWidth();
        int h = getHeight();
        Color c1 = isDarkMode() ? new Color(14, 18, 30) : new Color(224, 233, 246);
        Color c2 = isDarkMode() ? new Color(26, 32, 48) : new Color(245, 247, 252);
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // Subtle decorative shapes
        g2.setColor(isDarkMode() ? new Color(52, 120, 246, 12) : new Color(52, 120, 246, 24));
        g2.fillOval(w / 12, h / 8, 320, 320);

        g2.setColor(isDarkMode() ? new Color(46, 204, 113, 8) : new Color(46, 204, 113, 16));
        g2.fillOval(w - w / 3, h - h / 2, 280, 280);

        g2.setColor(isDarkMode() ? new Color(243, 156, 18, 6) : new Color(243, 156, 18, 12));
        g2.fillOval(w / 2, h / 12, 180, 180);

        g2.dispose();
    }
}
