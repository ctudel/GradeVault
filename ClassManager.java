import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassManager {
  private Class currClass;

  // TODO: – list the assignments with their point values, grouped by category
  // Get all the categories
  // Create a prepared statement to get assignments based on each category
  // Print the Statements "grouped" with the categories as their headers

  /**
  * 
  */
  public ClassManager() {
    this.currClass = new Class();
  }

  /**
   * @param currClass
   */
  public ClassManager(Class currClass) {
    this.currClass = currClass;
  }

  // ================
  // Create Functions
  // ================
  //
  /**
   * Create a class
   *
   * @param course      The course number of a class, i.e. "CS410"
   * @param term        Term this class exists, i.e. "Sp20"
   * @param section     Section of this class, i.e. "001"
   * @param description Descriptor of the class
   */
  public void createClass(String course, String term, int section, String description) {
    try {
      // Validate passed in values
      if (course.isBlank() || term.isBlank() || section < 0)
        throw new SQLException("course and term cannot be empty values, and section must be positive");

      System.out.println("Adding class to database...");
      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO class (course_number, term, section_number, description) VALUES (?,?,?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setString(1, course);
      stmt.setString(2, term);
      stmt.setInt(3, section);
      stmt.setString(4, (description.isBlank() ? null : description)); // null if description is empty
      stmt.execute(); // Execute query

    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Successfully added class to database");
  }

  // "CREATE TABLE assignment (" +
  // "assignment_name varchar(255) NOT NULL,\n" +
  // "class_id int NOT NULL,\n" +
  // "category_name varchar(255) NOT NULL,\n" +
  // "point_value double NOT NULL,\n" +
  // "description TEXT,\n" +
  public void createAssignment(String name, String category, double pointVal, String description) {
    try {
      if (name.isBlank() || category.isBlank() || pointVal < 0.0)
        throw new SQLException("Assignment needs a name and/or category, as well as point value of at least 0.0");

      Connection conn = DB.getDatabaseConnection();
      String query = "INSERT INTO assignment VALUES (?,?,?,?,?)";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setString(1, name);
      stmt.setInt(2, currClass.getClassId());
      stmt.setString(3, category);
      stmt.setDouble(4, pointVal);
      stmt.setString(5, (description.isBlank()) ? null : description);

      stmt.execute();
      conn.close();

    } catch (Exception e) {
      System.out.println("Failed to add category: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // =============
  // Get Functions
  // =============

  /**
   * List classes, with the # of students in each
   */
  public void getAllClasses() {
    System.out.println("Getting all classes from database.");
    try {
      Connection conn = DB.getDatabaseConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM class;");
      ResultSetMetaData rsData = rs.getMetaData();
      new TablePrinter().printTable(rs, rsData);

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Successfully retrieved classes from database");
  }

  /**
   * 
   */
  public void getCategories() {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT c.category_name, cc.weight " +
          "FROM category c " +
          "JOIN class_categories cc ON c.category_name = cc.category_name " +
          "WHERE class_id = ?;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, currClass.getClassId());

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      new TablePrinter().printTable(rs, rsData);

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void getStudents() {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM student s " +
          "JOIN enrolled_students es ON s.student_id = es.student_id " +
          "WHERE class_id = ?;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, currClass.getClassId());

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      new TablePrinter().printTable(rs, rsData);

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // =============
  // Add Functions
  // =============

  /**
  *
  */
  public void addCategory(String name, double weight) {
    try {
      if (name.isBlank() || weight < 0.0)
        throw new SQLException("Category name cannot be blank, and weight is less than 0.0");

      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO class_categories VALUES (?,?,?)";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setInt(1, currClass.getClassId());
      stmt.setString(2, name);
      stmt.setDouble(3, weight);
      stmt.execute();

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param studentID
   */
  public void addStudent(int studentID) {
    try {
      if (studentID < 0)
        throw new SQLException("studentID cannot be less than 0");

      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO enrolled_students VALUES (?,?)";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setInt(1, currClass.getClassId());
      stmt.setInt(2, studentID);
      stmt.execute();

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // =========================
  // Class Getters and Setters
  // =========================

  /**
   * @return
   */
  public Class getCurrClass() {
    return currClass;
  }

  /**
   * @param currClass
   */
  public void setCurrClass(Class currClass) {
    this.currClass = currClass;
  }

  public static void main(String[] args) {
    ClassManager cm = new ClassManager();
    StudentManager sm = new StudentManager();
    Class test = new Class(1, "CS402", "Sp20", 001, "");

    Schema.resetDatabase();
    Schema.createTables();

    System.out.println("Creating 'homework' category");
    new Category().createCategory("homework");

    System.out.println("Creating test class...");
    cm.createClass(test.getCourse(), test.getTerm(), test.getSection(), test.getDescription());

    System.out.println("Setting class...");
    cm.setCurrClass(test);

    System.out.println("Adding category");
    cm.addCategory("homework", 20.0);

    System.out.println("Creating Assignment...");
    cm.createAssignment("First Homework", "homework", 10, "");

    System.out.println("Creating Student...");
    sm.createStudent(1234, "ctudel", "Chris", "Tudela");

    System.out.println("Enrolling Student...");
    cm.addStudent(1234);

    System.out.println("Getting classes...\n");
    cm.getAllClasses();

    System.out.println("Getting categories...");
    cm.getCategories();

    System.out.println("Getting students...");
    cm.getStudents();
  }

}
