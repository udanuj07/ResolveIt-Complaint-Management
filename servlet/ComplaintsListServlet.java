package resolveit;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/complaints-list")
public class ComplaintsListServlet extends HttpServlet {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/resolveit?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<html><head><title>Complaints List</title></head><body>");
        out.println("<h2>All Complaints</h2>");
        out.println("<table border='1' cellpadding='5'>");
        out.println("<tr><th>ID</th><th>Category</th><th>Description</th><th>Status</th><th>Created At</th></tr>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, category, description, status, created_at FROM complaints");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("category") + "</td>");
                out.println("<td>" + rs.getString("description") + "</td>");
                out.println("<td>" + rs.getString("status") + "</td>");
                out.println("<td>" + rs.getTimestamp("created_at") + "</td>");
                out.println("</tr>");
            }

            con.close();
        } catch (Exception e) {
            out.println("</table>");
            out.println("<h3>Error fetching complaints</h3>");
            e.printStackTrace(out);
            out.println("</body></html>");
            return;
        }

        out.println("</table>");
        out.println("<br><a href='complaint'>Back to Complaint Form</a>");
        out.println("</body></html>");
    }
}
