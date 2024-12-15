import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentManager {

  // Create a class: new-class CS410 Sp20 1 "Databases
  public void createStudent(int studentID, String username, String firstName, String lastName) {
    try {
      // Validate passed in values
      if (studentID < 0 || username.isBlank() || firstName.isBlank() || lastName.isBlank())
        throw new SQLException("Invalid value for at least one column value");

      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO student VALUES (?,?,?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setInt(1, studentID);
      stmt.setString(2, username);
      stmt.setString(3, firstName);
      stmt.setString(4, lastName);
      stmt.execute(); // Execute query

    } catch (Exception e) {
      e.printStackTrace();
    }
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
}
