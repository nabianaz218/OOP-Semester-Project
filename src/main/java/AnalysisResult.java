package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.*;

public class AnalysisResult extends JFrame {
    String selectedUser, metricType;
    JTextArea dataArea;

    // Database Credentials
    final String url = "jdbc:mysql://localhost:3306/datab";
    final String db_user = "root";
    final String db_pass = "n@bIa123";

    public AnalysisResult(String user, String metric) {
        this.selectedUser = user;
        this.metricType = metric;

        this.setTitle("Social Analysis: " + user);
        this.setSize(1540, 840);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(null);

        // Main Background
        JPanel bg = new JPanel();
        bg.setBounds(0, 0, 1540, 840);
        bg.setBackground(Color.WHITE);
        bg.setLayout(null);
        this.add(bg);

        // --- SIDEBAR SETUP ---
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(181, 52, 224));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            }
        };
        // Height adjusted to 680 to fit standard screens
        sidebar.setBounds(20, 12, 280, 680);
        sidebar.setLayout(null);
        sidebar.setOpaque(false);
        bg.add(sidebar);

        // --- IMAGE: STRETCHED VERTICALLY ---
        JLabel imageLabel = new JLabel();
        URL imageUrl = getClass().getClassLoader().getResource("image_9.png");

        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            // Scaled to 580 height to leave room for the button below
            Image scaledImage = icon.getImage().getScaledInstance(280, 580, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setBounds(0, 0, 280, 580);
        } else {
            imageLabel.setText("Social Media Graph");
            imageLabel.setForeground(Color.WHITE);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setBounds(0, 0, 280, 580);
        }
        sidebar.add(imageLabel);

        // --- BACK BUTTON ---
        // Positioned at 610 (below the image but inside the 680 sidebar)
        JButton back = createStyledButton("Back to Home", 610);
        back.addActionListener(e -> this.dispose());
        sidebar.add(back);

        // --- CONTENT AREA ---
        JPanel content = new JPanel();
        content.setBounds(320, 20, 1180, 760);
        content.setBackground(new Color(245, 245, 245));
        content.setLayout(null);
        bg.add(content);

        JLabel head = new JLabel("User Interaction Report");
        head.setFont(new Font("Arial", Font.BOLD, 35));
        head.setBounds(50, 30, 800, 50);
        content.add(head);

        dataArea = new JTextArea();
        dataArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        dataArea.setEditable(false);
        dataArea.setOpaque(false);

        JScrollPane scroll = new JScrollPane(dataArea);
        scroll.setBounds(50, 110, 1080, 600);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(181, 52, 224), 2));
        content.add(scroll);

        fetchStatistics();
        this.setVisible(true);
    }

    private void fetchStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("   FULL REPORT FOR: ").append(selectedUser.toUpperCase()).append("\n");
        sb.append("   ").append("=".repeat(65)).append("\n\n");

        try (Connection con = DriverManager.getConnection(url, db_user, db_pass)) {
            String query = "";
            switch (metricType) {
                case "All Info":
                    query = "SELECT 'Summary', " +
                            "(SELECT COUNT(*) FROM posts p JOIN users u ON p.user_id = u.user_id WHERE u.username = ?) as posts, " +
                            "(SELECT COUNT(*) FROM followers f JOIN users u ON f.following_id = u.user_id WHERE u.username = ?) as followers, " +
                            "(SELECT COUNT(*) FROM likes l JOIN posts p ON l.post_id = p.post_id JOIN users u_o ON p.user_id = u_o.user_id JOIN users u_l ON l.user_id = u_l.user_id WHERE u_o.username = ? AND u_l.username != ?) as likes_rcvd, " +
                            "(SELECT COUNT(*) FROM comments c JOIN posts p ON c.post_id = p.post_id JOIN users u_o ON p.user_id = u_o.user_id JOIN users u_c ON c.user_id = u_c.user_id WHERE u_o.username = ? AND u_c.username != ?) as comm_rcvd, " +
                            "(SELECT COUNT(*) FROM likes l JOIN posts p ON l.post_id = p.post_id JOIN users u_o ON p.user_id = u_o.user_id JOIN users u_l ON l.user_id = u_l.user_id WHERE u_l.username = ? AND u_o.username != ?) as likes_given, " +
                            "(SELECT COUNT(*) FROM comments c JOIN posts p ON c.post_id = p.post_id JOIN users u_o ON p.user_id = u_o.user_id JOIN users u_c ON c.user_id = u_c.user_id WHERE u_c.username = ? AND u_o.username != ?) as comm_given, " +
                            "'ALL'";
                    break;
                case "Posts":
                    query = "SELECT p.content, p.created_at, 'N/A', 'POST' FROM posts p JOIN users u ON p.user_id = u.user_id WHERE u.username = ?";
                    break;
                case "Comments":
                    query = "(SELECT p.content, u_comm.username, c.comment, 'OWN_POST' FROM posts p JOIN users u_o ON p.user_id = u_o.user_id JOIN comments c ON p.post_id = c.post_id JOIN users u_comm ON c.user_id = u_comm.user_id WHERE u_o.username = ? AND u_comm.username != ?)" +
                            " UNION " +
                            "(SELECT p.content, u_o.username, c.comment, 'OTHER_POST' FROM posts p JOIN users u_o ON p.user_id = u_o.user_id JOIN comments c ON p.post_id = c.post_id JOIN users u_comm ON c.user_id = u_comm.user_id WHERE u_comm.username = ? AND u_o.username != ?)";
                    break;
                case "Likes":
                    query = "(SELECT p.content, GROUP_CONCAT(DISTINCT u_liker.username SEPARATOR ', '), 'N/A', 'OWN_POST' FROM posts p JOIN users u_o ON p.user_id = u_o.user_id JOIN likes l ON p.post_id = l.post_id JOIN users u_liker ON l.user_id = u_liker.user_id WHERE u_o.username = ? AND u_liker.username != ? GROUP BY p.post_id)" +
                            " UNION " +
                            "(SELECT p.content, u_o.username, 'Liked by You', 'OTHER_POST' FROM posts p JOIN users u_o ON p.user_id = u_o.user_id JOIN likes l ON p.post_id = l.post_id JOIN users u_liker ON l.user_id = u_liker.user_id WHERE u_liker.username = ? AND u_o.username != ?)";
                    break;
                case "Followers":
                    query = "SELECT u_f.username, 'Active', 'N/A', 'FOLLOW' FROM followers f JOIN users u_t ON f.following_id = u_t.user_id JOIN users u_f ON f.follower_id = u_f.user_id WHERE u_t.username = ?";
                    break;
                default:
                    query = "SELECT 'Error', 'Invalid Metric', 'N/A', 'N/A'";
                    break;
            }

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, selectedUser);

            if (metricType.equals("All Info")) {
                for(int i=2; i<=10; i++) pst.setString(i, selectedUser);
            } else if (metricType.equals("Comments") || metricType.equals("Likes")) {
                pst.setString(2, selectedUser);
                pst.setString(3, selectedUser);
                pst.setString(4, selectedUser);
            }

            ResultSet rs = pst.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                if (metricType.equals("All Info")) {
                    sb.append("   [ CONTENT STATISTICS ]\n");
                    sb.append("   Total Posts Created:      ").append(rs.getString(2)).append("\n");
                    sb.append("   Total Followers:          ").append(rs.getString(3)).append("\n\n");
                    sb.append("   [ ENGAGEMENT RECEIVED ]\n");
                    sb.append("   Total Likes Received:     ").append(rs.getString(4)).append("\n");
                    sb.append("   Total Comments Received:  ").append(rs.getString(5)).append("\n\n");
                    sb.append("   [ USER ACTIVITY GIVEN ]\n");
                    sb.append("   Other Posts You Liked:    ").append(rs.getString(6)).append("\n");
                    sb.append("   Other Posts You Commented: ").append(rs.getString(7)).append("\n");
                } else {
                    String contentStr = rs.getString(1);
                    String person = rs.getString(2);
                    String tag = rs.getString(4);
                    if (metricType.equals("Posts")) {
                        sb.append("   POST: \"").append(contentStr).append("\"\n   DATE: ").append(person).append("\n");
                    } else if (metricType.equals("Likes")) {
                        if (tag.equals("OWN_POST")) {
                            sb.append("   [Your Post] \"").append(contentStr).append("\"\n   ▶ LIKED BY: ").append(person).append("\n");
                        } else {
                            sb.append("   [You Liked ").append(person).append("'s Post]\n   ▶ Post: \"").append(contentStr).append("\"\n");
                        }
                    } else if (metricType.equals("Comments")) {
                        if (tag.equals("OWN_POST")) {
                            sb.append("   [On Your Post] \"").append(contentStr).append("\"\n   ▶ ").append(person).append(" said: ").append(rs.getString(3)).append("\n");
                        } else {
                            sb.append("   [Your Comment on ").append(person).append("'s Post]\n   ▶ Msg: ").append(rs.getString(3)).append("\n");
                        }
                    } else {
                        sb.append("   ▶ ").append(contentStr).append(" follows you.\n");
                    }
                    sb.append("   ").append("-".repeat(60)).append("\n\n");
                }
            }
            dataArea.setText(found ? sb.toString() : "   No records found.");
        } catch (SQLException e) {
            dataArea.setText("   Database Error: " + e.getMessage());
        }
    }

    private JButton createStyledButton(String text, int y) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(181, 52, 224)); g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setBounds(40, y, 200, 40);
        b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}