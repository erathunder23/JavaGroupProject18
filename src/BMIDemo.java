import javax.swing.SwingUtilities;

public class BMIDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BMICalculatorFrame());
    }
}
