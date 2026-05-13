<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Product" %>
<%@ page import="com.ecommerce.model.Review" %>
<%@ page import="com.ecommerce.model.User" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ include file="header.jsp" %>

<div class="container">

    <%-- Success / error messages --%>
    <% if ("true".equals(request.getParameter("added"))) { %>
        <div class="alert alert-success">Product added successfully!</div>
    <% } %>


    <% if (!loggedIn) { %>
        <div class="alert alert-error" style="background:#fff3cd; color:#856404; border:1px solid #ffc107;">
            Welcome! Please <a href="${pageContext.request.contextPath}/login" 
            style="color:#2980b9; font-weight:bold;">Login</a> 
            or <a href="${pageContext.request.contextPath}/register" 
            style="color:#27ae60; font-weight:bold;">Register</a> to access all features.
        </div>
    <% } %>

    <%-- Logged-in user info card --%>
    <% User user = (User) request.getAttribute("user"); %>
    <% if (loggedIn && user != null) { %>
        <div class="card" style="background:#eaf4fb;">
            <h3>Welcome back, <%= user.getUsername() %>!</h3>
            <p>Email: <%= user.getEmail() %> &nbsp;|&nbsp; Role: <%= user.getRole() %></p>
            <form action="${pageContext.request.contextPath}/account/delete"
                  method="post" style="margin-top:10px"
                  onsubmit="return confirm('Are you sure you want to delete your account?');">
                <button class="btn btn-danger">Delete My Account</button>
            </form>
        </div>
    <% } %>

    <%-- Products Section --%>
    <h2 style="margin-bottom:20px;">Products</h2>

    <%
        List<Product> products = (List<Product>) request.getAttribute("products");
        boolean adminCheck = loggedIn && "ADMIN".equals(
            request.getSession(false).getAttribute("role")
        );
        if (products == null || products.isEmpty()) {
    %>
        <div class="card"><p>No products available yet.</p></div>
    <%
        } else {
            for (Product p : products) {
    %>
        <div class="card" style="display:flex; justify-content:space-between; align-items:center;">
            <div>
                <h3>
                    <a href="${pageContext.request.contextPath}/products/detail?id=<%= p.getId() %>">
                        <%= p.getName() %>
                    </a>
                </h3>
                <p style="color:#666; margin:5px 0;"><%= p.getDescription() %></p>
                <p><strong>$<%= p.getPrice() %></strong>
                   &nbsp;|&nbsp; Stock: <%= p.getStock() %></p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/products/detail?id=<%= p.getId() %>"
                   class="btn btn-primary">View</a>
                <% if (adminCheck) { %>
                    <form action="${pageContext.request.contextPath}/products/delete"
                          method="post" style="display:inline"
                          onsubmit="return confirm('Delete this product?');">
                        <input type="hidden" name="id" value="<%= p.getId() %>">
                        <button class="btn btn-danger">Delete</button>
                    </form>
                <% } %>
            </div>
        </div>
    <%
            }
        }
    %>

    <%-- Reviews Section --%>
    <h2 style="margin:30px 0 20px;">Recent Reviews</h2>
    <%
        List<Review> reviews = (List<Review>) request.getAttribute("reviews");
        if (reviews == null || reviews.isEmpty()) {
    %>
        <div class="card"><p>No reviews yet.</p></div>
    <%
        } else {
            for (Review r : reviews) {
    %>
        <div class="card">
            <p><strong><%= r.getUsername() %></strong>
               &nbsp;<%= r.getRating() %>/5</p>
            <p style="margin-top:5px; color:#555;"><%= r.getComment() %></p>
            <p style="font-size:0.8em; color:#aaa; margin-top:5px;"><%= r.getCreatedAt() %></p>
        </div>
    <%
            }
        }
    %>

</div>

<%@ include file="footer.jsp" %>