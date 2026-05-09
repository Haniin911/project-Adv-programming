<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:550px; margin-top:40px;">
    <div class="card">
        <h2 style="margin-bottom:20px;">➕ Add New Product</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">❌ <%= request.getAttribute("error") %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/products/add" method="post">
            <div style="margin-bottom:15px;">
                <label>Product Name</label><br>
                <input type="text" name="name" required
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <div style="margin-bottom:15px;">
                <label>Description</label><br>
                <textarea name="description" rows="3"
                          style="width:100%; padding:10px; margin-top:5px;
                                 border:1px solid #ddd; border-radius:5px;"></textarea>
            </div>
            <div style="margin-bottom:15px;">
                <label>Price ($)</label><br>
                <input type="number" name="price" step="0.01" min="0" required
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <div style="margin-bottom:20px;">
                <label>Stock</label><br>
                <input type="number" name="stock" min="0" required
                       style="width:100%; padding:10px; margin-top:5px;
                              border:1px solid #ddd; border-radius:5px;">
            </div>
            <button type="submit" class="btn btn-success" style="width:100%; padding:12px;">
                Add Product
            </button>
        </form>
    </div>
</div>

<%@ include file="footer.jsp" %>