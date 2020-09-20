import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ResortServlet")
public class ResortServlet extends HttpServlet {

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

      // Echo back anything in request body
      try (PrintWriter out = response.getWriter(); BufferedReader in = request.getReader()) {
        String line;
        while ((line = in.readLine()) != null) {
          out.println(line);
        }
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

    // Need to adjust the ending index in case the path has a trailing "/'
    // and therefore an empty string as the last array element.
    int len = pathParts.length;
    if (pathParts[len-1].isEmpty()) {
      --len;
    }

    // Must be at least as many path parts as the shortest resource path
    int minPathPartsLen = 3;
    int maxPathPartsLen = 6;  // used below
    if (len <= minPathPartsLen) {
      return false;
    }

    // Primary checking
    try {
      // First part is number for both paths
      Integer.parseUnsignedInt(pathParts[1]);

      // Second part is a string and separates the two paths
      String option1 = "vertical";
      String option2 = "days";
      int optionId = 2;
      String actual = pathParts[optionId];

      // All options ignore any trailing info in path
      if (actual.equals(option1)) {
        return true;

      } else if (actual.equals(option2) && len >= maxPathPartsLen) {
        int dayIdIndex = 3;
        int skiersStrIndex = 4;
        int skierIdIndex = 5;
        String skiersStrExpected = "skiers";

        // Check number conversions
        Integer.parseUnsignedInt(pathParts[dayIdIndex]);
        Integer.parseUnsignedInt(pathParts[skierIdIndex]);

        // If those work, check the string between them
        return pathParts[skiersStrIndex].equals(skiersStrExpected);

      } else {
        // Got something unexpected
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
