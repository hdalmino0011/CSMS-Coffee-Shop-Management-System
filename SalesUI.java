package coffeeshop;
// For saving the receipt to a .txt file
import java.io.PrintWriter;
import java.io.IOException;

import java.text.SimpleDateFormat; 
import java.util.Date;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.print.PrinterException;
import java.util.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class SalesUI extends JFrame implements ActionListener {
    
    // THIS IS FOR THE SALES PANEL
    JTextField date_input = new JTextField(10);
    JButton select_date_bttn = new JButton("SELECT DATE");
    JButton current_date_bttn = new JButton("TODAY'S SALES");
    
    //  THIS IS FOR  THE DATABASE TO KNOW WHO'S CASHIER THAT MADE THE TRANSACTION
    int user_id = 0;
    String fullname = null;
    /* THIS IF FOR THE WINDOW DISPLAY*/
    JButton back_bttn = new JButton("BACK");
    JButton back_sales_bttn = new JButton("BACK");
    JPanel tab = new JPanel();
    JPanel pos_panel = new JPanel();
    JPanel dash_panel = new JPanel();
    ImageIcon dash = new ImageIcon("C:\\Users\\Anthon\\Downloads\\pictures\\dboardcoffee.jpg");
    Image originalImage = dash.getImage();

    
     Image scaledImage = originalImage.getScaledInstance(1100, 750, Image.SCALE_SMOOTH);   
    JLabel backgrnd =  new JLabel(){
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // This line draws the image to the current width and height of the label
        g.drawImage(originalImage, 0, 0, getWidth(), getHeight(), this);
    }
};

    JButton POS;
    JButton VIEW_SALES;
    JButton LOG_OUT;

   /*  THIS IF FOR THE WINDOW DISPLAY */
/*   LEFT SIDE  PART OF THE POINT OF SALES WINDOW    */
    JPanel left_side = new JPanel();
    JPanel center_left = new JPanel();
    DefaultTableModel sales_data = new DefaultTableModel(); // Fixed: Initialize to avoid null
    DefaultTableModel not_editable = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            // This makes all cells uneditable
            return false;
        }
    };

    JTable sales_table = new JTable(not_editable); // Fixed: Set model directly
    JScrollPane sales_scroll = new JScrollPane(sales_table);
    JPanel sales_panel = new JPanel(new GridBagLayout());
    DefaultTableModel get_order = new DefaultTableModel(new Object[]{"PRODUCT NAME", "QTY", "PRICE"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            // This makes all cells uneditable
            return false;
        }
    };
    SpinnerModel model = new SpinnerNumberModel(1, 1, 10000000, 1);
    JSpinner get_qntty = new JSpinner(model);
    JTextField prdct_name = new JTextField(40);

    String  [] col_heads = {"PRODUCT NAME","QUANTITY"};
    JTable current_order = new JTable(get_order);
    JScrollPane currents_scroll =  new JScrollPane(current_order);

    JButton add_ordr_bttn = new JButton("ADD ORDER");
    JButton delete_ordr_bttn = new JButton("DELETE ORDER");

    JTextField cash_field = new JTextField(20);
    JButton cancel_bttn = new JButton("CANCEL ORDER");
    JButton purchase_bttn = new JButton("PURCHASE ORDER");
    JLabel Cash_Amount = new JLabel ("Cash Amount: ");
    double sub_t = 0, total = 0, change = 0;
    JLabel Sub_Total = new JLabel ();
    JLabel Total = new JLabel ();
    JLabel Change = new JLabel ();

/*   LEFT SIDE  PART OF THE POINT OF SALES WINDOW    */

/*  MIDDLE PART OF THE POINT OF SALES WINDOW */
    JPanel center_middle = new JPanel();
    JPanel middle_side = new JPanel();
    JPanel bttns_part = new JPanel(new GridLayout(0,3, 10, 10));
    Map <String, JButton> bttns;
    JPanel products_panel = new JPanel(new BorderLayout());

    JScrollPane for_bttns = new JScrollPane(bttns_part);
    JComboBox <String> prodct_catgory =  new JComboBox<>();

