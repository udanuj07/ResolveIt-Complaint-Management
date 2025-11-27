-- ResolveIt Complaint Management System Database Schema
-- Created for GenZ University

CREATE DATABASE IF NOT EXISTS resolveit;
USE resolveit;

-- Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'AGENT', 'STUDENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Agents Table
CREATE TABLE agents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    agent_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Complaints Table
CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    reporter VARCHAR(100) NOT NULL,
    assigned_to VARCHAR(100),
    category VARCHAR(50),
    priority VARCHAR(20),
    status ENUM('Open', 'In Progress', 'Resolved', 'Closed') DEFAULT 'Open',
    details LONGTEXT,
    created_at DATETIME,
    resolved_at DATETIME,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX(complaint_id),
    INDEX(reporter),
    INDEX(status)
);

-- Feedback Table
CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    feedback_id VARCHAR(50) UNIQUE NOT NULL,
    complaint_id VARCHAR(50) NOT NULL,
    author VARCHAR(100) NOT NULL,
    comment LONGTEXT,
    date DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE CASCADE,
    INDEX(complaint_id)
);

-- Attachments Table
CREATE TABLE attachments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(50) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    file_data LONGBLOB,
    file_size BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE CASCADE,
    INDEX(complaint_id)
);

-- Initial Data - Sample Users
INSERT INTO users (username, password, email, role) VALUES
('admin', '123', 'admin@uni.edu', 'ADMIN'),
('agent', '123', 'agent@uni.edu', 'AGENT'),
('student', '123', 'student@uni.edu', 'STUDENT');

-- Initial Data - Sample Agents
INSERT INTO agents (agent_id, name, email, role) VALUES
('AG01', 'Neha Sharma', 'neha@uni.edu', 'Support'),
('AG02', 'Rakesh Rao', 'rakesh@uni.edu', 'Senior');

-- Initial Data - Sample Complaint
INSERT INTO complaints (complaint_id, title, reporter, assigned_to, category, priority, status, details, created_at, resolved_at) VALUES
('C123456A', 'Sample login issue', 'student', 'Unassigned', 'Authentication', 'Medium', 'Open', 'Sample complaint for testing', NOW(), NULL);

-- Create Indexes for Better Performance
CREATE INDEX idx_user_role ON users(role);
CREATE INDEX idx_complaint_status ON complaints(status);
CREATE INDEX idx_complaint_category ON complaints(category);
CREATE INDEX idx_feedback_complaint ON feedback(complaint_id);

-- Grants (if needed)
-- GRANT ALL PRIVILEGES ON resolveit.* TO 'resolveit_user'@'localhost' IDENTIFIED BY 'password';
-- FLUSH PRIVILEGES;
