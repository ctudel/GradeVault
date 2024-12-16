# README

## About the implementation
This project implements a comprehensive classroom management system using Java and MySQL. The system is designed to handle multiple classes, students, assignments, and grading categories. It utilizes a relational database structure to efficiently manage the relationships between various entities such as classes, students, assignments, and grades. The implementation includes features for creating and managing classes, enrolling students, defining assignment categories with weights, creating assignments, and assigning grades.

The core functionality is divided into several manager classes, including ClassManager and StudentManager, which handle operations related to classes and students respectively. The database schema is defined in the Schema class, which sets up tables for classes, students, categories, assignments, and their relationships. The system supports operations like adding new classes and students, enrolling students in classes, creating assignments, and recording grades, all while maintaining data integrity through foreign key relationships and unique constraints.

Everything is encapsulated in the Driver class which contains the interactive program containing the UI. This involves a user experience revolving around the terminal where they are presented with a text menu and use commands through text. This allows the user to utilize all the previous functions that were mentioned without having to know how to navigate the entire backend.
