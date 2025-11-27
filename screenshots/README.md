# ResolveIt - Complaint Management System
## Visual Documentation & Project Reference

This directory contains comprehensive visual documentation and project reference for the ResolveIt Complaint Management System. The ResolveIt system is a role-based Java Swing desktop application integrated with MySQL database for managing student complaints, feedback, and resolution tracking in an educational institution.

---

## 📋 Contents

### 1. Application GUI

The graphical user interface showcases all major screens and functionalities:

- **Login Screen** - Authentication interface with role selection (Admin, Agent, Student)
- **Admin Dashboard** - Administrative dashboard with analytics and complaint statistics
- **Agent Queue** - Support agent view showing assigned complaints
- **Student Complaints** - Student interface for submitting and tracking complaints
- **Feedback Panel** - Interface for managing feedback on complaints
- **Agents Management** - Admin panel for managing support agents

### 2. Database Structure

MySQL database tables demonstrating the data architecture:

- **users table** - User authentication and role management
- **agents table** - Support agent information and contact details
- **complaints table** - Complaint records, status tracking, and metadata
- **feedback table** - Feedback and comments on complaints
- **attachments table** - File attachments associated with complaints

---

## ⚙️ Key Features

- **Role-based Access Control (RBAC)** - Three distinct user roles with specific permissions
- **Complaint Lifecycle Management** - Status progression: Open → In Progress → Resolved → Closed
- **Real-time Status Updates** - Instant reflection of complaint status changes
- **File Attachment Support** - Ability to attach and download files for complaints
- **CSV Export Functionality** - Export complaints data for reporting
- **Agent Assignment System** - Efficient distribution of complaints to support agents
- **Feedback Management** - Add and view feedback comments on complaints
- **Priority and Category Organization** - Classify complaints by priority (Low/Medium/High/Critical) and category (Authentication/Payments/UI/Stability/Other)
- **Timestamp Tracking** - Automatic creation and resolution timestamps

---

## 💻 Technology Stack

- **Frontend**: Java Swing (Desktop GUI Framework)
- **Backend**: Java with JDBC
- **Database**: MySQL 5.7+
- **UI Components**: JTable, GridBagLayout, JComboBox, JDialog
- **Color Scheme**: Cyan (#00FFFF) on Dark background (#000000)
- **Font**: Poppins (with system font fallback)
- **File Handling**: File I/O for attachments
- **Data Export**: CSV format

---

## 👥 User Roles & Permissions

### 1. Admin (Administrator)
- Full system access
- Create, Edit, Delete complaints
- Manage support agents (Add/Remove)
- Assign complaints to agents
- View system analytics and charts
- Export reports in CSV format
- Update complaint status and priority
- Manage feedback
- File attachment management

### 2. Agent (Support Agent)
- View dashboard with analytics
- Access "My Queue" tab showing assigned complaints
- Update complaint status (Mark as Resolved)
- Add feedback comments to complaints
- Attach files to complaints
- Download attachments
- View complaint details

### 3. Student (Reporter)
- View dashboard analytics
- Submit new complaints
- Track complaint status in "My Complaints" tab
- View feedback on submitted complaints
- Access complaint history
- View assigned agents and current status

---

## 🔐 Default Test Credentials

```
Admin Account:
  Username: admin
  Password: 123
  Role: ADMIN

Agent Account:
  Username: agent
  Password: 123
  Role: AGENT

Student Account:
  Username: student
  Password: 123
  Role: STUDENT
```

---

## 📊 Data Models

### Complaint Object Structure
- **ID**: Unique identifier (auto-generated with prefix 'C')
- **Title**: Complaint subject
- **Reporter**: Student username who filed complaint
- **Assigned To**: Agent ID or "Unassigned"
- **Category**: Classification (Authentication, Payments, UI, Stability, Other)
- **Priority**: Severity level (Low, Medium, High, Critical)
- **Status**: Current state (Open, In Progress, Resolved, Closed)
- **Created At**: Timestamp of complaint submission
- **Resolved At**: Timestamp when complaint was resolved
- **Details**: Full complaint description

### Feedback Object Structure
- **ID**: Unique feedback identifier (auto-generated with prefix 'F')
- **Complaint ID**: Associated complaint
- **Author**: User who added feedback
- **Date**: Timestamp of feedback
- **Comment**: Feedback text

### Agent Object Structure
- **ID**: Agent identifier
- **Name**: Full name of agent
- **Email**: Contact email
- **Role**: Job role/designation

---

## 📁 Project File Structure

```
ResolveIt-Complaint-Management/
├── screenshots/
│   ├── README.md (This file)
│   ├── Login Screen.png
│   ├── Admin Dashboard.png
│   ├── Agent Queue.png
│   ├── Student Complaints.png
│   ├── Feedback Panel.png
│   ├── Agents Management.png
│   ├── Database Tables.png
│   └── MySQL Schema.png
├── src/
│   └── Main.java
├── Database/
│   └── resolveit_schema.sql
└── README.md (Main project README)
```

---

## 🎨 UI Design Highlights

- **Modern Dark Theme**: Cyan and dark color palette for contemporary look
- **Responsive Layout**: GridBagLayout for flexible component positioning
- **Tabbed Interface**: Organized navigation with JTabbedPane
- **Table Views**: Data display with sortable JTable components
- **Dialog Windows**: Pop-up dialogs for editing and detailed operations
- **Consistent Styling**: Rounded buttons, uniform fonts, cohesive design
- **User-friendly Forms**: Input validation and clear labeling

---

## ✨ Notable Implementation Details

- **UUID-based ID Generation**: Unique complaint and feedback IDs using UUID
- **LinkedHashMap Usage**: Maintains insertion order for data display
- **Date Formatting**: Standardized date format (yyyy-MM-dd HH:mm:ss)
- **Byte Array Storage**: Attachment data stored in memory with human-readable size display
- **Event-driven Architecture**: Action listeners for all interactive components
- **File I/O Operations**: Reading and writing files with proper stream handling
- **Custom Border Class**: RoundedButtonBorder for stylized UI elements
- **Bar Chart Visualization**: Dynamic chart generation based on complaint statistics

---

## 🚀 Running the Application

1. Ensure Java Development Kit (JDK) 8 or higher is installed
2. Set up MySQL database and import `resolveit_schema.sql`
3. Compile and run `Main.java`
4. Login using default credentials (admin/123, agent/123, or student/123)
5. Navigate through different tabs based on user role

---

## 📝 Notes

- All data shown in screenshots is from the fully functional ResolveIt application
- Database is optimized for MySQL 5.7 and later versions
- UI maintains consistent color scheme throughout the application
- System supports unlimited complaints, agents, and feedback entries
- File attachments are stored in application memory (suitable for demo purposes)
- Timestamps are automatically generated for all events

---

**For complete project details, source code, and database schema, please refer to the main [README.md](https://github.com/udanuj07/ResolveIt-Complaint-Management/blob/main/README.md) file in the repository root.**
