<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Commerce App</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; color: #333; }

        nav {
            background: #2c3e50; color: white;
            padding: 15px 30px;
            display: flex; justify-content: space-between; align-items: center;
        }
        nav a { color: white; text-decoration: none; margin-left: 15px; }
        nav a:hover { text-decoration: underline; }
        nav .brand { font-size: 1.4em; font-weight: bold; }

        .container { max-width: 1100px; margin: 30px auto; padding: 0 20px; }

        .btn {
            padding: 8px 16px; border: none; border-radius: 5px;
            cursor: pointer; font-size: 0.9em; text-decoration: none;
            display: inline-block;
        }
        .btn-primary   { background: #2980b9; color: white; }
        .btn-danger    { background: #e74c3c; color: white; }
        .btn-success   { background: #27ae60; color: white; }
        .btn:hover     { opacity: 0.85; }

        .alert {
            padding: 10px 15px; border-radius: 5px;
            margin-bottom: 20px; font-size: 0.95em;
        }
        .alert-error   { background: #fdecea; color: #c0392b; border: 1px solid #e74c3c; }
        .alert-success { background: #eafaf1; color: #1e8449; border: 1px solid #27ae60; }

        .card {
            background: white; border-radius: 8px;
            padding: 20px; margin-bottom: 20px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
    </style>
</head>
<body>
<%
    HttpSession navSession = request.getSession(false);
    boolean loggedIn = (navSession != null && navSession.getAttribute("userId") != null);
    boolean isAdmin  = loggedIn && "ADMIN".equals(navSession.getAttribute("role"));
    String  navUsername = loggedIn ? (String) navSession.getAttribute("username") : "";
%>
<nav>
    <span class="brand">🛒 ShopApp</span>
    <div>
        <a href="${pageContext.request.contextPath}/home">Home</a>
        <% if (loggedIn) { %>
            <% if (isAdmin) { %>
                <a href="${pageContext.request.contextPath}/products/add">Add Product</a>
            <% } %>
            <span style="margin-left:15px; color:#ccc;"> <%= navUsername %></span>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        <% } else { %>
            <a href="${pageContext.request.contextPath}/login">Login</a>
            <a href="${pageContext.request.contextPath}/register">Register</a>
        <% } %>
    </div>
</nav>