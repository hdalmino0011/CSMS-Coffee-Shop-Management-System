package coffeeshop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CashierSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentCashier;
    private int currentCashierId;
    
    // Data storage
    private ArrayList<Product> products;
    private ArrayList<CartItem> currentCart;
    
    // Database connection
    private Connection conn;
    
    // Table models
    private DefaultTableModel cartTableModel;
    private DefaultTableModel salesTableModel;
    private DefaultTableModel inventoryTableModel;
    
    public CashierSystem(String cashierName, int cashierId) {
        this.currentCashier = cashierName;
        this.currentCashierId = cashierId;
        
        setTitle("CIMS - Coffee Inventory Management System - Cashier: " + cashierName);
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize database connection
        conn = DBConnection.getConnection();
        
        initializeData();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createDashboardScreen(), "dashboard");
        mainPanel.add(createTransactionScreen(), "transaction");
        mainPanel.add(createSalesRecordScreen(), "salesRecord");
        mainPanel.add(createInventoryViewScreen(), "inventory");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "dashboard");
    }
    
    private void initializeData() {
        products = new ArrayList<>();
        currentCart = new ArrayList<>();
        loadProductsFromDatabase();
    }
    
    private void loadProductsFromDatabase() {
        String query = "SELECT p.product_id, p.product_name, p.price, c.category_name " +
                       "FROM product_menu p " +
                       "LEFT JOIN product_categories c ON p.category_id = c.category_id " +
                       "ORDER BY p.product_id";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            products.clear();
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getDouble("price")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean checkAndDeductInventory(int productId, int quantity) {
        ensureConnection();
        
        String recipeQuery = "SELECT r.ingredients_need, r.amount_needed, s.item_name, s.total_unit_count " +
                             "FROM product_recipes r " +
                             "JOIN product_stocks s ON r.ingredients_need = s.item_id " +
                             "WHERE r.product_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(recipeQuery)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            Map<Integer, Double> ingredientsNeeded = new HashMap<>();
            Map<Integer, String> ingredientNames = new HashMap<>();
            Map<Integer, Integer> currentStocks = new HashMap<>();
            
            while (rs.next()) {
                int ingredientId = rs.getInt("ingredients_need");
                double amountNeeded = rs.getDouble("amount_needed") * quantity;
                String ingredientName = rs.getString("item_name");
                int currentStock = rs.getInt("total_unit_count");
                
                ingredientsNeeded.put(ingredientId, amountNeeded);
                ingredientNames.put(ingredientId, ingredientName);
                currentStocks.put(ingredientId, currentStock);
            }
            
            if (ingredientsNeeded.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No recipe found for this product!",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            StringBuilder insufficientMsg = new StringBuilder();
            for (Map.Entry<Integer, Double> entry : ingredientsNeeded.entrySet()) {
                int ingredientId = entry.getKey();
                double needed = entry.getValue();
                int currentStock = currentStocks.get(ingredientId);
                
                if (currentStock < needed) {
                    insufficientMsg.append("\n• ").append(ingredientNames.get(ingredientId))
                                  .append(": Available ").append(currentStock)
                                  .append(", Needed ").append(needed);
                }
            }
            
            if (insufficientMsg.length() > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Insufficient stock for:" + insufficientMsg.toString(),
                    "Stock Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            for (Map.Entry<Integer, Double> entry : ingredientsNeeded.entrySet()) {
                int ingredientId = entry.getKey();
                double needed = entry.getValue();
                
                String updateStock = "UPDATE product_stocks SET total_unit_count = total_unit_count - ? " +
                                    "WHERE item_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStock)) {
                    updateStmt.setDouble(1, needed);
                    updateStmt.setInt(2, ingredientId);
                    updateStmt.executeUpdate();
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error checking inventory: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    private void saveSaleToDatabase(String productName, int quantity, double totalPrice) {
        String query = "INSERT INTO sales_record (product_name, quantity, total_price) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, productName);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, totalPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving sale: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel createDashboardScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createHeaderPanel("CASHIER DASHBOARD");
        JPanel statsPanel = createStatsPanel();
        
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        
        JButton addTransactionBtn = createMenuButton("ADD TRANSACTION", new Color(46, 125, 50));
        JButton editTransactionBtn = createMenuButton("EDIT TRANSACTION", new Color(25, 118, 210));
        JButton viewInventoryBtn = createMenuButton("UPDATE INVENTORY", new Color(245, 124, 0));
        JButton viewSalesBtn = createMenuButton("VIEW SALES RECORD", new Color(156, 39, 176));
        JButton updateSalesBtn = createMenuButton("UPDATE SALES RECORD", new Color(96, 125, 139));
        
        addTransactionBtn.addActionListener(e -> {
            currentCart.clear();
            refreshCartTable();
            cardLayout.show(mainPanel, "transaction");
        });
        
        editTransactionBtn.addActionListener(e -> {
            if (currentCart.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No active transaction to edit. Start a new transaction first.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                refreshCartTable();
                cardLayout.show(mainPanel, "transaction");
            }
        });
        
        viewInventoryBtn.addActionListener(e -> {
            refreshInventoryTable();
            cardLayout.show(mainPanel, "inventory");
        });
        
        viewSalesBtn.addActionListener(e -> {
            refreshSalesTable();
            cardLayout.show(mainPanel, "salesRecord");
        });
        
        updateSalesBtn.addActionListener(e -> {
            refreshSalesTable();
            cardLayout.show(mainPanel, "salesRecord");
        });
        
        gbc.gridx = 0; gbc.gridy = 0;
        menuPanel.add(addTransactionBtn, gbc);
        gbc.gridx = 1;
        menuPanel.add(editTransactionBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        menuPanel.add(viewInventoryBtn, gbc);
        gbc.gridx = 1;
        menuPanel.add(viewSalesBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        menuPanel.add(updateSalesBtn, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Wrap stats + menu together so stats don't stretch to fill the screen
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(statsPanel);
        centerPanel.add(menuPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }
    
    // ===== UPDATED: Wider logout button + goes back to login =====
    private JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(92, 51, 23));
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        
        JLabel cashierLabel = new JLabel("Cashier: " + currentCashier);
        cashierLabel.setForeground(Color.WHITE);
        cashierLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(new Color(92, 51, 23));
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(120, 40)); // wider button
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new CIMSLogin(); // Go back to login page
            }
        });
        
            JPanel westPanel = new JPanel();
            westPanel.setOpaque(false);
            westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
            westPanel.add(Box.createVerticalStrut(8));
            westPanel.add(titleLabel);
            westPanel.add(Box.createVerticalStrut(4));
            westPanel.add(cashierLabel);
        
        headerPanel.add(westPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        
        int totalProducts = products.size();
        int todaySales = getTodaySalesCount();
        double todayRevenue = getTodayRevenue();
        int lowStockItems = getLowStockCount();
        
        statsPanel.add(createStatCard("TOTAL PRODUCTS", String.valueOf(totalProducts), new Color(46, 125, 50)));
        statsPanel.add(createStatCard("TODAY'S SALES", String.valueOf(todaySales), new Color(25, 118, 210)));
        statsPanel.add(createStatCard("TODAY'S REVENUE", "₱" + String.format("%.2f", todayRevenue), new Color(245, 124, 0)));
        statsPanel.add(createStatCard("️LOW STOCK ITEMS", String.valueOf(lowStockItems), new Color(198, 40, 40)));
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        
        return card;
    }
    
    private int getTodaySalesCount() {
        String query = "SELECT COUNT(*) as count FROM sales_record WHERE DATE(date_purchased) = CURDATE()";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    private double getTodayRevenue() {
        String query = "SELECT SUM(total_price) as total FROM sales_record WHERE DATE(date_purchased) = CURDATE()";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    private int getLowStockCount() {
        String query = "SELECT COUNT(*) as count FROM product_stocks WHERE total_unit_count <= minimum_unit_count";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    private JButton createMenuButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 65));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }
    
    private JPanel createTransactionScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createHeaderPanel("ADD / EDIT TRANSACTION");
        
        JButton backButton = new JButton("← BACK TO DASHBOARD");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(92, 51, 23));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(new Color(92, 51, 23), 2));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
        
        JPanel headerSouth = new JPanel(new BorderLayout());
        headerSouth.setOpaque(false);
        headerSouth.add(backButton, BorderLayout.EAST);
        headerPanel.add(headerSouth, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setBackground(Color.WHITE);
        
        JPanel productsPanel = createProductsPanel();
        JPanel cartPanel = createCartPanel();
        
        splitPane.setLeftComponent(productsPanel);
        splitPane.setRightComponent(cartPanel);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel productsLabel = new JLabel("PRODUCTS MENU");
        productsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        productsLabel.setForeground(new Color(92, 51, 23));
        productsLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(92, 51, 23)));
        
        JPanel productGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        productGrid.setBackground(Color.WHITE);
        productGrid.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        for (Product product : products) {
            JButton productBtn = new JButton("<html><center><b>" + product.name + "</b><br>" +
                "<font size='2'>" + product.type + "</font><br>" +
                "<font color='green'>₱" + String.format("%.2f", product.cost) + "</font></center></html>");
            productBtn.setFont(new Font("Arial", Font.PLAIN, 14));
            productBtn.setBackground(Color.WHITE);
            productBtn.setForeground(Color.BLACK);
            productBtn.setBorder(BorderFactory.createLineBorder(new Color(92, 51, 23), 2));
            productBtn.setFocusPainted(false);
            productBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            productBtn.setPreferredSize(new Dimension(220, 100));
            productBtn.addActionListener(e -> addToCart(product));
            productBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    productBtn.setBackground(new Color(245, 245, 220));
                }
                public void mouseExited(MouseEvent e) {
                    productBtn.setBackground(Color.WHITE);
                }
            });
            productGrid.add(productBtn);
        }
        
        JScrollPane productScroll = new JScrollPane(productGrid);
        productScroll.setBorder(null);
        productScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(productsLabel, BorderLayout.NORTH);
        panel.add(productScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel cartLabel = new JLabel("CURRENT TRANSACTION");
        cartLabel.setFont(new Font("Arial", Font.BOLD, 20));
        cartLabel.setForeground(new Color(92, 51, 23));
        cartLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(92, 51, 23)));
        
        String[] columns = {"Product", "Qty", "Price", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable cartTable = new JTable(cartTableModel);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
        cartTable.setRowHeight(35);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(new Color(92, 51, 23));
        cartTable.getTableHeader().setForeground(Color.BLACK);
        cartTable.setSelectionBackground(new Color(245, 245, 220));
        
        JScrollPane tableScroll = new JScrollPane(cartTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel totalLabel = new JLabel("TOTAL: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 28));
        totalLabel.setForeground(new Color(92, 51, 23));
        totalLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(200, 200, 200)));
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton removeBtn = createActionButton("REMOVE SELECTED", new Color(198, 40, 40));
        JButton clearBtn = createActionButton("CLEAR ALL", new Color(96, 125, 139));
        JButton processBtn = createActionButton("PROCESS PAYMENT", new Color(46, 125, 50));
        
        removeBtn.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                currentCart.remove(selectedRow);
                updateCartTable(totalLabel);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove!");
            }
        });
        
        clearBtn.addActionListener(e -> {
            currentCart.clear();
            updateCartTable(totalLabel);
        });
        
        processBtn.addActionListener(e -> processPayment(totalLabel));
        
        buttonPanel.add(removeBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(processBtn);
        
        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(cartLabel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void addToCart(Product product) {
        for (CartItem item : currentCart) {
            if (item.product.id == product.id) {
                item.quantity++;
                updateCartTable(null);
                return;
            }
        }
        currentCart.add(new CartItem(product, 1));
        updateCartTable(null);
    }
    
    private void updateCartTable(JLabel totalLabel) {
        cartTableModel.setRowCount(0);
        double total = 0;
        
        for (CartItem item : currentCart) {
            double itemTotal = item.product.cost * item.quantity;
            total += itemTotal;
            cartTableModel.addRow(new Object[]{
                item.product.name,
                item.quantity,
                "₱" + String.format("%.2f", item.product.cost),
                "₱" + String.format("%.2f", itemTotal)
            });
        }
        
        if (totalLabel != null) {
            totalLabel.setText("TOTAL: ₱" + String.format("%.2f", total));
        }
    }
    
    private void processPayment(JLabel totalLabel) {
        if (currentCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in transaction!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        final double total = currentCart.stream()
            .mapToDouble(item -> item.product.cost * item.quantity)
            .sum();
        
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel totalAmountLabel = new JLabel("Total Amount: ₱" + String.format("%.2f", total));
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalAmountLabel.setForeground(new Color(92, 51, 23));
        
        JLabel cashLabel = new JLabel("Cash Received:");
        cashLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cashLabel.setForeground(Color.BLACK);
        
        JTextField cashField = new JTextField(15);
        cashField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel changeLabel = new JLabel("Change: ₱0.00");
        changeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        changeLabel.setForeground(new Color(46, 125, 50));
        
        cashField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    double cash = Double.parseDouble(cashField.getText());
                    double change = cash - total;
                    if (change >= 0) {
                        changeLabel.setText("Change: ₱" + String.format("%.2f", change));
                        changeLabel.setForeground(new Color(46, 125, 50));
                    } else {
                        changeLabel.setText("Change: Insufficient!");
                        changeLabel.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    changeLabel.setText("Change: ₱0.00");
                }
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        paymentPanel.add(totalAmountLabel, gbc);
        gbc.gridy = 1; gbc.gridwidth = 1;
        paymentPanel.add(cashLabel, gbc);
        gbc.gridx = 1;
        paymentPanel.add(cashField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        paymentPanel.add(changeLabel, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, paymentPanel, 
            "Payment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double cash = Double.parseDouble(cashField.getText());
                if (cash >= total) {
                    double change = cash - total;
                    
                    boolean allSuccessful = true;
                    for (CartItem item : currentCart) {
                        if (checkAndDeductInventory(item.product.id, item.quantity)) {
                            saveSaleToDatabase(item.product.name, item.quantity, item.product.cost * item.quantity);
                        } else {
                            allSuccessful = false;
                            break;
                        }
                    }
                    
                    if (allSuccessful) {
                        JOptionPane.showMessageDialog(this,
                            "✅ PAYMENT SUCCESSFUL!\n\n" +
                            "Total: ₱" + String.format("%.2f", total) + "\n" +
                            "Cash: ₱" + String.format("%.2f", cash) + "\n" +
                            "Change: ₱" + String.format("%.2f", change) + "\n\n" +
                            "Thank you for your purchase!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        currentCart.clear();
                        updateCartTable(totalLabel);
                        loadProductsFromDatabase();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient cash!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshCartTable() {
        updateCartTable(null);
        Component[] components = ((JPanel)mainPanel.getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JSplitPane) {
                JPanel rightPanel = (JPanel)((JSplitPane)comp).getRightComponent();
                Component[] rightComps = rightPanel.getComponents();
                for (Component rc : rightComps) {
                    if (rc instanceof JPanel) {
                        Component[] bottomComps = ((JPanel)rc).getComponents();
                        for (Component bc : bottomComps) {
                            if (bc instanceof JPanel) {
                                Component[] innerComps = ((JPanel)bc).getComponents();
                                for (Component ic : innerComps) {
                                    if (ic instanceof JLabel && ((JLabel)ic).getText().startsWith("TOTAL:")) {
                                        double total = currentCart.stream()
                                            .mapToDouble(item -> item.product.cost * item.quantity)
                                            .sum();
                                        ((JLabel)ic).setText("TOTAL: ₱" + String.format("%.2f", total));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private JPanel createSalesRecordScreen() {
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    
    JPanel headerPanel = createHeaderPanel("SALES RECORD");
    
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchPanel.setBackground(Color.WHITE);
    searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
    JLabel searchLabel = new JLabel("Search:");
    searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
    searchLabel.setForeground(Color.BLACK);
    
    JTextField searchField = new JTextField(20);
    searchField.setFont(new Font("Arial", Font.PLAIN, 14));
    
    JButton searchBtn = new JButton("SEARCH");
    searchBtn.setBackground(new Color(92, 51, 23));
    searchBtn.setForeground(Color.BLACK);
    searchBtn.setFocusPainted(false);
    
    JButton refreshBtn = new JButton("REFRESH");
    refreshBtn.setBackground(new Color(96, 125, 139));
    refreshBtn.setForeground(Color.BLACK);
    refreshBtn.setFocusPainted(false);
    
    searchPanel.add(searchLabel);
    searchPanel.add(searchField);
    searchPanel.add(searchBtn);
    searchPanel.add(refreshBtn);
    
    String[] columns = {"Order ID", "Product", "Quantity", "Total", "Date & Time"};
    salesTableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable salesTable = new JTable(salesTableModel);
    salesTable.setFont(new Font("Arial", Font.PLAIN, 14));
    salesTable.setRowHeight(35);
    salesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    salesTable.getTableHeader().setBackground(new Color(92, 51, 23));
    salesTable.getTableHeader().setForeground(Color.BLACK);
    
    JScrollPane tableScroll = new JScrollPane(salesTable);
    tableScroll.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 20, 10, 20),
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
    ));
    
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(Color.WHITE);
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    
    JButton backButton = new JButton("BACK TO DASHBOARD");
    backButton.setBackground(new Color(92, 51, 23));
    backButton.setForeground(Color.BLACK);
    backButton.setFont(new Font("Arial", Font.BOLD, 14));
    backButton.setFocusPainted(false);
    backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    backButton.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
    
    JButton updateBtn = new JButton("UPDATE SELECTED RECORD");
    updateBtn.setBackground(new Color(25, 118, 210));
    updateBtn.setForeground(Color.BLACK);
    updateBtn.setFont(new Font("Arial", Font.BOLD, 14));
    updateBtn.setFocusPainted(false);
    updateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    updateBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
    updateBtn.addActionListener(e -> {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) salesTableModel.getValueAt(selectedRow, 0);
            updateSalesRecord(orderId);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a record to update!");
        }
    });
    
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leftPanel.setOpaque(false);
    leftPanel.add(backButton);
    
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.setOpaque(false);
    rightPanel.add(updateBtn);
    
    bottomPanel.add(leftPanel, BorderLayout.WEST);
    bottomPanel.add(rightPanel, BorderLayout.EAST);
    
    searchBtn.addActionListener(e -> {
        String keyword = searchField.getText().trim();
        searchSalesRecords(keyword);
    });
    
    refreshBtn.addActionListener(e -> refreshSalesTable());
    
    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(searchPanel, BorderLayout.NORTH);
    panel.add(tableScroll, BorderLayout.CENTER);
    panel.add(bottomPanel, BorderLayout.SOUTH);
    
    return panel;
}
    
    private void refreshSalesTable() {
        ensureConnection();
        salesTableModel.setRowCount(0);
        String query = "SELECT order_number, product_name, quantity, total_price, date_purchased " +
                       "FROM sales_record ORDER BY date_purchased DESC LIMIT 200";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                salesTableModel.addRow(new Object[]{
                    rs.getInt("order_number"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    "₱" + String.format("%.2f", rs.getDouble("total_price")),
                    sdf.format(rs.getTimestamp("date_purchased"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading sales: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void searchSalesRecords(String keyword) {
        salesTableModel.setRowCount(0);
        String query = "SELECT order_number, product_name, quantity, total_price, date_purchased " +
                       "FROM sales_record WHERE product_name LIKE ? ORDER BY date_purchased DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                salesTableModel.addRow(new Object[]{
                    rs.getInt("order_number"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    "₱" + String.format("%.2f", rs.getDouble("total_price")),
                    sdf.format(rs.getTimestamp("date_purchased"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching sales: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateSalesRecord(int orderId) {
        String selectQuery = "SELECT product_name, quantity, total_price FROM sales_record WHERE order_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String currentProduct = rs.getString("product_name");
                int currentQty = rs.getInt("quantity");
                double currentTotal = rs.getDouble("total_price");
                
                JPanel updatePanel = new JPanel(new GridBagLayout());
                updatePanel.setBackground(Color.WHITE);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                
                JLabel productLabel = new JLabel("Product:");
                productLabel.setFont(new Font("Arial", Font.BOLD, 14));
                productLabel.setForeground(Color.BLACK);
                
                JTextField productField = new JTextField(currentProduct, 20);
                productField.setFont(new Font("Arial", Font.PLAIN, 14));
                
                JLabel qtyLabel = new JLabel("Quantity:");
                qtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
                qtyLabel.setForeground(Color.BLACK);
                
                JTextField qtyField = new JTextField(String.valueOf(currentQty), 10);
                qtyField.setFont(new Font("Arial", Font.PLAIN, 14));
                
                JLabel priceLabel = new JLabel("Price:");
                priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
                priceLabel.setForeground(Color.BLACK);
                
                JTextField priceField = new JTextField(String.valueOf(currentTotal), 15);
                priceField.setFont(new Font("Arial", Font.PLAIN, 14));
                
                gbc.gridx = 0; gbc.gridy = 0;
                updatePanel.add(productLabel, gbc);
                gbc.gridx = 1;
                updatePanel.add(productField, gbc);
                gbc.gridx = 0; gbc.gridy = 1;
                updatePanel.add(qtyLabel, gbc);
                gbc.gridx = 1;
                updatePanel.add(qtyField, gbc);
                gbc.gridx = 0; gbc.gridy = 2;
                updatePanel.add(priceLabel, gbc);
                gbc.gridx = 1;
                updatePanel.add(priceField, gbc);
                
                int result = JOptionPane.showConfirmDialog(this, updatePanel, 
                    "Update Sales Record #" + orderId, 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
                if (result == JOptionPane.OK_OPTION) {
                    String newProduct = productField.getText().trim();
                    int newQty = Integer.parseInt(qtyField.getText());
                    double newPrice = Double.parseDouble(priceField.getText());
                    
                    String updateQuery = "UPDATE sales_record SET product_name = ?, quantity = ?, total_price = ? WHERE order_number = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newProduct);
                        updateStmt.setInt(2, newQty);
                        updateStmt.setDouble(3, newPrice);
                        updateStmt.setInt(4, orderId);
                        updateStmt.executeUpdate();
                        
                        JOptionPane.showMessageDialog(this, "Sales record updated successfully!");
                        refreshSalesTable();
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating record: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel createInventoryViewScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel headerPanel = createHeaderPanel("INVENTORY MANAGEMENT");
        
        JButton backButton = new JButton("← BACK TO DASHBOARD");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(92, 51, 23));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createLineBorder(new Color(92, 51, 23), 2));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
        
        JPanel headerSouth = new JPanel(new BorderLayout());
        headerSouth.setOpaque(false);
        headerSouth.add(backButton, BorderLayout.EAST);
        headerPanel.add(headerSouth, BorderLayout.SOUTH);
        
        String[] columns = {"Item ID", "Item Name", "Current Stock", "Min Stock", "Unit", "Status", "Supplier"};
        inventoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setRowHeight(35);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inventoryTable.getTableHeader().setBackground(new Color(92, 51, 23));
        inventoryTable.getTableHeader().setForeground(Color.BLACK);
        
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    try {
                        int currentStock = Integer.parseInt(table.getValueAt(row, 2).toString());
                        int minStock = Integer.parseInt(table.getValueAt(row, 3).toString());
                        
                        if (currentStock <= 0) {
                            c.setBackground(new Color(255, 235, 238));
                            c.setForeground(new Color(198, 40, 40));
                        } else if (currentStock <= minStock) {
                            c.setBackground(new Color(255, 243, 224));
                            c.setForeground(new Color(245, 124, 0));
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    } catch (Exception ex) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(inventoryTable);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 20, 10, 20),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBackground(Color.WHITE);
        refreshPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JButton refreshInvBtn = new JButton("REFRESH INVENTORY");
        refreshInvBtn.setBackground(new Color(92, 51, 23));
        refreshInvBtn.setForeground(Color.BLACK);
        refreshInvBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshInvBtn.setFocusPainted(false);
        refreshInvBtn.addActionListener(e -> refreshInventoryTable());
        
        refreshPanel.add(refreshInvBtn);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(refreshPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void ensureConnection() {
        try {
            if (conn == null || conn.isClosed() || !conn.isValid(2)) {
                conn = DBConnection.getConnection();
            }
        } catch (SQLException e) {
            conn = DBConnection.getConnection();
        }
    }
    
    private void refreshInventoryTable() {
        ensureConnection();
        inventoryTableModel.setRowCount(0);
        
        String query = "SELECT s.item_id, s.item_name, s.total_unit_count, s.minimum_unit_count, " +
                       "u.unit_type, sup.supplier_name " +
                       "FROM product_stocks s " +
                       "LEFT JOIN product_unit u ON s.unit_type = u.unit_id " +
                       "LEFT JOIN supplier sup ON s.supplier_id = sup.supplier_id " +
                       "ORDER BY s.item_id";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int currentStock = rs.getInt("total_unit_count");
                int minStock = rs.getInt("minimum_unit_count");
                String status;
                
                if (currentStock <= 0) {
                    status = "⚠️ OUT OF STOCK";
                } else if (currentStock <= minStock) {
                    status = "⚠️ LOW STOCK";
                } else {
                    status = "✅ IN STOCK";
                }
                
                inventoryTableModel.addRow(new Object[]{
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    currentStock,
                    minStock,
                    rs.getString("unit_type") != null ? rs.getString("unit_type") : "pcs",
                    status,
                    rs.getString("supplier_name") != null ? rs.getString("supplier_name") : "N/A"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading inventory: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // ===== DATA CLASSES =====
    class Product {
        int id;
        String name;
        String type;
        double cost;
        
        Product(int id, String name, String type, double cost) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.cost = cost;
        }
    }
    
    class CartItem {
        Product product;
        int quantity;
        
        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}

