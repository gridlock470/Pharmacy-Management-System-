import java.awt.*;
import java.awt.event.*;

public class ManagerDashboard extends Frame {

    private static final Color BG = new Color(240, 247, 250);
    private static final Color BLUE = new Color(30, 90, 160);
    private static final int LOW_STOCK = 20;

    private LoginPage login;

    private List medicineList = new List(12);
    private TextField nameField = new TextField(15);
    private TextField priceField = new TextField(6);
    private TextField qtyField = new TextField(5);
    private Label summary = new Label("", Label.CENTER);
    private Label message = new Label("", Label.CENTER);

    public ManagerDashboard(LoginPage login, String user) {
        super("Manager Dashboard");
        this.login = login;

        setSize(720, 560);
        setLocationRelativeTo(null);
        setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        // ---------- Top: title, summary, logout ----------
        Label title = new Label("Manager Dashboard - Welcome, " + user, Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(BLUE);

        Button logoutBtn = new Button("Logout");

        Panel top = new Panel(new BorderLayout());
        top.add(title, BorderLayout.CENTER);
        top.add(logoutBtn, BorderLayout.EAST);
        top.add(summary, BorderLayout.SOUTH);

        // ---------- Centre: medicine list ----------
        Label header = new Label(String.format("%-22s %10s %8s", "Medicine", "Price(Rs)", "Stock"));
        header.setFont(new Font("Monospaced", Font.BOLD, 13));
        medicineList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        medicineList.setBackground(Color.WHITE);

        Panel center = new Panel(new BorderLayout());
        center.add(header, BorderLayout.NORTH);
        center.add(medicineList, BorderLayout.CENTER);

        // ---------- Bottom: input fields, buttons, message ----------
        Panel fields = new Panel(new FlowLayout());
        fields.add(new Label("Name:"));
        fields.add(nameField);
        fields.add(new Label("Price:"));
        fields.add(priceField);
        fields.add(new Label("Quantity:"));
        fields.add(qtyField);

        Button addBtn = new Button("Add Medicine");
        Button updateBtn = new Button("Update Selected");
        Button deleteBtn = new Button("Delete Selected");
        Button clearBtn = new Button("Clear");

        Panel buttons = new Panel(new FlowLayout());
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        message.setFont(new Font("SansSerif", Font.BOLD, 13));

        Panel bottom = new Panel(new GridLayout(3, 1));
        bottom.add(fields);
        bottom.add(buttons);
        bottom.add(message);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        add(new Label("    "), BorderLayout.WEST);   // side spacing
        add(new Label("    "), BorderLayout.EAST);

        // ---------- Actions ----------
        addBtn.addActionListener(e -> addMedicine());
        updateBtn.addActionListener(e -> updateMedicine());
        deleteBtn.addActionListener(e -> deleteMedicine());
        clearBtn.addActionListener(e -> clearFields());
        logoutBtn.addActionListener(e -> logout());
        medicineList.addItemListener(e -> fillFields()); // click a row to edit it

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        refreshList();
        setVisible(true);
    }

    // ---------- List display ----------
    private void refreshList() {
        medicineList.removeAll();
        int low = 0;
        for (Medicine m : Inventory.medicines) {
            String row = String.format("%-22s %10.2f %8d", m.name, m.price, m.stock);
            if (m.stock < LOW_STOCK) {
                row += "   LOW";
                low++;
            }
            medicineList.add(row);
        }
        summary.setText("Total medicines: " + Inventory.medicines.size()
                + "   |   Low stock (below " + LOW_STOCK + "): " + low);
    }

    // Copy the selected medicine into the input fields
    private void fillFields() {
        int i = medicineList.getSelectedIndex();
        if (i >= 0) {
            Medicine m = Inventory.medicines.get(i);
            nameField.setText(m.name);
            priceField.setText(String.valueOf(m.price));
            qtyField.setText(String.valueOf(m.stock));
            message.setText("");
        }
    }

    // ---------- Add / Update / Delete ----------
    private void addMedicine() {
        Medicine m = readInput();
        if (m == null) {
            return;
        }
        if (findIndex(m.name) != -1) {
            showError(m.name + " already exists. Select it and use Update.");
            return;
        }
        Inventory.medicines.add(m);
        refreshList();
        clearFields();
        showSuccess(m.name + " added.");
    }

    private void updateMedicine() {
        int index = medicineList.getSelectedIndex();
        if (index < 0) {
            showError("Select a medicine from the list first.");
            return;
        }
        Medicine m = readInput();
        if (m == null) {
            return;
        }
        int found = findIndex(m.name);
        if (found != -1 && found != index) {
            showError("Another medicine already has this name.");
            return;
        }
        Inventory.medicines.set(index, m);
        refreshList();
        medicineList.select(index);
        showSuccess(m.name + " updated.");
    }

    private void deleteMedicine() {
        int index = medicineList.getSelectedIndex();
        if (index < 0) {
            showError("Select a medicine from the list first.");
            return;
        }
        Medicine removed = Inventory.medicines.remove(index);
        refreshList();
        clearFields();
        showSuccess(removed.name + " deleted.");
    }

    // ---------- Helpers ----------
    // Reads the three input fields; shows an error and returns null if invalid
    private Medicine readInput() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter the medicine name.");
            return null;
        }
        double price;
        int qty;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            qty = Integer.parseInt(qtyField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Price and quantity must be valid numbers.");
            return null;
        }
        if (price < 0 || qty < 0) {
            showError("Price and quantity cannot be negative.");
            return null;
        }
        return new Medicine(name, price, qty);
    }

    // Position of a medicine by name (ignoring case), or -1 if not found
    private int findIndex(String name) {
        for (int i = 0; i < Inventory.medicines.size(); i++) {
            if (Inventory.medicines.get(i).name.equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        qtyField.setText("");
        int i = medicineList.getSelectedIndex();
        if (i >= 0) {
            medicineList.deselect(i);
        }
    }

    private void showError(String text) {
        message.setForeground(Color.RED);
        message.setText(text);
    }

    private void showSuccess(String text) {
        message.setForeground(new Color(0, 130, 0));
        message.setText(text);
    }

    private void logout() {
        dispose();
        login.showHome();
    }
}