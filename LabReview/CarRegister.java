import java.io.*;
import java.sql.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class CarRegister extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
                    "jdbc:mysql://localhost:3306/CarDB",
                    "root",
                    "123456");
            String sql = "insert into CarRegister values(?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, 0);
            ps.setString(2, fullname);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, country);
            ps.setString(7, style);
            ps.setString(8, engine);
            ps.setString(9, automaker);
            ps.setInt(10, quantity);
            ps.setDouble(11, price);
            ps.executeUpdate();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from CarRegister where Email='" + email + "'");
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<body>");
            out.println("<h2>Your Order</h2>");
            while (rs.next()) {
                out.println(
                        "Name: "
                                + rs.getString("FullName")
                                + "<br>");
                out.println(
                        "Car: "
                                + rs.getString("Automaker")
                                + " "
                                + rs.getString("Style")
                                + "<br>");
                out.println(
                        "Engine: "
                                + rs.getString("Engine")
                                + "<br>");
                out.println(
                        "Quantity: "
                                + rs.getInt("Quantity")
                                + "<br>");
                out.println(
                        "Price: "
                                + rs.getDouble("Price")
                                + "<br>");
            }
            out.println("</body>");
            out.println("</html>");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}