# 🛠️ Jenkins Deployment for an Employee Helpdesk System

[![Build Status](https://img.shields.io/badge/Jenkins-CI%2FCD-blue.svg?logo=jenkins)](https://github.com/AdiSinghCodes/Jenkins)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED.svg?logo=docker)](https://github.com/AdiSinghCodes/Jenkins)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F.svg?logo=springboot)](https://github.com/AdiSinghCodes/Jenkins)
[![Java 17](https://img.shields.io/badge/Java-17-ED8B00.svg?logo=java)](https://github.com/AdiSinghCodes/Jenkins)
[![License](https://img.shields.io/badge/Coursework-DevOps%20SEM--7-indigo.svg)](https://github.com/AdiSinghCodes/Jenkins)

---

## 📌 Project Overview

**Student Name**: Aditya Singh  
**Roll Number**: `23102B0010`  
**Class/Branch**: CMPN SEM-7 DIV-B  
**Project Title**: Jenkins Deployment for an Employee Helpdesk System  
**Repository**: [https://github.com/AdiSinghCodes/Jenkins.git](https://github.com/AdiSinghCodes/Jenkins.git)

The **Employee Helpdesk System** is an enterprise IT service management web application paired with an end-to-end automated **DevOps CI/CD Lifecycle**. The system allows employees to submit IT incident tickets, track progress, and enables IT support teams to manage role-based status workflows via an interactive real-time analytics dashboard.

---

## 🏗️ Technical Architecture & DevOps Flow

```mermaid
graph TD
    Developer([Developer]) -->|Git Push| GitHub[(GitHub Repository)]
    GitHub -->|Poll / Webhook| Jenkins[Jenkins CI/CD Server]
    
    subgraph CI/CD Pipeline Lifecycle
        Jenkins --> Stage1[1. Checkout SCM]
        Stage1 --> Stage2[2. Maven Build & Package]
        Stage2 --> Stage3[3. Selenium UI Quality Gate]
        Stage3 --> Stage4[4. Docker Image Packaging]
        Stage4 --> Stage5[5. Registry Publish]
        Stage5 --> Stage6[6. Ansible Provisioning & Deployment]
    end
    
    Stage6 --> TargetServer[Target Production Node]
    TargetServer --> AppContainer[Helpdesk Container :8082]
    AppContainer --> H2Database[(H2 Database)]
    EndUsers([Employees & IT Staff]) -->|HTTP Port 8082| AppContainer
```

---

## 🚀 Quick Start & Local Setup

### Prerequisites
- **Java 17 JDK** or higher
- **Git**

### Running the Application Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/AdiSinghCodes/Jenkins.git
   cd Jenkins
   ```

2. **Build and Run using Maven Wrapper**:
   ```bash
   # On Windows PowerShell
   .\mvnw spring-boot:run
   
   # On Linux / macOS
   ./mvnw spring-boot:run
   ```

3. **Access the Web Dashboard**:
   Open your browser and navigate to:
   - **Dashboard**: `http://localhost:8082/dashboard`
   - **All Tickets**: `http://localhost:8082/tickets`
   - **Submit Ticket**: `http://localhost:8082/tickets/new`
   - **Actuator Health Check**: `http://localhost:8082/actuator/health`
   - **H2 Database Console**: `http://localhost:8082/h2-console` (JDBC URL: `jdbc:h2:mem:helpdeskdb`)

---

## 📁 Repository Directory Structure

```text
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md
│   │   └── feature_request.md
│   └── PULL_REQUEST_TEMPLATE.md
├── docs/
│   ├── 01_Problem_Statement_and_Scope.md
│   ├── 02_Agile_Planning_and_DevOps_Workflow.md
│   ├── 03_SRS_Architecture_and_DataModel.md
│   ├── 04_Git_Policy_and_Branching.md
│   ├── 13_14_Ansible_Configuration_and_Provisioning.md
│   └── 15_Final_DevOps_Project_Report.md
├── devops/
│   ├── jenkins/
│   │   ├── Jenkinsfile
│   │   └── jenkins_job_config.xml
│   ├── docker/
│   │   ├── Dockerfile
│   │   ├── docker-compose.yml
│   │   └── docker_lifecycle.sh
│   └── ansible/
│       ├── inventory.ini
│       ├── playbook.yml
│       └── health_check_rollback.sh
├── src/
│   ├── main/
│   │   ├── java/com/helpdesk/
│   │   └── resources/
│   └── test/
│       └── java/com/helpdesk/
├── pom.xml
├── README.md
└── .gitignore
```

---

## 🔀 Git Branching Policy

We strictly adhere to standard Gitflow rules:
- **`main`**: Production release-ready code (tagged e.g., `v1.0.0`).
- **`develop`**: Integration branch for upcoming features.
- **`feature/*`**: Individual feature developments (e.g., `feature/ticket-crud`).
- **`bugfix/*`**: Emergency fixes (e.g., `bugfix/port-binding`).

Commit message convention: `type(scope): message` (e.g., `feat(tickets): add role-based status workflow`).

---

## 👨‍💻 Author & Maintainer

- **Aditya Singh** (Roll No: `23102B0010`)
- Department of Computer Engineering (CMPN SEM-7 DIV-B)
- GitHub: [@AdiSinghCodes](https://github.com/AdiSinghCodes)
