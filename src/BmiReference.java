
public class BmiReference {
    public static double calculate(double weight, double height, boolean isMetric) {
        if (isMetric) {
            return weight / (height * height);
        } else {
            return (weight / (height * height)) * 703;
        }
    }

    public static String getStatus(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }
}
