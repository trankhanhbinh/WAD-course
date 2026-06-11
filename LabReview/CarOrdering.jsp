<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Car Ordering</title>
    <script>
        function validateForm() {
            var form = document.forms["carForm"];
            var fullname = form["fullname"].value.trim();
            var email = form["email"].value.trim();
            var phone = form["phone"].value.trim();
            var address = form["address"].value.trim();
            var country = form["country"].value;
            var style = form["style"].value;
            var engine = form["engine"].value;
            var automaker = form["automaker"].value;
            var quantity = form["quantity"].value;
            var price = form["price"].value.trim();

            if (fullname === "") {
                alert("FullName is required.");
                form["fullname"].focus();
                return false;
            }
            if (email === "") {
                alert("Email is required.");
                form["email"].focus();
                return false;
            }
            if (phone === "") {
                alert("PhoneNumber is required.");
                form["phone"].focus();
                return false;
            }
            if (address === "") {
                alert("Address is required.");
                form["address"].focus();
                return false;
            }
            if (country === "") {
                alert("Please select a Country.");
                form["country"].focus();
                return false;
            }
            if (style === "") {
                alert("Please select a Car Style.");
                form["style"].focus();
                return false;
            }
            if (engine === "") {
                alert("Please select an Engine type.");
                form["engine"].focus();
                return false;
            }
            if (automaker === "") {
                alert("Please select an Automaker.");
                form["automaker"].focus();
                return false;
            }
            if (quantity === "" || isNaN(quantity) || quantity <= 0) {
                alert("Quantity must be a number greater than 0.");
                form["quantity"].focus();
                return false;
            }
            if (price === "" || isNaN(price) || price <= 0) {
                alert("Price must be a number greater than 0.");
                form["price"].focus();
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
    <h2>Car Ordering Form</h2>
    <form name="carForm" action="CarRegister" method="post" onsubmit="return validateForm()">
        FullName:
        <input type="text" name="fullname"><br><br>

        Email:
        <input type="email" name="email"><br><br>

        PhoneNumber:
        <input type="text" name="phone"><br><br>

        Address:
        <input type="text" name="address"><br><br>

        Country:
        <select name="country">
            <option value="">-- Select Country --</option>
            <option value="Vietnam">Vietnam</option>
            <option value="USA">USA</option>
            <option value="Japan">Japan</option>
        </select><br><br>

        Style:
        <select name="style">
            <option value="">-- Select Style --</option>
            <option value="SUV">SUV</option>
            <option value="SEDAN">SEDAN</option>
        </select><br><br>

        Engine:
        <select name="engine">
            <option value="">-- Select Engine --</option>
            <option value="Gasoline">Gasoline</option>
            <option value="Electric">Electric</option>
        </select><br><br>

        Automaker:
        <select name="automaker">
            <option value="">-- Select Automaker --</option>
            <option value="Toyota">Toyota</option>
            <option value="Ford">Ford</option>
            <option value="Suzuki">Suzuki</option>
        </select><br><br>

        Quantity:
        <input type="number" name="quantity" min="1"><br><br>

        Price:
        <input type="text" name="price"><br><br>

        <input type="submit" value="Order">
        <input type="reset" value="Reset">
    </form>
</body>
</html>