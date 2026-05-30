package library.gui.dashboard;

import library.auth.AuthConstants;
import library.config.AppInfo;
import library.exception.*;
import library.gui.auth.AuthPortalPanel;
import library.gui.components.CustomButton;
import library.gui.components.DashboardCard;
import library.gui.components.VectorIcon;
import library.gui.controller.LibraryController;
import library.model.*;
import library.util.StartupErrorHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static library.gui.util.UIHelper.*;
import static library.gui.components.VectorIcon.IconType.*;

public class LibrarySwingApp extends JFrame {

    private final LibraryController controller = new LibraryController();
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JPanel headerPanel;
    private JPanel statusBarPanel;
    private JLabel headerTitle;
    private JLabel statusLabel;
    private JLabel versionLabel;
    private JButton activeNavButton = null;
    private final List<JButton> navButtons = new ArrayList<>();
    private String currentScreen = "Dashboard";
    private String currentFilter = "ALL";
    private String currentDetailId = "";
    private AuthAccount currentUser = null;

    public LibrarySwingApp() {
        setTitle(AppInfo.NAME + " — Library Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        showAuthPortal();
    }

    private void showAuthPortal() {
        currentUser = null;
        getContentPane().removeAll();
        AuthPortalPanel authPanel = new AuthPortalPanel(this, controller, this::onLoginSuccess);
        add(authPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void onLoginSuccess(AuthAccount account) {
        this.currentUser = account;
        warnIfDefaultAdminCredentials(account);
        setupAuthenticatedUI();
    }

    private void warnIfDefaultAdminCredentials(AuthAccount account) {
        if (account == null) return;
        if ("admin".equalsIgnoreCase(account.getUsername())
                && AuthConstants.DEFAULT_ADMIN_PASSWORD_HASH.equals(account.getPasswordHash())) {
            JOptionPane.showMessageDialog(this,
                    "You are signed in with the factory default administrator password.\n"
                            + "Change it immediately under User Profiles or register a new admin account.",
                    "Security notice",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setupAuthenticatedUI() {
        getContentPane().removeAll();

        headerPanel = buildHeader();
        sidebarPanel = buildSidebar();
        statusBarPanel = buildStatusBar();
        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(statusBarPanel, BorderLayout.SOUTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CONTENT_BG);
        add(contentPanel, BorderLayout.CENTER);

        showDashboard();
        revalidate();
        repaint();
    }

    private boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    private boolean isLibrarian() {
        return currentUser != null && "LIBRARIAN".equalsIgnoreCase(currentUser.getRole());
    }

    private void logout() {
        if (confirm(this, "Are you sure you want to log out?")) {
            showAuthPortal();
        }
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        
        headerTitle = new JLabel("Library Management System");
        headerTitle.setForeground(TEXT_LIGHT);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerTitle.setIconTextGap(12);
        headerTitle.setIcon(new VectorIcon(LIBRARY, 24, TEXT_LIGHT));
        header.add(headerTitle, BorderLayout.WEST);

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightWrap.setOpaque(false);

        JLabel userLabel = new JLabel(currentUser != null ? currentUser.getDisplayName() + " (" + currentUser.getRole() + ")  " : "");
        userLabel.setForeground(TEXT_LIGHT);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightWrap.add(userLabel);

        versionLabel = new JLabel(AppInfo.VERSION_LABEL + "  ");
        versionLabel.setForeground(TEXT_GRAY);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rightWrap.add(versionLabel);

        header.add(rightWrap, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        activeNavButton = null;
        navButtons.clear();

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout(0, 12));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(244, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        JPanel navStack = new JPanel();
        navStack.setLayout(new BoxLayout(navStack, BoxLayout.Y_AXIS));
        navStack.setOpaque(false);

        JLabel nav = new JLabel("  MAIN MENU");
        nav.setForeground(TEXT_GRAY);
        nav.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nav.setAlignmentX(Component.LEFT_ALIGNMENT);
        navStack.add(nav);
        navStack.add(Box.createVerticalStrut(10));

        java.util.List<Object[]> visibleItems = new ArrayList<>();
        visibleItems.add(new Object[]{"Dashboard", HOME});

        if (isAdmin()) {
            visibleItems.add(new Object[]{"Add Book", PLUS_BOOK});
            visibleItems.add(new Object[]{"Register User", PERSON});
        }

        if (isAdmin() || isLibrarian()) {
            visibleItems.add(new Object[]{"Issue Book", ARROW_RIGHT});
            visibleItems.add(new Object[]{"Return Book", ARROW_LEFT});
        }

        visibleItems.add(new Object[]{"View Books", LIBRARY});

        if (isAdmin() || isLibrarian()) {
            visibleItems.add(new Object[]{"User Profiles", USERS});
            visibleItems.add(new Object[]{"Transactions", HISTORY});
        }

        if (isAdmin()) {
            visibleItems.add(new Object[]{"Removed Items", TRASH});
        }

        visibleItems.add(new Object[]{"About Me", PERSON});

        for (Object[] item : visibleItems) {
            JButton btn = createNavBtn((String) item[0], (VectorIcon.IconType) item[1]);
            navStack.add(btn);
            navStack.add(Box.createVerticalStrut(4));
            if (((String) item[0]).equals(getActiveNavLabel())) {
                setActiveNav(btn);
            }
        }

        navStack.add(Box.createVerticalStrut(8));
        JButton darkModeBtn = createNavBtn(isDarkMode() ? "Light Mode" : "Dark Mode", isDarkMode() ? SUN : MOON);
        darkModeBtn.addActionListener(e -> toggleTheme());
        navStack.add(darkModeBtn);
        navStack.add(Box.createVerticalStrut(4));

        JButton logoutBtn = createNavBtn("Logout", LOGOUT);
        logoutBtn.addActionListener(e -> logout());
        navStack.add(logoutBtn);
        navStack.add(Box.createVerticalStrut(4));

        JButton exitBtn = createNavBtn("Exit", LOGOUT);
        exitBtn.addActionListener(e -> System.exit(0));
        navStack.add(exitBtn);

        JScrollPane navScroll = new JScrollPane(navStack);
        navScroll.setBorder(null);
        navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        navScroll.getVerticalScrollBar().setUnitIncrement(14);
        navScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        navScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebar.add(navScroll, BorderLayout.CENTER);

        return sidebar;
    }

    private JButton createNavBtn(String label, VectorIcon.IconType iconType) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setPreferredSize(new Dimension(216, 38));
        btn.setMaximumSize(new Dimension(216, 38));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.setIcon(new VectorIcon(iconType, 18, TEXT_LIGHT));
        btn.setIconTextGap(14);
        navButtons.add(btn);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                if (btn != activeNavButton) btn.setBackground(SIDEBAR_HOVER); 
            }
            public void mouseExited(MouseEvent e) { 
                if (btn != activeNavButton) btn.setBackground(SIDEBAR_BG); 
            }
        });

        if (!label.equals("Exit") && !label.equals("Dark Mode") && !label.equals("Light Mode") && !label.equals("Logout")) {
            btn.addActionListener(e -> {
                setActiveNav(btn);
                switch (label) {
                    case "Dashboard"      -> showDashboard();
                    case "Add Book"       -> showAddBook();
                    case "Register User"  -> showRegisterUser();
                    case "Issue Book"     -> showIssueBook();
                    case "Return Book"    -> showReturnBook();
                    case "View Books"     -> showViewBooks("ALL");
                    case "User Profiles"  -> showUserProfileSearch();
                    case "Transactions"   -> showTransactions("ALL");
                    case "Removed Items"  -> showRemovedItems();
                    case "About Me"       -> showAboutMe();
                }
            });
        }
        return btn;
    }

    private void openLink(String url) {
        try {
            if (!Desktop.isDesktopSupported()) {
                showError(this, "Opening links is not supported on this system.");
                return;
            }
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            showError(this, "Could not open link: " + url);
        }
    }

    private String getActiveNavLabel() {
        return switch (currentScreen) {
            case "AddBook" -> "Add Book";
            case "RegisterUser" -> "Register User";
            case "IssueBook" -> "Issue Book";
            case "ReturnBook" -> "Return Book";
            case "ViewBooks" -> "View Books";
            case "UserProfiles" -> "User Profiles";
            case "Transactions" -> "Transactions";
            case "RemovedItems" -> "Removed Items";
            case "AboutMe" -> "About Me";
            case "BookDetails" -> "View Books";
            case "UserDetails" -> "User Profiles";
            default -> "Dashboard";
        };
    }

    private void toggleTheme() {
        setDarkMode(!isDarkMode());
        getContentPane().remove(sidebarPanel);
        sidebarPanel = buildSidebar();
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        headerPanel.setBackground(HEADER_BG);
        statusBarPanel.setBackground(CARD_BG);
        statusBarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        statusLabel.setForeground(TEXT_GRAY);
        contentPanel.setBackground(CONTENT_BG);
        renderCurrentScreen();
        revalidate();
        repaint();
        setStatus((isDarkMode() ? "Dark" : "Light") + " mode enabled");
    }

    private void renderCurrentScreen() {
        switch (currentScreen) {
            case "AddBook" -> showAddBook();
            case "RegisterUser" -> showRegisterUser();
            case "IssueBook" -> showIssueBook();
            case "ReturnBook" -> showReturnBook();
            case "ViewBooks" -> showViewBooks(currentFilter);
            case "UserProfiles" -> showUserProfileSearch();
            case "Transactions" -> showTransactions(currentFilter);
            case "Search" -> showSearch();
            case "RemovedItems" -> showRemovedItems();
            case "AboutMe" -> showAboutMe();
            case "BookDetails" -> showBookDetails(currentDetailId);
            case "UserDetails" -> showUserDetails(currentDetailId);
            default -> showDashboard();
        }
    }

    private void setActiveNav(JButton btn) {
        if (activeNavButton != null) { 
            activeNavButton.setBackground(SIDEBAR_BG); 
            activeNavButton.setForeground(TEXT_LIGHT);
            Icon oldIcon = activeNavButton.getIcon();
            if (oldIcon instanceof VectorIcon) {
                activeNavButton.setIcon(new VectorIcon(((VectorIcon)oldIcon).getType(), 18, TEXT_LIGHT));
            }
        }
        activeNavButton = btn;
        btn.setBackground(SIDEBAR_ACTIVE);
        btn.setForeground(Color.WHITE);
        Icon newIcon = btn.getIcon();
        if (newIcon instanceof VectorIcon) {
            btn.setIcon(new VectorIcon(((VectorIcon)newIcon).getType(), 18, Color.WHITE));
        }
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_GRAY);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void setStatus(String msg) {
        if (currentUser != null) {
            statusLabel.setText("[" + currentUser.getRole() + "] " + currentUser.getDisplayName() + " | " + msg);
        } else {
            statusLabel.setText(msg);
        }
    }

    private void showContent(JPanel panel, String title) {
        headerTitle.setText(title);
        headerTitle.setForeground(TEXT_LIGHT);
        headerTitle.setIcon(new VectorIcon(LIBRARY, 24, TEXT_LIGHT));
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel makePanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CONTENT_BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        return p;
    }

    private void addTitle(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setForeground(TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(6));
    }

    private void addHint(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(18));
    }

    // ═══════════════════ PANELS ═══════════════════

    private void showDashboard() {
        currentScreen = "Dashboard";
        currentFilter = "ALL";
        JPanel p = makePanel();
        addTitle(p, "Dashboard");
        addHint(p, "Overview of your library at a glance.");

        List<Book> books = controller.getAllBooks();
        int total = books.size(), users = controller.getAllUsers().size();
        long avail = books.stream().filter(Book::isAvailable).count();
        long issued = total - avail;
        int removed = controller.getRemovedItems().size();

        JPanel stats = new JPanel(new GridLayout(1, 5, 10, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        stats.add(new DashboardCard("Total Books", String.valueOf(total), "All active books", PRIMARY, () -> showViewBooks("ALL")));
        stats.add(new DashboardCard("Available Books", String.valueOf(avail), "Ready to be issued", SUCCESS, () -> showViewBooks("AVAILABLE")));
        stats.add(new DashboardCard("Issued", String.valueOf(issued), "Currently checked out", AMBER, () -> showTransactions("ACTIVE")));
        stats.add(new DashboardCard("Users", String.valueOf(users), "Active registered members", PURPLE, this::showUserProfileSearch));
        stats.add(new DashboardCard("Removed", String.valueOf(removed), "Deleted items history", DANGER, this::showRemovedItems));
        
        p.add(stats);
        p.add(Box.createVerticalStrut(32));

        addTitle(p, "Recent Additions");
        if (books.isEmpty()) { 
            addHint(p, "No books available. Click 'Add Book' to get started."); 
        } else {
            List<Book> preview = books.size() > 5 ? books.subList(books.size() - 5, books.size()) : books;
            p.add(wrapTable(buildBookTable(preview)));
        }
        showContent(p, "Dashboard");
        setStatus(total + " books, " + users + " active users");
    }

    private void showAddBook() {
        currentScreen = "AddBook";
        currentFilter = "ALL";
        JPanel p = makePanel();
        addTitle(p, "Add New Book");
        addHint(p, "Register a new book in the library catalog.");
        
        JTextField idF = createField("e.g. B007"), titleF = createField("Book title"),
                  authorF = createField("Author name"), genreF = createField("Genre (optional)"),
                  quantityF = createField("Quantity");
        
        p.add(createFormPanel(new String[]{"Book ID", "Title", "Author", "Genre", "Quantity"},
                new JTextField[]{idF, titleF, authorF, genreF, quantityF}));
        p.add(Box.createVerticalStrut(18));
        
        CustomButton save = new CustomButton("Add Book");
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        save.addActionListener(e -> {
            String id = getFieldText(idF), title = getFieldText(titleF),
                   author = getFieldText(authorF), genre = getFieldText(genreF),
                   qtyText = getFieldText(quantityF);
            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                showError(this, "Book ID, Title, and Author are required."); return;
            }
            if (genre.isEmpty()) genre = "General";
            int quantity;
            try {
                quantity = Integer.parseInt(qtyText);
            } catch (NumberFormatException ex) {
                showError(this, "Please enter a valid positive quantity.");
                return;
            }
            if (quantity <= 0) {
                showError(this, "Quantity must be at least 1.");
                return;
            }
            try {
                controller.addBook(new Book(id, title, author, genre, quantity));
                showSuccess(this, "Book '" + title + "' added successfully!");
                idF.setText(""); titleF.setText(""); authorF.setText(""); genreF.setText(""); quantityF.setText("");
                setStatus("Added: " + title);
            } catch (InvalidInputException ex) { showError(this, ex.getMessage()); }
        });
        p.add(save);
        showContent(p, "Add Book");
    }

    private void showRegisterUser() {
        currentScreen = "RegisterUser";
        currentFilter = "ALL";
        JPanel p = makePanel();
        addTitle(p, "Register New User");
        addHint(p, "Add a new library member.");
        
        JTextField idF = createField("e.g. U004"), nameF = createField("Full name"),
                  emailF = createField("Email (optional)");
        
        p.add(createFormPanel(new String[]{"User ID", "Name", "Email"},
                new JTextField[]{idF, nameF, emailF}));
        p.add(Box.createVerticalStrut(18));
        
        CustomButton save = new CustomButton("Register");
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        save.addActionListener(e -> {
            String id = getFieldText(idF), name = getFieldText(nameF), email = getFieldText(emailF);
            if (id.isEmpty() || name.isEmpty()) { showError(this, "User ID and Name are required."); return; }
            try {
                controller.registerUser(new User(id, name, email));
                showSuccess(this, "User '" + name + "' registered successfully!");
                idF.setText(""); nameF.setText(""); emailF.setText("");
                setStatus("Registered: " + name);
            } catch (InvalidInputException ex) { showError(this, ex.getMessage()); }
        });
        p.add(save);
        showContent(p, "Register User");
    }

    private void showIssueBook() {
        currentScreen = "IssueBook";
        currentFilter = "ALL";
        JPanel p = makePanel();
        addTitle(p, "Issue a Book");
        addHint(p, "Lend a book to a registered user.");
        
        JTextField bF = createField("Book ID"), uF = createField("User ID");
        p.add(createFormPanel(new String[]{"Book ID", "User ID"}, new JTextField[]{bF, uF}));
        p.add(Box.createVerticalStrut(18));
        
        CustomButton go = new CustomButton("Issue Book", PRIMARY, PRIMARY_HOVER);
        go.setAlignmentX(Component.LEFT_ALIGNMENT);
        go.addActionListener(e -> {
            String b = getFieldText(bF), u = getFieldText(uF);
            if (b.isEmpty() || u.isEmpty()) { showError(this, "Both fields are required."); return; }
            
            // Check if user already has an active borrow
            if (controller.hasActiveBooks(u)) {
                showWarning(this, "Active Borrow Restriction",
                    "This user already has an active borrowed book.\n\nPlease return the current book before borrowing another.");
                setStatus("Borrowing blocked: user already has an active loan");
                return;
            }
            
            if (!confirm(this, "Issue book '" + b + "' to user '" + u + "'?")) return;
            try {
                controller.issueBook(b, u);
                showSuccess(this, "Book issued successfully!");
                bF.setText(""); uF.setText("");
                setStatus("Issued " + b + " to " + u);
            } catch (BookNotAvailableException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("active borrowed book")) {
                    showWarning(this, "Active Borrow Restriction", ex.getMessage());
                    setStatus("Borrowing blocked: user already has an active loan");
                } else {
                    showError(this, ex.getMessage());
                }
            } catch (Exception ex) { showError(this, ex.getMessage()); }
        });
        p.add(go);
        showContent(p, "Issue Book");
    }

    private void showReturnBook() {
        currentScreen = "ReturnBook";
        currentFilter = "ALL";
        JPanel p = makePanel();
        addTitle(p, "Return a Book");
        addHint(p, "Process the return of a borrowed book.");
        
        JTextField bF = createField("Book ID"), uF = createField("User ID");
        p.add(createFormPanel(new String[]{"Book ID", "User ID"}, new JTextField[]{bF, uF}));
        p.add(Box.createVerticalStrut(18));
        
        CustomButton go = new CustomButton("Return Book", SUCCESS, new Color(39, 174, 96));
        go.setAlignmentX(Component.LEFT_ALIGNMENT);
        go.addActionListener(e -> {
            String b = getFieldText(bF), u = getFieldText(uF);
            if (b.isEmpty() || u.isEmpty()) { showError(this, "Both fields are required."); return; }
            if (!confirm(this, "Confirm return of book '" + b + "' from user '" + u + "'?")) return;
            try {
                controller.returnBook(b, u);
                showSuccess(this, "Book returned successfully!");
                bF.setText(""); uF.setText("");
                setStatus("Returned " + b + " from " + u);
            } catch (Exception ex) { showError(this, ex.getMessage()); }
        });
        p.add(go);
        showContent(p, "Return Book");
    }

    private void showViewBooks(String filterType) {
        currentScreen = "ViewBooks";
        currentFilter = filterType;
        JPanel p = makePanel();
        addTitle(p, filterType.equals("AVAILABLE") ? "Available Books" : "Book Catalog");
        addHint(p, "Select a book to view details, transactions, and removal options.");
        
        List<Book> books = controller.getAllBooks();
        if (filterType.equals("AVAILABLE")) {
            books = books.stream().filter(Book::isAvailable).collect(Collectors.toList());
        }
        final List<Book> visibleBooks = books;

        JPanel topBar = new JPanel();
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
        topBar.setOpaque(false);
        
        JTextField filter = createField("Search by title, author, or ID...");
        filter.setPreferredSize(new Dimension(200, 36));
        filter.setMaximumSize(new Dimension(320, 36));
        topBar.add(filter);
        topBar.add(Box.createHorizontalStrut(8));

        CustomButton viewBtn = new CustomButton("View", PRIMARY, PRIMARY_HOVER);
        viewBtn.setPreferredSize(new Dimension(100, 36));
        viewBtn.setMaximumSize(new Dimension(140, 36));
        topBar.add(viewBtn);
        topBar.add(Box.createHorizontalGlue());

        if (books.isEmpty()) {
            addHint(p, "No books found matching this filter.");
            showContent(p, filterType.equals("AVAILABLE") ? "Available Books" : "View Books");
            return;
        }

        DefaultListModel<String> listModel = new DefaultListModel<>();
        populateBookList(listModel, visibleBooks, "");

        JList<String> bookList = new JList<>(listModel);
        bookList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.setBackground(CARD_BG);
        bookList.setForeground(TEXT_DARK);
        bookList.setSelectionBackground(LIST_SELECTION_BG);
        bookList.setSelectionForeground(SELECTION_TEXT);
        bookList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bookList.setFixedCellHeight(38);
        bookList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                if (isSelected) {
                    label.setBackground(LIST_SELECTION_BG);
                    label.setForeground(SELECTION_TEXT);
                } else {
                    label.setBackground(index % 2 == 0 ? LIST_ALT_ROW : CARD_BG);
                    label.setForeground(PRIMARY);
                }
                return label;
            }
        });

