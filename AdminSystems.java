package coffeeshop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import coffeeshop.InventoryPanel;
import coffeeshop.UsersPanel;

public class AdminSystems extends JFrame {
    private Connection conn;
    JLabel lblTotal, lblLow, lblOut;
    JLabel totalVal, prodVal, lowVal, outVal;
    JPanel inventoryTab, usersTab, salesTab;

    ResultSet rs;
    public AdminSystems() {
        conn = DBConnection.getConnection();
        setTitle("Admin System Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Setup Enhanced Top Panel
        setupTopPanel();

        JTabbedPane tabs = new JTabbedPane();
        // Initialize Panels
        inventoryTab = new InventoryPanel(conn);
        usersTab = new UsersPanel(conn);
        salesTab = new SalesPanel(conn);

        tabs.addTab("Inventory", inventoryTab);
        tabs.addTab("Users", usersTab);
        tabs.addTab("Sales", salesTab);

        add(tabs, BorderLayout.CENTER);
        // refreshAllStats(); // Requires DB
        setVisible(true);
    }

    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding

        // 1. Title on the Left
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // 2. Center Stats with Color
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        prodVal = new JLabel("0");
        lowVal = new JLabel("0");
        outVal = new JLabel("0");

        lblTotal = new JLabel("Total Products: ");
        lblLow = new JLabel("Low Stock: ");
        lblOut = new JLabel("Out of Stock: ");

        Font statsFont = new Font("Arial", Font.BOLD, 14);
        lblTotal.setFont(statsFont);
        prodVal.setFont(statsFont);

        lblLow.setFont(statsFont);
        lowVal.setFont(statsFont);

        lblOut.setFont(statsFont);
        outVal.setFont(statsFont);

        statsPanel.add(lblTotal);
        statsPanel.add(prodVal);
        statsPanel.add(lblLow);
        statsPanel.add(lowVal);
        statsPanel.add(lblOut);
        statsPanel.add(outVal);

        refresh();

        topPanel.add(statsPanel, BorderLayout.CENTER);
        JButton btnNotif = new JButton("Alerts");
        btnNotif.addActionListener(e -> lowStocks());

        JButton btnLogout = new JButton("Log Out");
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new CIMSLogin();
            }
        });
        topPanel.add(btnNotif,BorderLayout.EAST);
        topPanel.add(btnLogout, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }
    void refresh() {
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        try {
            Statement stmt = conn.createStatement();
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
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void lowStocks() {
        // Query to find items where current units are below the threshold
        String query = "SELECT item_name, total_unit_count FROM product_stocks WHERE total_unit_count < minimum_unit_count";
        StringBuilder lowStockList = new StringBuilder();

        try {
            Connection conn = DBConnection.getConnection();
            Statement sttmnt = conn.createStatement();
            ResultSet result = sttmnt.executeQuery(query);

            // Iterate through the results and append them to the notification list
            while (result.next()) {
                String name = result.getString("item_name");
                int count = result.getInt("total_unit_count");
                lowStockList.append("- ").append(name).append(" (Current: ").append(count).append(")\n");
            }

            // Only show the alert if the list is not empty
            if (lowStockList.length() > 0) {
                JOptionPane.showMessageDialog(this,
                        "The following items are low on stock:\n\n" + lowStockList.toString(),
                        "Low Inventory Alert",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            // Log the error if the database connection fails
            JOptionPane.showMessageDialog(this, "Error checking stock levels: " + e.getMessage());
        }
    }
}

