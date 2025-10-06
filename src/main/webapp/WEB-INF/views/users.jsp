<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  // Datos del request y sesión
  java.util.List users = (java.util.List) request.getAttribute("users");
  Object sessionUser = session.getAttribute("sessionUser");
  boolean canEdit = (sessionUser != null);
  String ctx = request.getContextPath();
%>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Users</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="p-4">
<div class="container">

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="m-0">Users</h3>
    <div>
      <% if (canEdit) { %>
        <a class="btn btn-sm btn-secondary" href="<%= ctx %>/shared/logout">Logout</a>
      <% } else { %>
        <a class="btn btn-sm btn-primary" href="<%= ctx %>/shared/login.jsp">Login</a>
      <% } %>
    </div>
  </div>

  <table class="table table-striped">
    <thead>
      <tr><th>ID</th><th>Username</th><th style="width:360px"></th></tr>
    </thead>
    <tbody>
    <% if (users == null || users.isEmpty()) { %>
      <tr>
        <td colspan="3" class="text-center text-muted">No hay usuarios aún.</td>
      </tr>
    <% } else {
         for (Object o : users) {
           net.ausiasmarch.shared.model.UserBean u = (net.ausiasmarch.shared.model.UserBean) o; %>
      <tr>
        <td><%= u.getId() %></td>
        <td><%= u.getUsername() %></td>
        <td>
          <% if (canEdit) { %>
            <!-- Cambiar username -->
            <form class="d-inline" method="post" action="<%= ctx %>/shared/users">
              <input type="hidden" name="action" value="update">
              <input type="hidden" name="id"     value="<%= u.getId() %>">
              <input class="form-control form-control-sm d-inline w-auto" type="text" name="username"
                     value="<%= u.getUsername() %>" required>
              <button class="btn btn-sm btn-outline-primary">Guardar</button>
            </form>

            <!-- Cambiar password (espera SHA-256 en hex) -->
            <form class="d-inline ms-2" method="post" action="<%= ctx %>/shared/users">
              <input type="hidden" name="action" value="setpass">
              <input type="hidden" name="id"     value="<%= u.getId() %>">
              <input class="form-control form-control-sm d-inline w-auto" type="text" name="password"
                     placeholder="SHA-256 (64 hex)" required>
              <button class="btn btn-sm btn-outline-warning">Set pass</button>
            </form>

            <!-- Borrar -->
            <form class="d-inline ms-2" method="post" action="<%= ctx %>/shared/users"
                  onsubmit="return confirm('¿Borrar usuario <%= u.getUsername() %>?');">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id"     value="<%= u.getId() %>">
              <button class="btn btn-sm btn-outline-danger">Borrar</button>
            </form>
          <% } else { %>
            <span class="text-muted">Solo lectura</span>
          <% } %>
        </td>
      </tr>
    <% } } %>
    </tbody>
  </table>

  <% if (canEdit) { %>
  <h5 class="mt-4">Nuevo usuario</h5>
  <form method="post" action="<%= ctx %>/shared/users" class="row g-2">
    <input type="hidden" name="action" value="create">
    <div class="col-md-5"><input class="form-control" name="username" placeholder="usuario" required></div>
    <div class="col-md-5"><input class="form-control" name="password" type="password"
                                 placeholder="contraseña (se guardará con SHA-256)" required></div>
    <div class="col-md-2"><button class="btn btn-primary w-100">Crear</button></div>
  </form>
  <% } %>

</div>
</body>
</html>