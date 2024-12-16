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

  public void createCategory(String name) {
    try {
      if (name.isBlank())
        throw new SQLException("Category name");

      Connection conn = DB.getDatabaseConnection();
      String query = "INSERT INTO category (category_name) VALUES (?) " +
          "ON DUPLICATE KEY UPDATE category_name = category_name;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, new TablePrinter().formatString(name));

      stmt.execute();
      conn.close();

    } catch (Exception e) {
      System.out.println("Failed to add category: " + e.getMessage());
      e.printStackTrace();
    }
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
      System.out.println("Failed to create assignment");
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

      System.out.println();
      int cols = rsData.getColumnCount();
      int chars = 0;

      // Print column names
      for (int i = 1; i <= cols; i++) {
        String colName = rsData.getColumnName(i);
        System.out.print(colName + " | " + "  ");
        chars += colName.length() + 5;
      }
      System.out.print('\n');

      // Print line
      for (int i = 0; i < chars; i++) {
        System.out.print("-");
      }
      System.out.print('\n');

      // Print each row of data
      while (rs.next()) {
        for (int i = 1; i <= cols; i++) {
          if (i == 2)
            System.out.print(rs.getString(i) + "%" + "\t");
          else
            System.out.print(rs.getString(i) + "\t");
        }
        System.out.print('\n');
      }
      System.out.print('\n');

      conn.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean getCategory(String name) {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM category " +
          "WHERE category_name = ?" +
          ";";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, name);

      ResultSet rs = stmt.executeQuery();

      return (rs.next()) ? true : false;

    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;

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
      int chars = 0;

      // Print column names
      System.out.print("\n");
      for (int i = 1; i <= cols; i++) {
        String colName = rsData.getColumnName(i);
        System.out.print(colName + " | " + "  ");
        chars += colName.length() + 5;
      }
      System.out.print("\n");

      // Print horizontal line
      for (int i = 0; i < chars; i++) {
        System.out.print("-");
      }
      System.out.print("\n\n");

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

      String query = "SELECT " +
          "s.username, " +
          "s.studentID, " +
          "s.first_name, " +
          "s.last_name, " +
          "CONCAT(ROUND(AVG((sa.grade / a.point_value) * 100), 1), '%') AS 'total grade' " +
          "FROM student s " +
          "JOIN student_assignments sa ON s.username = sa.username " +
          "JOIN assignment a ON sa.assignment_name = a.assignment_name AND sa.class_id = a.class_id " +
          "WHERE sa.class_id = ? " +
          "GROUP BY s.username, s.studentID, s.first_name, s.last_name;";

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
  // Set Functions
  // =============

  /**
   * @param course
   * @return
   */
  public int setClass(String course) {
    int classFound = 0;
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM class " +
          "WHERE course_number = ? " +
          "AND term = ( " + // get the current term
          "SELECT term FROM class " +
          "ORDER BY term DESC " +
          "LIMIT 1 " +
          ");";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, course);

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      int cols = rsData.getColumnCount();

      int classId = 0;
      String currCourse = "";
      String term = "";
      int section = 0;
      String description = "";

      while (rs.next()) {
        classFound = 1;
        for (int i = 1; i <= cols; i++) {
          String data = rs.getString(i);

          if (i == 1)
            classId = Integer.parseInt(data);
          if (i == 2)
            currCourse = data;
          if (i == 3)
            term = data;
          if (i == 4)
            section = Integer.parseInt(data);
          if (i == 5)
            description = data;
        }

        if (rs.next()) {
          System.out.println("Failed to select class: Change term, or choose a specific section\n");
          break;
        }
        currClass = new Class(classId, currCourse, term, section, description);
        System.out.println("Successfully selected course\n");
      }

      if (classFound == 0)
        System.out.println("Failed to select class: class does not exist in this term\n");

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return classFound;
  }

  /**
   * @param course
   * @param term
   * @return
   */
  public int setClass(String course, String term) {
    int classFound = 0;
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM class " +
          "WHERE course_number = ? " +
          "AND term = ?;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, course);
      stmt.setString(2, term);

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      int cols = rsData.getColumnCount();

      int classId = 0;
      String currCourse = "";
      String currTerm = "";
      int section = 0;
      String description = "";

      while (rs.next()) {
        classFound = 1;
        for (int i = 1; i <= cols; i++) {
          String data = rs.getString(i);

          if (i == 1)
            classId = Integer.parseInt(data);
          if (i == 2)
            currCourse = data;
          if (i == 3)
            currTerm = data;
          if (i == 4)
            section = Integer.parseInt(data);
          if (i == 5)
            description = data;
        }

        if (rs.next()) {
          System.out.println("Failed to select class: Change term, or choose a specific section\n");
          break;
        }
        currClass = new Class(classId, currCourse, currTerm, section, description);
        System.out.println("Successfully selected course\n");
      }

      if (classFound == 0)
        System.out.println("Failed to select class: class does not exist in this term\n");

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return classFound;
  }

  /**
   * @param course
   * @param term
   * @param section
   * @return
   */
  public int setClass(String course, String term, int section) {
    int classFound = 0;
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM class " +
          "WHERE course_number = ? " +
          "AND term = ? " +
          "AND section_number = ?;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, course);
      stmt.setString(2, term);
      stmt.setInt(3, section);

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      int cols = rsData.getColumnCount();

      int classId = 0;
      String currCourse = "";
      String currTerm = "";
      int currSection = 0;
      String description = "";

      while (rs.next()) {
        classFound = 1;
        for (int i = 1; i <= cols; i++) {
          String data = rs.getString(i);

          if (i == 1)
            classId = Integer.parseInt(data);
          if (i == 2)
            currCourse = data;
          if (i == 3)
            currTerm = data;
          if (i == 4)
            currSection = Integer.parseInt(data);
          if (i == 5)
            description = data;
        }

        if (rs.next()) {
          System.out.println("Failed to select class: Change term, or choose a specific section\n");
          break;
        }
        currClass = new Class(classId, currCourse, currTerm, currSection, description);
        System.out.println("Successfully selected course\n");
      }

      if (classFound == 0)
        System.out.println("Failed to select class: class does not exist in this term\n");

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return classFound;
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

      String query = "INSERT INTO class_categories VALUES (?,?,?) " +
          "ON DUPLICATE KEY UPDATE weight = ?;";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setInt(1, currClass.getClassId());
      stmt.setString(2, new TablePrinter().formatString(name));
      stmt.setDouble(3, weight);
      stmt.setDouble(4, weight);
      stmt.execute();

      conn.close();

    } catch (Exception e) {
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

  public void showClass() {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM class " +
          "WHERE class_id = ?;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, currClass.getClassId());

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      new TablePrinter().printTable(rs, rsData);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private ResultSet queryAllCategories(Connection conn) {
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

    System.out.println("Resetting and creating database tables...");
    Schema.resetDatabase();
    Schema.createTables();

    // Create multiple classes
    Class[] classes = {
        new Class(1, "CS410", "Sp24", 001, "Databases"),
        new Class(2, "CS402", "Sp20", 001, "Mobile Development"),
    };

    System.out.println("\nCreating classes...");
    for (Class c : classes) {
      System.out.println("Creating class: " + c.getCourse() + " " + c.getTerm() + "Section " + c.getSection());
      cm.createClass(c.getCourse(), c.getTerm(), c.getSection(),
          c.getDescription());
    }

    // Create students
    String[][] students = {
        { "1234", "ctudel", "Chris", "Tudela" },
        { "1235", "jsmith", "John", "Smith" },
        { "1236", "agarcia", "Ana", "Garcia" },
        { "1237", "mlee", "Mike", "Lee" },
        { "1238", "sjohnson", "Sarah", "Johnson" },
        { "1239", "rkim", "Rachel", "Kim" },
        { "1240", "dchen", "David", "Chen" },
        { "1241", "lbrown", "Lisa", "Brown" }
    };

    System.out.println("\nCreating students...");
    for (String[] student : students) {
      System.out.println("Creating student: " + student[2] + " " + student[3] + "(" + student[1] + ")");
      sm.createStudent(Integer.parseInt(student[0]), student[1], student[2],
          student[3]);
    }

    // For each class, add categories, assignments, and enroll students
    for (Class c : classes) {
      System.out.println("\nSetting current class to: " + c.getCourse() + " " +
          c.getTerm());
      cm.setCurrClass(c);

      // Create categories
      String[] categories = { "homework", "quiz", "exam", "project" };
      double[] weights = { 20.0, 30.0, 40.0, 10.0 };
      System.out.println("Creating categories for " + c.getCourse() + ":");
      for (int i = 0; i < categories.length; i++) {
        System.out.println(" - Creating category: " + categories[i] + " (weight: " +
            weights[i] + "%)");
        cm.createCategory(categories[i]);
        cm.addCategory(categories[i], weights[i]);
      }

      // Create assignments
      String[][] assignments = {
          { "Homework 1", "homework", "10" },
          { "Homework 2", "homework", "15" },
          { "Quiz 1", "quiz", "20" },
          { "Quiz 2", "quiz", "25" },
          { "Midterm Exam", "exam", "100" },
          { "Final Project", "project", "50" }
      };

      System.out.println("Creating assignments for " + c.getCourse() + ":");
      for (String[] assignment : assignments) {
        System.out.println(
            " - Creating assignment: " + assignment[0] + " (" + assignment[1] + ", " +
                assignment[2] + " points)");
        cm.createAssignment(assignment[0], assignment[1],
            Integer.parseInt(assignment[2]), "");
      }

      // Enroll students (varying number per class)
      int numStudents = 5 + (int) (Math.random() * 4); // 5 to 8 students per class
      System.out.println("Enrolling students in " + c.getCourse() + ":");
      for (int i = 0; i < numStudents; i++) {
        System.out.println(" - Enrolling student: " + students[i][1]);
        cm.addStudent(students[i][1]);
      }

      // Assign grades
      System.out.println("Assigning grades for " + c.getCourse() + ":");
      for (int i = 0; i < numStudents; i++) {
        for (String[] assignment : assignments) {
          double maxPoints = Integer.parseInt(assignment[2]);
          double grade = Math.round(Math.random() * Integer.parseInt(assignment[2]) *
              10.0) / 10.0;
          grade = Math.min(grade, maxPoints);
          System.out.println(" - Assigning grade for " + students[i][1] + " in " +
              assignment[0] + ": " + grade);
          sm.addGrade(assignment[0], c.getClassId(), students[i][1], grade);
        }
      }
    }
    cm.setClass("CS410");

    System.out.println("\nGetting all classes...");
    cm.getAllClasses();

    System.out.println("\nGetting categories for the last class...");
    cm.getCategories();

    System.out.println("\nGetting students for the last class...");
    cm.getStudents();

    System.out.println("\nGetting assignments for the last class...");
    cm.getAssignments();

    System.out.println("\nGetting grades for the first student in the first class...");
    sm.getGrades(classes[0].getClassId(), students[0][1]);

    System.out.println("\nSetting the most current class...");
    cm.setClass("CS410", "Sp24", 1);
  }
}
