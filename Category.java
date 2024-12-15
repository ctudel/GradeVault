import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Category
 */
public class Category {

  public void createCategory(String name) {
    try {
      if (name.isBlank())
        throw new SQLException("Category name");

      Connection conn = DB.getDatabaseConnection();
      String query = "INSERT INTO category (category_name) VALUES (?)";
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, new TablePrinter().formatString(name));

      stmt.execute();
      conn.close();

    } catch (Exception e) {
      System.out.println("Failed to add category: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
