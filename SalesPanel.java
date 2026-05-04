package coffeeshop;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.Vector;

public class SalesPanel extends JPanel implements ActionListener {
    private DefaultTableModel salesModel;
    private JTable table;
    private Connection conn;
    JTextField date_input;
    JButton select_date_bttn;
    JButton current_date_bttn;

    public SalesPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        // Note: I updated these columns to be more "Sales" specific
        String[] cols = {"Order #", "Product Name", "Quantity", "Total Price", "Date Purchased","User ID #"};

        salesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(salesModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setupButtons();
        refreshSalesData();
    }

    private void setupButtons() {
        JPanel btnPanel = new JPanel();
        date_input = new JTextField(10);
        select_date_bttn = new JButton("SELECT DATE");
        current_date_bttn = new JButton("TODAY'S SALES");

        date_input.addActionListener(this);
        select_date_bttn.addActionListener(this);
        current_date_bttn.addActionListener(this);
        btnPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        btnPanel.add(date_input);
        btnPanel.add(select_date_bttn);
        btnPanel.add(current_date_bttn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshSalesData() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM sales_record")) { // Change table name as needed
            salesModel.setRowCount(0);
            while (rs.next()) {
                salesModel.addRow(new Object[]{
                        rs.getString("order_number"),
                        rs.getString("product_name"),
                        rs.getDouble("quantity"),
                        rs.getString("total_price"),
                        rs.getString("date_purchased"),
                        rs.getInt("user_id")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSaleFromDB(String saleId) {
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM sales_record WHERE order_number = ?")) {
            pstmt.setString(1, saleId);
            pstmt.executeUpdate();
            refreshSalesData();
            JOptionPane.showMessageDialog(this, "Record deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // METHOD FOR SYNC SALES DATA, KUNG DILI NI NINYO NEED SKIP LANG GURO


    // Function to refresh the table UI
    public void syncSalesData(String date) {
        DefaultTableModel freshData = Sales.getSalesRecord(date);
        String[] headers = {"Order #", "Product Name", "Quantity", "Total Price", "Date Purchased"};
        Vector<String> columnNames = new Vector<>(Arrays.asList(headers));

        // Update the model with fresh data from the database
        salesModel.setDataVector(freshData.getDataVector(), columnNames);
        table.setRowHeight(28);
        table.revalidate();
        table.repaint();
    }




//ANG KANI NGA METHOD KAY SA AKONG SALES CLASS NI DILI SA SALESUI, PARA MAKUHA IYANG TABLE DATA NGA NAG BASE SA DATE

    public static DefaultTableModel getSalesRecord(String date) {
        DefaultTableModel table = new DefaultTableModel();
        boolean isDateProvided = (date != null && !date.isEmpty());

        // Switch between specific date or today's date
        String query = !isDateProvided ?
                "SELECT order_number, product_name, quantity, total_price, date_purchased FROM sales_record WHERE date(date_purchased) = CURDATE()" :
                "SELECT order_number, product_name, quantity, total_price, date_purchased FROM sales_record WHERE date(date_purchased) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement sttmnt = conn.prepareStatement(query)) {

            if (isDateProvided) {
                sttmnt.setString(1, date);
            }

            try (ResultSet result = sttmnt.executeQuery()) {
                ResultSetMetaData data = result.getMetaData();

                // Setup Columns
                for(int i = 1; i <= data.getColumnCount(); i++){
                    table.addColumn(data.getColumnName(i));
                }

                // Fill Rows
                while(result.next()){
                    Object[] row = new Object[data.getColumnCount()];
                    for(int i = 1; i <= data.getColumnCount(); i++){
                        row[i-1] = result.getObject(i);
                    }
                    table.addRow(row);
                }
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error getting sales data: " + e.getMessage());
        }
        return table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Inside actionPerformed:
        if (e.getSource() == select_date_bttn) {
            String inputDate = date_input.getText().trim();
            if(!inputDate.isEmpty()) {
                syncSalesData(inputDate); // Fetches data for specific date
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a date first!");
            }
        } else if (e.getSource() == current_date_bttn) {
            syncSalesData(""); // Empty string triggers CURDATE() in SQL
            date_input.setText("");
        }

    }
}
