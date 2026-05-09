package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JFrame implements ActionListener {
    JButton btnAdd, btnUpdate, btnDelete, btnShow, btnLeave, btnSearch;
    JComboBox<String> userSelector, metricSelector;
    JLabel lblTrackedCount;

    // Database Credentials
    final String url = "jdbc:mysql://localhost:3306/datab";
    final String db_user = "root";
    final String db_pass = "n@bIa123";

    public Dashboard() {
        this.setTitle("Social Media Intelligence System");
        this.setSize(1540, 840);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        JPanel bg = new JPanel();
        bg.setBounds(0, 0, 1540, 840);
        bg.setBackground(Color.WHITE);
        bg.setLayout(null);
        this.add(bg);

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(181, 52, 224));
                g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 50, 50);
            }
        };
        sidebar.setBounds(20, 12, 280, 750);
        sidebar.setLayout(null);
        sidebar.setOpaque(false);
        bg.add(sidebar);

        try {
            ImageIcon icon2 = new ImageIcon(ClassLoader.getSystemResource("login2.png"));
            RoundImagePanel profilePic = new RoundImagePanel(icon2.getImage(), 120);
            profilePic.setBounds(80, 40, 120, 120);
            sidebar.add(profilePic);
        } catch (Exception e) { System.out.println("login2.png missing."); }

        JLabel title = new JLabel("MENU", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBounds(40, 180, 200, 40);
        sidebar.add(title);

        this.btnAdd = createStyledButton("Add User", 250);
        this.btnUpdate = createStyledButton("Update User", 310);
        this.btnDelete = createStyledButton("Delete User", 370);
        this.btnShow = createStyledButton("Show All Users", 430);
        this.btnLeave = createStyledButton("Leave", 490);

        sidebar.add(btnAdd); sidebar.add(btnUpdate); sidebar.add(btnDelete);
        sidebar.add(btnShow); sidebar.add(btnLeave);

        // ===== CONTENT AREA =====
        JPanel content = new JPanel();
        content.setBounds(320, 20, 1180, 760);
        content.setBackground(new Color(245, 245, 245));
        content.setLayout(null);
        bg.add(content);

        JLabel mainHeading = new JLabel("Social Media Analyzer Dashboard");
        mainHeading.setFont(new Font("Arial", Font.BOLD, 36));
        mainHeading.setForeground(new Color(50, 50, 50));
        mainHeading.setBounds(50, 20, 600, 50);
        content.add(mainHeading);

        try {
            ImageIcon icon3 = new ImageIcon(ClassLoader.getSystemResource("login3.png"));
            Image scaled3 = icon3.getImage().getScaledInstance(850, 320, Image.SCALE_SMOOTH);
            JLabel widePic = new JLabel(new ImageIcon(scaled3));
            widePic.setBounds(50, 80, 850, 320);
            content.add(widePic);
        } catch (Exception e) { System.out.println("login3.png missing."); }

        userSelector = new JComboBox<>(fetchAllUsers());
        userSelector.setBounds(50, 480, 350, 45);
        content.add(userSelector);

        metricSelector = new JComboBox<>(new String[]{"All Info", "Posts", "Likes", "Comments", "Followers"});
        metricSelector.setBounds(50, 540, 350, 45);
        content.add(metricSelector);

        btnSearch = createRunButton("RUN ANALYSIS", 50, 620);
        btnSearch.addActionListener(this);
        content.add(btnSearch);

        JPanel statsPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        statsPanel.setBounds(480, 420, 600, 255);
        statsPanel.setLayout(null);
        content.add(statsPanel);

        lblTrackedCount = new JLabel("● Total Users Tracked: " + userSelector.getItemCount());
        lblTrackedCount.setBounds(30, 75, 400, 25);
        lblTrackedCount.setFont(new Font("Arial", Font.PLAIN, 18));
        statsPanel.add(lblTrackedCount);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            String user = (String) userSelector.getSelectedItem();
            String metric = (String) metricSelector.getSelectedItem();
            if (user != null && !user.equals("No Users Found")) {
                new AnalysisResult(user, metric);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid user.");
            }
        } else if (e.getSource() == btnAdd || e.getSource() == btnUpdate || e.getSource() == btnDelete) {
            new UserManagement(this);
        } else if (e.getSource() == btnShow) {
            new UserListWindow(this);
        } else if (e.getSource() == btnLeave) {
            System.exit(0);
        }
    }

    public void refreshDashboard() {
        userSelector.setModel(new DefaultComboBoxModel<>(fetchAllUsers()));
        lblTrackedCount.setText("● Total Users Tracked: " + userSelector.getItemCount());
    }

    private String[] fetchAllUsers() {
        ArrayList<String> users = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, db_user, db_pass);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT username FROM users")) {
            while (rs.next()) users.add(rs.getString("username"));
        } catch (SQLException e) { return new String[]{"No Users Found"}; }
        return users.isEmpty() ? new String[]{"No Users Found"} : users.toArray(new String[0]);
    }

    private JButton createStyledButton(String text, int yPos) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(181, 52, 224));
                g2.setFont(new Font("Arial", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setBounds(40, yPos, 200, 45);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        return btn;
    }

    private JButton createRunButton(String text, int x, int y) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(181, 52, 224));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setBounds(x, y, 350, 55);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    class RoundImagePanel extends JPanel {
        private Image img; private int size;
        public RoundImagePanel(Image img, int size) { this.img = img; this.size = size; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Double(0, 0, size, size));
            g2.drawImage(img, 0, 0, size, size, this);
            g2.dispose();
        }
    }

    public static void main(String[] args) { new Dashboard(); }
}

