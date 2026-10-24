import java.util.ArrayList;

public class Inventory {

    public static ArrayList<Medicine> medicines = new ArrayList<>();

    static {
        medicines.add(new Medicine("Paracetamol", 20.00, 100));
        medicines.add(new Medicine("Amoxicillin", 85.50, 50));
        medicines.add(new Medicine("Cetirizine", 30.00, 75));
        medicines.add(new Medicine("Ibuprofen", 45.00, 60));
        medicines.add(new Medicine("Azithromycin", 120.00, 40));
        medicines.add(new Medicine("Pantoprazole", 65.75, 55));
        medicines.add(new Medicine("Vitamin C", 15.00, 200));
        medicines.add(new Medicine("Cough Syrup", 95.00, 15));
    }
}