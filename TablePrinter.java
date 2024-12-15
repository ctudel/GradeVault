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
          System.out.print(rs.getString(i) + "\t");
        }
        System.out.print('\n');
      }
      System.out.print('\n');

    } catch (Exception e) {
      System.out.println("Failed to print table: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public String formatString(String str) {
    if (str.isBlank())
      return str;

    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }
}
