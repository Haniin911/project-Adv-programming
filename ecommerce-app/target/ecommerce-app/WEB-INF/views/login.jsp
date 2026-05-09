<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:450px; margin-top:60px;">
    <div class="card">
        <h2 style="margin-bottom:20px; text-align:center;">🔐 Login</h2>

        <% if ("true".equals(request.getParameter("registered"))) { %>
            <div class="alert alert-success">✅ Registered! Please log in.</div>
        <% } %>
        <% if ("true".equals(request.getParameter("deleted"))) { %>
            <div class="alert alert-success">Account deleted successfully.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">❌ <%= request.getAttribute("error") %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div style="margin-bottom:15px;">
                <label>Email</label><br>
                <input type="email" name="email" required
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <div style="margin-bottom:20px;">
                <label>Password</label><br>
                <input type="password" name="password" required
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%; padding:12px;">
                Login
            </button>
        </form>

        <p style="text-align:center; margin-top:15px;">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/register">Register</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>