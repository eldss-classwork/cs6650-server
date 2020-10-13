package data;

import data.models.JSONable;
import data.models.Resort;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods to access the database for the skiers servlet.
 */
public class SkierDbConnection {

  /**
   * Adds a single lift ride to the database.
   *
   * @param resortID the resort name
   * @param dayID    the day of the year
   * @param skierID  the skier's ID
   * @param time     the time in minutes after opening
   * @param liftID   the lift number
   * @throws SQLException if there is a problem executing the query
   */
  public void postLiftRide(String resortID, int dayID, int skierID, int time, int liftID)
      throws SQLException {
    String SQL_QUERY = String.format(
        "INSERT INTO LiftRides(skierID, liftNum, resortID, `day`, `time`) VALUES (%d, %d, '%s', %d, %d);",
        skierID, liftID, resortID, dayID, time);
    Connection conn = DataSource.getConnection();
    PreparedStatement pst = conn.prepareStatement(SQL_QUERY);
    pst.executeUpdate();
  }

  // TODO: Temp function
  public List<JSONable> getAllResorts() throws SQLException {
    String SQL_QUERY = "SELECT * FROM Resorts";
    List<JSONable> resorts;
    try (Connection conn = DataSource.getConnection();
        PreparedStatement pst = conn.prepareStatement(SQL_QUERY);
        ResultSet rs = pst.executeQuery()) {

      resorts = new ArrayList<>();
      Resort resort;
      while (rs.next()) {
        String resortID = rs.getString("resortID");
        resort = new Resort(resortID);
        resorts.add(resort);
      }
    }
    return resorts;
  }
}
