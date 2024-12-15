import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentManager {

  // Create a class: new-class CS410 Sp20 1 "Databases
  public void createStudent(String username, String name) {
    try {
      System.out.println("Adding student to database...");
      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO student (username, name) VALUES (?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      // Validate passed in values
      if (username.isBlank() || name.isBlank())
        throw new SQLException("course and term cannot be empty values, and section must be positive");

      stmt.setString(1, username);
      stmt.setString(2, name);
      stmt.execute(); // Execute query

    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Successfully added student to database");
  }

  // List classes, with the # of students in each: list-classes
  public void getAllStudents() {
    System.out.println("Getting all students from database.");
    try {
      Connection conn = DB.getDatabaseConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM student;");
      ResultSetMetaData rsData = rs.getMetaData();
      new TablePrinter().printTable(rs, rsData);

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Successfully retrieved students from database");
  }

  public static void main(String[] args) {
    StudentManager sg = new StudentManager();
    sg.createStudent("ctudel", "Chris Tudela");
    sg.getAllStudents();
  }
}
