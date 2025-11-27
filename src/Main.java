import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Main extends JFrame {

    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Agent> agents = new LinkedHashMap<>();
    private final Map<String, Complaint> complaints = new LinkedHashMap<>();
    private final Map<String, Feedback> feedbacks = new LinkedHashMap<>();
    private final Map<String, List<Attachment>> attachments = new HashMap<>();

    private final DefaultTableModel complaintTableModel = new DefaultTableModel(
            new Object[]{"ID","Title","Reporter","Assigned","Category","Priority","Status","Created","Resolved"}, 0);
    private final DefaultTableModel feedbackTableModel = new DefaultTableModel(
            new Object[]{"FB ID","Complaint ID","Author","Date","Comment"}, 0);
    private final DefaultTableModel agentTableModel = new DefaultTableModel(
            new Object[]{"Agent ID","Name","Email","Role"}, 0);

    private final JComboBox<String> assignCompCombo = new JComboBox<>();
    private final JComboBox<String> assignAgentCombo = new JComboBox<>();
    private final JComboBox<String> assignStatusCombo = new JComboBox<>(new String[]{"Open","In Progress","Resolved","Closed"});
    private final JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Low","Medium","High","Critical"});
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Authentication","Payments","UI","Stability","Other"});
    private final JComboBox<String> reporterSelector = new JComboBox<>();
    private final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private User currentUser = null;

    public Main() {
        setTitle("ResolveIt - GenZ University");
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        bootstrapDefaults();
        showLogin();
    }

    private void bootstrapDefaults() {
        users.put("admin", new User("admin","123","admin@uni.edu","ADMIN"));
        users.put("agent", new User("agent","123","agent@uni.edu","AGENT"));
        users.put("student", new User("student","123","student@uni.edu","STUDENT"));
        agents.put("AG01", new Agent("AG01","Neha Sharma","neha@uni.edu","Support"));
        agents.put("AG02", new Agent("AG02","Rakesh Rao","rakesh@uni.edu","Senior"));
        String id = genId("C");
        Complaint c = new Complaint(id,"Sample login issue","student","Unassigned","Authentication","Medium","Open",now(),null,"Sample details");
        complaints.put(id,c);
        refreshAllModels();
    }

    private void showLogin() {
        JFrame loginFrame = new JFrame("ResolveIt Login");
        loginFrame.setSize(520,400);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(0,0,0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ResolveIt", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 28));
        title.setForeground(Color.CYAN);

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JComboBox<String> roleSelect = new JComboBox<>(new String[]{"ADMIN","AGENT","STUDENT"});
        JButton loginBtn = new JButton("Login");
        JButton exitBtn = new JButton("Exit");

        styleLoginComponents(user, pass, loginBtn, exitBtn, roleSelect);

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        root.add(title,gbc);

        gbc.gridwidth=1;
        gbc.gridy=1; JLabel lu = new JLabel("Username:"); lu.setForeground(Color.CYAN); root.add(lu,gbc);
        gbc.gridx=1; root.add(user,gbc);
        gbc.gridx=0; gbc.gridy=2; JLabel lp = new JLabel("Password:"); lp.setForeground(Color.CYAN); root.add(lp,gbc);
        gbc.gridx=1; root.add(pass,gbc);
        gbc.gridx=0; gbc.gridy=3; JLabel lr = new JLabel("Role:"); lr.setForeground(Color.cyan); root.add(lr,gbc);
        gbc.gridx=1; root.add(roleSelect,gbc);

        gbc.gridx=0; gbc.gridy=4; root.add(loginBtn,gbc);
        gbc.gridx=1; root.add(exitBtn,gbc);

        loginFrame.setContentPane(root);
        loginFrame.setVisible(true);

        loginBtn.addActionListener(e -> {
            String u = user.getText().trim();
            String p = new String(pass.getPassword()).trim();
            String expectedRole = ((String)roleSelect.getSelectedItem()).toUpperCase();
            User found = users.get(u);
            if(found != null && found.password.equals(p) && found.role.equalsIgnoreCase(expectedRole)) {
                currentUser = found;
                loginFrame.dispose();
                showMainUI();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid credentials or role mismatch. Test accounts: admin/123, agent/123, student/123");
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void showMainUI() {
        JFrame mainFrame = new JFrame("ResolveIt - " + currentUser.role + " - " + currentUser.username);
        mainFrame.setSize(1200,760);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Dashboard", dashboardPanel());
        if(currentUser.role.equalsIgnoreCase("ADMIN")) {
            tabs.add("Admin", adminPanel());
            tabs.add("Agents", agentsPanel());
        } else if(currentUser.role.equalsIgnoreCase("AGENT")) {
            tabs.add("My Queue", agentPanel());
        } else {
            tabs.add("My Complaints", reporterPanel());
        }
        tabs.add("Feedback", feedbackPanelSimple());

        mainFrame.setContentPane(tabs);
        mainFrame.setVisible(true);
        refreshAllModels();
    }

    private JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(topHeader("Analytics"), BorderLayout.NORTH);
        JPanel center = new JPanel(new GridLayout(1,2,12,12));
        center.setBorder(new EmptyBorder(12,12,12,12));
        center.add(chartCard());
        center.add(quickActionsCard());
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JComponent topHeader(String title) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(0,0,0));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Poppins", Font.BOLD, 20));
        t.setForeground(Color.cyan);
        h.add(t, BorderLayout.WEST);
        return h;
    }

    private JPanel chartCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));
        p.add(new JLabel("Complaints by Status", SwingConstants.CENTER), BorderLayout.NORTH);
        p.add(new SimpleBarChartPanel(), BorderLayout.CENTER);
        return p;
    }

    private JPanel quickActionsCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(8,8,8,8));
        JButton exportBtn = new JButton("Export Complaints CSV");
        JButton refreshBtn = new JButton("Refresh");
        stylePrimaryButton(exportBtn);
        stylePrimaryButton(refreshBtn);
        exportBtn.addActionListener(e -> exportComplaintsCsv());
        refreshBtn.addActionListener(e -> refreshAllModels());
        p.add(exportBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(refreshBtn);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel adminPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(topHeader("Admin Panel"), BorderLayout.NORTH);
        JPanel center = new JPanel(new GridLayout(1,2,12,12));
        center.setBorder(new EmptyBorder(12,12,12,12));
        center.add(adminLeft());
        center.add(adminRight());
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // Continuing with remaining methods (truncated for space)
    // Please refer to your complete Main.java code for full implementation

    static class User {
        String username; String password; String email; String role;
        User(String u,String p,String e,String r){username=u;password=p;email=e;role=r;}
    }

    static class Agent {
        String id; String name; String email; String role;
        Agent(String id,String name,String email,String role){this.id=id;this.name=name;this.email=email;this.role=role;}
    }

    static class Complaint {
        String id; String title; String reporter; String assignedTo; String category; String priority; String status; String createdAt; String resolvedAt; String details;
        Complaint(String id,String title,String reporter,String assignedTo,String category,String priority,String status,String createdAt,String resolvedAt,String details){
            this.id=id;this.title=title;this.reporter=reporter;this.assignedTo=assignedTo;this.category=category;this.priority=priority;this.status=status;this.createdAt=createdAt;this.resolvedAt=resolvedAt;this.details=details;
        }
    }

    static class Feedback {
        String id; String complaintId; String author; String date; String comment;
        Feedback(String id,String complaintId,String author,String date,String comment){this.id=id;this.complaintId=complaintId;this.author=author;this.date=date;this.comment=comment;}
    }

    static class Attachment {
        String name; byte[] data;
        Attachment(String name, byte[] data){this.name=name;this.data=data;}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
