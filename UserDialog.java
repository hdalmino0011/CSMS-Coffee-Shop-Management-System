package coffeeshop;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

class UserDialog extends JDialog {
    private JTextField tfFirst, tfLast, tfBirth, tfUser, tfPassword;
    private JComboBox<String> cbSex, cbRole;
    private boolean succeeded = false;
    private Object[] userIdStorage;
    private Connection conn; // Store your existing connection
    int roleId;

    public UserDialog(Frame parent, String title, Object[] data) {
        super(parent, title, true);

        conn = DBConnection.getConnection();

        // 8 rows: 7 for data fields, 1 for the button row
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tfFirst = new JTextField();
        tfLast = new JTextField();
        tfBirth = new JTextField("YYYY-MM-DD");
        tfUser = new JTextField();
        tfPassword = new JPasswordField(); // Standard for passwords
        cbSex = new JComboBox<>(new String[]{"M", "F"});
        cbRole = new JComboBox<>(new String[]{"Admin", "User", "Manager"});

        // Add components to the fixed 8x2 grid
        panel.add(new JLabel("First Name:")); panel.add(tfFirst);
        panel.add(new JLabel("Last Name:"));  panel.add(tfLast);
        panel.add(new JLabel("BirthDate:")); panel.add(tfBirth);
        panel.add(new JLabel("Sex:"));       panel.add(cbSex);
        panel.add(new JLabel("Role:"));      panel.add(cbRole);
        panel.add(new JLabel("Username:"));  panel.add(tfUser);
        panel.add(new JLabel("Password:"));  panel.add(tfPassword);

        if (data != null) {
            userIdStorage = new Object[]{data[0]};
            tfFirst.setText((String) data[1]);
            tfLast.setText((String) data[2]);
            tfBirth.setText((String) data[3]);
            cbSex.setSelectedItem(data[4]);
            cbRole.setSelectedItem(data[5]);
            tfUser.setText((String) data[6]);
            tfPassword.setText((String) data[7]);
        }
        String selectedRole = (String) cbRole.getSelectedItem();
        String selectedSex = (String) cbSex.getSelectedItem();

        switch (selectedRole) {
            case "Admin":   roleId = 1; break;
            case "User":    roleId = 2; break;
            case "Manager": roleId = 3; break;
            default:        roleId = 2;
        }
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            if (saveToDatabase()) {
                succeeded = true;
                dispose();
            }
        });

        panel.add(new JLabel("")); // Spacer for alignment
        panel.add(saveBtn);

        getContentPane().add(panel);
        setSize(400, 480); // Fixed size
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private boolean saveToDatabase() {
        String sql;
        boolean isEdit = (userIdStorage != null);

        if (!isEdit) {
            sql = "INSERT INTO user_data (first_name, last_name, birthdate, sex, roles, user_name, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE user_data SET first_name=?, last_name=?, birthdate=?, sex=?, roles=?, user_name=?, password=? WHERE id=?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tfFirst.getText());
            pstmt.setString(2, tfLast.getText());
            pstmt.setString(3, tfBirth.getText());
            pstmt.setString(4, (String) cbSex.getSelectedItem());
            pstmt.setInt(5, roleId);
            pstmt.setString(6, tfUser.getText());
            pstmt.setString(7, tfPassword.getText());

            if (isEdit) {
                pstmt.setObject(8, userIdStorage[0]);
            }

            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Save Failed: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    public Object[] getUserData() {
        return new Object[]{
                userIdStorage != null ? userIdStorage[0] : null, // Index 0: ID
                tfFirst.getText(),                               // Index 1: First Name
                tfLast.getText(),                                // Index 2: Last Name
                tfBirth.getText(),                               // Index 3: BirthDate
                cbSex.getSelectedItem(),                         // Index 4: Sex
                cbRole.getSelectedItem(),                        // Index 5: Role
                tfUser.getText(),                                // Index 6: Username
                tfPassword.getText()                             // Index 7: Password
        };
    }

    public boolean isSucceeded() { return succeeded; }
}
