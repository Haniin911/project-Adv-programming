<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ include file="header.jsp" %>

<div class="container" style="text-align:center; margin-top:80px;">
    <div class="card">
        <h2 style="margin:20px 0;">Something went wrong</h2>
        <% Integer code = (Integer) request.getAttribute("jakarta.servlet.error.status_code"); %>
        <% if (code != null && code == 404) { %>
            <p>The page you're looking for doesn't exist.</p>
        <% } else if (code != null && code == 429) { %>
            <p>Too many requests. Please slow down and try again.</p>
        <% } else { %>
            <p>An unexpected error occurred. Please try again later.</p>
        <% } %>
        <a href="${pageContext.request.contextPath}/home"
           class="btn btn-primary" style="margin-top:20px;">Go Home</a>
    </div>
</div>

<%@ include file="footer.jsp" %>