import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class CarRegister extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String country = request.getParameter("country");
        String style = request.getParameter("style");
        String engine = request.getParameter("engine");
        String automaker = request.getParameter("automaker");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        double price = Double.parseDouble(request.getParameter("price"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/CarDB", "root", "123456");

            // 1. Lưu dữ liệu (Bỏ cột ID vì đã set AUTO_INCREMENT)
            String insertSql = "INSERT INTO CarRegister (FullName, Email, Phone, Address, Country, Style, Engine, Automaker, Quantity, Price) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement insertPs = con.prepareStatement(insertSql);
            insertPs.setString(1, fullname);
            insertPs.setString(2, email);
            insertPs.setString(3, phone);
            insertPs.setString(4, address);
            insertPs.setString(5, country);
            insertPs.setString(6, style);
            insertPs.setString(7, engine);
            insertPs.setString(8, automaker);
            insertPs.setInt(9, quantity);
            insertPs.setDouble(10, price);
            insertPs.executeUpdate();

            // 2. Load và hiển thị dữ liệu
            String selectSql = "SELECT * FROM CarRegister WHERE Email = ?";
            PreparedStatement selectPs = con.prepareStatement(selectSql);
            selectPs.setString(1, email);
            ResultSet rs = selectPs.executeQuery();

            out.println("<html><body>");
            out.println("<h2>Your Order Confirmed</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>Name</th><th>Car</th><th>Engine</th><th>Quantity</th><th>Price</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("FullName") + "</td>");
                out.println("<td>" + rs.getString("Automaker") + " " + rs.getString("Style") + "</td>");
                out.println("<td>" + rs.getString("Engine") + "</td>");
                out.println("<td>" + rs.getInt("Quantity") + "</td>");
                out.println("<td>$" + rs.getDouble("Price") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            out.println("</body></html>");

            // Đóng tài nguyên
            rs.close();
            selectPs.close();
            insertPs.close();
            con.close();

        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        }
    }
}