class UserListWindow extends JFrame {
    public UserListWindow(Dashboard parent) {
        this.setTitle("User Directory");
        this.setSize(800, 450);
        this.setLocationRelativeTo(parent);
        Vector<String> col = new Vector<>();
        col.add("ID"); col.add("Username"); col.add("Email"); col.add("Created At");
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection con = DriverManager.getConnection(parent.url, parent.db_user, parent.db_pass);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY user_id ASC")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("user_id")); row.add(rs.getString("username"));
                row.add(rs.getString("email")); row.add(rs.getTimestamp("created_at"));
                data.add(row);
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
        this.add(new JScrollPane(new JTable(new DefaultTableModel(data, col))));
        this.setVisible(true);
    }
}

class UserManagement extends JFrame {
    JTextField txtUserId, txtUser, txtEmail;
    JButton btnSave, btnDel, btnUpdate;
    Dashboard parent;

    public UserManagement(Dashboard parent) {
        this.parent = parent;
        this.setTitle("Account Manager");
        this.setSize(450, 450);
        this.setLayout(null);
        this.setLocationRelativeTo(parent);
        this.getContentPane().setBackground(new Color(245, 245, 245));

        JLabel lblId = new JLabel("User ID (For Update/Delete):");
        lblId.setBounds(40, 20, 300, 30);
        this.add(lblId);

        txtUserId = new JTextField();
        txtUserId.setBounds(40, 50, 350, 35);
        txtUserId.setBorder(BorderFactory.createTitledBorder("Target ID"));
        this.add(txtUserId);

        JSeparator sep = new JSeparator();
        sep.setBounds(40, 105, 350, 2);
        this.add(sep);

        txtUser = new JTextField();
        txtUser.setBounds(40, 135, 350, 35);
        txtUser.setBorder(BorderFactory.createTitledBorder("New Username (For Add Only)"));
        this.add(txtUser);

        txtEmail = new JTextField();
        txtEmail.setBounds(40, 185, 350, 35);
        txtEmail.setBorder(BorderFactory.createTitledBorder("New Email Address (For Add Only)"));
        this.add(txtEmail);

        btnSave = new JButton("ADD AS NEW USER");
        btnSave.setBounds(40, 250, 350, 40);
        btnSave.setBackground(new Color(34, 139, 34));
        btnSave.setForeground(Color.WHITE);
        this.add(btnSave);

        btnUpdate = new JButton("UPDATE BY ID");
        btnUpdate.setBounds(40, 310, 170, 40);
        btnUpdate.setBackground(new Color(181, 52, 224));
        btnUpdate.setForeground(Color.WHITE);
        this.add(btnUpdate);

        btnDel = new JButton("DELETE BY ID");
        btnDel.setBounds(220, 310, 170, 40);
        btnDel.setBackground(new Color(178, 34, 34));
        btnDel.setForeground(Color.WHITE);
        this.add(btnDel);

        // ADD LOGIC
        btnSave.addActionListener(e -> {
            if(txtUser.getText().isEmpty() || txtEmail.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Please fill Name and Email!");
                return;
            }
            try (Connection con = DriverManager.getConnection(parent.url, parent.db_user, parent.db_pass);
                 PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, email) VALUES (?, ?)")) {
                ps.setString(1, txtUser.getText());
                ps.setString(2, txtEmail.getText());
                ps.executeUpdate();
                parent.refreshDashboard();
                this.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        // REFINED UPDATE LOGIC
        btnUpdate.addActionListener(e -> {
            String idStr = txtUserId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the User ID to update!");
                return;
            }

            // 1. Create a Selection Panel
            JCheckBox chkName = new JCheckBox("Update Username");
            JCheckBox chkEmail = new JCheckBox("Update Email");
            Object[] message = {
                    "Select fields to update for ID " + idStr + ":",
                    chkName, chkEmail
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Update Selection", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                if (!chkName.isSelected() && !chkEmail.isSelected()) {
                    JOptionPane.showMessageDialog(this, "Nothing selected to update.");
                    return;
                }

                try (Connection con = DriverManager.getConnection(parent.url, parent.db_user, parent.db_pass)) {
                    StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
                    ArrayList<String> values = new ArrayList<>();

                    if (chkName.isSelected()) {
                        String newName = JOptionPane.showInputDialog(this, "Enter New Username:");
                        if (newName != null && !newName.isEmpty()) {
                            queryBuilder.append("username = ?, ");
                            values.add(newName);
                        }
                    }

                    if (chkEmail.isSelected()) {
                        String newEmail = JOptionPane.showInputDialog(this, "Enter New Email:");
                        if (newEmail != null && !newEmail.isEmpty()) {
                            queryBuilder.append("email = ?, ");
                            values.add(newEmail);
                        }
                    }

                    // Remove trailing comma and space
                    String finalQuery = queryBuilder.toString();
                    if (finalQuery.endsWith(", ")) {
                        finalQuery = finalQuery.substring(0, finalQuery.length() - 2);
                    } else {
                        return; // No valid input provided in dialogs
                    }

                    finalQuery += " WHERE user_id = ?";

                    PreparedStatement ps = con.prepareStatement(finalQuery);
                    for (int i = 0; i < values.size(); i++) {
                        ps.setString(i + 1, values.get(i));
                    }
                    ps.setInt(values.size() + 1, Integer.parseInt(idStr));

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "User updated successfully!");
                        parent.refreshDashboard();
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "ID not found!");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        // DELETE LOGIC
        btnDel.addActionListener(e -> {
            String idStr = txtUserId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter ID to delete!");
                return;
            }
            try (Connection con = DriverManager.getConnection(parent.url, parent.db_user, parent.db_pass)) {
                PreparedStatement psDelete = con.prepareStatement("DELETE FROM users WHERE user_id = ?");
                psDelete.setInt(1, Integer.parseInt(idStr));
                if (psDelete.executeUpdate() > 0) {
                    // Re-index IDs for cleanliness
                    Statement st = con.createStatement();
                    st.execute("SET @count = 0;");
                    st.executeUpdate("UPDATE users SET user_id = (@count := @count + 1) ORDER BY user_id ASC;");
                    st.executeUpdate("ALTER TABLE users AUTO_INCREMENT = 1;");

                    JOptionPane.showMessageDialog(this, "User Deleted!");
                    parent.refreshDashboard();
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "ID not found!");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        this.setVisible(true);
    }
}