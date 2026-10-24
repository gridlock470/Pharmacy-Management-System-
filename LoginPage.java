import java.awt.*;
import java.awt.event.*;

/*
 * Demo credentials:
 *   Patient : patient  / patient123
 *   Manager : manager  / manager123
 */
public class LoginPage extends Frame {

    // Demo credentials (will be replaced by a proper user store later)
    private static final String PATIENT_USER = "patient";
    private static final String PATIENT_PASS = "patient123";
    private static final String MANAGER_USER = "manager";
    private static final String MANAGER_PASS = "manager123";

    private static final Color BG = new Color(240, 247, 250);
    private static final Color PATIENT_COLOR = new Color(46, 139, 87);
    private static final Color MANAGER_COLOR = new Color(30, 90, 160);

    private CardLayout cards = new CardLayout();
    private Panel container = new Panel(cards);

    private Label roleTitle = new Label("", Label.CENTER);
    private Label message = new Label("", Label.CENTER);
    private TextField userField = new TextField(18);
    private TextField passField = new TextField(18);

    private String currentRole = "";

    public LoginPage() {
        super("Pharmacy Management System");
        setSize(460, 420);
        setResizable(false);
        setLocationRelativeTo(null); // centre on screen
        setBackground(BG);

        passField.setEchoChar('*');

        container.add(buildHomePanel(), "home");
        container.add(buildLoginPanel(), "login");
        add(container);

        // AWT needs this, otherwise the window cannot be closed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }

    // ---------- Home screen: choose Patient or Manager ----------
    private Panel buildHomePanel() {
        Panel p = new Panel(new GridBagLayout());
        p.setBackground(BG);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 40, 10, 40);

        Label title = new Label("PHARMACY", Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(20, 70, 100));
        c.gridy = 0;
        p.add(title, c);

        Label subtitle = new Label("Management System", Label.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        c.gridy = 1;
        c.insets = new Insets(0, 40, 30, 40);
        p.add(subtitle, c);

        Button patientBtn = makeButton("Patient Login", PATIENT_COLOR);
        patientBtn.addActionListener(e -> showLogin("Patient"));
        c.gridy = 2;
        c.insets = new Insets(10, 40, 10, 40);
        p.add(patientBtn, c);

        Button managerBtn = makeButton("Manager Login", MANAGER_COLOR);
        managerBtn.addActionListener(e -> showLogin("Manager"));
        c.gridy = 3;
        p.add(managerBtn, c);

        return p;
    }

    // ---------- Login form (shared by both roles) ----------
    private Panel buildLoginPanel() {
        Panel p = new Panel(new GridBagLayout());
        p.setBackground(BG);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);

        roleTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(roleTitle, c);

        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;

        c.gridx = 0;
        c.gridy = 1;
        p.add(new Label("Username:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        p.add(userField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;
        p.add(new Label("Password:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        p.add(passField, c);

        // Buttons row
        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttons.setBackground(BG);
        Button loginBtn = makeButton("Login", new Color(60, 60, 60));
        Button backBtn = new Button("Back");
        buttons.add(loginBtn);
        buttons.add(backBtn);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 8, 10);
        p.add(buttons, c);

        // Status / error message
        message.setForeground(Color.RED);
        c.gridy = 4;
        c.insets = new Insets(8, 10, 8, 10);
        p.add(message, c);

        // Actions
        loginBtn.addActionListener(e -> attemptLogin());
        passField.addActionListener(e -> attemptLogin()); // Enter key
        backBtn.addActionListener(e -> cards.show(container, "home"));

        return p;
    }

    private Button makeButton(String text, Color bg) {
        Button b = new Button(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        return b;
    }

    // ---------- Logic ----------
    private void showLogin(String role) {
        currentRole = role;
        roleTitle.setText(role + " Login");
        roleTitle.setForeground(role.equals("Patient") ? PATIENT_COLOR : MANAGER_COLOR);
        userField.setText("");
        passField.setText("");
        message.setText("");
        cards.show(container, "login");
        userField.requestFocus();
    }

    // Called by the dashboards when the user logs out
    public void showHome() {
        cards.show(container, "home");
        setVisible(true);
    }

    private void attemptLogin() {
        String user = userField.getText().trim();
        String pass = passField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            message.setText("Please enter both username and password.");
            return;
        }

        boolean valid;
        if (currentRole.equals("Patient")) {
            valid = user.equals(PATIENT_USER) && pass.equals(PATIENT_PASS);
        } else {
            valid = user.equals(MANAGER_USER) && pass.equals(MANAGER_PASS);
        }

        if (valid) {
            if (currentRole.equals("Manager")) {
                setVisible(false);
                new ManagerDashboard(this, user);
            } else {
                openDashboard(currentRole, user);
            }
        } else {
            message.setText("Invalid username or password.");
            passField.setText("");
        }
    }

    // Placeholder dashboard for the Patient - the real screen comes in a later step
    private void openDashboard(String role, String user) {
        setVisible(false);

        Frame dash = new Frame(role + " Dashboard");
        dash.setSize(460, 320);
        dash.setLocationRelativeTo(null);
        dash.setBackground(BG);
        dash.setLayout(new BorderLayout(10, 10));

        Label welcome = new Label("Welcome, " + user + "!", Label.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        Label note = new Label(role + " dashboard coming in the next step.", Label.CENTER);
        Panel center = new Panel(new GridLayout(2, 1));
        center.add(welcome);
        center.add(note);
        dash.add(center, BorderLayout.CENTER);

        Button logout = new Button("Logout");
        Panel south = new Panel(new FlowLayout());
        south.add(logout);
        dash.add(south, BorderLayout.SOUTH);

        // Logout (button or closing the window) returns to the login page
        logout.addActionListener(e -> {
            dash.dispose();
            showHome();
        });
        dash.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dash.dispose();
                showHome();
            }
        });

        dash.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginPage().setVisible(true);
    }
}