/*  MIDDLE PART OF THE POINT OF SALES WINDOW */
        
    SalesUI(String fullname, int user_id){
        this.user_id = user_id;
        this.fullname = fullname;
        setLayout(new BorderLayout());
        setSize(1100, 750); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // STYLING COLORS
        Color brownColor = new Color(101, 67, 33);

/*  SET FOR LEFT SIDE PANEL */
        left_side.setLayout(new BoxLayout(left_side, BoxLayout.Y_AXIS));
        left_side.setBorder(new EmptyBorder(15, 15, 15, 15));
        left_side.setBackground(new Color(245, 245, 245)); 
        left_side.setPreferredSize(new Dimension(340, 700));

// BACK BUTTON TO RETURN TO DASH BOARD
        back_bttn.setBackground(Color.WHITE);
        back_bttn.addActionListener(this);

// NEW: CASHIER NAME LABEL
        JLabel cashierLabel = new JLabel("Cashier Name: " + fullname);
        cashierLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        cashierLabel.setForeground(Color.DARK_GRAY);

//  PRODUCT & QUANTITY SECTION 
        JPanel inputGroup = new JPanel(new GridLayout(2, 2, 5, 10));
        inputGroup.setOpaque(false);
        inputGroup.setMaximumSize(new Dimension(320, 80));

        inputGroup.add(new JLabel("PRODUCT:"));
        prdct_name.setEditable(false);
        prdct_name.setFont(new Font("SansSerif", Font.BOLD, 12));
        inputGroup.add(prdct_name);

        inputGroup.add(new JLabel("QUANTITY:"));
        inputGroup.add(get_qntty);

// --- ORDER BUTTONS ---
        add_ordr_bttn.setMaximumSize(new Dimension(320, 40));
        add_ordr_bttn.setBackground(brownColor); 
        add_ordr_bttn.setForeground(Color.WHITE);
        add_ordr_bttn.setFocusPainted(false);
        add_ordr_bttn.setFont(new Font("SansSerif", Font.BOLD, 12));
        add_ordr_bttn.addActionListener(this); // FIXED: Added Listener

        delete_ordr_bttn.setMaximumSize(new Dimension(320, 40));
        delete_ordr_bttn.setBackground(Color.WHITE); 
        delete_ordr_bttn.setForeground(Color.BLACK);
        delete_ordr_bttn.setFocusPainted(false);
        delete_ordr_bttn.addActionListener(this); // FIXED: Added Listener

//  ORDER LIST 
        currents_scroll.setBorder(BorderFactory.createTitledBorder("Current Selection"));
        currents_scroll.setPreferredSize(new Dimension(320, 250));
        currents_scroll.setMaximumSize(new Dimension(320, 250));

//  PAYMENT SECTION 
        JPanel paymentGroup = new JPanel();
        paymentGroup.setLayout(new GridLayout(4, 1, 0, 5));
        paymentGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Payment Details"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        paymentGroup.setBackground(Color.WHITE);
        paymentGroup.setMaximumSize(new Dimension(320, 150));

        JPanel cashRow = new JPanel(new BorderLayout());
        cashRow.setOpaque(false);
        cashRow.add(new JLabel("CASH: "), BorderLayout.WEST);
        cash_field.setHorizontalAlignment(JTextField.RIGHT);
        cashRow.add(cash_field, BorderLayout.CENTER);

        Sub_Total.setText("Sub Total: 0.00");
        Total.setText("Total: 0.00");
        Total.setFont(new Font("SansSerif", Font.BOLD, 14));
        Change.setText("Change: 0.00");

        paymentGroup.add(cashRow);
        paymentGroup.add(Sub_Total);
        paymentGroup.add(Total);
        paymentGroup.add(Change);

//  FINAL ACTION BUTTONS 
        purchase_bttn.setMaximumSize(new Dimension(320, 50));
        purchase_bttn.setBackground(brownColor); 
        purchase_bttn.setForeground(Color.WHITE);
        purchase_bttn.setFont(new Font("SansSerif", Font.BOLD, 14));
        purchase_bttn.addActionListener(this); // FIXED: Added Listener

        cancel_bttn.setMaximumSize(new Dimension(320, 35));
        cancel_bttn.setBackground(Color.WHITE);
        cancel_bttn.setFocusPainted(false);
        cancel_bttn.addActionListener(this); // FIXED: Added Listener

//  ASSEMBLE LEFT SIDE 

        left_side.add(back_bttn); 
        left_side.add(Box.createVerticalStrut(5));
        left_side.add(cashierLabel); // ADDED BELOW BACK BUTTON
        left_side.add(Box.createVerticalStrut(10));
        left_side.add(inputGroup);
        left_side.add(Box.createVerticalStrut(15));
        left_side.add(add_ordr_bttn);
        left_side.add(Box.createVerticalStrut(10));
        left_side.add(delete_ordr_bttn);
        left_side.add(Box.createVerticalStrut(15));
        left_side.add(currents_scroll);
        left_side.add(Box.createVerticalStrut(15));
        left_side.add(paymentGroup);
        left_side.add(Box.createVerticalStrut(20));
        left_side.add(purchase_bttn);
        left_side.add(Box.createVerticalStrut(10));
        left_side.add(cancel_bttn);

        center_left.setLayout(new BorderLayout());
        center_left.add(left_side, BorderLayout.CENTER);
/*  SET FOR LEFT SIDE PANEL */

/*   SET FOR MIDDLE SIDE PANEL*/
        prodct_catgory.setModel(Sales.getCategories());
        prodct_catgory.setMaximumSize(new Dimension(300, 40));
        prodct_catgory.addActionListener(this);

        bttns_part.setBackground(Color.WHITE);

        middle_side.setLayout(new BoxLayout(middle_side , BoxLayout.Y_AXIS));
        middle_side.setBorder(new EmptyBorder(20, 20, 20, 20));
        middle_side.setBackground(Color.WHITE);

        JLabel catLabel = new JLabel("SELECT CATEGORY:");
        catLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        catLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        middle_side.add(catLabel);
        middle_side.add(Box.createVerticalStrut(10));
        middle_side.add(prodct_catgory);
        middle_side.add(Box.createVerticalStrut(20));

        for_bttns.setBorder(null);
        for_bttns.getViewport().setBackground(Color.WHITE);
        products_panel.add(for_bttns, BorderLayout.CENTER);
        middle_side.add(products_panel);

        center_middle.setLayout(new BorderLayout());
        center_middle.add(middle_side, BorderLayout.CENTER);

/*   SET FOR MIDDLE SIDE PANEL*/

/*          SET THE POINT OF SALES PANEL             */
        pos_panel.setLayout(new BorderLayout());
        pos_panel.add(center_left,  BorderLayout.WEST);
        pos_panel.add(center_middle, BorderLayout.CENTER);
//pos_panel.add(center_right , BorderLayout.EAST);

/*          SET THE POINT OF SALES PANEL             */

/*           ADD THE POINT OF SALES PANEL IN THE TAB*/
//tab.add( "POINT OF SALES", pos_panel);

//not_editable.setDataVector(sales_table.setModel(sales_data), Sales.colnames(sales_data));
// Configure the button
        back_sales_bttn.setBackground(Color.WHITE);
        back_sales_bttn.addActionListener(this);

// Setup the layout for sales_panel
        sales_panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
// Inside SalesUI Constructor
        back_sales_bttn.addActionListener(this);
        select_date_bttn.setBackground(Color.WHITE);
        select_date_bttn.addActionListener(this);
        current_date_bttn.setBackground(Color.WHITE);
        current_date_bttn.addActionListener(this);

        sales_panel.setLayout(new GridBagLayout());
        gbc.insets = new Insets(10, 10, 10, 10);

// Row 0: The Control Bar
        JPanel sales_controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sales_controls.add(back_sales_bttn);
        sales_controls.add(new JLabel("Date (YYYY-MM-DD):"));
        sales_controls.add(date_input);
        sales_controls.add(select_date_bttn);
        sales_controls.add(current_date_bttn);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sales_panel.add(sales_controls, gbc);

// Row 1: The Table
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        sales_panel.add(sales_scroll, gbc);

        sales_table.setDragEnabled(false);
        sales_table.getTableHeader().setReorderingAllowed(false);
        syncSalesData(); // Fixed: Call default sync

        POS = new JButton("POINT OF SALES");
        POS.setBackground(Color.WHITE);
        POS.addActionListener(this);
        VIEW_SALES = new JButton("VIEW SALES");
        VIEW_SALES.setBackground(Color.WHITE);
        VIEW_SALES.addActionListener(this);
        LOG_OUT = new JButton("LOG OUT");
        LOG_OUT.setBackground(Color.WHITE);
        LOG_OUT.addActionListener(this);

   
        backgrnd.setLayout(new FlowLayout());
        dash = new ImageIcon(scaledImage);
        backgrnd.setIcon(dash);
        backgrnd.add(POS);
        backgrnd.add(VIEW_SALES);
        backgrnd.add(LOG_OUT);

        add(backgrnd);


/*   ADD TO THE WINDOW TO MAKE IT VISIBLE */
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == prodct_catgory) { // THIS IS FOR THE JCOMBOBOX
            categoryBox();
        } else if(e.getSource() == add_ordr_bttn){
            addBTTN();
        } else if(e.getSource() == delete_ordr_bttn){
            deleteBTTN();
        } else if(e.getSource() == cancel_bttn){
            cancelBTTN();
        } else if(e.getSource() == purchase_bttn){
            purchaseBTTN();
        } else if(e.getSource() == POS){
            this.remove(backgrnd);
            this.add(pos_panel);
            this.revalidate();          
            this.repaint();             
        } else if(e.getSource() == VIEW_SALES){
            this.remove(backgrnd);
            this.add(sales_panel);
            this.revalidate();          
            this.repaint();      
        } else if(e.getSource() == back_bttn || e.getSource() == back_sales_bttn){
            showDashboard();
        } else if (e.getSource() == select_date_bttn) {
            String inputDate = date_input.getText().trim();
            if(!inputDate.isEmpty()) {
                syncSalesData(inputDate); // Use the updated method
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a date first!");
            }
        } else if (e.getSource() == current_date_bttn) {
            syncSalesData(""); // Empty string defaults to CURDATE() in your SQL logic
            date_input.setText("");
        }else if( e.getSource() == LOG_OUT){
    int result = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);         
    
    if(result == JOptionPane.YES_OPTION){
    this.dispose();
        }
        }
    }
    
    //method for selecting a product category in the JCOMBOBOX 
    public void categoryBox(){
        Object item = prodct_catgory.getSelectedItem();
        if (item != null) {
            bttns_part.removeAll();
            String getName = item.toString();
            bttns = Sales.getProducts(getName);
            if(bttns != null) { // Added null check
                for (JButton b : bttns.values()) {
                    // Styling generated product buttons
                    b.setBackground(Color.WHITE);
                    b.setFocusPainted(false);
                    b.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
                    b.addActionListener(event -> {
                        prdct_name.setText(b.getText());
                        get_qntty.setValue(1);
                    });
                    bttns_part.add(b);
                }
            }
            bttns_part.revalidate();
            bttns_part.repaint();
        }
    }

    //method for add order button
    public void addBTTN(){
        String name = prdct_name.getText();
        // CHECK EMPTY FIRST
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Select order first!");
            return; 
        }
        
        double price = Sales.getPrice(name);
        int max_order = Sales.getMaxPossible(name);
        int qtty = (Integer) get_qntty.getValue();
        double total_price = price * qtty;

        //THIS IS NUMBER 2
        for(int i = 0; i <  current_order.getRowCount(); i++){
            String exstng_prduct = current_order.getValueAt(i, 0).toString();
            if(name.equalsIgnoreCase(exstng_prduct)){
                int current_qtty = (int) current_order.getValueAt(i, 1);
                int new_qtty = current_qtty + qtty;  
                if(new_qtty <= max_order){
                    double old_price =(double) get_order.getValueAt(i, 2);
                    get_order.setValueAt(new_qtty, i, 1);
                    double updated_total = price * new_qtty;
                    get_order.setValueAt(updated_total , i, 2);
                    sub_t = (sub_t - old_price) + updated_total;
                    moneyLabels(sub_t);
                    return;
                } else {
                    JOptionPane.showMessageDialog(null, "Max order for this product is: " + max_order);
                    get_qntty.setValue(1);
                    return;
                }
            }
        }

        //THIS IS NUMBER 3
        if (qtty <= max_order) {
            get_order.addRow(new Object[]{name, qtty, total_price});
            prdct_name.setText("");
            get_qntty.setValue(1);
            sub_t += total_price;
            moneyLabels(sub_t);
        } else {
            JOptionPane.showMessageDialog(null, "Max order for this product is only " + max_order);
        }
    }

    //method for delete order button
    public void deleteBTTN(){
        int index = current_order.getSelectedRow();
        if(index > -1){
            double total_price = (double) get_order.getValueAt(index, 2);
            sub_t -= total_price;
            moneyLabels(sub_t);
            get_order.removeRow(index);
            if(get_order.getRowCount() == 0){
                clearComponents();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select an order before deleting!");
        }
    }

    ///cancel button method
    public void cancelBTTN(){
        int result = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            clearComponents();
        }
    }

    public void showDashboard() {
        this.getContentPane().removeAll();
        this.add(backgrnd);
        this.revalidate();
        this.repaint();
    }
    
    public void purchaseBTTN(){
        String field  = cash_field.getText();
        if(field.isEmpty()){
            JOptionPane.showMessageDialog(null, "Input cash amount first!");
            return;
        }
                
        int row_count = get_order.getRowCount();
        if(row_count < 1){
            JOptionPane.showMessageDialog(null, "Create order first!");
            clearComponents();
            return;
        }
              
        try{
            double cash = Double.parseDouble(cash_field.getText().trim());
            if(cash < sub_t){
                JOptionPane.showMessageDialog(null, "Cash amount is not enough! Try again" );
                return;
            }

            int result = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION){
                double get_change = cash - total;
                moneyLabels(sub_t, get_change);
                Sales.updateSales(get_order, user_id);
                generateReceipt(cash, get_change);
                syncSalesData();
                Sales.updateInventory(get_order);
                JOptionPane.showMessageDialog(this, "Purchase Successful!\nChange: " + String.format("%.2f", get_change));
                clearComponents();
            }
        } catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter numbers only: " + e.getMessage());
        }
    }
      
    public void clearComponents(){
        get_order.setRowCount(0);
        sub_t = 0;
        total = 0;
        change = 0;
        Sub_Total.setText("Sub Total: 0.00");
        Total.setText("Total: 0.00");
        Change.setText("Change: 0.00");
        prdct_name.setText("");
        get_qntty.setValue(1);
        cash_field.setText("");
    }
      
    //SET FOR ADD OR DELETE 
    public void moneyLabels(double sub_t){
        Sub_Total.setText("Sub Total: " + String.format("%.2f", sub_t));
        total = sub_t;
        Total.setText("Total: " + String.format("%.2f", total));
        change = 0.0;
        Change.setText("Change: " + String.format("%.2f", change));
    }
    
    // SET FOR THE PURCHASE PART
    public void moneyLabels(double sub_t, double get_change){
        Sub_Total.setText("Sub Total: " + String.format("%.2f", sub_t));
        total = sub_t;
        Total.setText("Total: " + String.format("%.2f", total));
        change = get_change;
        Change.setText("Change: " + String.format("%.2f", change));
    }

    public void syncSalesData() {
        syncSalesData("");
    }

    public void syncSalesData(String date) {
        DefaultTableModel freshData = Sales.getSalesRecord(date); 
        String[] headers = {"Order #", "Product Name", "Quantity", "Total Price", "Date Purchased"};
        Vector<String> columnNames = new Vector<>(Arrays.asList(headers));
        not_editable.setDataVector(freshData.getDataVector(), columnNames);
        sales_table.setRowHeight(28);
        sales_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 
        sales_table.revalidate();
        sales_table.repaint();
    }
    
  public void generateReceipt(double cash, double change) {
    StringBuilder receipt = new StringBuilder();
    String line = "------------------------------------------\n";
    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    // Header - Updated for your Cebu City location
    receipt.append("               COFFEE SHOP          \n");
    receipt.append("          Palma Street, Cebu City        \n");
    receipt.append("          Date: " + timestamp + "       \n");
    receipt.append(line);
    receipt.append(String.format("%-20s %-5s %-10s\n", "ITEM", "QTY", "PRICE"));
    receipt.append(line);

    // Loop through the current order table
    for (int i = 0; i < get_order.getRowCount(); i++) {
        String name = get_order.getValueAt(i, 0).toString();
        String qty = get_order.getValueAt(i, 1).toString();
        double price = (double) get_order.getValueAt(i, 2);
        receipt.append(String.format("%-20s %-5s %-10.2f\n", name, qty, price));
    }

    // Footer with Totals
    receipt.append(line);
    receipt.append(String.format("SUBTOTAL:           %10.2f\n", sub_t));
    receipt.append(String.format("CASH:               %10.2f\n", cash));
    receipt.append(String.format("CHANGE:             %10.2f\n", change));
    receipt.append(String.format("CASHIER: %s\n", fullname));
    receipt.append(line);
    receipt.append("       THANK YOU! COME AGAIN!       \n");

    String finalReceiptText = receipt.toString();

    // 1. DISPLAY ON SCREEN (As you had before)
    JTextArea textArea = new JTextArea(finalReceiptText);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
    textArea.setEditable(false);
    JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Receipt Preview", JOptionPane.INFORMATION_MESSAGE);

  
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(new Printable() {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) return NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

            int y = 20; // Starting Y position
            for (String lineText : finalReceiptText.split("\n")) {
                g2d.drawString(lineText, 10, y);
                y += 15; // Line spacing
            }
            return PAGE_EXISTS;
        }
    });

    boolean doPrint = job.printDialog();
    if (doPrint) {
        try {
            job.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Printer Error: " + e.getMessage());
        }
    }

   
    try (PrintWriter out = new PrintWriter("receipt_" + System.currentTimeMillis() + ".txt")) {
        out.println(finalReceiptText);
    } catch (IOException e) {
        System.out.println("File Error: " + e.getMessage());
    }
}
}
