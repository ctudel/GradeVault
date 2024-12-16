import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * StudentManager
 */
public class StudentManager {
  // Create a class: new-class CS410 Sp20 1 "Databases
  public void createStudent(int studentID, String username, String firstName, String lastName) {
    TablePrinter tb = new TablePrinter();
    try {
      // Validate passed in values
      if (studentID < 0 || username.isBlank() || firstName.isBlank() || lastName.isBlank())
        throw new SQLException("Invalid value for at least one column value");

      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO student VALUES (?,?,?,?);";
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setInt(1, studentID);
      stmt.setString(2, tb.formatString(username));
      stmt.setString(3, tb.formatString(firstName));
      stmt.setString(4, tb.formatString(lastName));
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

  /**
   * @param assignment
   * @param username
   * @param grade
   */
  public void addGrade(String assignment, int classId, String username, double grade) {
    try {
      if (username.isBlank() || assignment.isBlank())
        throw new Exception("Username and assignement cannot be blank strings");

      Connection conn = DB.getDatabaseConnection();

      String query = "INSERT INTO student_assignments " +
          "VALUES (" +
          "?," +
          "(SELECT username FROM student WHERE username = ?), " +
          "?," +
          "(SELECT assignment_name FROM assignment WHERE assignment_name = ? AND class_id = ?)" +
          ") " +
          "ON DUPLICATE KEY UPDATE grade = ?" +
          ";";

      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setDouble(1, grade);
      stmt.setString(2, username);
      stmt.setInt(3, classId);
      stmt.setString(4, assignment);
      stmt.setInt(5, classId);
      stmt.setDouble(6, grade);
      stmt.execute();

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean getStudent(String username, int studentID) {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM student " +
          "WHERE studentID = ? OR username = ?" +
          ";";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, studentID);
      stmt.setString(2, username);

      ResultSet rs = stmt.executeQuery();

      return (rs.next()) ? true : false;

    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;

  }

  public boolean getStudent(String username) {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM enrolled_students " +
          "WHERE username = ?" +
          ";";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, username);

      ResultSet rs = stmt.executeQuery();

      return (rs.next()) ? true : false;

    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;

  }

  public void searchStudent(String searchStr) {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM student " +
          "WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ?;";

      PreparedStatement stmt = conn.prepareStatement(query);

      String likeStr = "%" + searchStr + "%";
      stmt.setString(1, likeStr);
      stmt.setString(2, likeStr);
      stmt.setString(3, likeStr);

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();

      new TablePrinter().printTable(rs, rsData);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean inClass(String username, int classId) {
    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "SELECT * FROM enrolled_students " +
          "WHERE class_id = ? AND username = ?" +
          ";";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, classId);
      stmt.setString(2, username);

      ResultSet rs = stmt.executeQuery();

      return (rs.next()) ? true : false;

    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;

  }

  /**
  * 
  */
  public void getGrades(int classId, String username) {
    Map<String, String> assignments = new HashMap<String, String>();
    Map<String, Double> pointVals = new HashMap<String, Double>();
    Map<String, Double> grades = new HashMap<String, Double>();
    Map<String, Double> weights = new HashMap<String, Double>();

    try {
      Connection conn = DB.getDatabaseConnection();

      String query = "" +
          "SELECT " +
          "sa.assignment_name, " +
          "a.category_name, " +
          "sa.grade, " +
          "a.point_value, " +
          "c.weight " +
          "FROM student_assignments sa " +

          "JOIN assignment a ON sa.assignment_name = a.assignment_name " +
          "AND sa.class_id = a.class_id " +

          "JOIN class_categories c ON a.category_name = c.category_name " +
          "AND a.class_id = c.class_id " +

          "WHERE sa.username = ?;";

      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, username);

      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsData = rs.getMetaData();
      int cols = rsData.getColumnCount();
      int chars = 0;

      // Print column names
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
        String gradeStr = "";
        double grade = 0;
        double pointVal = 0;
        double weight = 0;

        // Extract row data and sort by their category
        for (int i = 1; i <= cols; i++) {
          String data = rs.getString(i);
          if (i == 2) {
            category = data;
          }
          if (i == 3) {
            grade = Double.parseDouble(data);
            gradeStr += grade + "% ";
          }
          if (i == 4) {
            pointVal = Double.parseDouble(data);
          }
          if (i == 5) {
            weight = Double.parseDouble(data);
          }
          if (i != 5 && i != 3) {
            gradeStr += data + " ";
          }
        }

        if (!assignments.containsKey(category)) { // should by synonymouse with other maps
          assignments.put(category, "");
          grades.put(category, 0.0);
          pointVals.put(category, 0.0);
          weights.put(category, weight);
        }

        // Insert data into category specific string
        String newStr = assignments.get(category) + gradeStr + "\n";
        assignments.put(category, newStr);

        double newGrade = grades.get(category) + grade;
        grades.put(category, newGrade);

        double newPVal = pointVals.get(category) + pointVal;
        pointVals.put(category, newPVal);
      }

      String printStr = "";
      double weightedGrade = 0.0;
      double totalWeight = 0.0;
      double totalGrade = 0.0;

      // Print all categorized assignments
      for (String category : assignments.keySet()) {
        grades.put(category, (grades.get(category) / pointVals.get(category)) * 100); // Calculate and store student
                                                                                      // total category grade
        printStr += category + "  " + grades.get(category) + "%" + "\n" +
            "-------------------------\n" +
            assignments.get(category) + "\n";

        grades.put(category, (grades.get(category) / 100) * weights.get(category)); // Calculate and store
                                                                                    // student weighted grade
        weightedGrade += grades.get(category);
      }

      for (double weight : weights.values()) {
        totalWeight += weight;
      }

      totalGrade = (weightedGrade / totalWeight) * 100;
      printStr += "Total Grade: " + totalGrade + "%" + "\n";

      System.out.println(printStr);

      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
