package coffeeshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CIMSLogin extends JFrame implements ActionListener {

    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin, btnEye, btnRegister;
    boolean passwordVisible = false;

    public CIMSLogin() {
        setTitle("CIMS - Coffee Inventory Management System");
        setSize(500, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Test database connection on startup
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(this, 
                "Cannot connect to database!\nPlease check your database connection.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Main panel with white background
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 40, 8, 40);
        gbc.anchor = GridBagConstraints.CENTER;

        // ===== LOGO / CIMS HEADER =====
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        
        JLabel logoText1 = new JLabel("CIMS", SwingConstants.CENTER);
        logoText1.setFont(new Font("Georgia", Font.BOLD, 60));
        logoText1.setForeground(new Color(92, 51, 23));
        logoText1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoText2 = new JLabel("COFFEE INVENTORY", SwingConstants.CENTER);
        logoText2.setFont(new Font("Georgia", Font.BOLD, 20));
        logoText2.setForeground(new Color(111, 78, 55));
        logoText2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoText3 = new JLabel("MANAGEMENT SYSTEM", SwingConstants.CENTER);
        logoText3.setFont(new Font("Georgia", Font.BOLD, 20));
        logoText3.setForeground(new Color(111, 78, 55));
        logoText3.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(Box.createVerticalStrut(20));
        logoPanel.add(logoText1);
        logoPanel.add(Box.createVerticalStrut(8));
        logoPanel.add(logoText2);
        logoPanel.add(logoText3);
        logoPanel.add(Box.createVerticalStrut(15));

        // Decorative line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(92, 51, 23));
        separator.setBackground(new Color(92, 51, 23));
        separator.setMaximumSize(new Dimension(350, 2));

        // ===== CREATE A UNIFORM PANEL FOR FORM FIELDS =====
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.gridx = 0;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(5, 0, 5, 0);
        formGbc.anchor = GridBagConstraints.WEST;

        // ===== USERNAME FIELD =====
        JLabel usernameLabel = new JLabel("USERNAME");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(new Color(92, 51, 23));
        
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setPreferredSize(new Dimension(350, 40));

        // ===== PASSWORD FIELD =====
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(92, 51, 23));
        
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        passPanel.setBackground(Color.WHITE);
        passPanel.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 1));

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtPassword.setBackground(Color.WHITE);

        btnEye = new JButton("SHOW");
        btnEye.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEye.setForeground(new Color(92, 51, 23));
        btnEye.setBackground(Color.WHITE);
        btnEye.setFocusPainted(false);
        btnEye.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 1));
        btnEye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEye.setPreferredSize(new Dimension(65, 40));
        btnEye.addActionListener(this);

        passPanel.add(txtPassword, BorderLayout.CENTER);
        passPanel.add(btnEye, BorderLayout.EAST);

        // Add components to form panel with consistent sizing
        formGbc.gridy = 0;
        formGbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(usernameLabel, formGbc);
        
        formGbc.gridy = 1;
        formGbc.insets = new Insets(0, 0, 15, 0);
        txtUsername.setMinimumSize(new Dimension(350, 40));
        txtUsername.setPreferredSize(new Dimension(350, 40));
        formPanel.add(txtUsername, formGbc);
        
        formGbc.gridy = 2;
        formGbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(passwordLabel, formGbc);
        
        formGbc.gridy = 3;
        formGbc.insets = new Insets(0, 0, 0, 0);
        passPanel.setPreferredSize(new Dimension(350, 40));
        formPanel.add(passPanel, formGbc);

        // ===== LOGIN BUTTON =====
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnLogin.setBackground(new Color(92, 51, 23));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btnLogin.setPreferredSize(new Dimension(350, 50));
        btnLogin.addActionListener(this);
        
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(70, 35, 15));
            }
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(92, 51, 23));
            }
        });

        // ===== REGISTER BUTTON =====
        btnRegister = new JButton("REGISTER");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setBackground(new Color(92, 51, 23));
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        btnRegister.setPreferredSize(new Dimension(200, 35));
        btnRegister.addActionListener(this);
        
        btnRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnRegister.setBackground(new Color(70, 35, 15));
            }
            public void mouseExited(MouseEvent e) {
                btnRegister.setBackground(new Color(92, 51, 23));
            }
        });

        // ===== FOOTER =====
        JLabel footerLabel = new JLabel("All rights reserved 2026", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(139, 69, 19));

        // ===== ADD ALL COMPONENTS TO MAIN PANEL =====
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 40, 5, 40);
        panel.add(logoPanel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 40, 20, 40);
        panel.add(separator, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 40, 10, 40);
        panel.add(formPanel, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 40, 5, 40);
        panel.add(btnLogin, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 40, 20, 40);
        panel.add(btnRegister, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 40, 20, 40);
        panel.add(footerLabel, gbc);

        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEye) {
            if (passwordVisible) {
                txtPassword.setEchoChar('•');
                btnEye.setText("SHOW");
                passwordVisible = false;
            } else {
                txtPassword.setEchoChar((char) 0);
                btnEye.setText("HIDE");
                passwordVisible = true;
            }
        }

        if (e.getSource() == btnLogin) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter password", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            authenticateUser(username, password);
        }
        
        if (e.getSource() == btnRegister) {
            openRegistrationWindow();
        }
    }
    
    private void openRegistrationWindow() {
        JFrame registerFrame = new JFrame("Register New Account");
        registerFrame.setSize(500, 600);
        registerFrame.setLocationRelativeTo(this);
        registerFrame.setResizable(false);
        registerFrame.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 30, 10, 30);
        
        JLabel titleLabel = new JLabel("CREATE NEW ACCOUNT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(92, 51, 23));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 30, 20, 30);
        mainPanel.add(titleLabel, gbc);
        
        JLabel roleLabel = new JLabel("SELECT YOUR JOB ROLE:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(new Color(92, 51, 23));
        
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(roleLabel, gbc);
        
        JPanel rolePanel = new JPanel();
        rolePanel.setBackground(Color.WHITE);
        rolePanel.setLayout(new GridLayout(1, 3, 10, 0));
        
        JButton btnAdmin = new JButton("ADMIN");
        JButton btnCashier = new JButton("CASHIER");
        JButton btnInventory = new JButton("INVENTORY");
        
        btnAdmin.setBackground(new Color(92, 51, 23));
        btnAdmin.setForeground(Color.BLACK);
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdmin.setFocusPainted(false);
        btnAdmin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnCashier.setBackground(new Color(92, 51, 23));
        btnCashier.setForeground(Color.BLACK);
        btnCashier.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCashier.setFocusPainted(false);
        btnCashier.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnInventory.setBackground(new Color(92, 51, 23));
        btnInventory.setForeground(Color.BLACK);
        btnInventory.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnInventory.setFocusPainted(false);
        btnInventory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        rolePanel.add(btnAdmin);
        rolePanel.add(btnCashier);
        rolePanel.add(btnInventory);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 30, 15, 30);
        mainPanel.add(rolePanel, gbc);
        
        JLabel firstNameLabel = new JLabel("FIRST NAME:");
        firstNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        firstNameLabel.setForeground(new Color(92, 51, 23));
        
        JTextField txtFirstName = new JTextField();
        txtFirstName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFirstName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(firstNameLabel, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 30, 10, 30);
        mainPanel.add(txtFirstName, gbc);
        
        JLabel lastNameLabel = new JLabel("LAST NAME:");
        lastNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lastNameLabel.setForeground(new Color(92, 51, 23));
        
        JTextField txtLastName = new JTextField();
        txtLastName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLastName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(lastNameLabel, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 30, 10, 30);
        mainPanel.add(txtLastName, gbc);
        
        JLabel usernameRegLabel = new JLabel("USERNAME:");
        usernameRegLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameRegLabel.setForeground(new Color(92, 51, 23));
        
        JTextField txtRegUsername = new JTextField();
        txtRegUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtRegUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JLabel usernameHint = new JLabel("Note: Use your email as username");
        usernameHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        usernameHint.setForeground(new Color(139, 69, 19));
        
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(usernameRegLabel, gbc);
        
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 30, 2, 30);
        mainPanel.add(txtRegUsername, gbc);
        
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 30, 10, 30);
        mainPanel.add(usernameHint, gbc);
        
        JLabel passwordRegLabel = new JLabel("PASSWORD:");
        passwordRegLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordRegLabel.setForeground(new Color(92, 51, 23));
        
        JPasswordField txtRegPassword = new JPasswordField();
        txtRegPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtRegPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JLabel passwordHint = new JLabel("Create a secure password (min. 6 characters)");
        passwordHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        passwordHint.setForeground(new Color(139, 69, 19));
        
        gbc.gridy = 10;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(passwordRegLabel, gbc);
        
        gbc.gridy = 11;
        gbc.insets = new Insets(0, 30, 2, 30);
        mainPanel.add(txtRegPassword, gbc);
        
        gbc.gridy = 12;
        gbc.insets = new Insets(0, 30, 15, 30);
        mainPanel.add(passwordHint, gbc);
        
        JLabel birthdateLabel = new JLabel("BIRTHDATE (YYYY-MM-DD):");
        birthdateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        birthdateLabel.setForeground(new Color(92, 51, 23));
        
        JTextField txtBirthdate = new JTextField();
        txtBirthdate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBirthdate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        gbc.gridy = 13;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(birthdateLabel, gbc);
        
        gbc.gridy = 14;
        gbc.insets = new Insets(0, 30, 15, 30);
        mainPanel.add(txtBirthdate, gbc);
        
        JLabel sexLabel = new JLabel("SEX (M/F):");
        sexLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sexLabel.setForeground(new Color(92, 51, 23));
        
        JComboBox<String> cbSex = new JComboBox<>(new String[]{"M", "F"});
        cbSex.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSex.setBackground(Color.WHITE);
        cbSex.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 1));
        
        gbc.gridy = 15;
        gbc.insets = new Insets(10, 30, 5, 30);
        mainPanel.add(sexLabel, gbc);
        
        gbc.gridy = 16;
        gbc.insets = new Insets(0, 30, 20, 30);
        mainPanel.add(cbSex, gbc);
        
        JButton btnSubmit = new JButton("REGISTER ACCOUNT");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(92, 51, 23));
        btnSubmit.setForeground(Color.BLACK);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(350, 45));
        
        gbc.gridy = 17;
        gbc.insets = new Insets(10, 30, 20, 30);
        mainPanel.add(btnSubmit, gbc);
        
        JButton btnBack = new JButton("BACK TO LOGIN");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(new Color(92, 51, 23));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(92, 51, 23), 1));
        btnBack.setPreferredSize(new Dimension(200, 35));
        
        gbc.gridy = 18;
        gbc.insets = new Insets(5, 30, 20, 30);
        mainPanel.add(btnBack, gbc);
        
        final int[] selectedRoleId = {0};
        
        btnAdmin.addActionListener(e -> {
            selectedRoleId[0] = 1;
            btnAdmin.setBackground(new Color(70, 35, 15));
            btnCashier.setBackground(new Color(92, 51, 23));
            btnInventory.setBackground(new Color(92, 51, 23));
            usernameHint.setText("Example: johndoe@admin.com");
        });
        
        btnCashier.addActionListener(e -> {
            selectedRoleId[0] = 2;
            btnCashier.setBackground(new Color(70, 35, 15));
            btnAdmin.setBackground(new Color(92, 51, 23));
            btnInventory.setBackground(new Color(92, 51, 23));
            usernameHint.setText("Example: johndoe@cashier.com");
        });
        
        btnInventory.addActionListener(e -> {
            selectedRoleId[0] = 3;
            btnInventory.setBackground(new Color(70, 35, 15));
            btnAdmin.setBackground(new Color(92, 51, 23));
            btnCashier.setBackground(new Color(92, 51, 23));
            usernameHint.setText("Example: johndoe@inventory.com");
        });
        
        btnSubmit.addActionListener(e -> {
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String username = txtRegUsername.getText().trim();
            String password = new String(txtRegPassword.getPassword()).trim();
            String birthdate = txtBirthdate.getText().trim();
            String sex = (String) cbSex.getSelectedItem();
            
            if (selectedRoleId[0] == 0) {
                JOptionPane.showMessageDialog(registerFrame, "Please select a job role!");
                return;
            }
            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Please enter your first and last name!");
                return;
            }
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Please enter a username!");
                return;
            }
            if (password.isEmpty() || password.length() < 6) {
                JOptionPane.showMessageDialog(registerFrame, "Password must be at least 6 characters!");
                return;
            }
            if (birthdate.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Please enter your birthdate!");
                return;
            }
            
            if (registerUser(firstName, lastName, birthdate, sex, password, selectedRoleId[0], username)) {
                int result = JOptionPane.showConfirmDialog(registerFrame, 
                    "Registration Successful!\n\nYou may now login with your credentials.\n\nWould you like to go back to Login page?",
                    "Success", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    registerFrame.dispose();
                }
            }
        });
        
        btnBack.addActionListener(e -> registerFrame.dispose());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        registerFrame.add(scrollPane);
        registerFrame.setVisible(true);
    }
    
    private boolean registerUser(String firstName, String lastName, String birthdate,
                                  String sex, String password, int roleId, String username) {
        String checkQuery = "SELECT first_name FROM user_data WHERE first_name = ? AND last_name = ?";
        String insertQuery = "INSERT INTO user_data (first_name, last_name, birthdate, sex, password, roles, user_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, firstName);
                checkStmt.setString(2, lastName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "User with this name already exists!");
                    return false;
                }
            }
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, birthdate);
                insertStmt.setString(4, sex);
                insertStmt.setString(5, password);
                insertStmt.setInt(6, roleId);
                insertStmt.setString(7, username);
                int rowsAffected = insertStmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    private void authenticateUser(String username, String password) {
        String query = "SELECT u.user_id, u.first_name, u.last_name, j.job_roles " +
                       "FROM user_data u " +
                       "JOIN job_position j ON u.roles = j.job_id " +
                       "WHERE u.user_name = ? AND u.password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String role = rs.getString("job_roles");
                String fullName = firstName + " " + lastName;
                
                dispose();
                
                if (role.equalsIgnoreCase("Admin")) {
                    openAdminSystem();
                } else if (role.equalsIgnoreCase("Inventory Manager")) {
                    openInventoryManager();
                } else {
                    openCashierSystem(fullName, userId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openCashierSystem(String cashierName, int cashierId) {
        try {
            CashierSystem cashierSystem = new CashierSystem(cashierName, cashierId);
            cashierSystem.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to open Cashier System.\nError: " + ex.getMessage());
            ex.printStackTrace();
            new CIMSLogin();
        }
    }

    private void openAdminSystem() {
        try {
            AdminSystems adminSystem = new AdminSystems();
            adminSystem.setVisible(true);
            JOptionPane.showMessageDialog(null, "Welcome Admin!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to open Admin System.\nError: " + ex.getMessage());
            ex.printStackTrace();
            new CIMSLogin();
        }
    }

    private void openInventoryManager() {
        try {
            InventoryManager invManager = new InventoryManager();
            invManager.setVisible(true);
            JOptionPane.showMessageDialog(null, "Welcome Inventory Manager!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to open Inventory Manager System.\nError: " + ex.getMessage());
            ex.printStackTrace();
            new CIMSLogin();
        }
    }
}
