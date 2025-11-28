import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JFrame {

    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Agent> agents = new LinkedHashMap<>();
    private final Map<String, Complaint> complaints = new LinkedHashMap<>();
    private final Map<String, Feedback> feedbacks = new LinkedHashMap<>();
    private final Map<String, List<Attachment>> attachments = new HashMap<>();
    private final Map<String, List<HistoryEntry>> history = new HashMap<>();

    private final DefaultTableModel complaintTableModel = new DefaultTableModel(
            new Object[]{"ID","Title","Reporter","Assigned","Category","Priority","Status","Created","Resolved"}, 0);
    private final DefaultTableModel feedbackTableModel = new DefaultTableModel(
            new Object[]{"FB ID","Complaint ID","Author","Date","Comment","Rating"}, 0);
    private final DefaultTableModel agentTableModel = new DefaultTableModel(
            new Object[]{"Agent ID","Name","Email","Role"}, 0);

    private final JComboBox<String> assignCompCombo = new JComboBox<>();
    private final JComboBox<String> assignAgentCombo = new JComboBox<>();
    private final JComboBox<String> assignStatusCombo = new JComboBox<>(new String[]{"Open","In Progress","Resolved","Closed"});
    private final JComboBox<String> priorityCombo = new JComboBox<>();
    private final JComboBox<String> categoryCombo = new JComboBox<>();
    private final JComboBox<String> reporterSelector = new JComboBox<>();
    private final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private User currentUser = null;

    private final JTextField searchField = new JTextField();
    private final JComboBox<String> filterStatus = new JComboBox<>();
    private final JComboBox<String> filterAgent = new JComboBox<>();
    private final JComboBox<String> filterPriority = new JComboBox<>();
    private final JComboBox<String> filterCategory = new JComboBox<>();

    private final java.util.List<String> categories = new ArrayList<>(Arrays.asList("Authentication","Payments","UI","Stability","Other"));
    private final java.util.List<String> priorities = new ArrayList<>(Arrays.asList("Low","Medium","High","Critical"));

    private boolean darkTheme = false;

    public Main() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored){}
        setTitle("ResolveIt - GenZ University");
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        bootstrapDefaults();
        showLogin();
    }
    
    private JPanel topHeader(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel l = new JLabel(title);
        l.setFont(new Font("SansSerif", Font.BOLD, 20));
        l.setForeground(new Color(0, 150, 220));
        p.add(l);
        return p;
    }

    private void bootstrapDefaults() {
        createUser("admin","123","admin@uni.edu","ADMIN");
        createUser("agent","123","agent@uni.edu","AGENT");
        createUser("student","123","student@uni.edu","STUDENT");

        agents.put("AG01", new Agent("AG01","Neha Sharma","neha@uni.edu","Support"));
        agents.put("AG02", new Agent("AG02","Rakesh Rao","rakesh@uni.edu","Senior"));

        String id = genId("C");
        Complaint c = new Complaint(id,"Sample login issue","student","Unassigned","Authentication","Medium","Open",now(),null,"Can't login using campus creds");
        complaints.put(id,c);
        addHistory(id, "Created", "student", "Complaint created");

        String id2 = genId("C");
        Complaint c2 = new Complaint(id2,"Payment failed on portal","student","AG01 - Neha Sharma","Payments","High","Resolved",now(),now(),"Payment gateway returned 502");
        complaints.put(id2,c2);
        addHistory(id2,"Created","student","Created complaint #2");
        addHistory(id2,"Assigned","admin","Assigned to AG01");
        addHistory(id2,"Resolved","AG01","Marked resolved");
        String fid = genId("F");
        Feedback fb = new Feedback(fid,id2,"student",now(),"Thanks, resolved quickly.",5);
        feedbacks.put(fid, fb);

        refreshAllModels();
    }

    private void showLogin() {
        JFrame loginFrame = new JFrame("ResolveIt Login");
        loginFrame.setSize(520,420);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(darkTheme? new Color(16,16,16): new Color(245,245,245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ResolveIt", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        title.setForeground(new Color(0,150,220));

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JComboBox<String> roleSelect = new JComboBox<>(new String[]{"ADMIN","AGENT","STUDENT"});
        JButton loginBtn = new JButton("Login");
        JButton exitBtn = new JButton("Exit");

        styleLoginComponents(user, pass, loginBtn, exitBtn, roleSelect);

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        root.add(title,gbc);

        gbc.gridwidth=1;
        gbc.gridy=1; JLabel lu = new JLabel("Username:"); root.add(lu,gbc);
        gbc.gridx=1; root.add(user,gbc);
        gbc.gridx=0; gbc.gridy=2; JLabel lp = new JLabel("Password:"); root.add(lp,gbc);
        gbc.gridx=1; root.add(pass,gbc);
        gbc.gridx=0; gbc.gridy=3; JLabel lr = new JLabel("Role:"); root.add(lr,gbc);
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
            if(found != null && verifyPassword(p, found.salt, found.passwordHash) && found.role.equalsIgnoreCase(expectedRole)) {
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
            tabs.add("Settings", settingsPanel());
        } else if(currentUser.role.equalsIgnoreCase("AGENT")) {
            tabs.add("My Queue", agentPanel());
        } else {
            tabs.add("My Complaints", reporterPanel());
        }
        tabs.add("Feedback", feedbackPanelSimple());

        mainFrame.setContentPane(tabs);
        mainFrame.setVisible(true);
        applyTheme(mainFrame);
        refreshAllModels();
    }

    private void applyTheme(Container container) {
        Color bg = darkTheme ? new Color(30, 30, 30) : new Color(240, 240, 240);
        Color fg = darkTheme ? Color.WHITE : Color.BLACK;

        if (container instanceof JComponent) {
            ((JComponent) container).setBackground(bg);
            ((JComponent) container).setForeground(fg);
        }

        for (Component component : container.getComponents()) {
            if (component instanceof JTabbedPane || component instanceof JPanel) {
                applyTheme((Container) component);
            } else if (component instanceof JLabel) {
                component.setForeground(fg);
            } else if (component instanceof JTextField || component instanceof JTextArea) {
                component.setBackground(darkTheme ? new Color(50, 50, 50) : Color.WHITE);
                component.setForeground(fg);
            } else if (component instanceof JButton) {
                if (component.getBackground().equals(new Color(0, 153, 255))) {
                    component.setForeground(Color.BLACK);
                } else if (component.getBackground().equals(new Color(220, 60, 60))) {
                    component.setForeground(Color.BLACK);
                } else {
                     component.setBackground(darkTheme ? new Color(50, 50, 50) : Color.LIGHT_GRAY);
                     component.setForeground(fg);
                }
            } else if (component instanceof JComboBox) {
                component.setBackground(darkTheme ? new Color(50, 50, 50) : Color.WHITE);
                component.setForeground(fg);
            }
        }
        container.repaint();
    }

    private JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(topHeader("Analytics & Search"), BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout(8,8));
        top.setBorder(new EmptyBorder(8,8,8,8));
        top.add(buildFilterBar(), BorderLayout.NORTH);
        top.add(summaryPanel(), BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(1,2,12,12));
        center.setBorder(new EmptyBorder(12,12,12,12));
        center.add(chartCard());
        center.add(quickActionsCard());
        p.add(top, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        
        // --- Search Fields ---
        p.add(new JLabel("Search:")); p.add(searchField);
        p.add(new JLabel("Status:")); p.add(filterStatus);
        p.add(new JLabel("Agent:")); p.add(filterAgent);
        p.add(new JLabel("Priority:")); p.add(filterPriority);
        p.add(new JLabel("Category:")); p.add(filterCategory);
        
        filterStatus.setModel(new DefaultComboBoxModel<>(new String[]{"All","Open","In Progress","Resolved","Closed"}));
        filterAgent.setModel(new DefaultComboBoxModel<>(getAgentFilterOptions()));
        filterPriority.setModel(new DefaultComboBoxModel<>(getPriorityFilterOptions()));
        filterCategory.setModel(new DefaultComboBoxModel<>(getCategoryFilterOptions()));

        // --- Buttons (Fixed Visibility) ---
        JButton apply = new JButton("Apply");
        stylePrimaryButton(apply);
        apply.addActionListener(e -> applyFilters());

        JButton reset = new JButton("Reset");
        stylePrimaryButton(reset);
        reset.addActionListener(e -> {
            searchField.setText("");
            filterStatus.setSelectedIndex(0);
            filterAgent.setSelectedIndex(0);
            filterPriority.setSelectedIndex(0);
            filterCategory.setSelectedIndex(0);
            applyFilters();
        });
        
        p.add(apply); 
        p.add(reset);

        searchField.setColumns(20);
        searchField.addActionListener(e -> applyFilters());

        return p;
    }

    private String[] getAgentFilterOptions() {
        List<String> opts = new ArrayList<>();
        opts.add("All");
        opts.add("Unassigned");
        for(Agent a : agents.values()) opts.add(a.id + " - " + a.name);
        return opts.toArray(new String[0]);
    }
    private String[] getPriorityFilterOptions() {
        List<String> opts = new ArrayList<>();
        opts.add("All"); opts.addAll(priorities);
        return opts.toArray(new String[0]);
    }
    private String[] getCategoryFilterOptions() {
        List<String> opts = new ArrayList<>();
        opts.add("All"); opts.addAll(categories);
        return opts.toArray(new String[0]);
    }

    private void applyFilters() {
        String q = searchField.getText().trim().toLowerCase();
        String st = (String) filterStatus.getSelectedItem();
        String ag = (String) filterAgent.getSelectedItem();
        String pr = (String) filterPriority.getSelectedItem();
        String cat = (String) filterCategory.getSelectedItem();

        complaintTableModel.setRowCount(0);
        for(Complaint c : complaints.values()) {
            if(!"All".equalsIgnoreCase(st) && !c.status.equalsIgnoreCase(st)) continue;
            if(!"All".equalsIgnoreCase(pr) && !pr.equalsIgnoreCase(c.priority)) continue;
            if(!"All".equalsIgnoreCase(cat) && !cat.equalsIgnoreCase(c.category)) continue;
            if(!"All".equalsIgnoreCase(ag)) {
                if(ag.equalsIgnoreCase("Unassigned")) {
                    if(!("Unassigned".equalsIgnoreCase(c.assignedTo) || c.assignedTo==null)) continue;
                } else {
                    if(c.assignedTo==null || !c.assignedTo.startsWith(ag.split(" - ")[0])) continue;
                }
            }
            if(!q.isEmpty()) {
                boolean match = c.id.toLowerCase().contains(q) || safeLower(c.title).contains(q) || safeLower(c.reporter).contains(q) || safeLower(c.details).contains(q);
                if(!match) continue;
            }
            complaintTableModel.addRow(new Object[]{c.id,c.title,c.reporter,c.assignedTo,c.category,c.priority,c.status,c.createdAt,c.resolvedAt});
        }
    }

    private String safeLower(String s) { return s==null? "": s.toLowerCase(); }

    private JPanel summaryPanel() {
        JPanel p = new JPanel(new GridLayout(1,4,8,8));
        p.setBorder(new EmptyBorder(8,8,8,8));
        p.add(infoCard("Total Complaints", String.valueOf(complaints.size())));
        p.add(infoCard("Open", String.valueOf(countByStatus("Open"))));
        p.add(infoCard("Resolved", String.valueOf(countByStatus("Resolved"))));
        p.add(infoCard("Avg Rating", String.format("%.2f", averageRating())));
        return p;
    }

    private JPanel infoCard(String title, String val) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(8,8,8,8), BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        JLabel t = new JLabel(title); t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JLabel v = new JLabel(val, SwingConstants.CENTER); v.setFont(new Font("SansSerif", Font.BOLD, 20));
        c.add(t, BorderLayout.NORTH); c.add(v, BorderLayout.CENTER);
        return c;
    }

    private int countByStatus(String s) {
        int ct=0; for(Complaint c: complaints.values()) if(s.equalsIgnoreCase(c.status)) ct++; return ct;
    }
    private double averageRating() {
        if(feedbacks.isEmpty()) return 0.0;
        double sum=0; int n=0;
        for(Feedback f: feedbacks.values()) { sum += f.rating; n++; }
        return n==0?0:sum/n;
    }

    private JPanel chartCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));
        p.add(new JLabel("Complaints by Status", SwingConstants.CENTER), BorderLayout.NORTH);
        p.add(new DashboardChartPanel(), BorderLayout.CENTER);
        return p;
    }

    private JPanel quickActionsCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(8,8,8,8));
        JButton exportBtn = new JButton("Export Complaints CSV");
        JButton exportPdfBtn = new JButton("Export Selected PDF");
        JButton refreshBtn = new JButton("Refresh");
        stylePrimaryButton(exportBtn);
        stylePrimaryButton(exportPdfBtn);
        stylePrimaryButton(refreshBtn);
        exportBtn.addActionListener(e -> exportComplaintsCsv());
        exportPdfBtn.addActionListener(e -> {
            String sel = pickSelectedComplaintId();
            if(sel==null) { JOptionPane.showMessageDialog(null,"Select a complaint from Admin/Agents tab to export"); return; }
            try {
                exportComplaintPdf(sel);
                JOptionPane.showMessageDialog(null,"PDF exported (if iText present).");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,"PDF export failed (no iText on classpath).");
            }
        });
        refreshBtn.addActionListener(e -> {
            refreshAllModels();
            JOptionPane.showMessageDialog(null,"Refreshed");
        });
        p.add(exportBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(exportPdfBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(refreshBtn);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private String pickSelectedComplaintId() {
        return complaints.keySet().stream().findFirst().orElse(null);
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

    private JPanel adminLeft() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(card("Create Complaint (Admin)", createComplaintForm(true)));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Assign / Status", assignPanel()));
        p.add(Box.createVerticalStrut(12));
        p.add(card("Add Feedback (Admin)", feedbackPanel()));
        return p;
    }

    private JPanel adminRight() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(complaintTableModel);
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        p.add(sp, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editBtn = new JButton("Edit Selected");
        JButton delBtn = new JButton("Delete Selected");
        JButton attachBtn = new JButton("Attachments");
        JButton assignSelectedBtn = new JButton("Assign Selected to Agent");
        JButton timelineBtn = new JButton("Show Timeline");
        JButton notifyBtn = new JButton("Notify Agent");
        stylePrimaryButton(editBtn);
        styleDangerButton(delBtn);
        stylePrimaryButton(attachBtn);
        stylePrimaryButton(assignSelectedBtn);
        stylePrimaryButton(timelineBtn);
        stylePrimaryButton(notifyBtn);

        bottom.add(editBtn); bottom.add(delBtn); bottom.add(attachBtn);
        bottom.add(assignSelectedBtn); bottom.add(timelineBtn); bottom.add(notifyBtn);
        p.add(bottom, BorderLayout.SOUTH);

        editBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r<0) { JOptionPane.showMessageDialog(p,"Select a complaint"); return; }
            String id = (String) complaintTableModel.getValueAt(r,0);
            Complaint c = complaints.get(id);
            if(c!=null) showEditComplaintDialog(c);
        });

        delBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r<0) { JOptionPane.showMessageDialog(p,"Select a complaint"); return; }
            String id = (String) complaintTableModel.getValueAt(r,0);
            int conf = JOptionPane.showConfirmDialog(p,"Delete " + id + " ?","Confirm",JOptionPane.YES_NO_OPTION);
            if(conf==JOptionPane.YES_OPTION) {
                complaints.remove(id);
                attachments.remove(id);
                history.remove(id);
                refreshAllModels();
            }
        });

        attachBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r<0) { JOptionPane.showMessageDialog(p,"Select a complaint"); return; }
            String id = (String) complaintTableModel.getValueAt(r,0);
            showAttachmentsDialog(id);
        });

        assignSelectedBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r<0) { JOptionPane.showMessageDialog(p,"Select a complaint"); return; }
            String cid = (String) complaintTableModel.getValueAt(r,0);
            pickAgentAndAssign(cid);
        });

        timelineBtn.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r<0) { JOptionPane.showMessageDialog(p,"Select row"); return; }
            String id = (String) complaintTableModel.getValueAt(r,0);
            showTimelineDialog(id);
        });

        notifyBtn.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r<0) { JOptionPane.showMessageDialog(p,"Select row"); return; }
            String id = (String) complaintTableModel.getValueAt(r,0);
            Complaint c = complaints.get(id);
            if(c==null) return;
            notifyAgents("Admin message regarding " + id + ": Please check.");
        });

        return p;
    }

    private JPanel agentsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(topHeader("Agents"), BorderLayout.NORTH);
        JTable table = new JTable(agentTableModel);
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JTextField aid = new JTextField();
        JTextField aname = new JTextField();
        JTextField aemail = new JTextField();
        JButton add = new JButton("Add Agent");
        JButton remove = new JButton("Remove Selected");
        stylePrimaryButton(add); styleDangerButton(remove);
        right.add(new JLabel("Agent ID")); right.add(aid);
        right.add(new JLabel("Name")); right.add(aname);
        right.add(new JLabel("Email")); right.add(aemail);
        right.add(add); right.add(Box.createVerticalStrut(8)); right.add(remove);

        add.addActionListener(e -> {
            String id = aid.getText().trim(); String name = aname.getText().trim(); String email = aemail.getText().trim();
            if(id.isEmpty()||name.isEmpty()) { JOptionPane.showMessageDialog(p,"Fill ID & name"); return; }
            Agent a = new Agent(id,name,email,"AGENT");
            agents.put(id,a);
            refreshAllModels();
            aid.setText(""); aname.setText(""); aemail.setText("");
        });

        remove.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r<0) { JOptionPane.showMessageDialog(p,"Select row"); return; }
            String id = (String) agentTableModel.getValueAt(r,0);
            agents.remove(id); refreshAllModels();
        });

        p.add(sp, BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel agentPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(topHeader("Agent Queue"), BorderLayout.NORTH);
        JTable tbl = new JTable(complaintTableModel);
        styleTable(tbl);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        JButton resolve = new JButton("Mark Selected Resolved");
        stylePrimaryButton(resolve);
        resolve.addActionListener(e -> {
            int r = tbl.getSelectedRow(); if(r<0) { JOptionPane.showMessageDialog(p,"Select complaint"); return; }
            String id = (String) complaintTableModel.getValueAt(r,0);
            Complaint c = complaints.get(id);
            if(c==null) return;
            c.status = "Resolved"; c.resolvedAt = now();
            addHistory(id,"Resolved", currentUser.username, "Marked Resolved by " + currentUser.username);
            refreshAllModels();
        });
        p.add(resolve, BorderLayout.SOUTH);
        return p;
    }

    private JPanel reporterPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(topHeader("Reporter - My Complaints"), BorderLayout.NORTH);

        JTable tbl = new JTable(complaintTableModel);
        styleTable(tbl);

        JScrollPane scroll = new JScrollPane(tbl);
        p.add(scroll, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JButton giveFb = new JButton("Give Feedback");
        JButton viewTimeline = new JButton("View Timeline");
        stylePrimaryButton(giveFb); stylePrimaryButton(viewTimeline);
        right.add(giveFb); right.add(Box.createVerticalStrut(8)); right.add(viewTimeline);
        p.add(right, BorderLayout.EAST);

        giveFb.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(p, "Select a complaint first!"); return; }
            String cid = (String) complaintTableModel.getValueAt(row, 0);
            Complaint c = complaints.get(cid);
            if (c == null) return;
            if (!(c.status.equalsIgnoreCase("Resolved") || c.status.equalsIgnoreCase("Closed"))) {
                JOptionPane.showMessageDialog(p, "You can only write feedback after complaint is resolved/closed.");
                return;
            }
            showFeedbackDialogForReporter(cid);
        });

        viewTimeline.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(p, "Select a complaint first!"); return; }
            String cid = (String) complaintTableModel.getValueAt(row, 0);
            showTimelineDialog(cid);
        });

        return p;
    }

    private JPanel createComplaintForm(boolean showId) {
        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        JTextField idField = new JTextField(); idField.setEditable(false);
        JTextField title = new JTextField();
        JTextField reporter = new JTextField(currentUser==null? "": currentUser.username);
        JComboBox<String> category = new JComboBox<>(categories.toArray(new String[0]));
        JComboBox<String> prio = new JComboBox<>(priorities.toArray(new String[0]));
        JTextArea details = new JTextArea(3,20);
        JButton save = new JButton("Create Complaint");
        stylePrimaryButton(save);

        if(showId) { p.add(new JLabel("ID (auto)")); p.add(idField); }
        p.add(new JLabel("Title")); p.add(title);
        p.add(new JLabel("Reporter")); p.add(reporter);
        p.add(new JLabel("Category")); p.add(category);
        p.add(new JLabel("Priority")); p.add(prio);
        p.add(new JLabel("Details")); p.add(new JScrollPane(details));
        p.add(save);

        save.addActionListener(e -> {
            String id = genId("C");
            Complaint c = new Complaint(id, title.getText(), reporter.getText(), "Unassigned", (String)category.getSelectedItem(), (String)prio.getSelectedItem(), "Open", now(), null, details.getText());
            complaints.put(id,c);
            addHistory(id,"Created", reporter.getText(), "Complaint created");
            refreshAllModels();
            title.setText(""); reporter.setText(""); details.setText("");
            JOptionPane.showMessageDialog(p,"Created " + id);
        });

        return p;
    }

    private JPanel assignPanel() {
        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        assignCompCombo.setModel(new DefaultComboBoxModel<>(getComplaintIds()));
        buildAssignAgentModel();
        JButton assignBtn = new JButton("Assign / Update Status");
        stylePrimaryButton(assignBtn);

        p.add(new JLabel("Select Complaint")); p.add(assignCompCombo);
        p.add(new JLabel("Assign Agent")); p.add(assignAgentCombo);
        p.add(new JLabel("Status")); p.add(assignStatusCombo);
        p.add(assignBtn);

        assignBtn.addActionListener(e -> {
            String cid = (String) assignCompCombo.getSelectedItem();
            String agDisp = (String) assignAgentCombo.getSelectedItem();
            String st = (String) assignStatusCombo.getSelectedItem();
            if(cid==null) { JOptionPane.showMessageDialog(p,"No complaint selected"); return; }
            Complaint c = complaints.get(cid);
            if(c==null) return;
            if(agDisp == null || agDisp.equalsIgnoreCase("UNASSIGNED")) {
                c.assignedTo = "Unassigned";
                addHistory(cid,"Unassigned", currentUser==null? "system": currentUser.username, "Unassigned");
            } else {
                String aid = agDisp.split(" - ")[0];
                Agent a = agents.get(aid);
                if(a!=null) {
                    c.assignedTo = aid + " - " + a.name;
                    addHistory(cid,"Assigned", currentUser==null? "system": currentUser.username, "Assigned to " + aid);
                    notifyAgent(a, "You have been assigned complaint " + cid);
                } else c.assignedTo = agDisp;
            }
            c.status = st;
            if("Resolved".equalsIgnoreCase(st) || "Closed".equalsIgnoreCase(st)) {
                c.resolvedAt = now();
                addHistory(cid,"Resolved", currentUser==null? "system": currentUser.username, "Status set to " + st);
            } else addHistory(cid,"Status", currentUser==null? "system": currentUser.username, "Status set to " + st);
            refreshAllModels();
        });

        return p;
    }

    private JPanel feedbackPanel() {
        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        JComboBox<String> comp = new JComboBox<>(getComplaintIds());
        JTextField author = new JTextField(currentUser==null? "system": currentUser.username);
        JTextArea comment = new JTextArea(3,20);
        JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[]{1,2,3,4,5});
        JButton add = new JButton("Add Feedback");
        stylePrimaryButton(add);
        p.add(new JLabel("Complaint")); p.add(comp);
        p.add(new JLabel("Author")); p.add(author);
        p.add(new JLabel("Rating")); p.add(ratingBox);
        p.add(new JLabel("Comment")); p.add(new JScrollPane(comment));
        p.add(add);

        add.addActionListener(e -> {
            String id = genId("F");
            String cid = (String) comp.getSelectedItem();
            int r = (int) ratingBox.getSelectedItem();
            Feedback fb = new Feedback(id,cid,author.getText(), now(), comment.getText(), r);
            feedbacks.put(id, fb);
            addHistory(cid,"Feedback", author.getText(), "Feedback posted (★" + r + ")");
            refreshAllModels();
            notifyAgents("New feedback on " + cid + " (★" + r + ")");
            comment.setText("");
        });

        return p;
    }

    private JPanel feedbackPanelSimple() {
        JPanel p = new JPanel(new BorderLayout());
        JTable t = new JTable(feedbackTableModel);
        styleTable(t);
        colorFeedbackTable(t);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void showEditComplaintDialog(Complaint c) {
        JDialog d = new JDialog((Frame)null,"Edit: " + c.id, true);
        d.setSize(600,520); d.setLocationRelativeTo(null);
        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        JTextField title = new JTextField(c.title);
        JTextField reporter = new JTextField(c.reporter);
        JComboBox<String> cat = new JComboBox<>(categories.toArray(new String[0]));
        cat.setSelectedItem(c.category);
        JComboBox<String> pr = new JComboBox<>(priorities.toArray(new String[0]));
        pr.setSelectedItem(c.priority);
        JComboBox<String> stat = new JComboBox<>(new String[]{"Open","In Progress","Resolved","Closed"});
        stat.setSelectedItem(c.status);
        JTextArea details = new JTextArea(c.details,4,30);
        JButton save = new JButton("Save");
        stylePrimaryButton(save);

        p.add(new JLabel("Title")); p.add(title);
        p.add(new JLabel("Reporter")); p.add(reporter);
        p.add(new JLabel("Category")); p.add(cat);
        p.add(new JLabel("Priority")); p.add(pr);
        p.add(new JLabel("Status")); p.add(stat);
        p.add(new JLabel("Details")); p.add(new JScrollPane(details));

        StringBuilder fbText = new StringBuilder();
        for (Feedback f : feedbacks.values()) {
            if (f.complaintId.equals(c.id)) {
                fbText.append("⭐ ").append(f.rating).append(" Stars\n");
                fbText.append(f.author).append(" (" + f.date + ")\n");
                fbText.append(f.comment).append("\n\n");
            }
        }
        JTextArea fbArea = new JTextArea(fbText.toString(), 6, 30);
        fbArea.setEditable(false);
        p.add(new JLabel("Feedback Received:"));
        p.add(new JScrollPane(fbArea));

        p.add(save);

        save.addActionListener(e -> {
            c.title = title.getText();
            c.reporter = reporter.getText();
            c.category = (String)cat.getSelectedItem();
            c.priority = (String)pr.getSelectedItem();
            String oldStatus = c.status;
            c.status = (String)stat.getSelectedItem();
            c.details = details.getText();
            if(!oldStatus.equalsIgnoreCase(c.status)) {
                addHistory(c.id,"Status", currentUser==null? "system": currentUser.username, "Status changed to " + c.status);
            }
            if("Resolved".equalsIgnoreCase(c.status) || "Closed".equalsIgnoreCase(c.status)) c.resolvedAt = now();
            refreshAllModels();
            d.dispose();
        });
        d.setContentPane(p); d.setVisible(true);
    }

    private void showAttachmentsDialog(String complaintId) {
        JDialog d = new JDialog((Frame)null,"Attachments: " + complaintId, true);
        d.setSize(600,420); d.setLocationRelativeTo(null);
        JPanel p = new JPanel(new BorderLayout());
        DefaultListModel<String> lm = new DefaultListModel<>();
        List<Attachment> list = attachments.getOrDefault(complaintId, new ArrayList<>());
        for(Attachment a : list) lm.addElement(a.name + " (" + humanReadable(a.data.length) + ")");
        JList<String> jl = new JList<>(lm);
        JScrollPane sp = new JScrollPane(jl);
        JButton add = new JButton("Attach File");
        JButton download = new JButton("Download Selected");
        JButton remove = new JButton("Remove Selected");
        stylePrimaryButton(add); stylePrimaryButton(download); styleDangerButton(remove);
        JPanel ctrl = new JPanel();
        ctrl.add(add); ctrl.add(download); ctrl.add(remove);
        p.add(sp, BorderLayout.CENTER);
        p.add(ctrl, BorderLayout.SOUTH);

        add.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int ret = fc.showOpenDialog(d);
            if(ret==JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    byte[] data = readAllBytes(f);
                    Attachment a = new Attachment(f.getName(), data);
                    attachments.computeIfAbsent(complaintId, k-> new ArrayList<>()).add(a);
                    lm.addElement(a.name + " (" + humanReadable(a.data.length) + ")");
                    addHistory(complaintId,"Attachment", currentUser==null? "system": currentUser.username, "Attached " + a.name);
                    refreshAllModels();
                } catch (IOException ex) { JOptionPane.showMessageDialog(d,"Failed to read file"); }
            }
        });

        download.addActionListener(e -> {
            int sel = jl.getSelectedIndex();
            if(sel<0) { JOptionPane.showMessageDialog(d,"Select an attachment"); return; }
            Attachment a = attachments.getOrDefault(complaintId, new ArrayList<>()).get(sel);
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(a.name));
            int r = fc.showSaveDialog(d);
            if(r==JFileChooser.APPROVE_OPTION) {
                File out = fc.getSelectedFile();
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    fos.write(a.data);
                    JOptionPane.showMessageDialog(d,"Saved to " + out.getAbsolutePath());
                } catch (IOException ex) { JOptionPane.showMessageDialog(d,"Save failed"); }
            }
        });

        remove.addActionListener(e -> {
            int sel = jl.getSelectedIndex();
            if(sel<0) { JOptionPane.showMessageDialog(d,"Select an attachment"); return; }
            attachments.getOrDefault(complaintId, new ArrayList<>()).remove(sel);
            lm.remove(sel);
            addHistory(complaintId,"Attachment", currentUser==null? "system": currentUser.username, "Removed attachment at index " + sel);
            refreshAllModels();
        });

        d.setContentPane(p); d.setVisible(true);
    }

    private void showFeedbackDialogForReporter(String complaintId) {
        JDialog d = new JDialog((Frame)null, "Feedback for " + complaintId, true);
        d.setSize(480,360);
        d.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0,1,6,6));

        JLabel rlbl = new JLabel("Rating (1–5 stars):");
        JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[]{1,2,3,4,5});
        JLabel lbl = new JLabel("Write your feedback:");
        JTextArea txt = new JTextArea(6,30);

        JButton submit = new JButton("Submit Feedback");
        stylePrimaryButton(submit);

        panel.add(rlbl);
        panel.add(ratingBox);
        panel.add(lbl);
        panel.add(new JScrollPane(txt));
        panel.add(submit);

        submit.addActionListener(e -> {
            String comment = txt.getText().trim();
            int rating = (int) ratingBox.getSelectedItem();

            if(comment.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Feedback cannot be empty");
                return;
            }

            String fid = genId("F");
            Feedback fb = new Feedback(fid, complaintId, currentUser.username, now(), comment, rating);

            feedbacks.put(fid, fb);
            addHistory(complaintId,"Feedback", currentUser.username, "Feedback given (★" + rating + ")");
            refreshAllModels();

            notifyAgents("Complaint " + complaintId + " received ★" + rating + " feedback.");
            JOptionPane.showMessageDialog(d, "Feedback submitted!");
            d.dispose();
        });

        d.setContentPane(panel);
        d.setVisible(true);
    }

    private void addHistory(String complaintId, String action, String actor, String note) {
        history.computeIfAbsent(complaintId, k-> new ArrayList<>()).add(new HistoryEntry(now(), action, actor, note));
    }

    private void showTimelineDialog(String complaintId) {
        JDialog d = new JDialog((Frame)null, "Timeline: " + complaintId, true);
        d.setSize(500,400); d.setLocationRelativeTo(null);
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        List<HistoryEntry> list = history.getOrDefault(complaintId, new ArrayList<>());
        StringBuilder sb = new StringBuilder();
        for(HistoryEntry h : list) {
            sb.append("[").append(h.time).append("] ").append(h.action).append(" by ").append(h.actor).append("\n");
            sb.append("    ").append(h.note).append("\n\n");
        }
        ta.setText(sb.toString());
        d.setContentPane(new JScrollPane(ta));
        d.setVisible(true);
    }

    private void notifyAgents(String msg) {
        for (User u : users.values()) {
            if (u.role.equalsIgnoreCase("AGENT")) {
                JOptionPane.showMessageDialog(null,
                        "New Feedback Alert for Agent: " + u.username + "\n" + msg,
                        "New Feedback",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void notifyAgent(Agent a, String msg) {
        JOptionPane.showMessageDialog(null, "Notify " + a.name + " ("+a.email+"):\n" + msg);
    }

    private void pickAgentAndAssign(String complaintId) {
        List<String> options = new ArrayList<>();
        options.add("UNASSIGNED");
        for(Agent a : agents.values()) options.add(a.id + " - " + a.name);
        String sel = (String) JOptionPane.showInputDialog(null, "Pick agent to assign:", "Assign Agent",
                JOptionPane.PLAIN_MESSAGE, null, options.toArray(new String[0]), options.get(0));
        if(sel == null) return;
        Complaint c = complaints.get(complaintId);
        if(c==null) return;
        if(sel.equalsIgnoreCase("UNASSIGNED")) {
            c.assignedTo = "Unassigned";
            addHistory(complaintId,"Unassigned", currentUser==null? "system": currentUser.username, "Set Unassigned");
        } else {
            String aid = sel.split(" - ")[0];
            Agent a = agents.get(aid);
            if(a!=null) {
                c.assignedTo = aid + " - " + a.name;
                addHistory(complaintId,"Assigned", currentUser==null? "system": currentUser.username, "Assigned to " + aid);
                notifyAgent(a, "You have been assigned complaint " + complaintId);
            } else c.assignedTo = sel;
        }
        refreshAllModels();
    }

    private void exportComplaintsCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("complaints_export.csv"));
        int r = fc.showSaveDialog(null);
        if(r!=JFileChooser.APPROVE_OPTION) return;
        File out = fc.getSelectedFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {
            bw.write("ID,Title,Reporter,Assigned,Category,Priority,Status,Created,Resolved");
            bw.newLine();
            for(Complaint c : complaints.values()) {
                bw.write(csvQuote(c.id) + "," + csvQuote(c.title) + "," + csvQuote(c.reporter) + "," + csvQuote(c.assignedTo) + "," +
                        csvQuote(c.category) + "," + csvQuote(c.priority) + "," + csvQuote(c.status) + "," + csvQuote(c.createdAt) + "," + csvQuote(c.resolvedAt==null?"":c.resolvedAt));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(null,"Exported to " + out.getAbsolutePath());
        } catch (IOException ex) { JOptionPane.showMessageDialog(null,"Export failed"); }
    }

    private String csvQuote(String s) { if(s==null) s=""; return "\"" + s.replace("\"","\"\"") + "\""; }

    private void exportComplaintPdf(String complaintId) throws Exception {
        throw new UnsupportedOperationException("PDF export requires iText on classpath. Uncomment code to use.");
    }

    private void sendEmail(String to, String subject, String body) throws Exception {
        throw new UnsupportedOperationException("Email sending requires JavaMail in classpath and SMTP settings.");
    }

    private void refreshAllModels() {
        refreshComplaintModel();
        refreshFeedbackModel();
        refreshAgentModel();
        refreshCombos();
    }

    private void refreshCombos() {
        assignCompCombo.setModel(new DefaultComboBoxModel<>(getComplaintIds()));
        buildAssignAgentModel();
        reporterSelector.setModel(new DefaultComboBoxModel<>(getReporterIds()));
        filterAgent.setModel(new DefaultComboBoxModel<>(getAgentFilterOptions()));
        filterPriority.setModel(new DefaultComboBoxModel<>(getPriorityFilterOptions()));
        filterCategory.setModel(new DefaultComboBoxModel<>(getCategoryFilterOptions()));
    }

    private void buildAssignAgentModel() {
        List<String> list = new ArrayList<>();
        list.add("UNASSIGNED");
        for(Agent a : agents.values()) list.add(a.id + " - " + a.name);
        assignAgentCombo.setModel(new DefaultComboBoxModel<>(list.toArray(new String[0])));
    }

    private void refreshComplaintModel() {
        complaintTableModel.setRowCount(0);
        for(Complaint c : complaints.values()) complaintTableModel.addRow(new Object[]{c.id,c.title,c.reporter,c.assignedTo,c.category,c.priority,c.status,c.createdAt,c.resolvedAt});
    }

    private void refreshFeedbackModel() {
        feedbackTableModel.setRowCount(0);
        for(Feedback f : feedbacks.values()) feedbackTableModel.addRow(new Object[]{f.id,f.complaintId,f.author,f.date,f.comment,f.rating});
    }

    private void refreshAgentModel() {
        agentTableModel.setRowCount(0);
        for(Agent a : agents.values()) agentTableModel.addRow(new Object[]{a.id,a.name,a.email,a.role});
    }

    private String[] getComplaintIds() { return complaints.keySet().toArray(new String[0]); }
    private String[] getReporterIds() {
        Set<String> s = new LinkedHashSet<>();
        for(Complaint c : complaints.values()) s.add(c.reporter);
        return s.toArray(new String[0]);
    }

    private String genId(String prefix) { return prefix + UUID.randomUUID().toString().substring(0,6).toUpperCase(); }
    private String now() { return DF.format(new Date()); }

    private void showMessage(String s) { JOptionPane.showMessageDialog(null,s); }

    private JPanel card(String title, JComponent body) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(8,8,8,8), BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        JLabel l = new JLabel(title);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        p.add(l, BorderLayout.NORTH);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private void styleLoginComponents(JTextField user, JPasswordField pass, JButton loginBtn, JButton exitBtn, JComboBox<String> role) {
        user.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pass.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginBtn.setBackground(new Color(255,51,153)); loginBtn.setForeground(Color.black); loginBtn.setFocusPainted(false);
        exitBtn.setBackground(new Color(200,60,60)); exitBtn.setForeground(Color.black); exitBtn.setFocusPainted(false);
        role.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(0,153,255)); b.setForeground(Color.black); b.setFocusPainted(false);
        b.setBorder(new RoundedButtonBorder(10)); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton b) {
        b.setBackground(new Color(220,60,60)); b.setForeground(Color.black); b.setFocusPainted(false);
        b.setBorder(new RoundedButtonBorder(10)); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            try { table.getColumnModel().getColumn(i).setCellRenderer(center); } catch(Exception ignored){}
        }
    }

    private void colorFeedbackTable(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                int rating = 0;
                try { rating = Integer.parseInt(tbl.getValueAt(row, 5).toString()); } catch (Exception ignored) {}
                if (!isSelected) {
                    if (rating >= 4) c.setBackground(new Color(200,255,200));
                    else if (rating == 3) c.setBackground(new Color(255,255,200));
                    else c.setBackground(new Color(255,200,200));
                }
                return c;
            }
        });
    }

    class DashboardChartPanel extends JPanel { 
        DashboardChartPanel() { setPreferredSize(new Dimension(400,300)); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();
            
            Map<String,Integer> statusCounts = new LinkedHashMap<>();
            statusCounts.put("Open",0); statusCounts.put("In Progress",0); statusCounts.put("Resolved",0); statusCounts.put("Closed",0);
            for(Complaint c: complaints.values()) statusCounts.put(c.status, statusCounts.getOrDefault(c.status,0)+1);
            
            int pieW = (int)(w * 0.4);
            int pieX = 20, pieY = 20; 
            int pieSize = Math.min(pieW - 40, h - 60);
            
            int total = statusCounts.values().stream().mapToInt(Integer::intValue).sum();
            int start = 0;
            Color[] colors = new Color[]{new Color(0,153,255), new Color(255,200,0), new Color(0,200,100), new Color(200,200,200)};
            int idx=0;
            for(Map.Entry<String,Integer> e: statusCounts.entrySet()) {
                int val = e.getValue();
                int angle = total==0?0: (int)Math.round(360.0 * val / total);
                g.setColor(colors[idx%colors.length]);
                g.fillArc(pieX, pieY, pieSize, pieSize, start, angle);
                start += angle;
                idx++;
            }
            g.setColor(Color.black);
            g.drawString("Status Distribution (left)", pieX, pieY + pieSize + 15);

            Map<String,Integer> catCounts = new LinkedHashMap<>();
            for(String cat: categories) catCounts.put(cat,0);
            for(Complaint c: complaints.values()) catCounts.put(c.category, catCounts.getOrDefault(c.category,0)+1);
            
            int numBars = catCounts.size();
            int barAreaStart = pieX + pieW + 20;
            int barAreaWidth = w - barAreaStart - 20;
            if (numBars == 0) numBars = 1;

            int bx = barAreaStart;
            int barW = Math.max(15, (barAreaWidth - (numBars * 5)) / numBars);
            
            int max = catCounts.values().stream().max(Integer::compareTo).orElse(1);
            int chartHeight = h - 60;
            idx=0;
            
            for(Map.Entry<String,Integer> e: catCounts.entrySet()) {
                int val = e.getValue();
                int barH = max==0?0: (val * (chartHeight - 40) / max);
                g.setColor(new Color(0,153,255));
                int x = bx + idx * (barW + 5);
                g.fillRect(x, h-40-barH, barW, barH);
                g.setColor(Color.BLACK);
                
                FontMetrics fm = g.getFontMetrics();
                String label = e.getKey() + " ("+val+")";
                
                if (fm.stringWidth(label) > barW + 5) {
                    g.drawString(e.getKey().substring(0, Math.min(e.getKey().length(), 4)) + "..", x, h-25);
                } else {
                    g.drawString(label, x, h-25);
                }
                
                idx++;
            }
            g.drawString("Category Counts (right)", barAreaStart, h - 5);
        }
    }

    private JPanel settingsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(topHeader("Settings / Admin"), BorderLayout.NORTH);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(12,12,12,12));

        JButton changeTheme = new JButton("Toggle Theme (Dark/Light)");
        stylePrimaryButton(changeTheme);
        changeTheme.addActionListener(e -> {
            darkTheme = !darkTheme;
            applyTheme(this.getContentPane());
            JOptionPane.showMessageDialog(null,"Theme toggled. Full theme application usually requires a restart, but basic colors updated.");
        });

        JButton changePass = new JButton("Change My Password");
        stylePrimaryButton(changePass);
        changePass.addActionListener(e -> {
            changePasswordForCurrentUser();
        });

        JButton editCats = new JButton("Edit Categories & Priorities");
        stylePrimaryButton(editCats);
        editCats.addActionListener(e -> {
            showEditListsDialog();
        });

        center.add(changeTheme); center.add(Box.createVerticalStrut(8));
        center.add(changePass); center.add(Box.createVerticalStrut(8));
        center.add(editCats);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private void showEditListsDialog() {
        JDialog d = new JDialog((Frame)null, "Edit Categories / Priorities", true);
        d.setSize(500,380); d.setLocationRelativeTo(null);
        JPanel p = new JPanel(new GridLayout(1,2,8,8));
        DefaultListModel<String> catModel = new DefaultListModel<>();
        categories.forEach(catModel::addElement);
        JList<String> catList = new JList<>(catModel);

        DefaultListModel<String> prModel = new DefaultListModel<>();
        priorities.forEach(prModel::addElement);
        JList<String> prList = new JList<>(prModel);

        JPanel left = new JPanel(new BorderLayout()); left.add(new JLabel("Categories"), BorderLayout.NORTH); left.add(new JScrollPane(catList), BorderLayout.CENTER);
        JPanel right = new JPanel(new BorderLayout()); right.add(new JLabel("Priorities"), BorderLayout.NORTH); right.add(new JScrollPane(prList), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(4,2,6,6));
        JTextField newCat = new JTextField(); JTextField newPr = new JTextField();
        JButton addCat = new JButton("Add Category"); JButton remCat = new JButton("Remove Selected Category");
        JButton addPr = new JButton("Add Priority"); JButton remPr = new JButton("Remove Selected Priority");
        stylePrimaryButton(addCat); styleDangerButton(remCat); stylePrimaryButton(addPr); styleDangerButton(remPr);

        addCat.addActionListener(e -> { String v=newCat.getText().trim(); if(!v.isEmpty()){ categories.add(v); catModel.addElement(v); newCat.setText(""); refreshAllModels(); }});
        remCat.addActionListener(e -> { int i=catList.getSelectedIndex(); if(i>=0){ categories.remove(i); catModel.remove(i); refreshAllModels(); }});
        addPr.addActionListener(e -> { String v=newPr.getText().trim(); if(!v.isEmpty()){ priorities.add(v); prModel.addElement(v); newPr.setText(""); refreshAllModels(); }});
        remPr.addActionListener(e -> { int i=prList.getSelectedIndex(); if(i>=0){ priorities.remove(i); prModel.remove(i); refreshAllModels(); }});

        controls.add(new JLabel("New Category:")); controls.add(newCat);
        controls.add(addCat); controls.add(remCat);
        controls.add(new JLabel("New Priority:")); controls.add(newPr);
        controls.add(addPr); controls.add(remPr);

        JPanel root = new JPanel(new BorderLayout());
        root.add(p, BorderLayout.CENTER);
        root.add(controls, BorderLayout.SOUTH);
        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.EAST);
        d.setContentPane(root);
        d.setVisible(true);
    }

    private void changePasswordForCurrentUser() {
        JPanel panel = new JPanel(new GridLayout(0,1,6,6));
        JPasswordField oldp = new JPasswordField();
        JPasswordField newp = new JPasswordField();
        panel.add(new JLabel("Old Password:")); panel.add(oldp);
        panel.add(new JLabel("New Password:")); panel.add(newp);
        int r = JOptionPane.showConfirmDialog(null, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if(r==JOptionPane.OK_OPTION) {
            String old = new String(oldp.getPassword()).trim();
            String nw = new String(newp.getPassword()).trim();
            if(!verifyPassword(old, currentUser.salt, currentUser.passwordHash)) { JOptionPane.showMessageDialog(null,"Old password incorrect"); return; }
            if(nw.length()<3) { JOptionPane.showMessageDialog(null,"Choose a longer password"); return; }
            byte[] salt = generateSalt();
            String hash = hashPassword(nw, salt);
            currentUser.salt = Base64.getEncoder().encodeToString(salt);
            currentUser.passwordHash = hash;
            users.put(currentUser.username, currentUser);
            JOptionPane.showMessageDialog(null,"Password changed");
        }
    }

    class RoundedButtonBorder implements Border {
        private final int r;
        RoundedButtonBorder(int r){this.r=r;}
        public Insets getBorderInsets(Component c){return new Insets(r,r,r,r);}
        public boolean isBorderOpaque(){return false;}
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){ g.drawRoundRect(x,y,w-1,h-1,r,r); }
    }

    private String humanReadable(long bytes) {
        if(bytes < 1024) return bytes + " B";
        int exp = (int)(Math.log(bytes) / Math.log(1024));
        String pre = "" + "KMGTPE".charAt(Math.max(0, exp-1));
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private byte[] readAllBytes(File f) throws IOException {
        try (FileInputStream in = new FileInputStream(f); ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192]; int r;
            while((r=in.read(buf))>0) bout.write(buf,0,r);
            return bout.toByteArray();
        }
    }

    private void createUser(String username, String plainPassword, String email, String role) {
        byte[] salt = generateSalt();
        String hash = hashPassword(plainPassword, salt);
        User u = new User(username, Base64.getEncoder().encodeToString(salt), hash, email, role);
        users.put(username, u);
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[12];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private String hashPassword(String plain, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private String hashPassword(String plain, String saltBase64) {
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        return hashPassword(plain, salt);
    }

    private boolean verifyPassword(String plain, String saltBase64, String hash) {
        try {
            String h = hashPassword(plain, saltBase64);
            return h.equals(hash);
        } catch (Exception ex) { return false; }
    }

    static class User {
        String username;
        String salt;
        String passwordHash;
        String email;
        String role;
        User(String u, String s, String h, String e, String r){username=u;salt=s;passwordHash=h;email=e;role=r;}
    }
    static class Agent { String id; String name; String email; String role; Agent(String id,String name,String email,String role){this.id=id;this.name=name;this.email=email;this.role=role;} }
    static class Complaint {
        String id; String title; String reporter; String assignedTo; String category; String priority; String status; String createdAt; String resolvedAt; String details;
        Complaint(String id,String title,String reporter,String assignedTo,String category,String priority,String status,String createdAt,String resolvedAt,String details){
            this.id=id;this.title=title;this.reporter=reporter;this.assignedTo=assignedTo;this.category=category;this.priority=priority;this.status=status;this.createdAt=createdAt;this.resolvedAt=resolvedAt;this.details=details;
        }
    }
    static class Feedback {
        String id; String complaintId; String author; String date; String comment; int rating;
        Feedback(String id,String complaintId,String author,String date,String comment,int rating){this.id=id;this.complaintId=complaintId;this.author=author;this.date=date;this.comment=comment;this.rating=rating;}
    }
    static class Attachment { String name; byte[] data; Attachment(String name, byte[] data){this.name=name;this.data=data;} }
    static class HistoryEntry { String time; String action; String actor; String note; HistoryEntry(String t,String a,String ac,String n){time=t;action=a;actor=ac;note=n;} }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
