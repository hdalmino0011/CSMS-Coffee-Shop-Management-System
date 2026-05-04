package coffeeshop;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UsersPanel extends JPanel {
    private DefaultTableModel userModel;
    private JTable table;
    private Connection conn;

    public UsersPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        String[] cols = {"User ID", "First Name", "Last Name", "BirthDate", "Sex","Password", "Role", "Username"};

        userModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(userModel);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setupButtons();
        refreshUserData();
    }

    private void setupButtons() {
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add User");
        JButton editBtn = new JButton("Edit User");
        JButton deleteBtn = new JButton("Delete User");
        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {

                String idStr = userModel.getValueAt(selectedRow, 0).toString();
                int userId = Integer.parseInt(idStr);

                int confirm = JOptionPane.showConfirmDialog(this, "Delete User ID: " + userId + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteUserFromDB(userId);
                }
            }
        });
        addBtn.addActionListener(e -> {
            UserDialog dialog = new UserDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", null);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                saveUserToDB(dialog.getUserData(), true);
            }
        });
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user to edit.");
                return;
            }
            Object[] rowData = new Object[userModel.getColumnCount()];
            for (int i = 0; i < rowData.length; i++) {
                rowData[i] = userModel.getValueAt(selectedRow, i);
            }

            UserDialog dialog = new UserDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", rowData);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                saveUserToDB(dialog.getUserData(), false);
            }
        });
    }
    public void refreshUserData() {
        conn = DBConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Do NOT put conn in a try-with-resources block
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT user_id, first_name, last_name, birthdate, sex,password, roles, user_name FROM user_data");

            userModel.setRowCount(0);
            while (rs.next()) {
                userModel.addRow(new Object[]{
                        rs.getString("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("birthdate"),
                        rs.getString("sex"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("user_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        } finally {
            // ONLY close the statement and result set, NEVER the conn here
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteUserFromDB(int userId) {
        String sql = "DELETE FROM user_data WHERE user_id = ?";
        conn = DBConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                refreshUserData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    private void saveUserToDB(Object[] data, boolean isNew) {
        String sql;
        conn = DBConnection.getConnection();
        if (isNew) {
            // Match your DB column names (roles vs role, user_name vs username)
            sql = "INSERT INTO user_data (first_name, last_name, birthdate, sex, roles, user_name, password) VALUES (?,?,?,?,?,?,?)";
        } else {
            sql = "UPDATE user_data SET first_name=?, last_name=?, birthdate=?, sex=?, roles=?, user_name=?, password=? WHERE user_id=?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, (String) data[1]); // First Name
            pstmt.setString(2, (String) data[2]); // Last Name
            pstmt.setString(3, (String) data[3]); // Birthdate
            pstmt.setString(4, data[4].toString()); // Sex
            pstmt.setString(5, data[5].toString()); // Roles
            pstmt.setString(6, (String) data[6]); // Username
            pstmt.setString(7, (String) data[7]); // Password

            if (!isNew) {
                pstmt.setInt(8, Integer.parseInt(data[0].toString())); // User ID
            }

            pstmt.executeUpdate();
            refreshUserData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Save Error: " + e.getMessage());
        }
    }



}

