package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Main extends JFrame implements ActionListener {

    // ===== GLOBAL VARIABLES =====
    JButton login;
    JTextField usernameField;
    JPasswordField passwordField;

    final String url = "jdbc:mysql://localhost:3306/datab";

    public Main() {

        setTitle("Login UI");
        setSize(1500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // ===== BACKGROUND =====
        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("login.png"));
        Image bgImg = bgIcon.getImage().getScaledInstance(1540, 840, Image.SCALE_SMOOTH);
        JLabel bgLabel = new JLabel(new ImageIcon(bgImg));
        bgLabel.setBounds(0, 0, 1540, 840);
        bgLabel.setLayout(null);
        add(bgLabel);

        // ===== TITLE =====
        JLabel title = new JLabel("Social Media Analyzer");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 64));
        title.setBounds(20, 60, 900, 100);
        bgLabel.add(title);

        // ===== LEFT TEXT =====
        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(new Font("Arial", Font.BOLD, 72));
        welcome.setForeground(Color.WHITE);
        welcome.setBounds(150, 250, 800, 80);
        bgLabel.add(welcome);

        JLabel sub = new JLabel("Have a great journey ahead...");
        sub.setFont(new Font("Arial", Font.PLAIN, 22));
        sub.setForeground(Color.WHITE);
        sub.setBounds(170, 350, 400, 40);
        bgLabel.add(sub);

        // ===== LOGIN CARD =====
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(96, 42, 105, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };

        card.setBounds(720, 70, 400, 520);
        card.setLayout(null);
        card.setOpaque(false);
        bgLabel.add(card);

        // ===== USERNAME =====
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setBounds(40, 50, 150, 25);
        card.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(40, 80, 320, 40);
        usernameField.setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding inside
        usernameField.setBackground(Color.WHITE);
        card.add(usernameField);

        // ===== PASSWORD =====
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setBounds(40, 140, 150, 25);
        card.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(40, 170, 320, 40);
        passwordField.setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding inside
        passwordField.setBackground(Color.WHITE);
        card.add(passwordField);

        // ===== 2D LOGIN BUTTON =====
        login = new JButton("SIGN IN");
        login.setBounds(40, 260, 320, 45);

        // 2D Styling
        login.setBackground(Color.WHITE);
        login.setForeground(new Color(96, 42, 105));
        login.setFont(new Font("Arial", Font.BOLD, 16));
        login.setFocusPainted(false);   // Remove focus ring
        login.setBorderPainted(false); // Remove 3D border
        login.setOpaque(true);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand pointer on hover

        login.addActionListener(this);
        card.add(login);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    // ===== LOGIN LOGIC =====
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == login) {

            String name = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter all fields");
                return;
            }

            // Using PreparedStatement for better security (2D logic, modern security!)
            String query = "SELECT * FROM login WHERE name=? AND password=?";

            try {
                Connection con = DriverManager.getConnection(url, "root", "n@bIa123");
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    this.dispose(); // Use dispose() instead of setVisible(false) to free memory
                    new Dashboard(); // Changed to Capital 'D'
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password");
                }

                con.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error");
            }
        }
    }
}