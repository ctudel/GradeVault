package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DB {

  /**
   * Default connection to database
   */
  public static Connection getDatabaseConnection() throws SQLException {
    Map<String, String> env = System.getenv();
    Connection databaseConnection = null;
    int databasePort = Integer.parseInt(System.getenv("CS410_PORT"));
    String databaseHost = System.getenv("CS410_HOST");
    String databaseUsername = System.getenv("CS410_USER");
    String databasePassword = System.getenv("CS410_PASS");
    String databaseName = System.getenv("CS410_DATABASENAME");

    System.out.println("Retrieved all data");

    return getDatabaseConnection(databaseUsername, databasePassword, databaseHost, databasePort, databaseName);
  }

  /**
   * Connect to a database w/ given information
   */
  public static Connection getDatabaseConnection(String username, String password, String host, int port,
      String databaseName) throws SQLException {

    String databaseURL = String.format(
        "jdbc:mysql://%s:%s/%s?verifyServerCertificate=false&useSSL=true&serverTimezone=UTC", host, port, databaseName);
    System.out.println("Starting server with url: " + databaseURL);

    try {
      System.out.println("Connecting to database...");
      return DriverManager.getConnection(databaseURL, username, password);

    } catch (SQLException sqlException) {
      System.out.println(String.format(
          "SQLException was thrown while trying to connection to database: %s", databaseURL));
      System.out.println(sqlException.getMessage());
      throw sqlException;
    }
  }

  public static void main(String[] args) {
    Connection connection = null;

    try {
      System.out.println("Initiating Database");
      connection = getDatabaseConnection();
      System.out.println("Successfully connected to database");
      connection.close();
      System.out.println("Successfully closed database");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
