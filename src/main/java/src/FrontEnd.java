package src;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FrontEnd extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://localhost/3dface";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Window size and position constants

    private static final int WINDOW_WIDTH = 1366;
    private static final int WINDOW_HEIGHT = 730;
    private static final int WINDOW_X = 0;
    private static final int WINDOW_Y = 0;
    public static final JFrame frame = new JFrame("FaceSecure 3D: Intelligent Facial Recognition");

    // Login panel components

    private final JPanel loginPanel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton cancelButton;
    private final JButton registerButton;

    // Registration panel components

    private final JPanel registrationPanel;
    private final JTextField regUsernameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JPasswordField regPasswordField;
    private final JPasswordField confirmPasswordField;
    private final JButton registerSubmitButton;
    private final JButton regCancelButton;

    // Card layout to switch between panels

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    public FrontEnd() {
        
        // Use card layout for switching between login and registration
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize panels

        loginPanel = new JPanel();
        registrationPanel = new JPanel();

        // Initialize login components

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        registerButton = new JButton("Click Here!");

        // Initialize registration components

        regUsernameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        regPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        registerSubmitButton = new JButton("Register");
        regCancelButton = new JButton("Cancel");

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create main container

        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Setup login panel

        setupModernLoginPanel();

        // Setup registration panel

        setupModernRegistrationPanel();

        // Add panels to card layout

        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(registrationPanel, "REGISTER");

        mainContainer.add(cardPanel);
        add(mainContainer);

        // Show login panel initially

        cardLayout.show(cardPanel, "LOGIN");

        // Add action listeners

        addActionListeners();
    }

    private void setupModernLoginPanel() {
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));
        loginPanel.setPreferredSize(new Dimension(500, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Face Recognition System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));
        loginPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Secure Login", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        loginPanel.add(subtitleLabel, gbc);

        gbc.gridy++;
        loginPanel.add(Box.createVerticalStrut(40), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.BLACK);
        loginPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Color.BLACK);
        loginPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 10, 15);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 100, 255)); // Bright Blue
        loginButton.setForeground(Color.BLUE);
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 80, 200), 2),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        loginPanel.add(buttonPanel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 15, 5, 15);
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);

        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerLabel.setForeground(Color.DARK_GRAY);

        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setForeground(new Color(41, 128, 185));
        registerButton.setBackground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerPanel.add(registerLabel);
        registerPanel.add(registerButton);
        loginPanel.add(registerPanel, gbc);
    }

    private void setupModernRegistrationPanel() {
        registrationPanel.setLayout(new GridBagLayout());
        registrationPanel.setBackground(Color.WHITE);
        registrationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        registrationPanel.setPreferredSize(new Dimension(550, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        registrationPanel.add(titleLabel, gbc);

        gbc.gridy++;
        registrationPanel.add(Box.createVerticalStrut(30), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.BLACK);
        registrationPanel.add(userLabel, gbc);

        regUsernameField.setPreferredSize(new Dimension(280, 40));
        regUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regUsernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        registrationPanel.add(regUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(Color.BLACK);
        registrationPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField.setPreferredSize(new Dimension(280, 40));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        registrationPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        phoneLabel.setForeground(Color.BLACK);
        registrationPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField.setPreferredSize(new Dimension(280, 40));
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        registrationPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Color.BLACK);
        registrationPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        regPasswordField.setPreferredSize(new Dimension(280, 40));
        regPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        registrationPanel.add(regPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmPassLabel.setForeground(Color.BLACK);
        registrationPanel.add(confirmPassLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField.setPreferredSize(new Dimension(280, 40));
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        registrationPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 10, 15);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        registerSubmitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerSubmitButton.setBackground(new Color(0, 150, 0)); // Bright Green
        registerSubmitButton.setForeground(Color.GREEN);
        registerSubmitButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 0), 2),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)));
        registerSubmitButton.setFocusPainted(false);
        registerSubmitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        regCancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        regCancelButton.setBackground(Color.LIGHT_GRAY);
        regCancelButton.setForeground(Color.BLACK);
        regCancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)));
        regCancelButton.setFocusPainted(false);
        regCancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(registerSubmitButton);
        buttonPanel.add(regCancelButton);
        registrationPanel.add(buttonPanel, gbc);
    }

    private void addActionListeners() {
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);
        registerButton.addActionListener(this);
        registerSubmitButton.addActionListener(this);
        regCancelButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == loginButton) {
            handleLogin();
        } else if (source == cancelButton) {
            System.exit(0);
        } else if (source == registerButton) {
            switchToRegistration();
        } else if (source == registerSubmitButton) {
            handleRegistration();
        } else if (source == regCancelButton) {
            switchToLogin();
        }
    }

    //Handles the login button click event
    //Validates input and attempts to authenticate the user
     
    private void handleLogin() {
        System.out.println("Login button clicked");

        if (!validateLoginInput()) {
            return;
        }

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            if (tryDatabaseLogin(username, password)) {
                return;
            }
            tryFileLogin(username, password);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Login failed: " + ex.getMessage());
        }
    }

    //Validates the login form input fields
    //Checks that required fields are not empty

    private boolean validateLoginInput() {
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter Username!");
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(frame, "Please enter Password!");
            return false;
        }
        return true;
    }

    //Attempts to authenticate the user against the database

    private boolean tryDatabaseLogin(String username, String password) throws SQLException {
        String hashedPassword = hashPassword(password);
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT UserName, Password FROM users WHERE UserName = ? AND Password = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, username);
                pst.setString(2, hashedPassword);
                try (ResultSet res = pst.executeQuery()) {
                    if (res.next()) {
                        launchMainApplication();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database login failed, trying file-based login: " + e.getMessage());
        }
        return false;
    }

    //Attempts to authenticate the user against the local file

    private void tryFileLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2 && username.equals(parts[0]) && password.equals(parts[1])) {
                    launchMainApplication();
                    return;
                }
            }
            JOptionPane.showMessageDialog(frame, "Invalid username or password!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Login failed: " + e.getMessage());
        }
    }

    //Launches the main application window
    //Creates and displays the face recognition interface
     
    private void launchMainApplication() {
        JFrame j2 = new JFrame("FaceSecure 3D: Intelligent Facial Recognition");
        Main mainPanel = new Main();
        j2.add(mainPanel);
        j2.setLocation(0, 0);
        j2.setSize(1366, 730);
        j2.setVisible(true);
        frame.dispose();

        j2.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int reply = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        j2.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        j2.setVisible(true);
    }

    //Switches the display from login to registration panel
    private void switchToRegistration() {
        System.out.println("Switching to registration");
        cardLayout.show(cardPanel, "REGISTER");
        clearRegistrationFields();
    }

    //Switches the display from registration to login panel
    private void switchToLogin() {
        System.out.println("Switching to login");
        cardLayout.show(cardPanel, "LOGIN");
        clearLoginFields();
    }

    //Clears all registration form fields
    private void clearRegistrationFields() {
        regUsernameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        regPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    //Clears all login form fields
    private void clearLoginFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    //Handles the registration button click event.
    //Validates input and attempts to register the new user.

    private void handleRegistration() {
        System.out.println("Register button clicked");

        if (!validateRegistrationInput()) {
            return;
        }

        String username = regUsernameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = new String(regPasswordField.getPassword());

        try {
            if (tryDatabaseRegistration(username, email, phone, password)) {
                return;
            }
            tryFileRegistration(username, password);
        } catch (Exception ex) {
            handleRegistrationError(ex);
        }
    }

    //Validates the registration form input fields
    //Checks that all required fields are filled and properly formatted
    
    private boolean validateRegistrationInput() {
        if (regUsernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter Username!");
            return false;
        }
        if (regUsernameField.getText().length() > 30) {
            JOptionPane.showMessageDialog(frame, "Maximum 30 characters allowed for Username!");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter Email!");
            return false;
        }
        if (!isValidEmail(emailField.getText())) {
            JOptionPane.showMessageDialog(frame, "Invalid Email!");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter Phone!");
            return false;
        }
        if (!isValidPhone(phoneField.getText())) {
            JOptionPane.showMessageDialog(frame, "Invalid Phone!");
            return false;
        }
        if (regPasswordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(frame, "Please enter Password!");
            return false;
        }
        if (regPasswordField.getPassword().length < 3 || regPasswordField.getPassword().length > 15) {
            JOptionPane.showMessageDialog(frame,
                    "Password should be minimum 3 characters and maximum 15 characters!");
            return false;
        }
        if (confirmPasswordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(frame, "Please confirm password!");
            return false;
        }
        if (!new String(regPasswordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
            JOptionPane.showMessageDialog(frame, "Password Mismatch: Check again!");
            return false;
        }
        return true;
    }

    //Validates an email address format
    
    private boolean isValidEmail(String email) {
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+" +
                "(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }

    //Validates a phone number format
     
    private boolean isValidPhone(String phone) {
        try {
            long p = 9999999999L;
            long r = 1000000000L;
            long q = Long.parseLong(phone, 10);
            return q <= p && q >= r;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //Attempts to register a new user in the database

    private boolean tryDatabaseRegistration(String username, String email,
            String phone, String password) throws SQLException {
        String hashedPassword = hashPassword(password);
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Check if email already exists
            String query = "SELECT Email FROM users WHERE Email = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, email);
                try (ResultSet res = pst.executeQuery()) {
                    if (res.next()) {
                        JOptionPane.showMessageDialog(frame, "Email already registered!");
                        return true;
                    }
                }
            }

            // Insert new user
            String insertQuery = "INSERT INTO users (UserName, Email, Phone, Password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
                pst.setString(1, username);
                pst.setString(2, email);
                pst.setString(3, phone);
                pst.setString(4, hashedPassword);
                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame, "Registration successful!");
            switchToLogin();
            return true;
        } catch (SQLException e) {
            System.out.println("Database registration failed, trying file-based: " + e.getMessage());
            return false;
        }
    }

    //Attempts to register a new user in the local file
    
    private void tryFileRegistration(String username, String password) {
        try (PrintWriter writer = new PrintWriter("users.txt", "UTF-8")) {
            writer.println(username + " " + password);
            JOptionPane.showMessageDialog(frame, "Registration successful!");
            switchToLogin();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Registration failed: " + e.getMessage());
        }
    }

    //Handles registration errors by displaying an error message

    private void handleRegistrationError(Exception ex) {
        JOptionPane.showMessageDialog(frame, "Registration failed: " + ex.getMessage());
    }

    //Hashes a password using SHA-256 algorithm

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    //Creates and shows the main application GUI
    //Sets up the main frame and displays the login interface
    
    private static void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JComponent newContentPane = new FrontEnd();
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);
        frame.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.setLocation(WINDOW_X, WINDOW_Y);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    //Main entry point for the application
    //Launches the login interface
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            createAndShowGUI();
        });
    }
}