import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BMICalculatorFrame extends JFrame {
    private JRadioButton metricBtn, englishBtn;
    private JTextField weightField, heightField, bmiResultField, statusField;
    private JLabel weightLabel, heightLabel;

    public BMICalculatorFrame() {
        setTitle("BMI CALCULATOR");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1, 10, 10));

        setupUnitPanel();
        setupInputPanel();
        setupButton();
        setupResultPanel();
        setupGuide();

        setVisible(true);
    }

    private void setupUnitPanel() {
        JPanel unitPanel = new JPanel();
        unitPanel.setBorder(BorderFactory.createTitledBorder("UNIT SELECTION"));
        metricBtn = new JRadioButton("Metric (kg/m)", true);
        englishBtn = new JRadioButton("English (lb/in)");

        ButtonGroup unitGroup = new ButtonGroup();
        unitGroup.add(metricBtn);
        unitGroup.add(englishBtn);

        unitPanel.add(metricBtn);
        unitPanel.add(englishBtn);

        ActionListener unitListener = e -> {
            weightLabel.setText(metricBtn.isSelected() ? "Weight (kg):" : "Weight (lbs):");
            heightLabel.setText(metricBtn.isSelected() ? "Height (m):" : "Height (inches):");
        };
        metricBtn.addActionListener(unitListener);
        englishBtn.addActionListener(unitListener);

        add(unitPanel);
    }

    private void setupInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        weightLabel = new JLabel("Weight (kg):");
        weightField = new JTextField();
        heightLabel = new JLabel("Height (m):");
        heightField = new JTextField();

        inputPanel.add(weightLabel);
        inputPanel.add(weightField);
        inputPanel.add(heightLabel);
        inputPanel.add(heightField);
        add(inputPanel);
    }

    private void setupButton() {
        JButton calcBtn = new JButton("Calculate BMI");
        calcBtn.addActionListener(e -> performCalculation());
        add(calcBtn);
    }

    private void setupResultPanel() {
        JPanel resultPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        bmiResultField = new JTextField();
        bmiResultField.setEditable(false);
        statusField = new JTextField();
        statusField.setEditable(false);

        resultPanel.add(new JLabel("Your BMI:"));
        resultPanel.add(bmiResultField);
        resultPanel.add(new JLabel("Status:"));
        resultPanel.add(statusField);
        add(resultPanel);
    }

    private void setupGuide() {
        JTextArea guide = new JTextArea(
                "REFERENCE GUIDE (NIH Standards):\n" +
                        "• Underweight: < 18.5\n" +
                        "• Normal: 18.5 - 24.9\n" +
                        "• Overweight: 25.0 - 29.9\n" +
                        "• Obese: 30.0 or greater"
        );
        guide.setEditable(false);
        guide.setBackground(getBackground());
        add(guide);
    }

    private void performCalculation() {
        try {
            double w = Double.parseDouble(weightField.getText());
            double h = Double.parseDouble(heightField.getText());

            double bmi = BmiReference.calculate(w, h, metricBtn.isSelected());

            bmiResultField.setText(String.format("%.2f", bmi));
            statusField.setText(BmiReference.getStatus(bmi));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        }
    }
}