        JScrollPane bookScroll = new JScrollPane(bookList);
        styleScrollPane(bookScroll);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setOpaque(false);
        leftPanel.setMinimumSize(new Dimension(310, 0));
        leftPanel.setPreferredSize(new Dimension(340, 440));
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 16));
        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(bookScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setMinimumSize(new Dimension(420, 0));
        rightPanel.setBorder(new EmptyBorder(0, 16, 0, 0));

        JLabel emptyBook = new JLabel("Select a book to view its profile.");
        emptyBook.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        emptyBook.setForeground(TEXT_GRAY);
        emptyBook.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(emptyBook, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(360);
        splitPane.setResizeWeight(0.0);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        splitPane.setPreferredSize(new Dimension(900, 440));
        splitPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));
        
        filter.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                populateBookList(listModel, visibleBooks, getFieldText(filter));
                setStatus(listModel.size() + " book(s) visible");
            }
        });

        filter.addActionListener(e -> {
            populateBookList(listModel, visibleBooks, getFieldText(filter));
            setStatus(listModel.size() + " book(s) visible");
        });

        viewBtn.addActionListener(e -> {
            populateBookList(listModel, visibleBooks, getFieldText(filter));
            if (listModel.isEmpty()) {
                showError(this, "No books matched your search.");
            } else if (bookList.getSelectedIndex() == -1) {
                bookList.setSelectedIndex(0);
            }
            setStatus(listModel.size() + " book(s) visible");
        });

        bookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookList.getSelectedIndex() != -1) {
                String selected = bookList.getSelectedValue();
                String bookId = selected.split(" - ")[0];
                Book book = visibleBooks.stream()
                        .filter(b -> b.getBookId().equalsIgnoreCase(bookId))
                        .findFirst()
                        .orElse(null);
                if (book != null) {
                    rightPanel.removeAll();
                    rightPanel.add(buildBookProfilePanel(book, () -> showViewBooks(filterType)), BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();
                }
            }
        });
        bookList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && bookList.getSelectedIndex() != -1) {
                    showBookDetails(bookList.getSelectedValue().split(" - ")[0]);
                }
            }
        });

        p.add(splitPane);
        showContent(p, filterType.equals("AVAILABLE") ? "Available Books" : "View Books");
    }

    // ── User Profiles Subsystem ──────────────────────────────────────────

    private void populateBookList(DefaultListModel<String> listModel, List<Book> books, String query) {
        String q = query == null ? "" : query.toLowerCase();
        listModel.clear();
        for (Book b : books) {
            if (q.isEmpty()
                    || b.getBookId().toLowerCase().contains(q)
                    || b.getTitle().toLowerCase().contains(q)
                    || b.getAuthor().toLowerCase().contains(q)) {
                listModel.addElement(b.getBookId() + " - " + b.getTitle());
            }
        }
    }

    private JTextArea createWrapText(String text, Font font, Color color) {
        JTextArea area = new JTextArea(text);
        area.setFont(font);
        area.setForeground(color);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(null);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        return area;
    }

    private JLabel createInfoBadge(String label, String value, Color accent) {
        JLabel badge = new JLabel(" " + label + ": " + value + " ");
        badge.setOpaque(true);
        badge.setBackground(statusBackground(accent));
        badge.setForeground(accent);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return badge;
    }

    private JPanel buildBookProfilePanel(Book book, Runnable onRefresh) {
        JPanel prof = new JPanel();
        prof.setLayout(new BoxLayout(prof, BoxLayout.Y_AXIS));
        prof.setBackground(CARD_BG);
        prof.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(24, 24, 24, 24)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel infoWrap = new JPanel(new BorderLayout(16, 0));
        infoWrap.setOpaque(false);

        JLabel icon = new JLabel(new VectorIcon(LIBRARY, 50, PRIMARY));
        icon.setVerticalAlignment(SwingConstants.TOP);
        infoWrap.add(icon, BorderLayout.WEST);

        JPanel textInfo = new JPanel();
        textInfo.setLayout(new BoxLayout(textInfo, BoxLayout.Y_AXIS));
        textInfo.setOpaque(false);

        JTextArea titleL = createWrapText(book.getTitle(), new Font("Segoe UI", Font.BOLD, 20), TEXT_DARK);
        JTextArea subInfo = createWrapText(
                "ID: " + book.getBookId() + "  |  " + book.getAuthor() + "  |  " + book.getGenre(),
                new Font("Segoe UI", Font.PLAIN, 13), TEXT_GRAY);
        textInfo.add(titleL);
        textInfo.add(Box.createVerticalStrut(4));
        textInfo.add(subInfo);
        infoWrap.add(textInfo, BorderLayout.CENTER);

        CustomButton delBtn = new CustomButton("Delete Book", DANGER, DANGER_HOVER);
        delBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        delBtn.setPreferredSize(new Dimension(112, 30));
        delBtn.setMaximumSize(new Dimension(112, 30));
        delBtn.addActionListener(e -> {
            if (!confirm(this, "Delete book '" + book.getTitle() + "' (ID: " + book.getBookId() + ")?")) return;
            try {
                controller.removeBook(book.getBookId());
                showSuccess(this, "Book removed successfully.");
                setStatus("Removed book: " + book.getBookId());
                onRefresh.run();
            } catch (Exception ex) { showError(this, ex.getMessage()); }
        });

        JPanel actionWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionWrap.setOpaque(false);
        actionWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        if (isAdmin()) {
            actionWrap.add(delBtn);
        }

        header.add(infoWrap, BorderLayout.CENTER);
        prof.add(header);
        prof.add(Box.createVerticalStrut(10));
        prof.add(actionWrap);
        prof.add(Box.createVerticalStrut(18));

        List<Transaction> txns = controller.getTransactionsByBook(book.getBookId());
        List<Transaction> activeTxns = txns.stream().filter(Transaction::isActive).collect(Collectors.toList());
        int activeLoanCount = activeTxns.size();
        int totalCopies = book.getQuantity() + activeLoanCount;

        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusWrap.setOpaque(false);
        JLabel statusL = new JLabel(book.isAvailable() ? " Available " : " Out of stock ");
        statusL.setOpaque(true);
        statusL.setBackground(book.isAvailable() ? STATUS_SUCCESS_BG : STATUS_DANGER_BG);
        statusL.setForeground(book.isAvailable() ? SUCCESS : DANGER);
        statusL.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusL.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        statusWrap.add(statusL);

        JPanel stockWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        stockWrap.setOpaque(false);
        stockWrap.add(createInfoBadge("Available Copies", String.valueOf(book.getQuantity()), SUCCESS));
        stockWrap.add(createInfoBadge("Borrowed", String.valueOf(activeLoanCount), AMBER));
        stockWrap.add(createInfoBadge("Total Copies", String.valueOf(totalCopies), PRIMARY));
        statusWrap.add(stockWrap);

        if (!book.isAvailable()) {
            Transaction activeLoan = activeTxns.isEmpty() ? null : activeTxns.get(0);
            JLabel borrower = new JLabel("  Borrower: " + getBorrower(book.getBookId()));
            borrower.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            borrower.setForeground(PRIMARY);
            borrower.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (activeLoan != null) {
                borrower.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        showUserDetails(activeLoan.getUserId());
                    }
                });
            }
            statusWrap.add(borrower);
        }
        prof.add(statusWrap);
        prof.add(Box.createVerticalStrut(24));

        JLabel curLbl = new JLabel("Current Loan");
        curLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        curLbl.setForeground(TEXT_DARK);
        prof.add(curLbl);
        prof.add(Box.createVerticalStrut(8));

        if (activeTxns.isEmpty()) {
            JLabel empty = new JLabel("This book is not currently checked out.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(TEXT_GRAY);
            prof.add(empty);
        } else {
            String[] curCols = {"Transaction ID", "User", "Issue Date"};
            DefaultTableModel curM = new DefaultTableModel(curCols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Transaction t : activeTxns) curM.addRow(new Object[]{t.getTransactionId(), displayUser(t), t.getIssueDate()});
            JTable curT = styleTable(new JTable(curM));
            attachTableNavigation(curT, -1, 1);
            JScrollPane curS = new JScrollPane(curT);
            styleScrollPane(curS);
            curS.setPreferredSize(new Dimension(400, 120));
            prof.add(curS);
        }

        prof.add(Box.createVerticalStrut(24));

        JLabel histLbl = new JLabel("Full Transaction History");
        histLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        histLbl.setForeground(TEXT_DARK);
        prof.add(histLbl);
        prof.add(Box.createVerticalStrut(8));

        if (txns.isEmpty()) {
            JLabel empty = new JLabel("No previous transactions.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(TEXT_GRAY);
            prof.add(empty);
        } else {
            String[] histCols = {"Transaction ID", "User", "Issued", "Returned", "Status"};
            DefaultTableModel histM = new DefaultTableModel(histCols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Transaction t : txns) {
                histM.addRow(new Object[]{
                    t.getTransactionId(), displayUser(t), t.getIssueDate(),
                    t.getReturnDate() == null ? "\u2014" : t.getReturnDate(),
                    t.getStatus()
                });
            }
            JTable histT = styleTable(new JTable(histM));
            attachTableNavigation(histT, -1, 1);
            JScrollPane histS = new JScrollPane(histT);
            styleScrollPane(histS);
            histS.setPreferredSize(new Dimension(400, 160));
            prof.add(histS);
        }

        return prof;
    }

    private void showUserProfileSearch() {
        currentScreen = "UserProfiles";
        currentFilter = "ALL";
        JPanel p = makePanel();
        addTitle(p, "User Profiles");
        addHint(p, "Select a user from the list or search by ID/Name to view their full profile.");

        // We use a Split Pane for professional feel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // LEFT PANEL: User List
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 16));

        JTextField searchF = createField("Search users...");
        searchF.setPreferredSize(new Dimension(200, 36));
        searchF.setMaximumSize(new Dimension(320, 36));
        JPanel searchWrap = new JPanel();
        searchWrap.setLayout(new BoxLayout(searchWrap, BoxLayout.X_AXIS));
        searchWrap.setOpaque(false);
        searchWrap.add(searchF);
        searchWrap.add(Box.createHorizontalGlue());
        leftPanel.add(searchWrap, BorderLayout.NORTH);

        List<User> users = controller.getAllUsers();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (User u : users) listModel.addElement(u.getUserId() + " - " + u.getName());
        
        JList<String> userList = new JList<>(listModel);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setBackground(CARD_BG);
        userList.setForeground(TEXT_DARK);
        userList.setSelectionBackground(LIST_SELECTION_BG);
        userList.setSelectionForeground(SELECTION_TEXT);
        userList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        userList.setFixedCellHeight(38);
        userList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                if (isSelected) {
                    label.setBackground(LIST_SELECTION_BG);
                    label.setForeground(SELECTION_TEXT);
                } else {
                    label.setBackground(index % 2 == 0 ? LIST_ALT_ROW : CARD_BG);
                    label.setForeground(PRIMARY);
                }
                return label;
            }
        });
        
        JScrollPane listScroll = new JScrollPane(userList);
        styleScrollPane(listScroll);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        // RIGHT PANEL: Profile Display Area
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 16, 0, 0));
        
        JLabel emptyProf = new JLabel("Select a user to view their profile.");
        emptyProf.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        emptyProf.setForeground(TEXT_GRAY);
        emptyProf.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(emptyProf, BorderLayout.CENTER);

        // Search filtering for the left list
        searchF.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String q = getFieldText(searchF).toLowerCase();
                listModel.clear();
                for (User u : users) {
                    if (u.getName().toLowerCase().contains(q) || u.getUserId().toLowerCase().contains(q)) {
                        listModel.addElement(u.getUserId() + " - " + u.getName());
                    }
                }
            }
        });

        // List selection changes the right panel
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userList.getSelectedIndex() != -1) {
                String selected = userList.getSelectedValue();
                String uId = selected.split(" - ")[0];
                User user = users.stream().filter(u -> u.getUserId().equals(uId)).findFirst().orElse(null);
                if (user != null) {
                    rightPanel.removeAll();
                    rightPanel.add(buildUserProfilePanel(user, () -> showUserProfileSearch()), BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();
                }
            }
        });
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && userList.getSelectedIndex() != -1) {
                    showUserDetails(userList.getSelectedValue().split(" - ")[0]);
                }
            }
        });

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        p.add(splitPane);
        
        showContent(p, "User Profiles");
    }

    private JPanel buildUserProfilePanel(User user, Runnable onRefresh) {
        JPanel prof = new JPanel();
        prof.setLayout(new BoxLayout(prof, BoxLayout.Y_AXIS));
        prof.setBackground(CARD_BG);
        prof.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(24, 24, 24, 24)
        ));

        // Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Avatar + Info
        JPanel infoWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        infoWrap.setOpaque(false);
        
        JLabel avatar = new JLabel(new VectorIcon(PERSON, 50, PRIMARY));
        infoWrap.add(avatar);

        JPanel textInfo = new JPanel(new GridLayout(2, 1));
        textInfo.setOpaque(false);
        JLabel nameL = new JLabel(user.getName());
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameL.setForeground(TEXT_DARK);
        
        JLabel subInfo = new JLabel("ID: " + user.getUserId() + "  |  " + 
                                  (user.getEmail().isEmpty() ? "No Email" : user.getEmail()));
        subInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subInfo.setForeground(TEXT_GRAY);
        textInfo.add(nameL);
        textInfo.add(subInfo);
        infoWrap.add(textInfo);

        // Delete Button correctly located in profile
        CustomButton delBtn = new CustomButton("Delete User", DANGER, DANGER_HOVER);
        delBtn.setPreferredSize(new Dimension(130, 36));
        delBtn.addActionListener(e -> {
            if (!confirm(this, "Delete user '" + user.getName() + "' (ID: " + user.getUserId() + ")?")) return;
            try {
                controller.removeUser(user.getUserId());
                showSuccess(this, "User removed successfully.");
                onRefresh.run();
            } catch (Exception ex) { showError(this, ex.getMessage()); }
        });

        header.add(infoWrap, BorderLayout.WEST);
        if (isAdmin()) {
            header.add(delBtn, BorderLayout.EAST);
        }
        prof.add(header);
        prof.add(Box.createVerticalStrut(24));

        // Get user history
        List<Transaction> txns = controller.getTransactionsByUser(user.getUserId());
        List<Transaction> activeTxns = txns.stream().filter(Transaction::isActive).collect(Collectors.toList());

        // Status badge
        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusWrap.setOpaque(false);
        JLabel statusL = new JLabel(activeTxns.isEmpty() ? " \u2022 Account Clear " : " \u2022 Has Books Due ");
        statusL.setOpaque(true);
        statusL.setBackground(activeTxns.isEmpty() ? STATUS_SUCCESS_BG : STATUS_AMBER_BG);
        statusL.setForeground(activeTxns.isEmpty() ? SUCCESS : AMBER);
        statusL.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusL.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 12));
        statusWrap.add(statusL);
        prof.add(statusWrap);
        prof.add(Box.createVerticalStrut(24));

        // Currently Borrowed Table
        JLabel curLbl = new JLabel("Currently Borrowed");
        curLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        curLbl.setForeground(TEXT_DARK);
        prof.add(curLbl);
        prof.add(Box.createVerticalStrut(8));

        if (activeTxns.isEmpty()) {
            JLabel empty = new JLabel("This user has no books currently checked out.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(TEXT_GRAY);
            prof.add(empty);
        } else {
            String[] curCols = {"Transaction ID", "Book", "Issue Date"};
            DefaultTableModel curM = new DefaultTableModel(curCols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Transaction t : activeTxns) curM.addRow(new Object[]{t.getTransactionId(), displayBook(t), t.getIssueDate()});
            JTable curT = styleTable(new JTable(curM));
            attachTableNavigation(curT, 1, -1);
            JScrollPane curS = new JScrollPane(curT);
            styleScrollPane(curS);
            curS.setPreferredSize(new Dimension(400, 120));
            prof.add(curS);
        }
        
        prof.add(Box.createVerticalStrut(24));

        // Full History Table
        JLabel histLbl = new JLabel("Full Transaction History");
        histLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        histLbl.setForeground(TEXT_DARK);
        prof.add(histLbl);
        prof.add(Box.createVerticalStrut(8));

        if (txns.isEmpty()) {
            JLabel empty = new JLabel("No previous transactions.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            empty.setForeground(TEXT_GRAY);
            prof.add(empty);
        } else {
            String[] histCols = {"Transaction ID", "Book", "Issued", "Returned", "Status"};
            DefaultTableModel histM = new DefaultTableModel(histCols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Transaction t : txns) {
                histM.addRow(new Object[]{
                    t.getTransactionId(), displayBook(t), t.getIssueDate(), 
                    t.getReturnDate() == null ? "\u2014" : t.getReturnDate(),
                    t.getStatus()
                });
            }
            JTable histT = styleTable(new JTable(histM));
            attachTableNavigation(histT, 1, -1);
            JScrollPane histS = new JScrollPane(histT);
            styleScrollPane(histS);
            histS.setPreferredSize(new Dimension(400, 160));
            prof.add(histS);
        }

        return prof;
    }

    // ── Transactions & Removals ──────────────────────────────────────────

    private void showTransactions(String filterType) {
        currentScreen = "Transactions";
        currentFilter = filterType;
        JPanel p = makePanel();
        addTitle(p, filterType.equals("ACTIVE") ? "Issued Books" : "Transaction History");
        addHint(p, filterType.equals("ACTIVE") ? "Books currently checked out by users." : "Complete log of all book issues and returns.");
        
        List<Transaction> txns = controller.getAllTransactions();
        if (filterType.equals("ACTIVE")) {
            txns = txns.stream().filter(Transaction::isActive).collect(Collectors.toList());
        }

        if (txns.isEmpty()) { 
            addHint(p, "No transactions found for this filter."); 
        } else {
            String[] cols = {"Transaction ID", "User", "Book", "Issue Date", "Return Date", "Status"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Transaction t : txns) {
                model.addRow(new Object[]{t.getTransactionId(), displayUser(t), displayBook(t),
                    t.getIssueDate().toString(),
                    t.getReturnDate() != null ? t.getReturnDate().toString() : "\u2014",
                    t.getStatus()});
            }
            JTable table = styleTable(new JTable(model));
            table.setRowSorter(new TableRowSorter<>(model));
            attachTableNavigation(table, 2, 1);
            p.add(wrapTable(table));
            setStatus(txns.size() + " transaction(s) found");
        }
        showContent(p, filterType.equals("ACTIVE") ? "Issued Books" : "Transactions");
    }

    private void showSearch() {
        currentScreen = "Search";
        currentFilter = "ALL";
        JPanel p = makePanel();
        addTitle(p, "Global Search");
        addHint(p, "Search the active catalog by any keyword.");
        
        JTextField sF = createField("Enter title, author, or ID...");
        p.add(createFormPanel(new String[]{"Keyword"}, new JTextField[]{sF}));
        p.add(Box.createVerticalStrut(18));

        JPanel results = new JPanel(new BorderLayout());
        results.setOpaque(false);
        results.setAlignmentX(Component.LEFT_ALIGNMENT);

        CustomButton go = new CustomButton("Search Catalog");
        go.setAlignmentX(Component.LEFT_ALIGNMENT);
        go.addActionListener(e -> {
            String q = getFieldText(sF);
            if (q.isEmpty()) { showError(this, "Please enter a search term."); return; }
            List<Book> res = controller.searchBooks(q);
            results.removeAll();
            if (res.isEmpty()) {
                JLabel no = new JLabel("No active books matched your search for '" + q + "'.");
                no.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                no.setForeground(TEXT_GRAY);
                results.add(no, BorderLayout.CENTER);
            } else {
                JTable table = buildBookTable(res);
                table.setRowSorter(new TableRowSorter<>((DefaultTableModel)table.getModel()));
                results.add(wrapTable(table), BorderLayout.CENTER);
            }
            results.revalidate(); results.repaint();
            setStatus("Search found " + res.size() + " result(s)");
        });
        
        p.add(go);
        p.add(Box.createVerticalStrut(22));
        p.add(results);
        showContent(p, "Search");
    }

    private void showRemovedItems() {
        currentScreen = "RemovedItems";
        currentFilter = "ALL";
        currentDetailId = "";
        JPanel p = makePanel();
        addTitle(p, "Removed Items History");
        addHint(p, "Audit log of deleted books and users. This is the only place removed items appear.");

        List<RemovedItem> items = controller.getRemovedItems();
        if (items.isEmpty()) {
            addHint(p, "No items have been removed yet.");
        } else {
            String[] cols = {"Type", "Item ID", "Name", "Removed On"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (RemovedItem ri : items) {
                model.addRow(new Object[]{
                    ri.getItemType(), ri.getItemId(), ri.getItemName(), ri.getRemovedAt().toString()
                });
            }
            JTable table = styleTable(new JTable(model));
            table.setRowSorter(new TableRowSorter<>(model));
            p.add(wrapTable(table));
            setStatus(items.size() + " removed item(s)");
        }
        showContent(p, "Removed Items");
    }

    private void showBookDetails(String bookId) {
        Book book = findBook(bookId);
        if (book == null) {
            showError(this, "Book '" + bookId + "' was not found.");
            showViewBooks("ALL");
            return;
        }
        currentScreen = "BookDetails";
        currentDetailId = book.getBookId();

        JPanel p = makePanel();
        addTitle(p, "Book Details");
        addHint(p, "Availability, borrower information, and transaction history.");
        p.add(buildBookProfilePanel(book, () -> showBookDetails(book.getBookId())));
        showContent(p, "Book Details");
        setStatus("Viewing book: " + book.getTitle());
    }

    private void showUserDetails(String userId) {
        User user = findUser(userId);
        if (user == null) {
            showError(this, "User '" + userId + "' was not found.");
            showUserProfileSearch();
            return;
        }
        currentScreen = "UserDetails";
        currentDetailId = user.getUserId();

        JPanel p = makePanel();
        addTitle(p, "User Profile");
        addHint(p, "Active borrowed books, account status, and borrowing history.");
        p.add(buildUserProfilePanel(user, () -> showUserDetails(user.getUserId())));
        showContent(p, "User Profile");
        setStatus("Viewing user: " + user.getName());
    }

    // ═══════════════════ TABLE BUILDERS ═══════════════════

    private void showAboutMe() {
        currentScreen = "AboutMe";
        currentFilter = "ALL";

        JPanel p = makePanel();
        addTitle(p, "About Me");
        addHint(p, "About the developer behind SmartLibrary.");

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));

        centerWrap.add(createAboutPageCard());
        p.add(centerWrap);

        showContent(p, "About Me");
        setStatus("Developer information");
    }

    private JPanel createAboutPageCard() {
        JPanel card = createRoundedPanel(CARD_BG, 18);
        card.setLayout(new BorderLayout(24, 22));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));
        card.setPreferredSize(new Dimension(760, 440));
        card.setMaximumSize(new Dimension(820, 480));

        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);

        JPanel avatar = createRoundedPanel(statusBackground(PRIMARY), 64);
        avatar.setLayout(new GridBagLayout());
        avatar.setPreferredSize(new Dimension(76, 76));
        avatar.add(new JLabel(new VectorIcon(PERSON, 42, PRIMARY)));
        header.add(avatar, BorderLayout.WEST);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("About the Developer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("SmartLibrary desktop application");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 24, 0));
        body.setOpaque(false);
        body.add(createDeveloperInfoPanel());
        body.add(createContactInfoPanel());
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDeveloperInfoPanel() {
        JPanel panel = createRoundedPanel(PANEL_BG, 14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        panel.add(sectionHeading("Developer Information"));
        panel.add(Box.createVerticalStrut(18));
        panel.add(infoLine("Developed by", "Alemu Chamada"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(infoLine("Program", "BSc in Computer Science and Engineering"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(infoLine("University", "Adama Science and Technology University"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(infoLine("Year", "3rd Year"));

        return panel;
    }

    private JPanel createContactInfoPanel() {
        JPanel panel = createRoundedPanel(PANEL_BG, 14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        panel.add(sectionHeading("Contact"));
        panel.add(Box.createVerticalStrut(14));
        panel.add(contactRow("Email", "alemuchamada@gmail.com", MAIL, "mailto:alemuchamada@gmail.com"));
        panel.add(Box.createVerticalStrut(9));
        panel.add(phoneGroup("+251992738116", "+251956047594"));
        panel.add(Box.createVerticalStrut(11));
        panel.add(contactRow("GitHub", "@alemu-chamada", LINK, "https://github.com/alemu-chamada"));
        panel.add(Box.createVerticalStrut(9));
        panel.add(contactRow("LinkedIn", "@alemu-chamada", LINK, "https://linkedin.com/in/alemu-chamada"));
        panel.add(Box.createVerticalStrut(9));
        panel.add(contactRow("Telegram", "@in_messiah", LINK, "https://t.me/in_messiah"));

        return panel;
    }

    private JLabel sectionHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel infoLine(String label, String value) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel key = new JLabel(label);
        key.setFont(new Font("Segoe UI", Font.BOLD, 11));
        key.setForeground(TEXT_GRAY);
        key.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val = new JLabel("<html><body style='width:260px'>" + value + "</body></html>");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(TEXT_DARK);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(key);
        row.add(Box.createVerticalStrut(3));
        row.add(val);
        return row;
    }

    private JPanel contactRow(String label, String value, VectorIcon.IconType iconType, String url) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.setCursor(url == null ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(new VectorIcon(iconType, 17, url == null ? TEXT_GRAY : PRIMARY));
        row.add(icon, BorderLayout.WEST);

        JLabel text = new JLabel("<html><b>" + label + ":</b> " + value + "</html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        text.setForeground(url == null ? TEXT_DARK : PRIMARY);
        row.add(text, BorderLayout.CENTER);

        if (url != null) {
            row.setToolTipText(url);
            row.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    text.setText("<html><b>" + label + ":</b> <u>" + value + "</u></html>");
                }

                public void mouseExited(MouseEvent e) {
                    text.setText("<html><b>" + label + ":</b> " + value + "</html>");
                }

                public void mouseClicked(MouseEvent e) {
                    openLink(url);
                }
            });
        }

        return row;
    }

    private JPanel phoneGroup(String primaryPhone, String secondaryPhone) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel icon = new JLabel(new VectorIcon(PHONE, 17, TEXT_GRAY));
        icon.setVerticalAlignment(SwingConstants.TOP);
        row.add(icon, BorderLayout.WEST);

        JPanel numbers = new JPanel(new GridBagLayout());
        numbers.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 4, 0);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setForeground(TEXT_DARK);
        phoneLabel.setPreferredSize(new Dimension(58, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        numbers.add(phoneLabel, gbc);

        JLabel first = contactValueLabel(primaryPhone);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        numbers.add(first, gbc);

        JLabel spacer = new JLabel("");
        spacer.setPreferredSize(new Dimension(58, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        numbers.add(spacer, gbc);

        JLabel second = contactValueLabel(secondaryPhone);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        numbers.add(second, gbc);

        row.add(numbers, BorderLayout.CENTER);
        return row;
    }

    private JLabel contactValueLabel(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JPanel createRoundedPanel(Color color, int radius) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JTable buildBookTable(List<Book> books) {
        String[] cols = {"ID", "Title", "Author", "Genre", "Stock", "Status", "Borrower"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Book b : books) {
            String status = b.isAvailable() ? "Available" : "Out of stock";
            String borrower = getBorrower(b.getBookId());
            model.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getGenre(),
                    b.getQuantity() + " copies", status, borrower});
        }
        JTable table = styleTable(new JTable(model));
        attachTableNavigation(table, 0, -1);
        return table;
    }

    private String getBorrower(String bookId) {
        String n = controller.getCurrentBorrower(bookId);
        return n != null ? n : "\u2014";
    }

    private Book findBook(String bookId) {
        if (bookId == null) return null;
        for (Book book : controller.getAllBooks()) {
            if (book.getBookId().equalsIgnoreCase(bookId)) return book;
        }
        return null;
    }

    private User findUser(String userId) {
        if (userId == null) return null;
        for (User user : controller.getAllUsers()) {
            if (user.getUserId().equalsIgnoreCase(userId)) return user;
        }
        return null;
    }

    private String displayBook(Transaction t) {
        String name = t.getBookName().isBlank() ? t.getBookId() : t.getBookName();
        return name + " (" + t.getBookId() + ")";
    }

    private String displayUser(Transaction t) {
        String name = t.getUserName().isBlank() ? t.getUserId() : t.getUserName();
        return name + " (" + t.getUserId() + ")";
    }

    private String extractId(String value) {
        if (value == null) return "";
        int open = value.lastIndexOf('(');
        int close = value.lastIndexOf(')');
        if (open >= 0 && close > open) {
            return value.substring(open + 1, close).trim();
        }
        return value.trim();
    }

    private void attachTableNavigation(JTable table, int bookColumn, int userColumn) {
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                if (userColumn >= 0 && col == userColumn) {
                    showUserDetails(extractId(String.valueOf(table.getModel().getValueAt(modelRow, userColumn))));
                } else if (bookColumn >= 0) {
                    showBookDetails(extractId(String.valueOf(table.getModel().getValueAt(modelRow, bookColumn))));
                }
            }
        });
    }

    private JScrollPane wrapTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setPreferredSize(new Dimension(850, 360));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        styleScrollPane(sp);
        return sp;
    }

    // ═══════════════════ LAUNCH ═══════════════════

    public static void launch() {
        StartupErrorHandler.install();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    LibrarySwingApp app = new LibrarySwingApp();
                    app.setVisible(true);
                } catch (Throwable t) {
                    StartupErrorHandler.handle(t);
                }
            });
        } catch (Exception e) {
            StartupErrorHandler.handle(e);
        }
    }
}
