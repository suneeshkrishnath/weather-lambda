# General Instructions

## Overview
This document provides general guidelines and instructions for contributing to the Weather Notification Service project. It includes information on project setup, development workflow, and collaboration practices.

---

## Project Setup

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   ```

2. **Install Dependencies**:
   - Ensure Java 21 is installed.
   - Use Maven to install project dependencies:
     ```bash
     mvn clean install
     ```

3. **Set Up AWS Credentials**:
   - Configure AWS CLI with appropriate credentials:
     ```bash
     aws configure
     ```

4. **Run the Application Locally**:
   ```bash
   mvn spring-boot:run
   ```

---

## Development Workflow

1. **Branching Strategy**:
   - Use `main` for production-ready code.
   - Create feature branches for new functionality (e.g., `feature/weather-api`).

2. **Code Reviews**:
   - Submit pull requests for all changes.
   - Ensure at least one team member reviews and approves the PR.

3. **Testing**:
   - Write unit and integration tests for all new code.
   - Run tests locally before submitting a PR:
     ```bash
     mvn test
     ```

4. **Commit Messages**:
   - Use descriptive commit messages (e.g., `Add weather prediction service`).

---

## Collaboration Practices

1. **Communication**:
   - Use project management tools (e.g., Jira, Trello) to track progress.
   - Document decisions and discussions in the project wiki.

2. **Documentation**:
   - Update relevant documentation for any new features or changes.

3. **Version Control**:
   - Avoid committing directly to `main`.
   - Rebase feature branches before merging.

---

## Conclusion
By following these general instructions, contributors can ensure a smooth and efficient development process for the Weather Notification Service project.
