import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class EmployeePayrollSystem extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nameField, deptField, positionField, salaryField, searchIdField;
    private JLabel statsLabel;
    private Connection conn;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "payroll_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public EmployeePayrollSystem() {
        setTitle("Employee Payroll System");
        setSize(1100, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initDB();
        initUI();
        loadEmployees();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Header Panel with stats
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 150, 136));
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("EMPLOYEE PAYROLL SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        statsLabel = new JLabel("Total Employees: 0");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statsLabel.setForeground(Color.WHITE);
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(statsLabel, BorderLayout.EAST);
        
        // Left Panel - Employee Entry Form
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        leftPanel.setPreferredSize(new Dimension(320, 0));
        
        // Form Title
        JLabel formTitle = new JLabel("Employee Details");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(formTitle);
        leftPanel.add(Box.createVerticalStrut(20));
        
        // Input fields
        nameField = addFormField(leftPanel, "Employee Name:");
        deptField = addFormField(leftPanel, "Department:");
        positionField = addFormField(leftPanel, "Position:");
        salaryField = addFormField(leftPanel, "Salary ($):");
        
        leftPanel.add(Box.createVerticalStrut(10));
        
        // Add button
        JButton addBtn = createButton("Add Employee", new Color(76, 175, 80), e -> addEmployee());
        addBtn.setMaximumSize(new Dimension(280, 40));
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(addBtn);
        
        leftPanel.add(Box.createVerticalStrut(30));
        
        // Search section
        JLabel searchTitle = new JLabel("Search Employee");
        searchTitle.setFont(new Font("Arial", Font.BOLD, 16));
        searchTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(searchTitle);
        leftPanel.add(Box.createVerticalStrut(15));
        
        searchIdField = addFormField(leftPanel, "Employee ID:");
        
        leftPanel.add(Box.createVerticalStrut(10));
        
        JButton searchBtn = createButton("Find Employee", new Color(33, 150, 243), e -> findEmployee());
        searchBtn.setMaximumSize(new Dimension(280, 40));
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(searchBtn);
        
        leftPanel.add(Box.createVerticalGlue());
        
        // Right Panel - Table
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel tableTitle = new JLabel("Employee Records");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        String[] cols = {"ID", "Name", "Department", "Position", "Salary", "Date Added"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0, 150, 136));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 230, 201));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(120);  // Department
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Position
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Salary
        table.getColumnModel().getColumn(5).setPreferredWidth(150);  // Date Added
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        // Delete button panel
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deletePanel.setBackground(Color.WHITE);
        JButton deleteBtn = createButton("Delete Selected", new Color(244, 67, 54), e -> deleteEmployee());
        deleteBtn.setPreferredSize(new Dimension(160, 35));
        deletePanel.add(deleteBtn);
        
        rightPanel.add(tableTitle, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(deletePanel, BorderLayout.SOUTH);
        
        // Add all panels to frame
        add(header, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private JTextField addFormField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(280, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
        
        return field;
    }

    private JButton createButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.addActionListener(action);
        return btn;
    }

    private void initDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            conn.createStatement().execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
            
            // Drop existing table and recreate with new schema
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE IF EXISTS employees");
            stmt.execute(
                "CREATE TABLE employees (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(200)," +
                "department VARCHAR(100)," +
                "position VARCHAR(100)," +
                "salary DECIMAL(10,2)," +
                "date VARCHAR(50))");
            stmt.close();
            System.out.println("Database ready!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    private void loadEmployees() {
        try {
            tableModel.setRowCount(0);
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM employees");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getString("department"), 
                    rs.getString("position"),
                    String.format("$%.2f", rs.getDouble("salary")), 
                    rs.getString("date")
                });
            }
            updateStats();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load Error: " + e.getMessage());
        }
    }

    private void addEmployee() {
        String name = nameField.getText().trim();
        String dept = deptField.getText().trim();
        String position = positionField.getText().trim();
        String salaryStr = salaryField.getText().trim();
        
        if (name.isEmpty() || dept.isEmpty() || position.isEmpty() || salaryStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }
        try {
            double salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                JOptionPane.showMessageDialog(this, "Salary cannot be negative!");
                return;
            }
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO employees (name, department, position, salary, date) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setString(3, position);
            ps.setDouble(4, salary);
            ps.setString(5, date);
            ps.executeUpdate();
            nameField.setText("");
            deptField.setText("");
            positionField.setText("");
            salaryField.setText("");
            loadEmployees();
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid number!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee to delete!");
            return;
        }
        try {
            int id = (int) table.getValueAt(row, 0);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            loadEmployees();
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void findEmployee() {
        String idStr = searchIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Employee ID!");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String empInfo = "Employee Found!\n\n" +
                    "ID: " + rs.getInt("id") + "\n" +
                    "Name: " + rs.getString("name") + "\n" +
                    "Department: " + rs.getString("department") + "\n" +
                    "Position: " + rs.getString("position") + "\n" +
                    "Salary: $" + String.format("%.2f", rs.getDouble("salary")) + "\n" +
                    "Date Added: " + rs.getString("date");
                JOptionPane.showMessageDialog(this, empInfo, "Employee Details", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found with ID: " + id, 
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            }
            searchIdField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateStats() {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as total FROM employees");
            if (rs.next()) {
                statsLabel.setText("Total Employees: " + rs.getInt("total"));
            }
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeePayrollSystem());
    }
}
