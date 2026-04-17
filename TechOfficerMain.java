import javax.swing.*;
import java.awt.event.*;

public class TechOfficerMain extends JFrame {

    JTextField sid, course, date, type, status;
    JButton addBtn, viewBtn;

    JTable table;

    public TechOfficerMain() {

        setTitle("Tech Officer Panel");
        setSize(700, 500);
        setLayout(null);

        sid = new JTextField(); sid.setBounds(20, 20, 150, 25);
        course = new JTextField(); course.setBounds(20, 50, 150, 25);
        date = new JTextField(); date.setBounds(20, 80, 150, 25);
        type = new JTextField(); type.setBounds(20, 110, 150, 25);
        status = new JTextField(); status.setBounds(20, 140, 150, 25);

        addBtn = new JButton("ADD");
        addBtn.setBounds(20, 180, 100, 30);

        viewBtn = new JButton("VIEW");
        viewBtn.setBounds(130, 180, 100, 30);

        table = new JTable();
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(200, 20, 450, 400);

        add(sid); add(course); add(date); add(type); add(status);
        add(addBtn); add(viewBtn);
        add(sp);

        addBtn.addActionListener(e -> {
            try {
                Attendance a = new Attendance(
                        sid.getText(),
                        course.getText(),
                        date.getText(),
                        type.getText(),
                        status.getText()
                );

                new AttendanceDAO(a).add();
                JOptionPane.showMessageDialog(null, "Inserted");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        viewBtn.addActionListener(e -> {
            try {
                table.setModel(AttendanceDAO.view());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new TechOfficerMain();
    }
}