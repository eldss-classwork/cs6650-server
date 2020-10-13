import data.SkierDbConnection;
import data.models.JSONable;
import exceptions.EmptyPathException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import utils.JsonFormatter;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

  private SkierDbConnection dbConn = new SkierDbConnection();

  /**
   * Handle POST requests.
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Process path
    String[] pathParts;
    try {
      pathParts = ServletMethodSetup.setUp(request, response);
    } catch (EmptyPathException e) {
      // Error response taken care of in setup
      return;
    }

    // Get the body JSON
    String requestData = request.getReader().lines().collect(Collectors.joining());
    JSONObject requestJson = new JSONObject(requestData);

    // Validate path
    if (!isUrlValidPOST(pathParts, requestJson)) {
      // Invalid path
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(JsonFormatter.buildError("invalid request"));
      return;
    }

    // Collect the json arguments
    final String[] keys = new String[]{
        "resortID",
        "dayID",
        "skierID",
        "time",
        "liftID"
    };
    String resort = requestJson.getString(keys[0]);
    int day = requestJson.getInt(keys[1]);
    int skier = requestJson.getInt(keys[2]);
    int time = requestJson.getInt(keys[3]);
    int lift = requestJson.getInt(keys[4]);

    // Write the response
    PrintWriter out = response.getWriter();
    try {
      dbConn.postLiftRide(resort, day, skier, time, lift);
      response.setStatus(HttpServletResponse.SC_CREATED);
    } catch (SQLException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.println(JsonFormatter.buildError("problem executing SQL: "
          + e.getMessage()));
    }
  }

  /**
   * Handle GET requests.
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Process path
    String[] pathParts;
    try {
      pathParts = ServletMethodSetup.setUp(request, response);
    } catch (EmptyPathException e) {
      // Error response taken care of in setup
      return;
    }

    // Validate path
    if (!isUrlValidGET(pathParts)) {
      // Invalid path
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(JsonFormatter.buildError("invalid request"));
      return;
    }

    // Echo back the path and anything in the query
    PrintWriter out = response.getWriter();
    try {
      // TODO: Do actual gets for the get paths
      // Get the data
      List<JSONable> list = dbConn.getAllResorts();
      // Return the data
      response.setStatus(HttpServletResponse.SC_OK);
      out.println(JsonFormatter.buildArray(list));
    } catch (SQLException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      out.println(JsonFormatter.buildError("problem executing SQL "
          + e.getMessage()));
    }
  }

  /*
      Validation Methods
      Info on paths found at
      https://app.swaggerhub.com/apis/cloud-perf/SkiDataAPI/1.13#/
   */

  /**
   * Checks if a GET request path is valid.
   *
   * @param pathParts An array of path parts
   * @return true if valid, otherwise false
   */
  private boolean isUrlValidGET(String[] pathParts) {
    // Example pathParts
    // urlPath  = "/1/days/1/skier/123"
    // pathParts = [, 1, days, 1, skier, 123]

    // Path length constants
    int verticalByResortLen = 3;
    int verticalByDay = 6;

    // Need to adjust the ending index in case the path has a trailing "/'
    // and therefore an empty string as the last array element.
    int len = pathParts.length;
    if (pathParts[len - 1].isEmpty()) {
      --len;
    }

    // Check each path
    try {
      if (len == verticalByResortLen) {
        String skierID = pathParts[1];
        Integer.parseInt(skierID);
        return pathParts[len - 1].equals("vertical");

      } else if (len == verticalByDay) {
        String day = pathParts[3];
        String skierID = pathParts[5];
        Integer.parseInt(day);
        Integer.parseInt(skierID);
        return pathParts[2].equals("days") && pathParts[4].equals("skiers");

      } else {
        // Something unexpected
        return false;
      }
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Checks if a POST request path is valid.
   *
   * @param pathParts An array of path parts
   * @return true if valid, otherwise false
   */
  private boolean isUrlValidPOST(String[] pathParts, JSONObject body) {
    // urlPath  = "/liftrides"
    // pathParts = ["", "liftrides"]
    // Only one POST path

    // Check path is correct
    boolean pathIsValid = false;
    final String resource = "liftrides";
    if (pathParts.length >= 2) {
      pathIsValid = pathParts[1].equals(resource);
    }

    // Check body is correct
    Set<String> keys = body.keySet();
    final String[] requiredKeys = new String[]{
        "resortID",
        "dayID",
        "skierID",
        "time",
        "liftID"
    };

    // Check keys
    boolean bodyIsValid = true;
    for (String key : requiredKeys) {
      if (!keys.contains(key)) {
        bodyIsValid = false;
        break;
      }
    }

    // Check key int values are actually ints
    try {
      // All keys except first are ints
      for (int i = 1; i < requiredKeys.length; i++) {
        body.getInt(requiredKeys[i]);
      }
    } catch (JSONException e) {
      bodyIsValid = false;
    }

    return pathIsValid && bodyIsValid;
  }
}
