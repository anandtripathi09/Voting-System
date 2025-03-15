import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.Timer;

public class VotingSystem {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Anand's Voting System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        boolean isMobile = screenSize.width < 600;

        if (isMobile) {
            frame.setSize(400, 700);
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        JPanel borderedPanel = new JPanel(new GridBagLayout());
        borderedPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        borderedPanel.setBackground(new Color(50, 50, 50));
        borderedPanel.setPreferredSize(new Dimension(600, isMobile ? 550 : 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Anand's Voting System", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, isMobile ? 24 : 30));
        titleLabel.setForeground(Color.WHITE);
        borderedPanel.add(titleLabel, gbc);
        animateTitle(titleLabel);

        JTextField nameField = createLabeledTextField(borderedPanel, "📝 Enter Your Name:", gbc);
        JPanel votePanel = createVoteSelectionPanel();
        borderedPanel.add(votePanel, gbc);
        JTextArea reasonField = createLabeledTextArea(borderedPanel, "📝 Reason for Your Choice:", gbc);

        JButton submitButton = new JButton("Submit Your Vote");
        submitButton.setBackground(new Color(75, 0, 130));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, isMobile ? 16 : 18));
        addHoverEffect(submitButton);
        borderedPanel.add(submitButton, gbc);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(new Color(30, 30, 30));
        wrapperPanel.add(borderedPanel);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        if (isMobile) {
            frame.add(scrollPane, BorderLayout.CENTER);
        } else {
            frame.add(wrapperPanel, BorderLayout.CENTER);
        }

        submitButton.addActionListener(e -> handleVoteSubmission(nameField, votePanel, reasonField));

        frame.setVisible(true);
    }

    private static JTextField createLabeledTextField(JPanel panel, String labelText, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Noto Color Emoji", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(textField, gbc);
        return textField;
    }

    private static JTextArea createLabeledTextArea(JPanel panel, String labelText, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Noto Color Emoji", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        JTextArea textArea = new JTextArea(3, 30);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, gbc);
        return textArea;
    }

    private static JPanel createVoteSelectionPanel() {
        JPanel votePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        votePanel.setBackground(new Color(50, 50, 50));

        JRadioButton yesButton = new JRadioButton("✅ Yes");
        JRadioButton noButton = new JRadioButton("❌ No");
        ButtonGroup group = new ButtonGroup();
        group.add(yesButton);
        group.add(noButton);
        votePanel.add(yesButton);
        votePanel.add(noButton);

        yesButton.setFont(new Font("Noto Color Emoji", Font.PLAIN, 16));
        noButton.setFont(new Font("Noto Color Emoji", Font.PLAIN, 16));
        yesButton.setForeground(new Color(0, 128, 0));
        noButton.setForeground(new Color(204, 0, 0));
        yesButton.setBackground(new Color(230, 255, 230));
        noButton.setBackground(new Color(255, 230, 230));

        return votePanel;
    }

    private static void handleVoteSubmission(JTextField nameField, JPanel votePanel, JTextArea reasonField) {
        String name = nameField.getText().trim();
        String reason = reasonField.getText().trim();
        String response = ((JRadioButton) votePanel.getComponents()[0]).isSelected() ? "Yes" : "No";

        if (name.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠️ Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        saveVote(name, response, reason);
        nameField.setText("");
        reasonField.setText("");
        JOptionPane.showMessageDialog(null, "🎉 Vote submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void animateTitle(JLabel label) {
        Timer timer = new Timer(500, e -> label.setForeground(label.getForeground() == Color.YELLOW ? Color.WHITE : Color.YELLOW));
        timer.start();
    }

    private static void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(139, 0, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 0, 130));
            }
        });
    }

    private static void saveVote(String name, String response, String reason) {
        try (Connection connection = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
            String query = "INSERT INTO Votes (name, response, reason) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, name);
                statement.setString(2, response);
                statement.setString(3, reason);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "⚠️ Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
