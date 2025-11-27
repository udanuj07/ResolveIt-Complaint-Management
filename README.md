# ResolveIt - Complaint Management System

A comprehensive complaint management system built for GenZ University using **Java Swing** and **MySQL**. This application enables students, support agents, and administrators to efficiently manage complaints with role-based access control.

## 📋 Project Overview

ResolveIt is a desktop application that streamlines complaint resolution by providing:
- **Role-based access control** (Admin, Agent, Student)
- **Complaint tracking and assignment**
- **Real-time status updates**
- **Feedback management system**
- **File attachment support**
- **CSV export functionality**

## 🎯 Features

### For Students
- Submit new complaints with detailed description
- Track complaint status in real-time
- View assigned agent information
- Track complaint resolution history

### For Support Agents
- View assigned complaints queue
- Update complaint status
- Add feedback and comments
- Attach files for documentation

### For Administrators
- Full complaint management (create, edit, delete)
- Agent management
- Complaint assignment to agents
- Generate complaint reports (CSV export)
- System analytics and dashboard

## 🛠️ Technology Stack

- **Frontend**: Java Swing
- **Backend**: Java
- **Database**: MySQL
- **Build Tool**: JDK 8+
- **Version Control**: Git

## 📦 Project Structure

```
ResolveIt-Complaint-Management/
├── src/
│   └── Main.java              # Main application class
├── database/
│   └── resolveit_schema.sql   # Database schema and initial data
├── screenshots/               # UI screenshots and database structure
├── .gitignore                 # Git ignore rules
└── README.md                  # This file
```

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- MySQL Workbench or any MySQL client

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/udanuj07/ResolveIt-Complaint-Management.git
   cd ResolveIt-Complaint-Management
   ```

2. **Setup Database**
   - Open MySQL Workbench
   - Execute the SQL script: `database/resolveit_schema.sql`
   - Verify tables are created successfully

3. **Compile the Java Application**
   ```bash
   javac src/Main.java
   ```

4. **Run the Application**
   ```bash
   java -cp src Main
   ```

## 👤 Default Test Credentials

### Admin Account
- **Username**: admin
- **Password**: 123
- **Role**: ADMIN

### Agent Account
- **Username**: agent
- **Password**: 123
- **Role**: AGENT

### Student Account
- **Username**: student
- **Password**: 123
- **Role**: STUDENT

## 📊 Database Schema

The system uses the following main tables:
- **users** - User accounts and authentication
- **agents** - Support agent information
- **complaints** - Complaint records
- **feedback** - Feedback and comments on complaints
- **attachments** - File attachments for complaints

## 🎨 User Interface

### Dashboard Tab
- Complaint statistics by status
- Quick action buttons
- System analytics

### Admin Panel Tab
- Create/Edit complaints
- Assign complaints to agents
- Add feedback
- View all complaints

### Agents Tab (Admin only)
- Manage support agents
- Add/Remove agents
- View agent details

### My Queue Tab (Agent only)
- View assigned complaints
- Mark complaints as resolved
- Update status in real-time

### My Complaints Tab (Student only)
- Submit new complaints
- Track complaint status
- View complaint history

### Feedback Tab
- View feedback on complaints
- Add new feedback entries

## 📈 Complaint Lifecycle

1. **Open** - Complaint submitted
2. **In Progress** - Complaint assigned to agent
3. **Resolved** - Issue resolved, pending closure
4. **Closed** - Complaint closed

## 💾 CSV Export

Administrators can export all complaints to CSV format with the following fields:
- Complaint ID
- Title
- Reporter Name
- Assigned Agent
- Category
- Priority
- Status
- Created Date
- Resolved Date

## 🔐 Security Features

- Password-based authentication
- Role-based access control
- Session management
- Input validation

## 📝 Complaint Categories

- Authentication Issues
- Payment Problems
- User Interface Bugs
- System Stability
- Other

## ⚠️ Priority Levels

- **Low** - Minor issues
- **Medium** - Standard issues
- **High** - Urgent issues
- **Critical** - System-breaking issues

## 🤝 Contributing

To contribute to this project:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit your changes (`git commit -am 'Add improvement'`)
4. Push to the branch (`git push origin feature/improvement`)
5. Create a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

**Udanuj Singh**
- GitHub: [@udanuj07](https://github.com/udanuj07)

## 📧 Support

For issues and questions, please open an issue on GitHub or contact the development team.

## 🎓 Educational Use

This project is developed as part of the BCA AI/ML program at Galgotia University and is intended for educational purposes.

---

**Last Updated**: November 2025
