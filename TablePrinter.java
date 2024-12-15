import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * TablePrinter
 */
public class TablePrinter {
  public void printTable(ResultSet rs, ResultSetMetaData rsData) {
    System.out.println();
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
}
