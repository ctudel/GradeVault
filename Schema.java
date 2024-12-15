import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Schema
 */
public class Schema {
  /**
   * Reset the database
   */
  public static void resetDatabase() {
    try {
      Connection connection = DB.getDatabaseConnection();
      Statement stmt = connection.createStatement();
      stmt.execute("DROP DATABASE IF EXISTS " + System.getenv("CS410_DATABASENAME") + ";");
      stmt.execute("CREATE DATABASE " + System.getenv("CS410_DATABASENAME") + ";");

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create all database tables
   */
  public static void createTables() {
    try {
      Connection connection = DB.getDatabaseConnection();
      Statement stmt = connection.createStatement();

      createClassTable(stmt);
      createCategoryTable(stmt);
      createStudentTable(stmt);
      createRelations(connection); // Relationships for each 'main' table

      connection.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param stmt
   */
  public static void createClassTable(Statement stmt) {
    try {
      System.out.println("Creating 'class' table...");
      stmt.execute(
          "CREATE TABLE class (" +
              "class_id int PRIMARY KEY AUTO_INCREMENT,\n" +
              "course_number varchar(20) NOT NULL,\n" +
              "term varchar(4) NOT NULL,\n" +
              "section_number int NOT NULL,\n" +
              "description TEXT,\n" +
              "UNIQUE(course_number, term, section_number)\n" +
              ");");
      System.out.println("Successfully created 'class' table");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param stmt
   */
  public static void createCategoryTable(Statement stmt) {
    try {
      System.out.println("Creating 'category' table...");
      stmt.execute(
          "CREATE TABLE category (" +
              "category_name varchar(255) PRIMARY KEY NOT NULL" +
              ");");
      System.out.println("Successfully created 'category' table");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param stmt
   */
  public static void createStudentTable(Statement stmt) {
    try {
      System.out.println("Creating 'student' table...");
      stmt.execute(
          "CREATE TABLE student (" +
              "student_id int PRIMARY KEY AUTO_INCREMENT,\n" +
              "username varchar(255) NOT NULL,\n" +
              "name varchar(255) NOT NULL\n" +
              ");");
      System.out.println("Successfully created 'student' table");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param connection
   */
  public static void createRelations(Connection connection) {
    System.out.println("Creating relationships...");
    try {
      Statement stmt = connection.createStatement();

      System.out.println("Creating 'enrolled_students' table..."); // Class Enrolls Student
      stmt.execute(
          "CREATE TABLE enrolled_students (" +
              "class_id int NOT NULL,\n" +
              "student_id int NOT NULL,\n" +
              "PRIMARY KEY (class_id, student_id),\n" +
              "FOREIGN KEY (class_id) REFERENCES class(class_id),\n" +
              "FOREIGN KEY (student_id) REFERENCES student(student_id)\n" +
              ");");
      System.out.println("Successfully created 'enrolled_students' table");

      System.out.println("Creating 'class_categories' table..."); // Class contains Cateogories
      stmt.execute(
          "CREATE TABLE class_categories (" +
              "class_id int NOT NULL, " +
              "category_name varchar(255), " +
              "weight double NOT NULL, " +
              "PRIMARY KEY (class_id, category_name), " +
              "FOREIGN KEY (class_id) REFERENCES class(class_id), " +
              "FOREIGN KEY (category_name) REFERENCES category(category_name)" +
              ");");
      System.out.println("Successfully created 'class_categories' table");

      // Relies on relationships to create its own
      System.out.println("Creating 'assignment' table...");
      stmt.execute(
          "CREATE TABLE assignment (" +
              "assignment_name varchar(255) NOT NULL,\n" +
              "class_id int NOT NULL,\n" +
              "category_name varchar(255) NOT NULL,\n" +
              "point_value double NOT NULL,\n" +
              "description TEXT,\n" +
              "PRIMARY KEY (assignment_name, class_id),\n" +
              // validates assignment category exists in class
              "FOREIGN KEY (class_id, category_name) REFERENCES class_categories(class_id, category_name)\n" +
              ");");
      System.out.println("Successfully created 'assignment' table");

      System.out.println("Creating 'student_class_assignments' table...");
      stmt.execute(
          "CREATE TABLE student_assignments (" + // Student has Assignments
              "grade double,\n" +
              "student_id int NOT NULL,\n" +
              "class_id int NOT NULL,\n" +
              "assignment_name varchar(255) NOT NULL,\n" +
              "PRIMARY KEY (student_id, class_id, assignment_name),\n" +
              "FOREIGN KEY (student_id) REFERENCES student(student_id),\n" +
              "FOREIGN KEY (assignment_name, class_id) REFERENCES assignment(assignment_name, class_id)\n" +
              ");");
      System.out.println("Successfully created 'student_class_assignments' table");

    } catch (SQLException e) {
      e.printStackTrace();
    }

    System.out.println("Successfully created relationships");
  }

  // public static void create

  public static void main(String[] args) {
    try {
      System.out.println("Creating Schema");
      System.out.println("Dropping and recreating database...");
      resetDatabase();
      System.out.println("Successfully dropped and recreated database");

      System.out.println("Creating Schema Tables...");
      createTables();
      System.out.println("Successfully created tables");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
