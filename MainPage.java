package coffeeshop;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {

    public MainPage() {
        setTitle("CSMS - Coffee Shop Management System");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        // Use the custom BackgroundPanel for auto-resizing background
        BackgroundPanel background = new BackgroundPanel("C:\\Users\\Laptop Supplier PH\\Documents\\NetBeansProjects\\Cashier\\src\\coffeeshop\\nbg4.jpg");
        background.setLayout(new BorderLayout());

        // ── NAVBAR (Dark Brown - matching inventory and cashier colors) ──
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(92, 51, 23));
        navbar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        navbar.setPreferredSize(new Dimension(900, 80));

        JLabel csmsLogo = new JLabel("CSMS");
        csmsLogo.setFont(new Font("Times New Roman", Font.BOLD, 32));
        csmsLogo.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        btnPanel.setOpaque(false);

        JButton loginBtn = navButton("LOGIN");
        JButton registerBtn = navButton("REGISTER");
        JButton aboutBtn = navButton("ABOUT");

        loginBtn.setToolTipText("Click to login to your existing account. You will be redirected to the login page where you can enter your username and password.");
        registerBtn.setToolTipText("Click to create a new account. You will be redirected to the registration page to fill in your details and choose your job role.");
        aboutBtn.setToolTipText("Click to view information about the developers, system overview, and tool descriptions.");

        // Open login window without disposing main page
        loginBtn.addActionListener(e -> {
            CIMSLogin login = new CIMSLogin();
            login.setVisible(true);
        });

        // Open registration window without disposing main page
        registerBtn.addActionListener(e -> {
            CIMSLogin loginPage = new CIMSLogin();
            loginPage.setVisible(true);
            loginPage.btnRegister.doClick();
        });

        aboutBtn.addActionListener(e -> showAboutDialog());

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        btnPanel.add(aboutBtn);

        navbar.add(csmsLogo, BorderLayout.WEST);
        navbar.add(btnPanel, BorderLayout.EAST);

        // ── HERO CONTENT ──
        JPanel heroPanel = new JPanel(new GridBagLayout());
        heroPanel.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 0, 0));

        JLabel headline1 = new JLabel("Track Every Bean,");
        headline1.setFont(new Font("Times New Roman", Font.BOLD, 36));
        headline1.setForeground(Color.WHITE);
        headline1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headline2 = new JLabel("Serve Every Dream.");
        headline2.setFont(new Font("Times New Roman", Font.BOLD, 36));
        headline2.setForeground(Color.WHITE);
        headline2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea(
            "Stay on top of every bean, every supply, and every\n" +
            "sale with a system built for efficiency and consistency."
        );
        desc.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        desc.setForeground(Color.WHITE);
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setMaximumSize(new Dimension(400, 80));

        JButton getStartedBtn = new JButton("GET STARTED");
        getStartedBtn.setFont(new Font("Times New Roman", Font.BOLD, 14));
        getStartedBtn.setBackground(Color.WHITE);
        getStartedBtn.setForeground(new Color(92, 51, 23));
        getStartedBtn.setFocusPainted(false);
        getStartedBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getStartedBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        getStartedBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        getStartedBtn.setToolTipText("Click to get started with CSMS. You will be taken to the login page to begin using the system.");
        
        getStartedBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                getStartedBtn.setBackground(new Color(245, 245, 220));
            }
            public void mouseExited(MouseEvent e) {
                getStartedBtn.setBackground(Color.WHITE);
            }
        });
        
        // Open login window without disposing main page
        getStartedBtn.addActionListener(e -> {
            CIMSLogin login = new CIMSLogin();
            login.setVisible(true);
        });

        textPanel.add(headline1);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(headline2);
        textPanel.add(Box.createVerticalStrut(16));
        textPanel.add(desc);
        textPanel.add(Box.createVerticalStrut(30));
        textPanel.add(getStartedBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        heroPanel.add(textPanel, gbc);

        // ── FOOTER ──
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(0, 0, 0, 150));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JLabel footerLabel = new JLabel("All Rights Reserved 2026 | CSMS - Coffee Shop Management System");
        footerLabel.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        footerLabel.setForeground(Color.WHITE);

        footerPanel.add(footerLabel);

        background.add(navbar, BorderLayout.NORTH);
        background.add(heroPanel, BorderLayout.CENTER);
        background.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(background);
        setVisible(true);
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "About CSMS", true);
        aboutDialog.setSize(600, 550);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setResizable(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Create tabbed pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Times New Roman", Font.BOLD, 13));

        // Panel 1: System Overview (with scroll)
        JPanel overviewPanel = createOverviewPanel();
        JScrollPane overviewScroll = new JScrollPane(overviewPanel);
        overviewScroll.setBorder(null);
        overviewScroll.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("System Overview", overviewScroll);

        // Panel 2: How to Register (with scroll)
        JPanel registerPanel = createRegisterPanel();
        JScrollPane registerScroll = new JScrollPane(registerPanel);
        registerScroll.setBorder(null);
        registerScroll.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("How to Register", registerScroll);

        // Panel 3: Tools Description (with scroll)
        JPanel toolsPanel = createToolsPanel();
        JScrollPane toolsScroll = new JScrollPane(toolsPanel);
        toolsScroll.setBorder(null);
        toolsScroll.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("Tools Description", toolsScroll);

        // Panel 4: Developers (with scroll)
        JPanel developersPanel = createDevelopersPanel();
        JScrollPane developersScroll = new JScrollPane(developersPanel);
        developersScroll.setBorder(null);
        developersScroll.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("Developers", developersScroll);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Close button at bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        
        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setFont(new Font("Times New Roman", Font.BOLD, 12));
        closeBtn.setBackground(new Color(92, 51, 23));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        closeBtn.addActionListener(e -> aboutDialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        aboutDialog.add(mainPanel);
        aboutDialog.setVisible(true);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("About CSMS");
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.setForeground(new Color(92, 51, 23));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(92, 51, 23));

        JLabel whyTitle = new JLabel("Why was CSMS developed?");
        whyTitle.setFont(new Font("Times New Roman", Font.BOLD, 14));
        whyTitle.setForeground(new Color(92, 51, 23));
        whyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        whyTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JTextArea whyDesc = new JTextArea(
            "CSMS (Coffee Shop Management System) was developed to help coffee shops and cafes\n" +
            "manage their daily operations more efficiently. The system addresses common challenges such as:\n\n" +
            "• Manual tracking of inventory leading to stockouts or overstocking\n" +
            "• Inefficient sales transaction processing\n" +
            "• Lack of real-time visibility into stock levels\n" +
            "• Difficulty in managing user roles and permissions\n" +
            "• No centralized system for recording sales history\n\n" +
            "This system provides a digital solution that streamlines inventory management, sales processing,\n" +
            "and user administration, ultimately helping coffee businesses save time, reduce errors,\n" +
            "and improve overall operational efficiency."
        );
        whyDesc.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        whyDesc.setForeground(Color.DARK_GRAY);
        whyDesc.setEditable(false);
        whyDesc.setOpaque(false);
        whyDesc.setWrapStyleWord(true);
        whyDesc.setLineWrap(true);
        whyDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel version = new JLabel("Version: 1.0");
        version.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        version.setForeground(Color.GRAY);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        version.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sep);
        panel.add(whyTitle);
        panel.add(whyDesc);
        panel.add(version);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("How to Register");
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.setForeground(new Color(92, 51, 23));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(92, 51, 23));

        JTextArea instructions = new JTextArea(
            "Follow these steps to create a new user account:\n\n" +
            "Step 1: From the Main Page, click the REGISTER button in the top navigation bar\n\n" +
            "Step 2: Select your job role by clicking one of the following buttons:\n" +
            "        - ADMIN (Full system access for management)\n" +
            "        - CASHIER (Sales and transaction access)\n" +
            "        - INVENTORY (Stock and product management access)\n\n" +
            "Step 3: Fill out the registration form with your:\n" +
            "        - First Name\n" +
            "        - Last Name\n" +
            "        - Username (Use your email address)\n" +
            "        - Password (Must be at least 6 characters)\n" +
            "        - Birthdate (Format: YYYY-MM-DD)\n" +
            "        - Sex (Select M or F from dropdown)\n\n" +
            "Step 4: Click REGISTER ACCOUNT to submit your registration\n\n" +
            "Step 5: A success message will appear. Click Yes to go back to the login page\n\n" +
            "Step 6: Login using your new username and password\n\n" +
            "Note: If you receive a message that the username already exists, try a different username\n" +
            "or contact your administrator."
        );
        instructions.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        instructions.setForeground(Color.DARK_GRAY);
        instructions.setEditable(false);
        instructions.setOpaque(false);
        instructions.setWrapStyleWord(true);
        instructions.setLineWrap(true);
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(15));
        panel.add(instructions);

        return panel;
    }

    private JPanel createToolsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("System Tools Description");
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.setForeground(new Color(92, 51, 23));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(92, 51, 23));

        // Admin Section
        JLabel adminTitle = new JLabel("ADMIN SYSTEM");
        adminTitle.setFont(new Font("Times New Roman", Font.BOLD, 14));
        adminTitle.setForeground(new Color(92, 51, 23));
        adminTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        adminTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JTextArea adminDesc = new JTextArea(
            "Provides full control over the entire system. Access includes:\n" +
            "• Inventory Management - Add, edit, delete inventory items\n" +
            "• Users Management - Add, edit, delete user accounts and assign roles\n" +
            "• Sales Management - View all sales records and print reports"
        );
        adminDesc.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        adminDesc.setForeground(Color.DARK_GRAY);
        adminDesc.setEditable(false);
        adminDesc.setOpaque(false);
        adminDesc.setWrapStyleWord(true);
        adminDesc.setLineWrap(true);
        adminDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cashier Section
        JLabel cashierTitle = new JLabel("CASHIER SYSTEM");
        cashierTitle.setFont(new Font("Times New Roman", Font.BOLD, 14));
        cashierTitle.setForeground(new Color(92, 51, 23));
        cashierTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        cashierTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JTextArea cashierDesc = new JTextArea(
            "Designed for processing customer transactions. Access includes:\n" +
            "• Add Transaction - Select products and add to cart for checkout\n" +
            "• Edit Transaction - Modify quantities before payment\n" +
            "• View Sales Record - View all past sales transactions\n" +
            "• Update Sales Record - Edit existing sales entries\n" +
            "• Update Inventory - View current stock levels"
        );
        cashierDesc.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        cashierDesc.setForeground(Color.DARK_GRAY);
        cashierDesc.setEditable(false);
        cashierDesc.setOpaque(false);
        cashierDesc.setWrapStyleWord(true);
        cashierDesc.setLineWrap(true);
        cashierDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Inventory Section
        JLabel inventoryTitle = new JLabel("INVENTORY MANAGER SYSTEM");
        inventoryTitle.setFont(new Font("Times New Roman", Font.BOLD, 14));
        inventoryTitle.setForeground(new Color(92, 51, 23));
        inventoryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inventoryTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JTextArea inventoryDesc = new JTextArea(
            "Responsible for managing stock levels and product recipes. Access includes:\n" +
            "• Dashboard - View summary statistics and alerts\n" +
            "• Inventory Tab - Add, edit, delete stock items\n" +
            "• Product Menu Tab - Add products and set prices\n" +
            "• Recipe Table Tab - View ingredient requirements\n" +
            "• Print / Export Tab - Generate inventory reports"
        );
        inventoryDesc.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        inventoryDesc.setForeground(Color.DARK_GRAY);
        inventoryDesc.setEditable(false);
        inventoryDesc.setOpaque(false);
        inventoryDesc.setWrapStyleWord(true);
        inventoryDesc.setLineWrap(true);
        inventoryDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sep);
        panel.add(adminTitle);
        panel.add(adminDesc);
        panel.add(cashierTitle);
        panel.add(cashierDesc);
        panel.add(inventoryTitle);
        panel.add(inventoryDesc);

        return panel;
    }

    private JPanel createDevelopersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Development Team");
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.setForeground(new Color(92, 51, 23));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(92, 51, 23));

        JLabel groupInfo = new JLabel("Cebu Technological University - Main Campus");
        groupInfo.setFont(new Font("Times New Roman", Font.BOLD, 13));
        groupInfo.setForeground(new Color(92, 51, 23));
        groupInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        groupInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel courseInfo = new JLabel("Bachelor of Science in Information Technology");
        courseInfo.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        courseInfo.setForeground(Color.GRAY);
        courseInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel membersTitle = new JLabel("Developers:");
        membersTitle.setFont(new Font("Times New Roman", Font.BOLD, 13));
        membersTitle.setForeground(new Color(92, 51, 23));
        membersTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        membersTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel membersPanel = new JPanel();
        membersPanel.setLayout(new BoxLayout(membersPanel, BoxLayout.Y_AXIS));
        membersPanel.setOpaque(false);
        membersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] members = {
            "Anthon Nicholas",
            "Hanz Dee L. Dalmino",
            "Steve Larino",
            "Rica Pepito",
            "Hannah Oliva",
            "Wesley Tampus"
        };

        for (String member : members) {
            JLabel memberLabel = new JLabel(member);
            memberLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
            memberLabel.setForeground(new Color(60, 60, 60));
            memberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            membersPanel.add(memberLabel);
            membersPanel.add(Box.createVerticalStrut(5));
        }

        JLabel thanks = new JLabel("Thank you for using CSMS!");
        thanks.setFont(new Font("Times New Roman", Font.BOLD, 12));
        thanks.setForeground(new Color(92, 51, 23));
        thanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        thanks.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sep);
        panel.add(groupInfo);
        panel.add(courseInfo);
        panel.add(membersTitle);
        panel.add(membersPanel);
        panel.add(thanks);

        return panel;
    }

    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Times New Roman", Font.BOLD, 13));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(92, 51, 23));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(200, 180, 150));
                b.setForeground(new Color(92, 51, 23));
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(Color.WHITE);
                b.setForeground(new Color(92, 51, 23));
            }
        });
        return b;
    }

    // --- CUSTOM INNER CLASS FOR AUTO-RESIZING BACKGROUND ---
    class BackgroundPanel extends JPanel {
        private Image img;

        public BackgroundPanel(String path) {
            try {
                java.io.File file = new java.io.File(path);
                if (file.exists()) {
                    ImageIcon icon = new ImageIcon(path);
                    this.img = icon.getImage();
                    System.out.println("Background image loaded successfully");
                } else {
                    System.out.println("Background image not found at: " + path);
                }
            } catch (Exception e) {
                System.out.println("Error loading background: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            } else {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(92, 51, 23), getWidth(), getHeight(), new Color(160, 100, 60));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainPage();
        });
    }
}
