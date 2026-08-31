# Modules 13 & 14 Deliverable: Configuration Management Script, Automated Provisioning, and Reliability Validation

**Student Name**: Aditya Singh  
**Roll Number**: `23102B0010`  
**Class/Branch**: CMPN SEM-7 DIV-B  
**Project Title**: Jenkins Deployment for an Employee Helpdesk System  
**Repository URL**: [https://github.com/AdiSinghCodes/Jenkins.git](https://github.com/AdiSinghCodes/Jenkins.git)

---

## 1. Module 13: Server Prerequisites & Configuration Specification

### Target Server Prerequisites Matrix

| Category | Component / Specification | Purpose / Function |
| :--- | :--- | :--- |
| **Packages** | `openjdk-17-jre-headless`, `docker.io`, `docker-compose`, `curl`, `git`, `nginx` | Application runtime, containerization, reverse proxying, and health checks. |
| **User & Group** | `helpdesk` (System User / Group) | Isolated non-root execution context for container security. |
| **Directories** | `/opt/helpdesk/{config,logs}` | Application deployment home, configuration files, and log retention. |
| **Ports** | `8082` (App Container), `80` (Nginx Reverse Proxy), `22` (SSH) | Network connectivity boundaries. |
| **Services** | `docker`, `nginx` | System daemon services managed by Systemd. |

---

## 2. Ansible Inventory & Playbook Architecture

### Inventory Configuration ([`devops/ansible/inventory.ini`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/devops/ansible/inventory.ini))
Defines target node host groups (`[target_nodes]`) and deployment variables (`app_port=8082`, `app_dir=/opt/helpdesk`).

### Idempotent Playbook ([`devops/ansible/playbook.yml`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/devops/ansible/playbook.yml))
The Ansible playbook is fully idempotent—executing it multiple times results in `changed=0` on an already provisioned node:

```mermaid
graph TD
    Playbook[Ansible Playbook: playbook.yml] --> Task1[1. Update APT Cache]
    Task1 --> Task2[2. Install Prerequisites: Java 17, Docker, Nginx]
    Task2 --> Task3[3. Create Group & System User 'helpdesk']
    Task3 --> Task4[4. Create /opt/helpdesk Directories]
    Task4 --> Task5[5. Enable & Start Docker Service]
    Task5 --> Task6[6. Deploy Container helpdesk-app-container]
    Task6 --> Task7[7. Verify Actuator Health Status HTTP 200]
```

### Ansible Playbook Execution Command
```bash
ansible-playbook -i devops/ansible/inventory.ini devops/ansible/playbook.yml
```

---

## 3. Module 14: Idempotency Evidence & Automated Rollback Demonstration

### Idempotency Validation Proof
Upon running the playbook a second time against a provisioned server:

```text
PLAY RECAP *********************************************************************
localhost                  : ok=9    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0
```
- `changed=0` proves that Ansible verified existing system state without causing unnecessary restarts or system state drift.

### Automated Health Check & Zero-Downtime Rollback Flow
The reliability script [`health_check_rollback.sh`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/devops/ansible/health_check_rollback.sh) safeguards production deployments:

```mermaid
flowchart TD
    Deploy[Deploy New Container Image] --> QueryHealth[Query GET /actuator/health]
    QueryHealth --> Condition{HTTP Status == 200?}
    Condition -->|Yes| Success[Mark Deployment SUCCESS - Keep Live]
    Condition -->|No| Fail[Health Check FAILED!]
    Fail --> Stop[Stop Failing Container]
    Stop --> Rollback[Launch Previous Stable Release Image v1.0.0-stable]
    Rollback --> Verify[Re-verify Health Status]
    Verify --> Restored[Production Service Restored Automatically]
```

### Rollback Execution Command
```bash
bash devops/ansible/health_check_rollback.sh
```
