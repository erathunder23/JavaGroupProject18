import javax.swing.*;

public class MedicalGUI extends JFrame {

    JTextField mid, sid, course, date, desc, status;
    JButton addBtn, viewBtn;
    JTable table;

    public MedicalGUI() {

        setTitle("Medical Panel");
        setSize(700, 500);
        setLayout(null);

        mid = new JTextField(); mid.setBounds(20,20,150,25);
        sid = new JTextField(); sid.setBounds(20,50,150,25);
        course = new JTextField(); course.setBounds(20,80,150,25);
        date = new JTextField(); date.setBounds(20,110,150,25);
        desc = new JTextField(); desc.setBounds(20,140,150,25);
        status = new JTextField(); status.setBounds(20,170,150,25);

        addBtn = new JButton("ADD");
        viewBtn = new JButton("VIEW");

        addBtn.setBounds(20,210,100,30);
        viewBtn.setBounds(130,210,100,30);

        table = new JTable();
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(200,20,450,400);

        add(mid); add(sid); add(course); add(date); add(desc); add(status);
        add(addBtn); add(viewBtn); add(sp);

        addBtn.addActionListener(e -> {
            try {
                Medical m = new Medical(
                        mid.getText(), sid.getText(), course.getText(),
                        date.getText(), desc.getText(), status.getText()
                );

                new MedicalDAO(m).add();
                JOptionPane.showMessageDialog(null,"Inserted");

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        viewBtn.addActionListener(e -> {
            try {
                table.setModel(MedicalDAO.view());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MedicalGUI();
    }
}