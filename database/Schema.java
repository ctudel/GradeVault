package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Schema
 */
public class Schema {
  public static void resetDatabase() {
    try {
      Connection connection = DB.getDatabaseConnection();
      Statement stmt = connection.createStatement();
      stmt.execute("DROP DATABASE IF EXISTS " + System.getenv("CS410_DATABASENAME") + ";");
      stmt.execute("CREATE DATABASE " + System.getenv("CS410_DATABASENAME") + ";");

    } catch (SQLException e) {
      System.err.println("Failed to drop database");
      e.printStackTrace();
    }
  }

  public static void createTables() {
    try {
      Connection connection = database.DB.getDatabaseConnection();
      Statement stmt = connection.createStatement();

      System.out.println("Creating 'class' table...");
      stmt.execute(
          "CREATE TABLE class (" +
              "class_id int PRIMARY KEY AUTO_INCREMENT,\n" +
              "course_number varchar(20) NOT NULL,\n" +
              "term varchar(4) NOT NULL,\n" +
              "section_number int NOT NULL,\n" +
              "description TEXT\n" +
              ");");
      System.out.println("Successfully created 'class' table");

      System.out.println("Creating 'category' table...");
      stmt.execute(
          "CREATE TABLE category (" +
              "category_name varchar(255) PRIMARY KEY NOT NULL,\n" +
              "weight int NOT NULL\n" +
              ");");
      System.out.println("Successfully created 'category' table");

      System.out.println("Creating 'student' table...");
      stmt.execute(
          "CREATE TABLE student (" +
              "student_id int PRIMARY KEY AUTO_INCREMENT,\n" +
              "username varchar(255) NOT NULL,\n" +
              "name varchar(255) NOT NULL\n" +
              ");");
      System.out.println("Successfully created 'student' table");

      System.out.println("Creating 'assignment' table...");
      stmt.execute(
          "CREATE TABLE assignment (" +
              "assignment_name varchar(255) PRIMARY KEY NOT NULL,\n" +
              "category_name varchar(255) NOT NULL,\n" +
              "point_value double NOT NULL,\n" +
              "description TEXT,\n" +
              "FOREIGN KEY (category_name) REFERENCES category(category_name)\n" +
              ");");
      System.out.println("Successfully created 'assignment' table");

      createRelations(connection); // Relationships for each 'main' table
      connection.close();

    } catch (SQLException e) {
      System.err.println("SQLException: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void createRelations(Connection connection) {
    try {
      Statement stmt = connection.createStatement();

      System.out.println("Creating 'enrolled_students' table...");
      stmt.execute(
          "CREATE TABLE enrolled_students (" +
              "class_id int NOT NULL,\n" +
              "student_id int NOT NULL,\n" +
              "FOREIGN KEY (class_id) REFERENCES class(class_id),\n" +
              "FOREIGN KEY (student_id) REFERENCES student(student_id),\n" +
              "PRIMARY KEY (class_id, student_id)\n" +
              ");");
      System.out.println("Successfully created 'enrolled_students' table");

      System.out.println("Creating 'class_assignments' table...");
      stmt.execute(
          "CREATE TABLE class_assignments (" +
              "class_id int NOT NULL,\n" +
              "assignment_name varchar(255) NOT NULL,\n" +
              "FOREIGN KEY (class_id) REFERENCES class(class_id),\n" +
              "FOREIGN KEY (assignment_name) REFERENCES assignment(assignment_name),\n" +
              "PRIMARY KEY (class_id, assignment_name)\n" +
              ");");
      System.out.println("Successfully created 'class_assignments' table");

    } catch (SQLException e) {
      System.err.println("SQLException: " + e.getMessage());
      e.printStackTrace();
    }

  }

  // public static void create

  public static void main(String[] args) {
    try {
      System.out.println("Starting connection to database...");
      Connection conn = database.DB.getDatabaseConnection();

      System.out.println("Dropping and recreating database...");
      resetDatabase();
      System.out.println("Successfully recreated database");

      System.out.println("Creating 'class' table...");
      createTables();
      System.out.println("Successfully Created 'class' table");

      System.out.println("Inserting data..");
      conn.createStatement()
          .execute("INSERT INTO class(course_number, term, section_number) VALUES ('CS410', 'Sp20', 001)");
      System.out.println("Succesfully inserted data..");

      System.out.println("Printing table...");
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM class");
      ResultSetMetaData data = rs.getMetaData();
      int cols = data.getColumnCount();

      // Prints all rows from ResultSet
      while (rs.next()) {
        // Print each column data (not column names)
        for (int i = 1; i <= cols; i++) {
          System.out.print(rs.getString(i) + " ");
        }
        System.out.println();
      }

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
