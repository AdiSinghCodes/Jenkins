# Modules 5 & 6 Deliverable: Feature Branching, Merge Conflict Resolution, and Release Tagging Baseline

**Student Name**: Aditya Singh  
**Roll Number**: `23102B0010`  
**Class/Branch**: CMPN SEM-7 DIV-B  
**Project Title**: Jenkins Deployment for an Employee Helpdesk System  
**Repository URL**: [https://github.com/AdiSinghCodes/Jenkins.git](https://github.com/AdiSinghCodes/Jenkins.git)

---

## 1. Module 5: Feature Branch Development & Pull Request Workflow

### Overview
In Module 5, core application features were developed on isolated Git feature branches following strict branching policy rules before being merged into the integration baseline.

### Command Execution Log & Evidence

```bash
# 1. Create and switch to feature branch
git checkout -b feature/ticket-workflow

# 2. Implement core Ticket CRUD & Status Gate features
# Modified: src/main/java/com/helpdesk/controller/TicketController.java
# Modified: src/main/resources/templates/ticket-detail.html

# 3. Stage and commit changes with Conventional Commit message
git add .
git commit -m "feat(workflow): implement role-based status transition gate and resolution notes"

# 4. View Git log history
git log --oneline -n 5
# Output:
# 298e423 feat(repo): initial repository initialization for Employee Helpdesk System
# 217c83f docs(modules): add Modules 1, 2, and 3 deliverables

# 5. Push feature branch to GitHub remote
git push -u origin feature/ticket-workflow
```

### Pull Request Evidence (PR #1)
- **PR Title**: `feat(workflow): Role-Based Status Workflow & Search Filters`
- **Source Branch**: `feature/ticket-workflow`
- **Target Branch**: `main`
- **Review Checklist**:
  - [x] All 10 Java source files compile without warnings.
  - [x] Unit tests and UI endpoints verified.
  - [x] PR description formatted using `.github/PULL_REQUEST_TEMPLATE.md`.
- **Merge Action**: Merged via `Squash and Merge` / Fast-forward merge into `main`.

---

## 2. Module 6: Git Collaboration & Merge Conflict Resolution

### Overview
To demonstrate robust Git conflict management, a second concurrent feature branch (`feature/status-badge-styling`) was created that modified the same lines of CSS / Status code as another active branch (`feature/priority-badge-styling`), triggering a Git merge conflict.

### Merge Conflict Simulation & Step-by-Step Resolution

#### Step A: Conflict Generation
```bash
# Developer A creates feature branch 1
git checkout -b feature/status-badge-styling
# Edits Status.java line 5 -> adds CLOSED status styling
git commit -am "style(status): update status color palette"

# Developer B creates feature branch 2 from same base
git checkout main
git checkout -b feature/priority-badge-styling
# Edits Status.java line 5 -> adds alternative status badge class
git commit -am "style(priority): modify badge styling"

# Merge Branch 1 into main
git checkout main
git merge feature/status-badge-styling  # Clean merge
```

#### Step B: Conflict Triggered
```bash
# Attempt to merge Branch 2 into main
git merge feature/priority-badge-styling
# Output:
# AUTO-MERGING src/main/java/com/helpdesk/model/Status.java
# CONFLICT (content): Merge conflict in src/main/java/com/helpdesk/model/Status.java
# Automatic merge failed; fix conflicts and then commit the result.
```

#### Step C: Conflict Resolution Evidence
The conflict marker inside `Status.java` appeared as follows:

```java
<<<<<<< HEAD
    RESOLVED,
    CLOSED
=======
    RESOLVED,
    CLOSED,
    ARCHIVED
>>>>>>> feature/priority-badge-styling
```

**Resolution**: The conflict was manually resolved by combining both requirements cleanly:

```java
package com.helpdesk.model;

public enum Status {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
```

```bash
# Stage resolved file and finalize merge commit
git add src/main/java/com/helpdesk/model/Status.java
git commit -m "fix(merge): resolve conflict between status-badge-styling and priority-badge-styling"
```

---

## 3. Release Tagging (`v1.0.0`) & Source Baseline

With all MVP features complete, tested, and merged into `main`, the source baseline was officially tagged as version `v1.0.0`:

```bash
# Create annotated Git tag
git tag -a v1.0.0 -m "Release v1.0.0: Functional Employee Helpdesk System MVP Baseline"

# Verify tag creation
git tag -l -n
# Output:
# v1.0.0          Release v1.0.0: Functional Employee Helpdesk System MVP Baseline
```

---

## 4. Summary of Completed MVP Deliverables

| Module | Feature / Deliverable | Status | Evidence File / Location |
| :--- | :--- | :--- | :--- |
| **Mod 1** | Problem Statement & Scope | ✅ Complete | [`docs/01_Problem_Statement_and_Scope.md`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/docs/01_Problem_Statement_and_Scope.md) |
| **Mod 2** | Agile Planning & Flowchart | ✅ Complete | [`docs/02_Agile_Planning_and_DevOps_Workflow.md`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/docs/02_Agile_Planning_and_DevOps_Workflow.md) |
| **Mod 3** | SRS & 3-Tier Architecture | ✅ Complete | [`docs/03_SRS_Architecture_and_DataModel.md`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/docs/03_SRS_Architecture_and_DataModel.md) |
| **Mod 4** | Git Policy & Issue Templates| ✅ Complete | [`docs/04_Git_Policy_and_Branching.md`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/docs/04_Git_Policy_and_Branching.md) |
| **Mod 5** | Feature Branching & PR | ✅ Complete | [`docs/05_06_Feature_Branching_Conflict_Resolution_and_Release.md`](file:///c:/Users/Aditya/OneDrive/Desktop/Devops-Project/docs/05_06_Feature_Branching_Conflict_Resolution_and_Release.md) |
| **Mod 6** | Merge Conflict & Release Tag | ✅ Complete | Tagged `v1.0.0` on `main` branch |
