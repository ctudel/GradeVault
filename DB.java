import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DB {

  /**
   * Default connection to database
   */
  public static Connection getDatabaseConnection() throws SQLException {
    int databasePort = Integer.parseInt(System.getenv("CS410_PORT"));
    String databaseHost = System.getenv("CS410_HOST");
    String databaseUsername = System.getenv("CS410_USER");
    String databasePassword = System.getenv("CS410_PASS");
    String databaseName = System.getenv("CS410_DATABASENAME");

    return getDatabaseConnection(databaseUsername, databasePassword, databaseHost, databasePort, databaseName);
  }

  /**
   * Connect to a database w/ given information
   */
  public static Connection getDatabaseConnection(String username, String password, String host, int port,
      String databaseName) throws SQLException {

    String databaseURL = String.format(
        "jdbc:mysql://%s:%s/%s?verifyServerCertificate=false&useSSL=true&serverTimezone=UTC", host, port, databaseName);

    try {
      return DriverManager.getConnection(databaseURL, username, password);

    } catch (SQLException se) {
      se.printStackTrace();
      System.exit(-1);
    }

    return null;
  }
}
