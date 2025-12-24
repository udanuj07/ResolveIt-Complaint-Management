package resolveit;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/complaint")
public class ComplaintServlet extends HttpServlet {

    // ---- DATABASE CONFIG ----
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/resolveit?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";     // change if needed
    private static final String DB_PASS = "password"; // change this

    // ---- JDBC HELPER ----
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("""
            <html>
            <head>
                <title>ResolveIt - Complaint Form</title>
            </head>
            <body>
                <h2>Raise Complaint</h2>
                <form method='post' action='complaint'>
                    Category:
                    <select name='category'>
                        <option>Network</option>
                        <option>Software</option>
                        <option>Hardware</option>
                    </select><br><br>

                    Description:<br>
                    <textarea name='description' rows='4' cols='40'></textarea><br><br>

                    <input type='submit' value='Submit Complaint'>
                </form>

                <br><hr><br>
                <a href='complaints-list'>View All Complaints</a>
            </body>
            </html>
        """);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String category = req.getParameter("category");
        String description = req.getParameter("description");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // ---- VALIDATION ----
        if (description == null || description.trim().isEmpty()) {
            out.println("<h3>Error: Description cannot be empty</h3>");
            out.println("<a href='complaint'>Go Back</a>");
            return;
        }

        try (Connection con = getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO complaints(category, description, status) VALUES (?, ?, ?)"
            );
            ps.setString(1, category);
            ps.setString(2, description);
            ps.setString(3, "Open");

            ps.executeUpdate();

            out.println("<h3>Complaint Submitted Successfully!</h3>");
            out.println("<a href='complaint'>Submit Another</a><br>");
            out.println("<a href='complaints-list'>View All Complaints</a>");

        } catch (Exception e) {
            out.println("<h3>Database Error</h3>");
            e.printStackTrace(out);
        }
    }
}
