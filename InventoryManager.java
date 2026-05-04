package coffeeshop;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import coffeeshop.DBConnection;

public class InventoryManager extends JFrame {

    // COFFEE BROWN AND WHITE COLOR SCHEME - ALL FONTS BLACK
    static final Color BG       = new Color(255, 255, 255); // White
    static final Color SURFACE  = new Color(245, 245, 245); // Light gray-white
    static final Color CARD     = new Color(255, 255, 255); // White
    static final Color BORDER   = new Color(139, 69, 19); // Coffee brown border
    static final Color ACCENT   = new Color(139, 69, 19); // Coffee brown accent
    static final Color TEXT     = Color.BLACK; // Black text
    static final Color MUTED    = new Color(80, 80, 80); // Dark gray
    static final Color COFFEE_BROWN = new Color(139, 69, 19); // Coffee brown
    static final Color LIGHT_COFFEE = new Color(160, 82, 45); // Lighter coffee brown

    // Database connection
    private Connection conn;

    // Constructor
    public InventoryManager() {
        setTitle("Inventory Manager System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setBackground(BG);

        // Initialize database connection
        conn = DBConnection.getConnection();

        // Check if connection was successful
        if (conn == null) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to database!\nPlease check your database settings.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        try {
            UIManager.put("Panel.background", BG);
            UIManager.put("OptionPane.background", CARD);
            UIManager.put("OptionPane.messageForeground", TEXT);
        } catch (Exception e) {}

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel root = new JPanel();
        root.setBackground(BG);
        root.setLayout(new BorderLayout());

        // TOPBAR - Coffee brown with BLACK text
        JPanel topbar = new JPanel();
        topbar.setBackground(COFFEE_BROWN);
        topbar.setPreferredSize(new Dimension(0, 48));
        topbar.setBorder(new MatteBorder(0, 0, 1, 0, COFFEE_BROWN));
        topbar.setLayout(new BorderLayout());

        JLabel brand = new JLabel("  Inventory Manager");
        brand.setFont(timesNewRomanFont(16).deriveFont(Font.BOLD));
        brand.setForeground(Color.BLACK);
        topbar.add(brand, BorderLayout.WEST);

        JPanel topRight = new JPanel();
        topRight.setBackground(COFFEE_BROWN);
        topRight.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBackground(COFFEE_BROWN);
        logoutBtn.setForeground(Color.BLACK);
        logoutBtn.setFont(new Font("Times New Roman", Font.BOLD, 11));
        logoutBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setOpaque(true);
        logoutBtn.setContentAreaFilled(true);
        logoutBtn.setPreferredSize(new Dimension(70, 25));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new CIMSLogin();
            }
        });
        topRight.add(logoutBtn);
        topbar.add(topRight, BorderLayout.EAST);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, LIGHT_COFFEE));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(Box.createVerticalStrut(12));

        String[][] navItems = {
                {"MAIN", null},
                {"Dashboard", "Dashboard"},
                {"TABLES", null},
                {"Inventory", "Inventory"},
                {"Product Menu", "Product Menu"},
                {"Recipe Table", "Recipe Table"},
                {"REPORTS", null},
                {"Print / Export", "Print / Export"},
        };

        ArrayList<JButton> navBtns = new ArrayList<>();
        CardLayout cardLayout = new CardLayout();
        JPanel contentArea = new JPanel(cardLayout);
        contentArea.setBackground(BG);

        for (String[] item : navItems) {
            if (item[1] == null) {
                sidebar.add(Box.createVerticalStrut(10));
                JLabel grp = monoLabel(item[0], 9, COFFEE_BROWN);
                grp.setBorder(new EmptyBorder(0, 14, 0, 0));
                grp.setAlignmentX(Component.LEFT_ALIGNMENT);
                sidebar.add(grp);
                sidebar.add(Box.createVerticalStrut(4));
            } else {
                JButton btn = createNavButton(item[1], item[1]);
                navBtns.add(btn);
                sidebar.add(btn);
                sidebar.add(Box.createVerticalStrut(2));
            }
        }

        // Create panels
        DashboardPanel dashboardPanel = new DashboardPanel();
        InventoryPanel inventoryPanel = new InventoryPanel();
        ProductPanel productPanel = new ProductPanel();
        RecipePanel recipePanel = new RecipePanel();
        PrintPanel printPanel = new PrintPanel();

        contentArea.add(dashboardPanel, "Dashboard");
        contentArea.add(inventoryPanel, "Inventory");
        contentArea.add(productPanel, "Product Menu");
        contentArea.add(recipePanel, "Recipe Table");
        contentArea.add(printPanel, "Print / Export");

        // Nav actions
        for (JButton btn : navBtns) {
            btn.addActionListener(e -> {
                conn = DBConnection.getConnection();
                String page = btn.getActionCommand();
                cardLayout.show(contentArea, page);
                for (JButton b : navBtns) {
                    b.setBackground(BG);
                    b.setForeground(Color.BLACK);
                }
                btn.setBackground(LIGHT_COFFEE);
                btn.setForeground(Color.BLACK);

                // Refresh the panel when switched
                if (page.equals("Dashboard")) dashboardPanel.refresh();
                else if (page.equals("Inventory")) inventoryPanel.refresh();
                else if (page.equals("Product Menu")) productPanel.refresh();
                else if (page.equals("Recipe Table")) recipePanel.refresh();
            });
        }

        // Activate dashboard
        if (!navBtns.isEmpty()) {
            navBtns.get(0).setBackground(LIGHT_COFFEE);
            navBtns.get(0).setForeground(Color.BLACK);
        }

        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BorderLayout());
        body.add(sidebar, BorderLayout.WEST);
        body.add(contentArea, BorderLayout.CENTER);

        root.add(topbar, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JButton createNavButton(String text, String cmd) {
        JButton b = new JButton(text);
        b.setActionCommand(cmd);
        b.setBackground(BG);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setMinimumSize(new Dimension(180, 40));
        b.setPreferredSize(new Dimension(180, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_COFFEE));
        return b;
    }

    // DASHBOARD PANEL
    class DashboardPanel extends JPanel {
        JLabel totalVal, prodVal, lowVal, outVal;
        DefaultTableModel tableModel;

        DashboardPanel() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(24, 28, 24, 28));

            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

            inner.add(sectionHeader("Dashboard", "OVERVIEW - ALL TABLES"));
            inner.add(Box.createVerticalStrut(16));

            // Stat cards
            JPanel stats = new JPanel();
            stats.setBackground(BG);
            stats.setLayout(new GridLayout(1, 4, 10, 0));
            stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            totalVal = new JLabel("0");
            prodVal = new JLabel("0");
            lowVal = new JLabel("0");
            outVal = new JLabel("0");
            stats.add(statCard("Total Items", totalVal, COFFEE_BROWN));
            stats.add(statCard("Products", prodVal, COFFEE_BROWN));
            stats.add(statCard("Low Stock", lowVal, COFFEE_BROWN));
            stats.add(statCard("Out of Stock", outVal, COFFEE_BROWN));
            inner.add(stats);
            inner.add(Box.createVerticalStrut(16));

            // Table
            String[] cols = {"Item Name", "Category", "Qty", "Unit", "Status"};
            tableModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable table = styledTable(tableModel);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setResizingAllowed(false);
            JScrollPane scroll = styledScroll(table);
            inner.add(tableWrap("Recent Inventory", scroll));

            add(inner, BorderLayout.CENTER);
            refresh();
        }

        void refresh() {
            loadDashboardStats();
        }

        private void loadDashboardStats() {
            try {
                // Get total inventory items
                String query1 = "SELECT COUNT(*) as total FROM product_stocks";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query1);
                if (rs.next()) totalVal.setText(String.valueOf(rs.getInt("total")));

                // Get total products
                String query2 = "SELECT COUNT(*) as total FROM product_menu";
                rs = stmt.executeQuery(query2);
                if (rs.next()) prodVal.setText(String.valueOf(rs.getInt("total")));

                // Get low stock and out of stock counts
                String query3 = "SELECT SUM(CASE WHEN total_unit_count <= minimum_unit_count AND total_unit_count > 0 THEN 1 ELSE 0 END) as low, " +
                        "SUM(CASE WHEN total_unit_count <= 0 THEN 1 ELSE 0 END) as out_stock FROM product_stocks";
                rs = stmt.executeQuery(query3);
                if (rs.next()) {
                    lowVal.setText(String.valueOf(rs.getInt("low")));
                    outVal.setText(String.valueOf(rs.getInt("out_stock")));
                }

                // Load inventory table
                String query4 = "SELECT s.item_name, sup.supply_type, s.total_unit_count, u.unit_type " +
                        "FROM product_stocks s " +
                        "LEFT JOIN supplier sup ON s.supplier_id = sup.supplier_id " +
                        "LEFT JOIN product_unit u ON s.unit_type = u.unit_id";
                rs = stmt.executeQuery(query4);
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int qty = rs.getInt("total_unit_count");
                    String status = qty <= 0 ? "Out of Stock" : qty <= 5 ? "Low Stock" : "In Stock";
                    tableModel.addRow(new Object[]{
                            rs.getString("item_name"),
                            rs.getString("supply_type") != null ? rs.getString("supply_type") : "N/A",
                            qty,
                            rs.getString("unit_type") != null ? rs.getString("unit_type") : "pcs",
                            status
                    });
                }
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // INVENTORY PANEL
    class InventoryPanel extends JPanel {
        DefaultTableModel model;
        JTable table;

        InventoryPanel() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(24, 28, 24, 28));

            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BorderLayout(0, 14));

            JButton addBtn = accentButton("+ Add Item");
            inner.add(sectionHeaderWithBtn("Inventory", "STOCKS - RAW MATERIALS", addBtn), BorderLayout.NORTH);

            String[] cols = {"ID", "Item Name", "Category", "Qty", "Unit", "Min Stock", "Status"};
            model = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = styledTable(model);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setResizingAllowed(false);
            inner.add(tableWrap("All Stocks", styledScroll(table)), BorderLayout.CENTER);

            // Toolbar
            JPanel toolbar = new JPanel();
            toolbar.setBackground(BG);
            toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton editBtn = ghostButton("EDIT SELECTED");
            JButton delBtn = dangerButton("DELETE SELECTED");
            toolbar.add(editBtn);
            toolbar.add(delBtn);
            inner.add(toolbar, BorderLayout.SOUTH);

            add(inner, BorderLayout.CENTER);
            refresh();

            addBtn.addActionListener(e -> showAddItemDialog());
            editBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Select a row first.");
                    return;
                }
                showEditItemDialog(row);
            });
            delBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Select a row first.");
                    return;
                }
                deleteItem(row);
            });
        }

        void refresh() {
            loadInventoryFromDatabase();
        }

        private void loadInventoryFromDatabase() {
            try {
                String query = "SELECT s.item_id, s.item_name, sup.supply_type, s.total_unit_count, " +
                        "u.unit_type, s.minimum_unit_count FROM product_stocks s " +
                        "LEFT JOIN supplier sup ON s.supplier_id = sup.supplier_id " +
                        "LEFT JOIN product_unit u ON s.unit_type = u.unit_id";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                model.setRowCount(0);
                while (rs.next()) {
                    int qty = rs.getInt("total_unit_count");
                    int minStock = rs.getInt("minimum_unit_count");
                    String status = qty <= 0 ? "Out of Stock" : qty <= minStock ? "Low Stock" : "In Stock";
                    model.addRow(new Object[]{
                            rs.getInt("item_id"),
                            rs.getString("item_name"),
                            rs.getString("supply_type") != null ? rs.getString("supply_type") : "N/A",
                            qty,
                            rs.getString("unit_type") != null ? rs.getString("unit_type") : "pcs",
                            minStock,
                            status
                    });
                }
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void showAddItemDialog() {
            JTextField name = new JTextField();
            JTextField category = new JTextField();
            JTextField qty = new JTextField();
            JComboBox<String> unit = new JComboBox<>(new String[]{"kg", "L", "pcs", "g", "ml"});
            JTextField minStock = new JTextField();

            JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
            form.add(new JLabel("Item Name:"));
            form.add(name);
            form.add(new JLabel("Category:"));
            form.add(category);
            form.add(new JLabel("Quantity:"));
            form.add(qty);
            form.add(new JLabel("Unit:"));
            form.add(unit);
            form.add(new JLabel("Min Stock Alert:"));
            form.add(minStock);

            int result = JOptionPane.showConfirmDialog(this, form, "Add Inventory Item",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String insertQuery = "INSERT INTO product_stocks (item_name, total_unit_count, minimum_unit_count) VALUES (?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                    pstmt.setString(1, name.getText());
                    pstmt.setInt(2, Integer.parseInt(qty.getText()));
                    pstmt.setInt(3, Integer.parseInt(minStock.getText()));
                    pstmt.executeUpdate();
                    refresh();
                } catch (SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage());
                }
            }
        }

        private void showEditItemDialog(int row) {
            int id = (int) model.getValueAt(row, 0);
            try {
                String query = "SELECT * FROM product_stocks WHERE item_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    JTextField qty = new JTextField(String.valueOf(rs.getInt("total_unit_count")));
                    JTextField minStock = new JTextField(String.valueOf(rs.getInt("minimum_unit_count")));

                    JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
                    form.add(new JLabel("Quantity:"));
                    form.add(qty);
                    form.add(new JLabel("Min Stock Alert:"));
                    form.add(minStock);

                    int result = JOptionPane.showConfirmDialog(this, form, "Edit Item",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String updateQuery = "UPDATE product_stocks SET total_unit_count = ?, minimum_unit_count = ? WHERE item_id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setInt(1, Integer.parseInt(qty.getText()));
                        updateStmt.setInt(2, Integer.parseInt(minStock.getText()));
                        updateStmt.setInt(3, id);
                        updateStmt.executeUpdate();
                        refresh();
                    }
                }
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage());
            }
        }

        private void deleteItem(int row) {
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this item?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM product_stocks WHERE item_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                    refresh();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage());
                }
            }
        }
    }

    // PRODUCT PANEL
    class ProductPanel extends JPanel {
        DefaultTableModel model;
        JTable table;

        ProductPanel() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(24, 28, 24, 28));

            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BorderLayout(0, 14));

            JButton addBtn = accentButton("+ Add Product");
            inner.add(sectionHeaderWithBtn("Product Menu", "MENU ITEMS - PRICES", addBtn), BorderLayout.NORTH);

            String[] cols = {"ID", "Product Name", "Category", "Price (P)", "Availability"};
            model = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = styledTable(model);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setResizingAllowed(false);
            inner.add(tableWrap("All Products", styledScroll(table)), BorderLayout.CENTER);

            JPanel toolbar = new JPanel();
            toolbar.setBackground(BG);
            toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton toggleBtn = ghostButton("TOGGLE AVAILABILITY");
            toolbar.add(toggleBtn);
            inner.add(toolbar, BorderLayout.SOUTH);

            add(inner, BorderLayout.CENTER);
            refresh();

            addBtn.addActionListener(e -> showAddProductDialog());
            toggleBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Select a row first.");
                    return;
                }
                toggleAvailability(row);
            });
        }

        void refresh() {
            loadProductsFromDatabase();
        }

        private void loadProductsFromDatabase() {
            try {
                String query = "SELECT p.product_id, p.product_name, c.category_name, p.price " +
                        "FROM product_menu p LEFT JOIN product_categories c ON p.category_id = c.category_id";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("category_name") != null ? rs.getString("category_name") : "N/A",
                            rs.getDouble("price"),
                            "Available"
                    });
                }
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void showAddProductDialog() {
            JTextField name = new JTextField();
            JTextField category = new JTextField();
            JTextField price = new JTextField();

            JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
            form.add(new JLabel("Product Name:"));
            form.add(name);
            form.add(new JLabel("Category:"));
            form.add(category);
            form.add(new JLabel("Price:"));
            form.add(price);

            int result = JOptionPane.showConfirmDialog(this, form, "Add Product",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String insertQuery = "INSERT INTO product_menu (product_name, price) VALUES (?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                    pstmt.setString(1, name.getText());
                    pstmt.setDouble(2, Double.parseDouble(price.getText()));
                    pstmt.executeUpdate();
                    refresh();
                } catch (SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
                }
            }
        }

        private void toggleAvailability(int row) {
            JOptionPane.showMessageDialog(this, "Toggle availability feature - Add 'available' column to database");
        }
    }

    // RECIPE PANEL
    class RecipePanel extends JPanel {
        DefaultTableModel model;

        RecipePanel() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(24, 28, 24, 28));

            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BorderLayout(0, 14));

            JButton addBtn = accentButton("+ Add Recipe");
            inner.add(sectionHeaderWithBtn("Recipe Table", "INGREDIENTS PER PRODUCT", addBtn), BorderLayout.NORTH);

            String[] cols = {"Product", "Ingredient", "Qty Required", "Unit"};
            model = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable table = styledTable(model);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setResizingAllowed(false);
            inner.add(tableWrap("All Recipes", styledScroll(table)), BorderLayout.CENTER);

            add(inner, BorderLayout.CENTER);
            refresh();

            addBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Add Recipe - Connect to product_recipes table");
            });
        }

        void refresh() {
            loadRecipesFromDatabase();
        }

        private void loadRecipesFromDatabase() {
            try {
                String query = "SELECT p.product_name, s.item_name, r.amount_needed, u.unit_type " +
                        "FROM product_recipes r " +
                        "JOIN product_menu p ON r.product_id = p.product_id " +
                        "JOIN product_stocks s ON r.ingredients_need = s.item_id " +
                        "LEFT JOIN product_unit u ON s.unit_type = u.unit_id";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("product_name"),
                            rs.getString("item_name"),
                            rs.getDouble("amount_needed"),
                            rs.getString("unit_type") != null ? rs.getString("unit_type") : "pcs"
                    });
                }
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // PRINT PANEL
    class PrintPanel extends JPanel {
        DefaultTableModel previewModel;
        JLabel previewTitle;

        PrintPanel() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(24, 28, 24, 28));

            JPanel inner = new JPanel();
            inner.setBackground(BG);
            inner.setLayout(new BorderLayout(0, 14));
            inner.add(sectionHeader("Print / Export", "REPORTS - TABLES"), BorderLayout.NORTH);

            JPanel cards = new JPanel();
            cards.setBackground(BG);
            cards.setLayout(new GridLayout(1, 2, 12, 0));
            cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            JButton stocksCard = printCard("Print Stocks Table", "Export current inventory stock levels");
            cards.add(stocksCard);

            previewTitle = monoLabel("SELECT A REPORT ABOVE TO PREVIEW", 10, MUTED);
            previewModel = new DefaultTableModel(new String[]{" "}, 0){
                public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable previewTable = styledTable(previewModel);
            previewTable.getTableHeader().setReorderingAllowed(false);
            previewTable.getTableHeader().setResizingAllowed(false);
            JPanel centerArea = new JPanel();
            centerArea.setBackground(BG);
            centerArea.setLayout(new BorderLayout(0, 10));
            centerArea.add(cards, BorderLayout.NORTH);
            centerArea.add(tableWrap("Preview", styledScroll(previewTable)), BorderLayout.CENTER);

            inner.add(centerArea, BorderLayout.CENTER);
            add(inner, BorderLayout.CENTER);

            stocksCard.addActionListener(e -> {
                previewTitle.setText("STOCKS TABLE - Preview");
                previewModel.setColumnIdentifiers(new String[]{"Item Name", "Category", "Qty", "Unit", "Status"});
                previewModel.setRowCount(0);
                try {
                    String query = "SELECT s.item_name, sup.supply_type, s.total_unit_count, u.unit_type " +
                            "FROM product_stocks s " +
                            "LEFT JOIN supplier sup ON s.supplier_id = sup.supplier_id " +
                            "LEFT JOIN product_unit u ON s.unit_type = u.unit_id";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        int qty = rs.getInt("total_unit_count");
                        String status = qty <= 0 ? "Out of Stock" : qty <= 5 ? "Low Stock" : "In Stock";
                        previewModel.addRow(new Object[]{
                                rs.getString("item_name"),
                                rs.getString("supply_type") != null ? rs.getString("supply_type") : "N/A",
                                qty,
                                rs.getString("unit_type") != null ? rs.getString("unit_type") : "pcs",
                                status
                        });
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    // UI HELPER METHODS
    Font timesNewRomanFont(int size) {
        return new Font("Times New Roman", Font.PLAIN, size);
    }

    JLabel monoLabel(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(timesNewRomanFont(size));
        l.setForeground(color);
        return l;
    }

    JPanel sectionHeader(String title, String sub) {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Times New Roman", Font.BOLD, 20));
        t.setForeground(Color.BLACK);
        JLabel s = monoLabel(sub, 10, MUTED);
        p.add(t);
        p.add(s);
        p.setBorder(new MatteBorder(0, 0, 1, 0, COFFEE_BROWN));
        return p;
    }

    JPanel sectionHeaderWithBtn(String title, String sub, JButton btn) {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setLayout(new BorderLayout());
        p.setBorder(new MatteBorder(0, 0, 1, 0, COFFEE_BROWN));
        JPanel left = new JPanel();
        left.setBackground(BG);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Times New Roman", Font.BOLD, 20));
        t.setForeground(Color.BLACK);
        JLabel s = monoLabel(sub, 10, MUTED);
        left.add(t);
        left.add(s);
        p.add(left, BorderLayout.WEST);
        p.add(btn, BorderLayout.EAST);
        return p;
    }

    JPanel tableWrap(String title, JScrollPane scroll) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(CARD);
        wrap.setBorder(new LineBorder(COFFEE_BROWN, 1));
        JPanel header = new JPanel();
        header.setBackground(COFFEE_BROWN);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, COFFEE_BROWN), new EmptyBorder(8, 14, 8, 14)
        ));
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel lbl = monoLabel(title.toUpperCase(), 10, Color.BLACK);
        header.add(lbl);
        wrap.add(header, BorderLayout.NORTH);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    JPanel statCard(String label, JLabel valLabel, Color color) {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, color),
                new EmptyBorder(12, 16, 12, 16)
        ));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = monoLabel(label.toUpperCase(), 9, COFFEE_BROWN);
        valLabel.setFont(new Font("Times New Roman", Font.BOLD, 26));
        valLabel.setForeground(Color.BLACK);
        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(valLabel);
        return p;
    }

    JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(CARD);
        table.setForeground(Color.BLACK);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setGridColor(COFFEE_BROWN);
        table.setShowGrid(true);
        table.setSelectionBackground(LIGHT_COFFEE);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(COFFEE_BROWN);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 10));
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, COFFEE_BROWN));
        return table;
    }

    JScrollPane styledScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(CARD);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    JButton accentButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(COFFEE_BROWN);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Times New Roman", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        return b;
    }

    JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(SURFACE);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COFFEE_BROWN, 1), new EmptyBorder(5, 12, 5, 12)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        return b;
    }

    JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(198, 40, 40));
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(198, 40, 40), 1), new EmptyBorder(5, 12, 5, 12)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        return b;
    }

    JButton printCard(String title, String desc) {
        JButton b = new JButton("<html><center><b>" + title + "</b><br><font size='2' color='#505050'>" + desc + "</font></center></html>");
        b.setBackground(CARD);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Times New Roman", Font.PLAIN, 13));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COFFEE_BROWN, 1), new EmptyBorder(14, 18, 14, 18)
        ));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        return b;
    }
}
