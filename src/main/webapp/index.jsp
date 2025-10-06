<%@ page contentType="text/html; charset=UTF-8" %>
<% response.sendRedirect(request.getContextPath() + "/trivia"); %>
<!-- Login/Signup/Users Buttons -->
<div class="container my-4 d-flex justify-content-center">
  <a href="<%= request.getContextPath() %>/shared/login.jsp"
     class="btn btn-primary btn-lg me-2"
     style="min-width: 180px; font-size: 1.3rem;">Login to play</a>

  <a href="<%= request.getContextPath() %>/shared/signup.jsp"
     class="btn btn-warning btn-lg me-2"
     style="min-width: 180px; font-size: 1.3rem; color: #212529;">Sign up</a>

  <!-- Nuevo botón Usuarios -->
  <a href="<%= request.getContextPath() %>/shared/users"
     class="btn btn-success btn-lg"
     style="min-width: 180px; font-size: 1.3rem;">Usuarios</a>
</div>