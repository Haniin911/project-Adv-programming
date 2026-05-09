<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:450px; margin-top:60px;">
    <div class="card">
        <h2 style="margin-bottom:20px; text-align:center;">📝 Register</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">❌ <%= request.getAttribute("error") %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div style="margin-bottom:15px;">
                <label>Username</label><br>
                <input type="text" name="username" required
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <div style="margin-bottom:15px;">
                <label>Email</label><br>
                <input type="email" name="email" required
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <div style="margin-bottom:20px;">
                <label>Password <small>(min 6 characters)</small></label><br>
                <input type="password" name="password" required minlength="6"
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <button type="submit" class="btn btn-success" style="width:100%; padding:12px;">
                Register
            </button>
        </form>

        <p style="text-align:center; margin-top:15px;">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login">Login</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>