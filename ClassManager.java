import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassManager {

  private int currClassId;
  private String currTerm;

  public ClassManager() {
    this.currClassId = -1;
    this.currTerm = "Fa24";
  }

  // Create a class: new-class CS410 Sp20 1 "Databases
  public void createClass(String course, String term, int section, String description) {
    try {
      System.out.println("Adding class to database...");
      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO class (course_number, term, section_number, description) VALUES (?,?,?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      // Validate passed in values
      if (course == "" || term == "" || section < 0)
        throw new SQLException("course and term cannot be empty values, and section must be positive");

      stmt.setString(1, course);
      stmt.setString(2, term);
      stmt.setInt(3, section);
      stmt.setString(4, (description.equals("") ? null : description)); // null if description is empty
      stmt.execute(); // Execute query

    } catch (Exception e) {
      System.out.println("Failed to create : " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("Successfully added class to database");
  }

  // List classes, with the # of students in each: list-classes
  public void getAllClasses() {
    System.out.println("Getting all classes from database.");
    try {
      Connection conn = DB.getDatabaseConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM class;");
      ResultSetMetaData rsData = rs.getMetaData();
      printTable(rs, rsData);

      conn.close();

    } catch (Exception e) {
      System.out.println("Failed to get categories: " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println("Successfully retrieved classes from database");
  }

  public void addNewCategory() {
    try {
      Connection conn = DB.getDatabaseConnection();
      Statement stmt = conn.createStatement();
      stmt.execute("INSERT INTO category (category_name, weight) VALUES ('homework', 20.0)");
      conn.close();

    } catch (Exception e) {
      System.out.println("Failed to add category: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void getAllCategories() {
    try {
      Connection conn = DB.getDatabaseConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM category;");
      ResultSetMetaData rsData = rs.getMetaData();
      printTable(rs, rsData);
      conn.close();

    } catch (Exception e) {
      System.out.println("Failed to get categories: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void printTable(ResultSet rs, ResultSetMetaData rsData) {
    try {
      int cols = rsData.getColumnCount();

      // Create column names
      for (int i = 1; i <= cols; i++) {
        System.out.print(rsData.getColumnName(i) + "  ");
      }
      System.out.println("\n-----------------------------------------");

      while (rs.next()) {
        for (int i = 1; i <= cols; i++) {
          System.out.print(rs.getString(i) + "\t");
        }
      }
      System.out.println();

    } catch (Exception e) {
      System.out.println("Failed to print table: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    ClassManager mg = new ClassManager();
    mg.createClass("CS410", "Sp20", 001, "");
    mg.getAllClasses();
  }
}
