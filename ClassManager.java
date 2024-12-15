import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

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
      stmt.setString(4, (description.equals("") ? null : description)); // null if description is empty
      stmt.execute(); // Execute query

    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Successfully added class to database");
  }

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

  public static void main(String[] args) {
    ClassManager mg = new ClassManager();
    Class test = new Class(1, "CS402", "Sp20", 001, "");

    // Schema.resetDatabase();
    // Schema.createTables();
    //
    // System.out.println("Creating 'homework' category");
    // new Category().createCategory("homework");
    //
    // System.out.println("Creating test class...");
    // mg.createClass(test.getCourse(), test.getTerm(), test.getSection(),
    // test.getDescription());

    System.out.println("Setting class...");
    mg.setCurrClass(test);

    // System.out.println("Adding category");
    // mg.addCategory("homework", 20.0);

    System.out.println("Getting classes...\n");
    mg.getAllClasses();

    System.out.println("Getting categories...");
    mg.getCategories();
  }

}
