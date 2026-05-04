package coffeeshop;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Anthon
 */
public class Sales {
    
  public static void updateInventory(DefaultTableModel get_order){
    
    String query = "UPDATE product_stocks ps " +
               "JOIN product_recipes pr ON ps.item_id = pr.ingredients_need " + 
               "SET ps.total_unit_count = ps.total_unit_count - (pr.amount_needed * ?) " +
               "WHERE pr.product_id = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement sttmnt = conn.prepareStatement(query)) {
     
     for(int i = 0 ; i < get_order.getRowCount(); i++){
         
         String prod_name = get_order.getValueAt(i, 0).toString();
         int quantity = Integer.parseInt(get_order.getValueAt(i, 1).toString());
       
           sttmnt.setInt(1, quantity);
           sttmnt.setInt(2, getprodID(prod_name));
         sttmnt.executeUpdate();
     }
        JOptionPane.showMessageDialog(null, "Updating stocks is complete!");
    } catch(SQLException e){
        e.printStackTrace();
    }
    
    return;
    }
    
    
static int getprodID(String prod_name){

    String query = "select product_id FROM product_menu where product_name = ?";
    int id = -5;
try (Connection conn = DBConnection.getConnection();
     PreparedStatement sttmnt = conn.prepareStatement(query)) {
     
     sttmnt.setString(1, prod_name);
     try (ResultSet result = sttmnt.executeQuery()) {
         while(result.next()){
             id = result.getInt("product_id");
         }
     }
} catch(SQLException e){
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error saving into sales database " + e.getMessage());
}
return id;
}

public static void updateSales(DefaultTableModel order, int user_id){

String query = "insert into sales_record(product_name, quantity, total_price, user_id) values( ?, ?, ?,?)";

try (Connection conn = DBConnection.getConnection();
     PreparedStatement sttmnt = conn.prepareStatement(query)) {
     
     for(int i = 0; i < order.getRowCount(); i++){
         String name = order.getValueAt(i, 0).toString();
         int quantity = Integer.parseInt(order.getValueAt(i, 1).toString());
         double price = Double.parseDouble(order.getValueAt(i, 2).toString());
              
         sttmnt.setString(1, name);
         sttmnt.setInt(2, quantity);
         sttmnt.setDouble(3, price);
         sttmnt.setInt(4, user_id);
         sttmnt.executeUpdate();
     }
         
} catch(SQLException e){
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error saving into sales database " + e.getMessage());
}
}

  public static Object[] colnames(DefaultTableModel sales ){
      
          Object [] col = new Object[sales.getColumnCount()];
      for(int i = 1 ; i < sales.getColumnCount(); i++){
      
      col[i - 1] = sales.getColumnName(i);
      
      }
      
      return col;
      }

    public static DefaultTableModel getSalesRecord(String date){

        DefaultTableModel table = new DefaultTableModel();
        boolean isDateProvided = (date != null && !date.isEmpty());

        String query = !isDateProvided ? 
                "SELECT order_number, product_name, quantity, total_price, date_purchased FROM sales_record where date(date_purchased) = CURDATE()" :
                "SELECT order_number, product_name, quantity, total_price, date_purchased FROM sales_record where date(date_purchased) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement sttmnt = conn.prepareStatement(query)) {
            
            if (isDateProvided) {
                sttmnt.setString(1, date);
            }

            try (ResultSet result = sttmnt.executeQuery()) {
                ResultSetMetaData data = result.getMetaData();

                for(int i =  1 ; i <=  data.getColumnCount(); i++){
                      table.addColumn(data.getColumnName(i));
                }

                while(result.next()){
                    Object [] row = new Object[data.getColumnCount()];
                    for(int i =  1 ; i <=  data.getColumnCount(); i++){
                          row[i-1] = result.getObject(i);
                    }
                    table.addRow(row);
                }
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error getting sales data : " + e.getMessage());
        }

    return table;
    }

public static int getMaxPossible(String productName) {
    int maxCanMake = Integer.MAX_VALUE; // Start with a huge number
    
    String query = "SELECT s.total_unit_count, r.amount_needed " +
                   "FROM product_recipes r " +
                   "JOIN product_stocks s ON r.ingredients_need = s.item_id " +
                   "JOIN product_menu p ON r.product_id = p.product_id " +
                   "WHERE p.product_name = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setString(1, productName);
        try (ResultSet rs = pstmt.executeQuery()) {
            boolean hasRecipe = false;
            while (rs.next()) {
                hasRecipe = true;
                double stock = rs.getDouble("total_unit_count");
                double needed = rs.getDouble("amount_needed");
                
                int possibleWithThis = (int) (stock / needed);
                
                if (possibleWithThis < maxCanMake) {
                    maxCanMake = possibleWithThis;
                }
            }
            return hasRecipe ? maxCanMake : 0;
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error checking stock: " + e.getMessage());
        return 0;
    }
}

static Map<String, JButton> getProducts(String getName){
    
    Map<String, JButton> map = new LinkedHashMap<>(); 
    String query = "select pm.product_name from product_menu pm join product_categories pc on pm.category_id = pc.category_id where pc.category_name = ?" ;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement sttmnt = conn.prepareStatement(query)) {
        
        sttmnt.setString(1, getName);
        try (ResultSet result = sttmnt.executeQuery()) {
            while(result.next()){
               String name = result.getString("product_name");
               var bttn = new JButton(name);
               bttn.setName(name);
               map.put(name, bttn);
            }
        }
    } catch(SQLException e){
        JOptionPane.showMessageDialog(null, "Error getting product names " + e.getMessage());
    }
    
    return map;
}

static double getPrice(String p_name){
    
 String query = "select price from product_menu where product_name = ?";
 double price = 0;
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement sttmnt = conn.prepareStatement(query)) {
        
        sttmnt.setString(1, p_name);
        try (ResultSet result = sttmnt.executeQuery()) {
            while(result.next()){
                price = result.getDouble(1);
            }
        }
    } catch(SQLException e){
        e.printStackTrace();
    }
    return price;
}

static DefaultComboBoxModel getCategories(){

 String query = "select category_name from product_categories";
 var list = new DefaultComboBoxModel();

 try (Connection conn = DBConnection.getConnection();
      Statement sttmnt = conn.createStatement();
      ResultSet result = sttmnt.executeQuery(query)) {
       
        while(result.next()){
            String name = result.getString(1);
            list.addElement(name);
        }
        
    } catch(SQLException e){
        JOptionPane.showMessageDialog(null, "Error getting category names " + e.getMessage());
    }

return list;
}

}
