# Screenshots and Documentation

This directory contains screenshots and visual documentation for the ResolveIt Complaint Management System project.

## Contents

### Application GUI Screenshots
- **Login Screen** - Shows the authentication interface with role selection (Admin, Agent, Student)
- **Admin Dashboard** - Administrative dashboard with analytics and complaint statistics
- **Agent Queue** - Support agent view showing assigned complaints
- **Student Complaints** - Student interface for submitting and tracking complaints
- **Feedback Panel** - Interface for managing feedback on complaints
- **Agents Management** - Admin panel for managing support agents

### Database Structure
- **users table** - User authentication and role management
- **agents table** - Support agent information
- **complaints table** - Complaint records and status tracking
- **feedback table** - Feedback and comments on complaints
- **attachments table** - File attachments associated with complaints

### Key Features Demonstrated
- Role-based access control (RBAC)
- Complaint lifecycle management (Open → In Progress → Resolved → Closed)
- Real-time status updates
- File attachment support
- CSV export functionality
- Agent assignment system
- Feedback management
- Priority and category-based organization

## How to Use These Screenshots

1. **For Understanding the UI**: Review screenshots to understand the application layout and workflow
2. **For Development**: Reference these screenshots during feature development
3. **For Documentation**: Include these in project presentations and reports
4. **For Testing**: Compare actual UI with these reference screenshots during testing

## Technologies Used (As Shown)

- **Java Swing**: Desktop GUI framework
- **MySQL**: Database backend
- **JDBC**: Database connectivity
- **JTable**: Data display components
- **GridBagLayout**: UI layout management
- **Color Scheme**: Cyan/Dark theme for modern appearance

## User Roles Demonstrated

### 1. Admin
- Full system access
- Create/Edit/Delete complaints
- Manage agents
- Assign complaints
- View analytics
- Export reports

### 2. Agent  
- View assigned complaints
- Update complaint status
- Add feedback
- Attach files

### 3. Student
- Submit complaints
- Track complaint status
- View feedback
- View complaint history

## Default Credentials

- **Admin**: admin / 123
- **Agent**: agent / 123  
- **Student**: student / 123

## Notes

- All screenshots are from the fully functional ResolveIt application
- Database structure is optimized for MySQL 5.7+
- UI uses consistent color scheme (Cyan #00FFFF on Dark #000000)
- Font used: Poppins (can be substituted with default system fonts)

For more detailed information, please refer to the main [README.md](../README.md) file.
