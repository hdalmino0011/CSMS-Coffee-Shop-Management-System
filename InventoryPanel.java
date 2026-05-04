package coffeeshop;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class InventoryPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private Connection conn;

    public InventoryPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        String[] cols = {"ID", "Item Name", "Category", "Qty", "Unit", "Min Stock", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        // Lock Columns
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    public void loadData() {
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
                int min = rs.getInt("minimum_unit_count");
                String status = qty <= 0 ? "Out of Stock" : qty <= min ? "Low Stock" : "In Stock";

                model.addRow(new Object[]{
                        rs.getInt("item_id"), rs.getString("item_name"),
                        rs.getString("supply_type"), qty, rs.getString("unit_type"),
                        min, status
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
