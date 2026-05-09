<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.ecommerce.model.Product" %>
<%@ page import="com.ecommerce.model.Review" %>
<%@ page import="java.util.List" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ include file="header.jsp" %>

<div class="container">

    <% Product product = (Product) request.getAttribute("product"); %>

    <div class="card">
        <h2><%= product.getName() %></h2>
        <p style="color:#666; margin:10px 0;"><%= product.getDescription() %></p>
        <p><strong style="font-size:1.3em;">$<%= product.getPrice() %></strong>
           &nbsp;|&nbsp; Stock: <%= product.getStock() %></p>
        <a href="${pageContext.request.contextPath}/home"
           class="btn btn-primary" style="margin-top:15px;">← Back to Home</a>
    </div>

    <%-- Add Review Form — logged in NON-ADMIN users only --%>
    <% if (loggedIn && !isAdmin) { %>
    <div class="card">
        <h3 style="margin-bottom:15px;">✍️ Write a Review</h3>
        <form action="${pageContext.request.contextPath}/reviews/add" method="post">
            <input type="hidden" name="productId" value="<%= product.getId() %>">
            <div style="margin-bottom:15px;">
                <label>Rating (1-5)</label><br>
                <select name="rating"
                        style="padding:8px; margin-top:5px;
                               border:1px solid #ddd; border-radius:5px;">
                    <option value="5">⭐⭐⭐⭐⭐ 5</option>
                    <option value="4">⭐⭐⭐⭐ 4</option>
                    <option value="3">⭐⭐⭐ 3</option>
                    <option value="2">⭐⭐ 2</option>
                    <option value="1">⭐ 1</option>
                </select>
            </div>
            <div style="margin-bottom:15px;">
                <label>Comment</label><br>
                <textarea name="comment" rows="3" required
                          style="width:100%; padding:10px; margin-top:5px;
                                 border:1px solid #ddd; border-radius:5px;"></textarea>
            </div>
            <button type="submit" class="btn btn-success">Submit Review</button>
        </form>
    </div>
    <% } else if (!loggedIn) { %>
    <div class="card" style="background:#fff8e1;">
        <p>Please <a href="${pageContext.request.contextPath}/login">login</a>
           to write a review.</p>
    </div>
    <% } %>

    <%-- Reviews List --%>
    <h3 style="margin-bottom:15px;">💬 Reviews</h3>
    <%
        List<Review> reviews = (List<Review>) request.getAttribute("reviews");
        if (reviews == null || reviews.isEmpty()) {
    %>
        <div class="card"><p>No reviews yet. Be the first!</p></div>
    <%
        } else {
            for (Review r : reviews) {
    %>
        <div class="card">
            <p><strong><%= r.getUsername() %></strong>
               &nbsp;⭐ <%= r.getRating() %>/5</p>
            <p style="margin-top:5px; color:#555;"><%= r.getComment() %></p>
            <p style="font-size:0.8em; color:#aaa; margin-top:5px;"><%= r.getCreatedAt() %></p>
        </div>
    <%
            }
        }
    %>

</div>

<%@ include file="footer.jsp" %>