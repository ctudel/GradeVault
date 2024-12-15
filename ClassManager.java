import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class ClassManager {
  private Class currClass;

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

  public void createAssignment(String name, String category, double points, String description) {
    try {
      if (name.isBlank() || category.isBlank() || points < 0.0)
        throw new SQLException("Assignment needs a name and/or category, as well as point value of at least 0.0");

      Connection conn = DB.getDatabaseConnection();
      String query = "INSERT INTO assignment VALUES (?,?,?,?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setString(1, name);
      stmt.setInt(2, currClass.getClassId());
      stmt.setString(3, new TablePrinter().formatString(category));
      stmt.setDouble(4, points);
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
  }

  /**
   * 
   */
  public void getCategories() {
    try {
      Connection conn = DB.getDatabaseConnection();

      ResultSet rs = queryAllCategories(conn);
      if (rs == null)
        throw new SQLException();

      ResultSetMetaData rsData = rs.getMetaData();

      new TablePrinter().printTable(rs, rsData);

      conn.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void getStudents() {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT studentID, s.username, first_name, last_name " +
          "FROM student s " +
          "JOIN enrolled_students es ON s.username = es.username " +
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

  /**
  * 
  */
  public void getAssignments() {
    Map<String, String> assignments = new HashMap<String, String>();

    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM assignment " +
          "WHERE class_id = ? " +
          "ORDER BY category_name;"; // "group by" category

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, currClass.getClassId());

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      int cols = rsData.getColumnCount();

      // Sort by category per string for printing
      while (rs.next()) {
        String category = "";
        String aStr = "";

        // Extract row data and sort by their category
        for (int i = 1; i <= cols; i++) {
          String data = rs.getString(i);
          if (i == 3)
            category = data;
          aStr += data + "  ";
        }

        // Check if string for category exists, otherwise create it
        if (!assignments.containsKey(category)) {
          assignments.put(category, category + "\n-------------------\n");
        }

        // Insert data into category specific string
        String newStr = assignments.get(category) + aStr + "\n";
        assignments.put(category, newStr);
      }

      // Print all categorized assignments
      for (String a : assignments.values()) {
        System.out.println(a);
      }

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void getGrades() {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM student_assignments";

      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery(query);
      ResultSetMetaData rsData = rs.getMetaData();
      new TablePrinter().printTable(rs, rsData);

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // =============
  // Set Functions
  // =============
  public void setClass(String course) {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM class c" +
          "WHERE course_number = ? " +
          "AND term = ( " +

          ") " +
          "ORDER BY category_name;"; // "group by" category

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, currClass.getClassId());

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      int cols = rsData.getColumnCount();

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

      String query = "INSERT INTO class_categories VALUES (?,?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setInt(1, currClass.getClassId());
      stmt.setString(2, new TablePrinter().formatString(name));
      stmt.setDouble(3, weight);
      stmt.execute();

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param username
   */
  public void addStudent(String username) {
    try {
      if (username.isBlank())
        throw new SQLException("Username cannot be blank");

      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO enrolled_students VALUES (?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setInt(1, currClass.getClassId());
      stmt.setString(2, username);
      stmt.execute();

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // ================
  // Helper Functions
  // ================
  public ResultSet queryAllCategories(Connection conn) {
    try {
      String query = "SELECT c.category_name, cc.weight " +
          "FROM category c " +
          "JOIN class_categories cc ON c.category_name = cc.category_name " +
          "WHERE class_id = ?;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, currClass.getClassId());

      return stmt.executeQuery();

    } catch (SQLException se) {
      se.printStackTrace();
    }

    return null;
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

  /**
   * ClassManager Tester
   *
   * @param args
   */
  public static void main(String[] args) {
    ClassManager cm = new ClassManager();
    StudentManager sm = new StudentManager();
    Class test = new Class(1, "CS402", "Sp20", 001, "");
    Class test2 = new Class(2, "CSEC119", "Sp20", 001, "");

    // Schema.resetDatabase();
    // Schema.createTables();

    // System.out.println("Creating test class...");
    // cm.createClass(test.getCourse(), test.getTerm(), test.getSection(),
    // test.getDescription());

    System.out.println("Setting class...");
    cm.setCurrClass(test);

    // // Create assignment and categories
    // System.out.println("Creating 'homework' category");
    // new Category().createCategory("homework");
    // System.out.println("Adding category");
    // cm.addCategory("homework", 20.0);
    // System.out.println("Creating Assignment...");
    // cm.createAssignment("First Homework", "homework", 10, "");
    //
    // // Create assignment and categories
    // System.out.println("Creating 'homework' category");
    // new Category().createCategory("quiz");
    // System.out.println("Adding category");
    // cm.addCategory("quiz", 30.0);
    // System.out.println("Creating Assignment...");
    // cm.createAssignment("First Quiz", "Quiz", 10, "");
    // cm.createAssignment("Second Quiz", "quiz", 10, "");
    //
    // // Create and enroll students
    // System.out.println("Creating Student...");
    // sm.createStudent(1234, "ctudel", "Chris", "Tudela");
    // sm.createStudent(1235, "jsmith", "John", "Smith");
    // System.out.println("Enrolling Student...");
    // cm.addStudent("ctudel");
    // cm.addStudent("jsmith");
    //
    // // Assigning grade to student
    // System.out.println("Assigning grade to student");
    // sm.addGrade("First Homework", cm.getCurrClass().getClassId(), "Ctudel", 10);
    // sm.addGrade("First Homework", cm.getCurrClass().getClassId(), "jsmith", 10);
    // sm.addGrade("First Quiz", cm.getCurrClass().getClassId(), "Jsmith", 7.5);
    // sm.addGrade("Second Quiz", cm.getCurrClass().getClassId(), "Jsmith", 8.5);

    System.out.println("Getting classes...\n");
    cm.getAllClasses();

    System.out.println("Getting categories...");
    cm.getCategories();

    System.out.println("Getting students...");
    cm.getStudents();

    System.out.println("Getting assignments...");
    cm.getAssignments();

    System.out.println("Getting grades...");
    sm.getGrades(cm.getCurrClass().getClassId(), "Jsmith");
  }
}
