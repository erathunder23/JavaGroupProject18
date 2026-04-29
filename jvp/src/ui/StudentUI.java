package ui;

import dao.*;
import model.*;
import utils.LogoUtil;
import utils.SessionManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class StudentUI extends JFrame {

    // ── DAOs ──────────────────────────────────────────────────────────────────
    private final StudentDAO        studentDAO    = new StudentDAO();
    private final CourseDAO         courseDAO     = new CourseDAO();
    private final MarkDAO           markDAO       = new MarkDAO();
    private final AttendanceDAO     attendanceDAO = new AttendanceDAO();
    private final NoticeDAO         noticeDAO     = new NoticeDAO();
    private final MedicalDAO        medicalDAO    = new MedicalDAO();
    private final TimetableDAO      timetableDAO  = new TimetableDAO();
    private final CourseMaterialDAO materialDAO   = new CourseMaterialDAO();

    private Student currentStudent;
    private JLabel  avatarLabel;

    // ── Blue palette ──────────────────────────────────────────────────────────
    private static final Color BD   = new Color(13,  71, 161);   // dark navy
    private static final Color BM   = new Color(25, 118, 210);   // mid blue (buttons)
    private static final Color BL   = new Color(66, 165, 245);   // light blue
    private static final Color BP   = new Color(227, 242, 253);  // pale blue bg
    private static final Color BROW = new Color(213, 232, 252);  // table alt row
    private static final Color BBR  = new Color(144, 202, 249);  // border
    private static final Color BHV  = new Color(187, 222, 251);  // hover
    private static final Color W    = Color.WHITE;
    private static final Color TXT  = new Color(13,  71, 161);   // text blue
    private static final Color BODY = new Color(30,  30,  30);   // body text

    // ═════════════════════════════════════════════════════════════════════════
    public StudentUI() {
        loadCurrentStudent();
        buildFrame();
        LogoUtil.setFrameIcon(this);
    }

    private void loadCurrentStudent() {
        int uid = SessionManager.getCurrentUser().getUserId();
        for (Student s : studentDAO.getAllStudents())
            if (s.getUserId() == uid) { currentStudent = s; return; }
    }

    // ── Frame ─────────────────────────────────────────────────────────────────
    private void buildFrame() {
        setTitle("Student Portal – Faculty of Technology");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1380, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BP);

        buildMenuBar();
        add(buildSidebar(), BorderLayout.WEST);
        add(buildTabs(),    BorderLayout.CENTER);
        add(buildStatus(),  BorderLayout.SOUTH);
    }

    // ── Menu bar ──────────────────────────────────────────────────────────────
    private void buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(BD);
        bar.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        // Profile dropdown
        JMenu pm = navMenu("👤  Profile");
        navItem(pm, "✏️  Edit Profile", e -> editProfile());
        navItem(pm, "🖼️  Change Photo",  e -> changePhoto());
        pm.addSeparator();
        navItem(pm, "🚪  Logout",         e -> logout());
        bar.add(pm);

        bar.add(Box.createHorizontalStrut(6));

        // Help button – solid visible button
        bar.add(navBtn("❓  Help",  e -> showHelp()));
        bar.add(Box.createHorizontalStrut(4));
        bar.add(navBtn("ℹ️  About", e -> showAbout()));

        bar.add(Box.createHorizontalGlue());

        // Name badge
        JLabel badge = new JLabel("  " + currentStudent.getFullName()
                + "   Sem " + currentStudent.getCurrentSemester() + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setForeground(BD);
        badge.setBackground(BHV);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BL, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        bar.add(badge);
        bar.add(Box.createHorizontalStrut(6));
        setJMenuBar(bar);
    }

    /** Solid blue button on the nav bar */
    private JButton navBtn(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BL : BM);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(BL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                g2.setColor(W);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setBorderPainted(false); b.setOpaque(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(
            new JButton(text).getPreferredSize().width + 28, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        return b;
    }

    private JMenu navMenu(String t) {
        JMenu m = new JMenu(t);
        m.setForeground(W);
        m.setFont(new Font("Arial", Font.BOLD, 13));
        return m;
    }
    private void navItem(JMenu m, String t, java.awt.event.ActionListener al) {
        JMenuItem i = new JMenuItem(t);
        i.setBackground(BD); i.setForeground(W);
        i.setFont(new Font("Arial", Font.BOLD, 12));
        i.addActionListener(al);
        m.add(i);
    }

    // ── Sidebar (no Edit/Logout buttons) ──────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(215, 0));
        p.setBackground(W);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, BBR),
            BorderFactory.createEmptyBorder(14, 10, 10, 10)));

        avatarLabel = new JLabel();
        avatarLabel.setAlignmentX(CENTER_ALIGNMENT);
        avatarLabel.setMaximumSize(new Dimension(92, 92));
        refreshAvatar();

        JButton photoBtn = sideBtn("📷  Change Photo");
        photoBtn.addActionListener(e -> changePhoto());

        p.add(avatarLabel);
        p.add(vg(5));
        p.add(photoBtn);
        p.add(vg(10));
        p.add(sdiv());
        p.add(vg(8));

        srow(p, "Name",     currentStudent.getFullName());
        srow(p, "Reg No",   currentStudent.getRegistrationNo());
        srow(p, "Batch",    String.valueOf(currentStudent.getBatchYear()));
        srow(p, "Semester", String.valueOf(currentStudent.getCurrentSemester()));
        srow(p, "Email",    nv(currentStudent.getEmail()));
        srow(p, "Phone",    nv(currentStudent.getPhone()));

        p.add(vg(8));
        p.add(sdiv());
        p.add(vg(8));

        // GPA badge
        double sgpa = markDAO.calculateSGPA(
                currentStudent.getStudentId(), currentStudent.getCurrentSemester());
        JPanel badge = new JPanel(new GridLayout(2, 1, 0, 2));
        badge.setBackground(BP);
        badge.setMaximumSize(new Dimension(192, 52));
        badge.setAlignmentX(CENTER_ALIGNMENT);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BM, 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        JLabel gv = new JLabel("GPA  " + String.format("%.2f", sgpa), SwingConstants.CENTER);
        gv.setFont(new Font("Arial", Font.BOLD, 15));
        gv.setForeground(BD);
        JLabel gs = new JLabel(sgpa >= 2.0 ? "✔ Eligible" : "⚠ Below Requirement",
                SwingConstants.CENTER);
        gs.setFont(new Font("Arial", Font.PLAIN, 10));
        gs.setForeground(sgpa >= 2.0 ? BM : new Color(183, 28, 28));
        badge.add(gv); badge.add(gs);
        p.add(badge);
        p.add(vg(10));
        p.add(Box.createVerticalGlue());
        return p;
    }

    private void srow(JPanel p, String lbl, String val) {
        JPanel row = new JPanel(new BorderLayout(3, 0));
        row.setBackground(W);
        row.setMaximumSize(new Dimension(192, 17));
        JLabel l = new JLabel(lbl + ":");
        l.setFont(new Font("Arial", Font.BOLD, 10));
        l.setForeground(BM);
        l.setPreferredSize(new Dimension(58, 15));
        String display = val != null && val.length() > 22 ? val.substring(0, 19) + "…" : val;
        JLabel v = new JLabel(display);
        v.setFont(new Font("Arial", Font.PLAIN, 10));
        v.setForeground(BODY);
        v.setToolTipText(val);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);
        p.add(row); p.add(vg(3));
    }

    private JButton sideBtn(String t) {
        JButton b = new JButton(t) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BD : BM);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(W);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setBorderPainted(false); b.setOpaque(false);
        b.setFont(new Font("Arial", Font.BOLD, 11));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(192, 28));
        b.setPreferredSize(new Dimension(188, 28));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JSeparator sdiv() {
        JSeparator s = new JSeparator(); s.setMaximumSize(new Dimension(192, 1));
        s.setForeground(BBR); return s;
    }
    private Component vg(int h) { return Box.createVerticalStrut(h); }

    // ── Tabs ──────────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane t = new JTabbedPane(JTabbedPane.TOP);
        t.setBackground(BP);
        t.setFont(new Font("Arial", Font.BOLD, 12));
        t.addTab("📖  Courses",    tabCourses());
        t.addTab("📚  Materials",  tabMaterials());
        t.addTab("📋  Attendance", tabAttendance());
        t.addTab("📊  Marks",      tabMarks());
        t.addTab("🏥  Medical",    tabMedical());
        t.addTab("📅  Timetable",  tabTimetable());
        t.addTab("📢  Notices",    tabNotices());
        return t;
    }

    private JPanel buildStatus() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BD);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        JLabel l = new JLabel("  Student: " + currentStudent.getFullName()
                + "   |   Reg: " + currentStudent.getRegistrationNo()
                + "   |   Semester: " + currentStudent.getCurrentSemester());
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(BHV);
        bar.add(l, BorderLayout.WEST);
        return bar;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB – COURSES
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel tabCourses() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(W);
        p.add(hdr("📖  Enrolled Courses – Semester " + currentStudent.getCurrentSemester()),
              BorderLayout.NORTH);

        String[] cols = {"Code","Course Name","Credits","Theory Cr.","Practical Cr.","Lecturer"};
        DefaultTableModel mdl = nem(cols);
        JTable tbl = tbl(mdl);
        int tot = 0;
        for (Course c : courseDAO.getEnrolledCourses(currentStudent.getStudentId())) {
            mdl.addRow(new Object[]{c.getCourseCode(), c.getCourseName(),
                c.getCredits(), c.getTheoryCredits(), c.getPracticalCredits(), nv(c.getLecturerName())});
            tot += c.getCredits();
        }
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        foot.setBackground(BP);
        foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BBR));
        JLabel tl = new JLabel("Total Credits: " + tot);
        tl.setFont(new Font("Arial", Font.BOLD, 13)); tl.setForeground(BD);
        foot.add(tl);
        p.add(sc(tbl), BorderLayout.CENTER);
        p.add(foot,    BorderLayout.SOUTH);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB – MATERIALS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel tabMaterials() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(W);
        p.add(hdr("📚  Lecture Materials"), BorderLayout.NORTH);

        JPanel fb = fbar();
        JComboBox<String> cb = combo(360);
        JButton vb  = btn("🔍  View");
        JButton vab = btn("📦  All");
        JButton dlb = btn("📥  Download");
        fb.add(lbl("Course:")); fb.add(cb);
        fb.add(vb); fb.add(vab); fb.add(dlb);

        List<Course> courses = courseDAO.getEnrolledCourses(currentStudent.getStudentId());
        for (Course c : courses)
            cb.addItem(c.getCourseId() + " – " + c.getCourseCode() + " – " + c.getCourseName());

        String[] cols = {"#","Title","Description","File Name","Size","Uploaded","Lecturer"};
        DefaultTableModel mdl = nem(cols);
        JTable t = tbl(mdl);
        t.getColumnModel().getColumn(0).setMaxWidth(40);
        t.getColumnModel().getColumn(1).setPreferredWidth(180);
        t.getColumnModel().getColumn(3).setPreferredWidth(160);
        t.getColumnModel().getColumn(4).setMaxWidth(80);
        List<String> paths = new ArrayList<>();

        Runnable one = () -> {
            if (cb.getSelectedIndex() < 0) return;
            int cid = Integer.parseInt(((String) cb.getSelectedItem()).split(" – ")[0]);
            mdl.setRowCount(0); paths.clear();
            List<CourseMaterial> ms = materialDAO.getMaterialsByCourse(cid);
            if (ms.isEmpty()) { mdl.addRow(new Object[]{"–","No materials yet","–","–","–","–","–"}); return; }
            int n = 1;
            for (CourseMaterial m : ms) {
                mdl.addRow(new Object[]{n++,m.getTitle(),nv(m.getDescription()),
                    m.getFileName(),m.getFormattedFileSize(),m.getUploadedDate(),nv(m.getLecturerName())});
                paths.add(m.getFilePath());
            }
        };
        Runnable all = () -> {
            mdl.setRowCount(0); paths.clear(); int n = 1;
            for (Course c : courses)
                for (CourseMaterial m : materialDAO.getMaterialsByCourse(c.getCourseId())) {
                    mdl.addRow(new Object[]{n++,m.getTitle(),nv(m.getDescription()),
                        m.getFileName(),m.getFormattedFileSize(),m.getUploadedDate(),nv(m.getLecturerName())});
                    paths.add(m.getFilePath());
                }
            if (n==1) mdl.addRow(new Object[]{"–","No materials found","–","–","–","–","–"});
        };

        vb.addActionListener(e -> one.run());
        vab.addActionListener(e -> all.run());
        dlb.addActionListener(e -> {
            int r = t.getSelectedRow();
            if (r >= 0 && r < paths.size()) saveFile(paths.get(r),(String)mdl.getValueAt(r,3));
            else JOptionPane.showMessageDialog(this,"Select a row first.");
        });
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount()==2) { int r = t.getSelectedRow();
                    if (r>=0&&r<paths.size()) saveFile(paths.get(r),(String)mdl.getValueAt(r,3)); }
            }
        });
        if (!courses.isEmpty()) one.run();

        JPanel c = new JPanel(new BorderLayout());
        c.add(fb,          BorderLayout.NORTH);
        c.add(sc(t),       BorderLayout.CENTER);
        c.add(hint("💡  Double-click any row to download the file."), BorderLayout.SOUTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB – ATTENDANCE
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel tabAttendance() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(W);
        root.add(hdr("📋  Attendance Tracker"), BorderLayout.NORTH);

        JPanel fb = fbar();
        JComboBox<String> cb = combo(350);
        JComboBox<String> tb = new JComboBox<>(new String[]{"all","theory","practical"});
        styleCombo(tb);
        JButton vb = btn("🔍  View"); JButton rb = btn("🔄  Refresh");
        fb.add(lbl("Course:")); fb.add(cb);
        fb.add(lbl("Session:")); fb.add(tb);
        fb.add(vb); fb.add(rb);

        List<Course> courses = courseDAO.getEnrolledCourses(currentStudent.getStudentId());
        for (Course c : courses)
            cb.addItem(c.getCourseId()+" – "+c.getCourseCode()+" – "+c.getCourseName());

        // stat cards
        JPanel cards = new JPanel(new GridLayout(1,4,12,0));
        cards.setBackground(W);
        cards.setBorder(BorderFactory.createEmptyBorder(12,14,8,14));
        JLabel[] cv = new JLabel[4];
        String[] ct = {"Total Sessions","Present","Absent","Attendance %"};
        for (int i=0;i<4;i++) {
            cv[i] = new JLabel("—", SwingConstants.CENTER);
            cv[i].setFont(new Font("Arial",Font.BOLD,26));
            cv[i].setForeground(BD);
            cards.add(statCard(ct[i],cv[i]));
        }

        // progress
        JProgressBar prog = new JProgressBar(0,100);
        prog.setStringPainted(true);
        prog.setString("Select a course and click View");
        prog.setFont(new Font("Arial",Font.BOLD,11));
        prog.setForeground(BM); prog.setBackground(BP);
        prog.setPreferredSize(new Dimension(0,22));

        JLabel pp = new JLabel("  —%  ");
        pp.setFont(new Font("Arial",Font.BOLD,14)); pp.setForeground(BD);

        JPanel pr = new JPanel(new BorderLayout(8,0));
        pr.setBackground(W); pr.setBorder(BorderFactory.createEmptyBorder(2,14,4,14));
        pr.add(lbl("Progress:"),BorderLayout.WEST);
        pr.add(prog,BorderLayout.CENTER);
        pr.add(pp,BorderLayout.EAST);

        JLabel sm = new JLabel(" ",SwingConstants.CENTER);
        sm.setFont(new Font("Arial",Font.BOLD,12));
        sm.setForeground(BD); sm.setBorder(BorderFactory.createEmptyBorder(2,14,6,14));

        // table – NO medical column
        String[] cols = {"#","Date","Day","Session Type","Session No.","Status"};
        DefaultTableModel mdl = nem(cols);
        JTable t = tbl(mdl);
        t.setRowHeight(30);
        t.getColumnModel().getColumn(0).setMaxWidth(45);
        t.getColumnModel().getColumn(1).setPreferredWidth(110);
        t.getColumnModel().getColumn(2).setPreferredWidth(80);
        t.getColumnModel().getColumn(3).setPreferredWidth(110);
        t.getColumnModel().getColumn(4).setPreferredWidth(100);
        t.getColumnModel().getColumn(5).setPreferredWidth(200);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tt,Object v,
                    boolean sel,boolean foc,int r,int c) {
                super.getTableCellRendererComponent(tt,v,sel,foc,r,c);
                if (!sel) {
                    String s = tt.getModel().getValueAt(r,5)!=null ? tt.getModel().getValueAt(r,5).toString():"";
                    if      (s.contains("Medical")) setBackground(BHV);
                    else if (s.contains("Present")) setBackground(new Color(225,245,254));
                    else if (s.contains("Absent"))  setBackground(new Color(255,235,238));
                    else                            setBackground(r%2==0?W:BROW);
                }
                setBorder(BorderFactory.createEmptyBorder(0,7,0,7));
                setFont(new Font("Arial",Font.PLAIN,12));
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BBR,1),"  Session Records",
            TitledBorder.LEFT,TitledBorder.TOP,new Font("Arial",Font.BOLD,12),BD));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS)); top.setBackground(W);
        top.add(fb); top.add(cards); top.add(pr); top.add(sm);

        JPanel tp = new JPanel(new BorderLayout());
        tp.setBackground(W); tp.setBorder(BorderFactory.createEmptyBorder(0,14,10,14));
        tp.add(sp,BorderLayout.CENTER);
        tp.add(hint("  💡  Approved medical leave sessions are counted as Present automatically."),BorderLayout.SOUTH);

        root.add(top,BorderLayout.NORTH);
        root.add(tp, BorderLayout.CENTER);

        Runnable load = () -> {
            if (cb.getSelectedIndex()<0) return;
            int cid = Integer.parseInt(((String)cb.getSelectedItem()).split(" – ")[0]);
            String tp2 = (String)tb.getSelectedItem();
            double pct = attendanceDAO.getAttendancePercentageWithMedical(
                    currentStudent.getStudentId(),cid,tp2);
            List<Attendance> al = attendanceDAO.getStudentCourseAttendance(
                    currentStudent.getStudentId(),cid);
            Set<String> meds = new HashSet<>();
            for (Medical m : medicalDAO.getStudentMedicals(currentStudent.getStudentId()))
                if ("approved".equals(m.getStatus())) meds.add(m.getMedicalDate().toString());

            int tot=0,prs=0,abs=0;
            for (Attendance a : al) {
                if (!tp2.equals("all")&&!a.getSessionType().equals(tp2)) continue;
                tot++;
                boolean med = meds.contains(a.getSessionDate().toString());
                if ("present".equals(a.getStatus())||med) prs++; else abs++;
            }
            cv[0].setText(String.valueOf(tot));
            cv[1].setText(String.valueOf(prs));
            cv[2].setText(String.valueOf(abs));
            cv[3].setText(String.format("%.1f%%",pct));

            int ip = (int)Math.round(pct);
            prog.setValue(ip);
            prog.setString(String.format("%.2f%%  attendance",pct));
            pp.setText("  "+ip+"%  ");

            if (pct>=80) { sm.setText("✔  Good standing – above 80%"); sm.setForeground(BM); }
            else {
                int need=(int)Math.ceil(0.8*tot)-prs;
                sm.setText("⚠  Below 80% – need "+Math.max(0,need)+" more sessions");
                sm.setForeground(new Color(183,28,28));
            }

            mdl.setRowCount(0);
            String[] dn={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
            int n=1;
            for (Attendance a : al) {
                if (!tp2.equals("all")&&!a.getSessionType().equals(tp2)) continue;
                boolean med = meds.contains(a.getSessionDate().toString());
                String d = "present".equals(a.getStatus()) ? "✔  Present"
                         : med ? "🏥  Present (Medical)" : "✖  Absent";
                Calendar cal=Calendar.getInstance(); cal.setTime(a.getSessionDate());
                String dy = dn[cal.get(Calendar.DAY_OF_WEEK)-1];
                String ty = a.getSessionType(); ty=ty.substring(0,1).toUpperCase()+ty.substring(1);
                mdl.addRow(new Object[]{n++,a.getSessionDate(),dy,ty,"No. "+a.getSessionNumber(),d});
            }
            if (mdl.getRowCount()==0)
                mdl.addRow(new Object[]{"–","–","–","–","No records found","–"});
        };
        vb.addActionListener(e->load.run());
        rb.addActionListener(e->load.run());
        return root;
    }

    private JPanel statCard(String title,JLabel val) {
        JPanel c=new JPanel(new BorderLayout(0,4));
        c.setBackground(W);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BBR,2),
            BorderFactory.createEmptyBorder(10,8,10,8)));
        JPanel top=new JPanel(); top.setBackground(BM); top.setPreferredSize(new Dimension(0,5));
        JLabel t=new JLabel(title,SwingConstants.CENTER);
        t.setFont(new Font("Arial",Font.BOLD,11)); t.setForeground(BM);
        c.add(top,BorderLayout.NORTH); c.add(val,BorderLayout.CENTER); c.add(t,BorderLayout.SOUTH);
        return c;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB – MARKS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel tabMarks() {
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(W);
        root.add(hdr("📊  Academic Performance Report"),BorderLayout.NORTH);

        double sgpa=markDAO.calculateSGPA(currentStudent.getStudentId(),currentStudent.getCurrentSemester());

        JPanel strip=new JPanel(new FlowLayout(FlowLayout.LEFT,16,8));
        strip.setBackground(BP);
        strip.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BBR));
        strip.add(chip("Student",  currentStudent.getFullName()));
        strip.add(chip("Reg No",   currentStudent.getRegistrationNo()));
        strip.add(chip("Semester", "0"+currentStudent.getCurrentSemester()));
        strip.add(chip("SGPA",     String.format("%.2f",sgpa)));
        strip.add(chip("Status",   sgpa>=2.0?"PASS":"FAIL"));

        String[] cols={"#","Course","Type","Obtained","Max","Percentage","Grade","Grade Pts"};
        DefaultTableModel mdl=nem(cols);
        JTable t=tbl(mdl);
        t.setRowHeight(30);
        t.getColumnModel().getColumn(0).setMaxWidth(40);
        for (int i=3;i<8;i++) t.getColumnModel().getColumn(i).setPreferredWidth(95);

        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tt,Object v,
                    boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(tt,v,sel,foc,r,c);
                setHorizontalAlignment(c>=3?CENTER:LEFT);
                if (!sel) setBackground(r%2==0?W:BROW);
                setBorder(BorderFactory.createEmptyBorder(0,7,0,7));
                setFont(new Font("Arial",Font.PLAIN,12));
                return this;
            }
        });

        List<Mark> marks=markDAO.getStudentAllMarks(currentStudent.getStudentId());
        int n=1;
        for (Mark m:marks) {
            double pct=m.getMaxMarks()>0?m.getMarksObtained()/m.getMaxMarks()*100:0;
            mdl.addRow(new Object[]{n++,m.getCourseCode(),m.getExamType(),
                String.format("%.1f",m.getMarksObtained()),
                String.format("%.0f",m.getMaxMarks()),
                String.format("%.1f%%",pct),nv(m.getGrade()),
                String.format("%.2f",m.getGradePoints())});
        }

        JPanel bot=new JPanel(new FlowLayout(FlowLayout.RIGHT,16,8));
        bot.setBackground(BP); bot.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BBR));
        JLabel sl=new JLabel(sgpa>=2.0?"✔  Eligible to continue":"⚠  Academic improvement required");
        sl.setFont(new Font("Arial",Font.PLAIN,12));
        sl.setForeground(sgpa>=2.0?BM:new Color(183,28,28));
        JLabel sv=new JLabel("SGPA:  "+String.format("%.2f",sgpa));
        sv.setFont(new Font("Arial",Font.BOLD,15)); sv.setForeground(BD);
        bot.add(sl); bot.add(sv);

        JPanel c=new JPanel(new BorderLayout());
        c.add(strip, BorderLayout.NORTH);
        c.add(sc(t), BorderLayout.CENTER);
        c.add(bot,   BorderLayout.SOUTH);
        root.add(c,BorderLayout.CENTER);
        return root;
    }

    private JPanel chip(String label,String value){
        JPanel c=new JPanel(new GridLayout(2,1,0,1));
        c.setBackground(BP);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BBR),
            BorderFactory.createEmptyBorder(4,12,4,12)));
        JLabel l=new JLabel(label,SwingConstants.CENTER);
        l.setFont(new Font("Arial",Font.PLAIN,9)); l.setForeground(BM);
        JLabel v=new JLabel(value,SwingConstants.CENTER);
        v.setFont(new Font("Arial",Font.BOLD,12)); v.setForeground(BD);
        c.add(l); c.add(v); return c;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB – MEDICAL
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel tabMedical() {
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(W);
        root.add(hdr("🏥  Medical Leave Records"),BorderLayout.NORTH);

        JPanel fb=fbar();
        JButton ab=btn("➕  Submit Medical Leave");
        JButton rb=btn("🔄  Refresh");
        fb.add(ab); fb.add(rb);

        String[] cols={"ID","Course","Session","Start Date","End Date","Reason","Status","Submitted"};
        DefaultTableModel mdl=nem(cols);
        JTable t=tbl(mdl);
        t.setRowHeight(30);
        t.getColumnModel().getColumn(0).setMaxWidth(45);
        t.getColumnModel().getColumn(1).setPreferredWidth(110);
        t.getColumnModel().getColumn(2).setPreferredWidth(90);
        t.getColumnModel().getColumn(3).setPreferredWidth(110);
        t.getColumnModel().getColumn(4).setPreferredWidth(110);
        t.getColumnModel().getColumn(5).setPreferredWidth(250);
        t.getColumnModel().getColumn(6).setPreferredWidth(120);

        Runnable load=()->{
            mdl.setRowCount(0);
            for (Medical m:medicalDAO.getStudentMedicals(currentStudent.getStudentId())){
                String st="pending".equals(m.getStatus())?"⏳ Pending"
                    :"approved".equals(m.getStatus())?"✔ Approved":"✖ Rejected";
                mdl.addRow(new Object[]{
                    m.getMedicalId(),nv(m.getCourseCode()),nv(m.getSessionType()),
                    m.getMedicalDate(),m.getEndDate()!=null?m.getEndDate():"–",
                    m.getReason(),st,m.getSubmissionDate()});
            }
        };
        load.run();

        ab.addActionListener(e->showMedDlg(load));
        rb.addActionListener(e->load.run());

        JPanel c=new JPanel(new BorderLayout());
        c.add(fb,   BorderLayout.NORTH);
        c.add(sc(t),BorderLayout.CENTER);
        root.add(c,BorderLayout.CENTER);
        return root;
    }

    private void showMedDlg(Runnable onSaved) {
        JDialog dlg=new JDialog(this,"Submit Medical Leave",true);
        dlg.setSize(520,545); dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel tb=new JPanel(new BorderLayout());
        tb.setBackground(BD); tb.setBorder(BorderFactory.createEmptyBorder(10,14,10,14));
        JLabel tl=new JLabel("🏥  New Medical Leave Request");
        tl.setFont(new Font("Arial",Font.BOLD,15)); tl.setForeground(W);
        tb.add(tl,BorderLayout.WEST); dlg.add(tb,BorderLayout.NORTH);

        JPanel form=new JPanel(new GridBagLayout());
        form.setBackground(W);
        form.setBorder(BorderFactory.createEmptyBorder(18,22,14,22));
        GridBagConstraints gc=new GridBagConstraints();
        gc.insets=new Insets(7,5,7,5);
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.anchor=GridBagConstraints.WEST;

        List<Course> courses=courseDAO.getEnrolledCourses(currentStudent.getStudentId());
        JComboBox<String> courseBox=new JComboBox<>();
        for (Course c:courses)
            courseBox.addItem(c.getCourseId()+" – "+c.getCourseCode()+" – "+c.getCourseName());
        styleCombo(courseBox);

        JComboBox<String> sessBox=new JComboBox<>(new String[]{"theory","practical","both"});
        styleCombo(sessBox);

        String today=java.time.LocalDate.now().toString();
        JTextField startFld=tf(today);
        JTextField endFld=tf(today);

        JTextArea reasonArea=new JTextArea(3,24);
        reasonArea.setLineWrap(true); reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(new Font("Arial",Font.PLAIN,12));
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BBR),
            BorderFactory.createEmptyBorder(3,5,3,5)));
        JScrollPane rsp=new JScrollPane(reasonArea);
        rsp.setBorder(BorderFactory.createLineBorder(BBR));

        JTextField imgFld=tf("No image selected"); imgFld.setEditable(false);
        JButton browseBtn=btn("📎  Browse");
        final File[] imgFile={null};
        browseBtn.addActionListener(e->{
            JFileChooser fc=new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Image Files","jpg","jpeg","png","gif","bmp"));
            fc.setDialogTitle("Select Medical Certificate Image");
            if (fc.showOpenDialog(dlg)==JFileChooser.APPROVE_OPTION){
                imgFile[0]=fc.getSelectedFile();
                imgFld.setText(imgFile[0].getName());
            }
        });

        int r=0;
        fr(form,gc,r++,"Course:",       courseBox);
        fr(form,gc,r++,"Session Type:", sessBox);
        fr(form,gc,r++,"Start Date:",   startFld);
        fr(form,gc,r++,"End Date:",     endFld);
        fr(form,gc,r++,"Reason:",       rsp);

        gc.gridx=0; gc.gridy=r; gc.gridwidth=1; gc.weightx=0;
        form.add(fl("Medical Image:"),gc);
        gc.gridx=1; gc.weightx=1;
        JPanel imgRow=new JPanel(new BorderLayout(6,0));
        imgRow.setBackground(W);
        imgRow.add(imgFld,BorderLayout.CENTER);
        imgRow.add(browseBtn,BorderLayout.EAST);
        form.add(imgRow,gc);
        dlg.add(form,BorderLayout.CENTER);

        JPanel bRow=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        bRow.setBackground(BP);
        bRow.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BBR));
        JButton submitBtn=btn("✔  Submit");
        JButton cancelBtn=btn("✖  Cancel");
        cancelBtn.addActionListener(e->dlg.dispose());

        submitBtn.addActionListener(e->{
            if (reasonArea.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(dlg,"Please enter a reason.","Validation",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (startFld.getText().trim().isEmpty()||endFld.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(dlg,"Please enter start and end dates (YYYY-MM-DD).","Validation",JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                java.sql.Date sd=java.sql.Date.valueOf(startFld.getText().trim());
                java.sql.Date ed=java.sql.Date.valueOf(endFld.getText().trim());
                if (ed.before(sd)){
                    JOptionPane.showMessageDialog(dlg,"End date cannot be before start date.","Validation",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Medical med=new Medical();
                med.setStudentId(currentStudent.getStudentId());
                med.setMedicalDate(sd); med.setEndDate(ed);
                med.setReason(reasonArea.getText().trim());
                med.setSessionType((String)sessBox.getSelectedItem());

                if (courseBox.getSelectedIndex()>=0){
                    String sel=(String)courseBox.getSelectedItem();
                    med.setCourseId(Integer.parseInt(sel.split(" – ")[0]));
                }

                if (imgFile[0]!=null){
                    String dir="uploads"+File.separator+"medical";
                    new File(dir).mkdirs();
                    String name=imgFile[0].getName();
                    String ext=name.substring(name.lastIndexOf('.'));
                    String dest=dir+File.separator+"med_"+currentStudent.getStudentId()
                               +"_"+System.currentTimeMillis()+ext;
                    Files.copy(imgFile[0].toPath(),Paths.get(dest),StandardCopyOption.REPLACE_EXISTING);
                    med.setDocumentPath(dest);
                }

                boolean ok=medicalDAO.submitMedical(med);
                if (ok){
                    JOptionPane.showMessageDialog(dlg,
                        "Medical leave submitted successfully!\nStatus: Pending review.",
                        "Submitted",JOptionPane.INFORMATION_MESSAGE);
                    onSaved.run(); dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg,
                        "Submission failed.\n\n"
                        +"Please run the following SQL in MySQL Workbench first:\n\n"
                        +"  ALTER TABLE medicals ADD COLUMN IF NOT EXISTS end_date DATE;\n"
                        +"  ALTER TABLE medicals ADD COLUMN IF NOT EXISTS course_id INT;\n"
                        +"  ALTER TABLE medicals ADD COLUMN IF NOT EXISTS session_type VARCHAR(20);\n\n"
                        +"Or simply re-import student_schema.sql.",
                        "Database Error",JOptionPane.ERROR_MESSAGE);
                }
            } catch (java.time.format.DateTimeParseException ex){
                JOptionPane.showMessageDialog(dlg,"Invalid date. Use YYYY-MM-DD (e.g. 2024-03-15).","Date Error",JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex){
                JOptionPane.showMessageDialog(dlg,"Unexpected error:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        bRow.add(cancelBtn); bRow.add(submitBtn);
        dlg.add(bRow,BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB – TIMETABLE
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel tabTimetable() {
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(W);
        root.add(hdr("📅  Weekly Timetable – Semester "+currentStudent.getCurrentSemester()),BorderLayout.NORTH);
        String[] cols={"Day","Start","End","Course Code","Session Type","Venue"};
        DefaultTableModel mdl=nem(cols);
        JTable t=tbl(mdl); t.setRowHeight(34);
        List<Timetable> tt=timetableDAO.getTimetableBySemester(currentStudent.getCurrentSemester());
        for (String day:new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"}){
            boolean any=false;
            for (Timetable x:tt) if (x.getDayOfWeek().equals(day)){
                String st=x.getSessionType();
                mdl.addRow(new Object[]{day,x.getStartTime(),x.getEndTime(),
                    x.getCourseCode(),st.substring(0,1).toUpperCase()+st.substring(1),x.getVenue()});
                any=true;
            }
            if (!any) mdl.addRow(new Object[]{day,"–","–","No Class","–","–"});
        }
        root.add(sc(t),BorderLayout.CENTER);
        return root;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB – NOTICES  (fixed file-not-found message)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel tabNotices() {
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(W);
        root.add(hdr("📢  Notices & Announcements"),BorderLayout.NORTH);

        JPanel fb=fbar();
        JButton rb=btn("🔄  Refresh");
        JButton vb=btn("👁  View Details");
        JButton db=btn("📥  Download Attachment");
        fb.add(rb); fb.add(vb); fb.add(db);

        String[] cols={"#","Title","Target","Date","Attachment","Content Preview"};
        DefaultTableModel mdl=nem(cols);
        JTable t=tbl(mdl);
        t.setRowHeight(30);
        t.getColumnModel().getColumn(0).setMaxWidth(40);
        t.getColumnModel().getColumn(1).setPreferredWidth(200);
        t.getColumnModel().getColumn(2).setPreferredWidth(90);
        t.getColumnModel().getColumn(3).setPreferredWidth(140);
        t.getColumnModel().getColumn(4).setPreferredWidth(150);
        t.getColumnModel().getColumn(5).setPreferredWidth(400);

        List<Notice>[] holder=new List[]{new ArrayList<>()};

        Runnable load=()->{
            mdl.setRowCount(0);
            holder[0]=noticeDAO.getNoticesForRole("student");
            int n=1;
            for (Notice no:holder[0]){
                boolean att=no.getAttachmentPath()!=null&&!no.getAttachmentPath().isEmpty();
                String prev=no.getContent().length()>90
                    ?no.getContent().substring(0,87)+"…":no.getContent();
                mdl.addRow(new Object[]{n++,no.getTitle(),no.getTargetRole(),
                    no.getCreatedDate(),att?"📎 "+no.getAttachmentName():"–",prev});
            }
        };
        load.run();

        rb.addActionListener(e->load.run());
        vb.addActionListener(e->{
            int r=t.getSelectedRow();
            if (r<0){JOptionPane.showMessageDialog(root,"Select a notice first.");return;}
            Notice no=holder[0].get(r);
            JTextArea ta=new JTextArea(
                "NOTICE\n"+"─".repeat(55)+"\nTitle:  "+no.getTitle()+
                "\nDate:   "+no.getCreatedDate()+"\nTarget: "+no.getTargetRole()+
                "\n"+"─".repeat(55)+"\n\n"+no.getContent());
            ta.setEditable(false); ta.setFont(new Font("Arial",Font.PLAIN,13));
            ta.setLineWrap(true); ta.setWrapStyleWord(true);
            JScrollPane sp=new JScrollPane(ta); sp.setPreferredSize(new Dimension(520,280));
            JOptionPane.showMessageDialog(this,sp,"Notice – "+no.getTitle(),JOptionPane.INFORMATION_MESSAGE);
        });
        db.addActionListener(e->{
            int r=t.getSelectedRow();
            if (r<0){JOptionPane.showMessageDialog(root,"Select a notice first.");return;}
            Notice no=holder[0].get(r);
            if (no.getAttachmentPath()==null||no.getAttachmentPath().isEmpty()){
                JOptionPane.showMessageDialog(root,"This notice has no attachment.");return;
            }
            saveFile(no.getAttachmentPath(),no.getAttachmentName());
        });
        t.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                if (e.getClickCount()==2){
                    int r=t.getSelectedRow();
                    if (r<0||r>=holder[0].size()) return;
                    Notice no=holder[0].get(r);
                    if (no.getAttachmentPath()!=null&&!no.getAttachmentPath().isEmpty())
                        saveFile(no.getAttachmentPath(),no.getAttachmentName());
                }
            }
        });

        JPanel c=new JPanel(new BorderLayout());
        c.add(fb,   BorderLayout.NORTH);
        c.add(sc(t),BorderLayout.CENTER);
        root.add(c,BorderLayout.CENTER);
        return root;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PROFILE PICTURE
    // ═════════════════════════════════════════════════════════════════════════
    private void refreshAvatar() {
        String path=currentStudent.getProfilePicture();
        if (path!=null&&!path.isEmpty()){
            File f=new File(path);
            if (f.exists()){
                try {
                    BufferedImage img=ImageIO.read(f);
                    if (img!=null){avatarLabel.setIcon(new ImageIcon(circle(img,88)));return;}
                }catch(Exception ignored){}
            }
        }
        avatarLabel.setIcon(defAvatar(88));
    }

    private void changePhoto() {
        JFileChooser fc=new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Image Files","jpg","jpeg","png","gif"));
        fc.setDialogTitle("Select Profile Picture");
        if (fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        File sel=fc.getSelectedFile();
        try {
            BufferedImage img=ImageIO.read(sel);
            if (img==null){JOptionPane.showMessageDialog(this,"Cannot read image file.");return;}
            String dir="uploads"+File.separator+"profile_pictures";
            new File(dir).mkdirs();
            String name=sel.getName();
            String ext=name.substring(name.lastIndexOf('.'));
            String dest=dir+File.separator+"stu_"+currentStudent.getUserId()+"_"+System.currentTimeMillis()+ext;
            BufferedImage resized=resize(img,300);
            String fmt=ext.substring(1).equalsIgnoreCase("jpg")?"jpeg":ext.substring(1);
            ImageIO.write(resized,fmt,new File(dest));
            if (studentDAO.updateProfilePicture(currentStudent.getUserId(),dest)){
                currentStudent.setProfilePicture(dest);
                refreshAvatar(); avatarLabel.revalidate(); avatarLabel.repaint();
                JOptionPane.showMessageDialog(this,"Profile picture updated!","Updated",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,"Image saved but DB update failed.\nCheck database password.","Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private BufferedImage circle(BufferedImage src,int size){
        BufferedImage o=new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=o.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(new Ellipse2D.Double(0,0,size,size));
        g.drawImage(src.getScaledInstance(size,size,Image.SCALE_SMOOTH),0,0,null);
        g.dispose(); return o;
    }
    private BufferedImage resize(BufferedImage src,int max){
        int w=src.getWidth(),h=src.getHeight();
        int nw=w>h?max:w*max/h; int nh=h>w?max:h*max/w;
        BufferedImage o=new BufferedImage(nw,nh,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=o.createGraphics();
        g.drawImage(src.getScaledInstance(nw,nh,Image.SCALE_SMOOTH),0,0,null);
        g.dispose(); return o;
    }
    private ImageIcon defAvatar(int size){
        BufferedImage img=new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0,0,BD,size,size,BM));
        g.fill(new Ellipse2D.Double(0,0,size,size));
        g.setColor(W); g.setFont(new Font("Arial",Font.BOLD,size/3));
        String ini=initials(currentStudent.getFullName());
        FontMetrics fm=g.getFontMetrics();
        g.drawString(ini,(size-fm.stringWidth(ini))/2,(size+fm.getAscent())/2-fm.getDescent());
        g.dispose(); return new ImageIcon(img);
    }
    private String initials(String n){
        if (n==null||n.isEmpty()) return "?";
        String[] p=n.trim().split("\\s+");
        return p.length>=2?""+Character.toUpperCase(p[0].charAt(0))+Character.toUpperCase(p[1].charAt(0))
            :n.substring(0,Math.min(2,n.length())).toUpperCase();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  EDIT PROFILE / LOGOUT
    // ═════════════════════════════════════════════════════════════════════════
    private void editProfile() {
        JDialog dlg=new JDialog(this,"Edit Profile",true);
        dlg.setSize(420,235); dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        JPanel tb2=new JPanel(new BorderLayout());
        tb2.setBackground(BD); tb2.setBorder(BorderFactory.createEmptyBorder(8,14,8,14));
        JLabel tl2=new JLabel("✏️  Edit Contact Details");
        tl2.setFont(new Font("Arial",Font.BOLD,14)); tl2.setForeground(W);
        tb2.add(tl2,BorderLayout.WEST); dlg.add(tb2,BorderLayout.NORTH);
        JPanel form=new JPanel(new GridBagLayout());
        form.setBackground(W); form.setBorder(BorderFactory.createEmptyBorder(16,22,16,22));
        GridBagConstraints gc=new GridBagConstraints();
        gc.insets=new Insets(8,5,8,5); gc.fill=GridBagConstraints.HORIZONTAL;
        JTextField ef=tf(nv(currentStudent.getEmail()));
        JTextField pf=tf(nv(currentStudent.getPhone()));
        fr(form,gc,0,"Email:",ef); fr(form,gc,1,"Phone:",pf);
        dlg.add(form,BorderLayout.CENTER);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        br.setBackground(BP);
        JButton sv=btn("💾  Save"); JButton cv=btn("✖  Cancel");
        cv.addActionListener(e->dlg.dispose());
        sv.addActionListener(e->{
            currentStudent.setEmail(ef.getText().trim());
            currentStudent.setPhone(pf.getText().trim());
            if (studentDAO.updateStudent(currentStudent)){
                JOptionPane.showMessageDialog(dlg,"Profile updated successfully!");
                dlg.dispose(); dispose(); new StudentUI().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(dlg,"Update failed.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        br.add(cv); br.add(sv); dlg.add(br,BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this,"Logout?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            SessionManager.clearSession(); dispose(); new LoginUI().setVisible(true);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELP / ABOUT
    // ═════════════════════════════════════════════════════════════════════════
    private void showHelp() {
        JTextArea ta=new JTextArea(
            "STUDENT PORTAL – HELP GUIDE\n"+"─".repeat(50)+"\n\n"
            +"📖  MY COURSES\n     Shows all courses enrolled for your current semester.\n\n"
            +"📚  MATERIALS\n     Download lecture notes uploaded by lecturers.\n"
            +"     Double-click any row to download.\n\n"
            +"📋  ATTENDANCE\n     Select a course then click View.\n"
            +"     Approved medical leaves are counted as Present.\n\n"
            +"📊  MARKS & GPA\n     All marks and semester GPA.\n\n"
            +"🏥  MEDICAL\n     Submit leave: select course, session type, start/end date,\n"
            +"     reason, and optional medical image.\n"
            +"     Date format: YYYY-MM-DD  (e.g. 2024-03-15)\n\n"
            +"📅  TIMETABLE\n     Weekly class schedule for your semester.\n\n"
            +"📢  NOTICES\n     Faculty announcements. Select a row and click\n"
            +"     Download Attachment to save any attached file.\n\n"
            +"👤  PROFILE  (top menu)\n     Edit email/phone or change your profile picture.\n\n"
            +"NOTE: If files are not found, the admin/lecturer needs to\n"
            +"upload the actual files to the server.");
        ta.setEditable(false); ta.setFont(new Font("Arial",Font.PLAIN,13));
        ta.setForeground(BODY); ta.setBackground(W);
        ta.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JScrollPane sp=new JScrollPane(ta); sp.setPreferredSize(new Dimension(520,420));
        sp.setBorder(BorderFactory.createLineBorder(BBR));
        JOptionPane.showMessageDialog(this,sp,"Help – Student Portal",JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JPanel panel=new JPanel(new BorderLayout(0,10));
        panel.setBackground(W); panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JLabel title=new JLabel("Faculty of Technology Management System",SwingConstants.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,15)); title.setForeground(BD);
        JTextArea ta=new JTextArea(
            "Student Portal  v3.0\n\nDatabase: MySQL (faculty_technology_db)\n\n"
            +"Features:\n"
            +"  • Profile picture (instant update)\n"
            +"  • Enrolled courses by semester\n"
            +"  • Lecture materials download\n"
            +"  • Full attendance tracker\n"
            +"  • Marks & GPA report\n"
            +"  • Medical leave submission\n"
            +"  • Weekly timetable\n"
            +"  • Notices & attachment download\n\n"
            +"Colour theme: Blue only");
        ta.setEditable(false); ta.setFont(new Font("Arial",Font.PLAIN,12));
        ta.setForeground(BODY); ta.setBackground(BP);
        ta.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        panel.add(title,BorderLayout.NORTH); panel.add(ta,BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this,panel,"About",JOptionPane.INFORMATION_MESSAGE);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FILE SAVE – clear message when file missing
    // ═════════════════════════════════════════════════════════════════════════
    private void saveFile(String path, String name) {
        if (path==null||path.isEmpty()){
            JOptionPane.showMessageDialog(this,
                "No file path stored in the database for this item.",
                "No Attachment",JOptionPane.WARNING_MESSAGE);
            return;
        }
        File src=new File(path);
        if (!src.exists()){
            JOptionPane.showMessageDialog(this,
                "<html><b>The attachment file is not on this computer.</b><br><br>"
                +"Path stored: <i>"+path+"</i><br><br>"
                +"This means the admin/lecturer saved a notice with an attachment<br>"
                +"reference in the database, but the actual file is stored on<br>"
                +"the admin's machine, not yet copied to this server.<br><br>"
                +"<b>Solution:</b> Ask the admin to copy the file to:<br>"
                +"<code>"+new File(path).getAbsolutePath()+"</code></html>",
                "Attachment Not Available",JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            JFileChooser fc=new JFileChooser();
            fc.setSelectedFile(new File(name!=null?name:src.getName()));
            fc.setDialogTitle("Save file as");
            if (fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
                Files.copy(src.toPath(),fc.getSelectedFile().toPath(),StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this,
                    "File saved to:\n"+fc.getSelectedFile().getAbsolutePath(),
                    "Saved",JOptionPane.INFORMATION_MESSAGE);
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Save failed:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SHARED UI HELPERS  (all blue)
    // ═════════════════════════════════════════════════════════════════════════
    /** Tab section header – dark blue bar with white title */
    private JPanel hdr(String text) {
        JPanel p=new JPanel(new BorderLayout());
        p.setBackground(BD); p.setBorder(BorderFactory.createEmptyBorder(11,14,11,14));
        JLabel l=new JLabel(text);
        l.setFont(new Font("Arial",Font.BOLD,16)); l.setForeground(W);
        p.add(l,BorderLayout.WEST); return p;
    }

    /** Filter/button bar below header */
    private JPanel fbar() {
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        p.setBackground(BP); p.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BBR));
        return p;
    }

    /** Blue label used in filter bars and forms */
    private JLabel lbl(String text) {
        JLabel l=new JLabel(text);
        l.setFont(new Font("Arial",Font.BOLD,12)); l.setForeground(BD);
        return l;
    }

    /** Bold blue form label */
    private JLabel fl(String text) {
        JLabel l=new JLabel(text);
        l.setFont(new Font("Arial",Font.BOLD,12)); l.setForeground(BD);
        return l;
    }

    /** Blue button – uses BasicButtonUI so Windows/system L&F cannot override colours */
    private JButton btn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // fill background
                g2.setColor(getModel().isPressed() ? BD
                          : getModel().isRollover() ? BD : BM);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                // border
                g2.setColor(BD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                // text
                g2.setColor(W);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) { /* handled above */ }
        };
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setForeground(W);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(
            new JButton(text).getPreferredSize().width + 24, 32));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Styled combo box */
    private JComboBox<String> combo(int w) {
        JComboBox<String> cb=new JComboBox<>();
        styleCombo(cb); cb.setPreferredSize(new Dimension(w,28)); return cb;
    }
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(W); cb.setForeground(BD);
        cb.setFont(new Font("Arial",Font.PLAIN,12));
        cb.setBorder(BorderFactory.createLineBorder(BBR));
    }

    /** Styled text field */
    private JTextField tf(String text) {
        JTextField f=new JTextField(text);
        f.setFont(new Font("Arial",Font.PLAIN,12)); f.setForeground(BD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BBR),
            BorderFactory.createEmptyBorder(3,6,3,6)));
        return f;
    }

    /** Blue-styled table */
    private JTable tbl(DefaultTableModel mdl) {
        JTable t=new JTable(mdl);
        t.setFont(new Font("Arial",Font.PLAIN,12));
        t.setRowHeight(28); t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));
        t.setSelectionBackground(BHV); t.setSelectionForeground(BD);
        t.setFillsViewportHeight(true); t.setBackground(W);
        JTableHeader h=t.getTableHeader();
        h.setBackground(BD); h.setForeground(W);
        h.setFont(new Font("Arial",Font.BOLD,12));
        h.setPreferredSize(new Dimension(0,32)); h.setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tt,Object v,
                    boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(tt,v,sel,foc,r,c);
                if (!sel) setBackground(r%2==0?W:BROW);
                setBorder(BorderFactory.createEmptyBorder(0,7,0,7));
                setForeground(BODY); setFont(new Font("Arial",Font.PLAIN,12));
                return this;
            }
        });
        return t;
    }

    private DefaultTableModel nem(String[] cols) {
        return new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
    }
    private JScrollPane sc(JTable t) {
        JScrollPane sp=new JScrollPane(t); sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(W); return sp;
    }
    private JLabel hint(String text) {
        JLabel l=new JLabel(text);
        l.setFont(new Font("Arial",Font.ITALIC,11)); l.setForeground(BM);
        l.setBackground(BP); l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(4,8,4,8)); return l;
    }
    private void fr(JPanel form,GridBagConstraints gc,int row,String label,JComponent comp){
        gc.gridx=0;gc.gridy=row;gc.gridwidth=1;gc.weightx=0; form.add(fl(label),gc);
        gc.gridx=1;gc.weightx=1; form.add(comp,gc);
    }
    private String nv(String s){return s!=null&&!s.isEmpty()?s:"–";}
}
