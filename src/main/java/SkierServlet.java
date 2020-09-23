import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String[] pathParts;
    try {
      pathParts = ServletMethodSetup.setUp(request, response);
    } catch (EmptyPathException e) {
      // Error response taken care of in setup
      return;
    }

    if (!isUrlValidPOST(pathParts)) {
      // Invalid path
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "unknown path provided");
    } else {
      response.setStatus(HttpServletResponse.SC_CREATED);

      // Echo back anything in request body
      try (PrintWriter out = response.getWriter(); BufferedReader in = request.getReader()) {
        String line;
        while ((line = in.readLine()) != null) {
          out.println(line);
        }
      }
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String[] pathParts;
    try {
      pathParts = ServletMethodSetup.setUp(request, response);
    } catch (EmptyPathException e) {
      // Error response taken care of in setup
      return;
    }

    if (!isUrlValidGET(pathParts)) {
      // Invalid path
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "unknown path provided");
    } else {
      response.setStatus(HttpServletResponse.SC_OK);

      // Echo back the path and anything in the query
      try (PrintWriter out = response.getWriter()) {
        out.println("{\"status\": \"request successful\"}");
      }
    }
  }

  /*
      Validation Methods
      Info on paths found at https://app.swaggerhub.com/apis/cloud-perf/SkiDataAPI/1.12#/
   */

  /**
   * Checks if a GET request path is valid.
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
    if (pathParts[len-1].isEmpty()) {
      --len;
    }

    // Check each path
    try {
      if (len == verticalByResortLen) {
        // TODO: Determine skier ID type and include a check for it here.
        return pathParts[len-1].equals("vertical");
      } else if (len == verticalByDay) {
        // TODO: Determine ID types and include checks for them
        // DayID can be checked
        int dayIdIndex = 3;
        Integer.parseUnsignedInt(pathParts[dayIdIndex]);
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
   * @param pathParts An array of path parts
   * @return true if valid, otherwise false
   */
  private boolean isUrlValidPOST(String[] pathParts) {
    // urlPath  = "/liftrides"
    // pathParts = ["", "liftrides"]
    // Only one POST path
    final String resource = "liftrides";
    if (pathParts.length >= 2) {
      return pathParts[1].equals(resource);
    }
    return false;
  }
}
