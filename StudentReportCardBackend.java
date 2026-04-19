import java.util.Arrays;

// --- Abstraction ---
abstract class StudentProcessor {
    public abstract String calculateGrade(double marks);
    public abstract double getGradePoint(String grade);
}

// --- Inheritance ---
public class ReportCardBackend extends StudentProcessor {

    /**
     * Calculate Marks for subjects by given Evaluation Criteria 
     */
    public double calculateFinalMarks(String courseCode, double q1, double q2, double q3, double assign, double proj, double midT, double midP, double endT, double endP) {

        // Calculate average of best two quizes(Best 2 Average)
        double[] quizzes = {q1, q2, q3};
        Arrays.sort(quizzes);
        double quizAvg = (quizzes[1] + quizzes[2]) / 2;

        switch (courseCode) {
            case "ICT2113":
                // Quizzes: 10%, Midterm: 20%, Assignment: 10%, Final Theory: 40%, Final Practical: 20%
                return (quizAvg * 0.10) + (midT * 0.20) + (assign * 0.10) + (endT * 0.40) + (endP * 0.20);

            case "ICT2122":
                // Quizzes: 10%, Mid Term Theory: 20%, Final Theory: 70%
                return (quizAvg * 0.10) + (midT * 0.20) + (endT * 0.70);

            case "ICT2142":
                // Quizzes: 10%, Assessment: 20%, Final Theory: 70%
                return (quizAvg * 0.10) + (assign * 0.20) + (endT * 0.70);

            case "ICT2152":
                // Mini Project: 30%, Mid Term: 10%, Final Theory: 60%
                return (proj * 0.30) + (midT * 0.10) + (endT * 0.60);

            case "TCS2112":
            case "TCS2121":
            case "ENG2112":
                // CA: 30% (Quiz 10% + Assign 20%), ESA: 70% (Final Theory)
                return (quizAvg * 0.10) + (assign * 0.20) + (endT * 0.70);

            case "ICT2132":
                // Common method because of projects and practicals (can change after give the percentages)
                return (quizAvg * 0.10) + (proj * 0.20) + (midT * 0.10) + (midP * 0.10) + (endT * 0.30) + (endP * 0.20);

            default:
                //Common method for other subjects
                return (quizAvg * 0.10) + (assign * 0.20) + (endT * 0.70);
        }
    }

    // --- Polymorphism (Method Overriding) ---
    @Override
    public String calculateGrade(double finalMarks) {
        if (finalMarks >= 85) return "A+";
        if (finalMarks >= 70) return "A";
        if (finalMarks >= 65) return "A-";
        if (finalMarks >= 60) return "B+";
        if (finalMarks >= 55) return "B";
        if (finalMarks >= 50) return "B-";
        if (finalMarks >= 45) return "C+";
        if (finalMarks >= 40) return "C";
        if (finalMarks >= 35) return "C-";
        if (finalMarks >= 30) return "D+";
        if (finalMarks >= 25) return "D";
        return "E";
    }

    @Override
    public double getGradePoint(String grade) {
        switch (grade) {
            case "A+": case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            default: return 0.0;
        }
    }
}

