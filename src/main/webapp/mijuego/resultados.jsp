<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
Integer score = (Integer) session.getAttribute("score");
java.util.List preguntas = (java.util.List) session.getAttribute("preguntas");
int total = preguntas == null ? 0 : preguntas.size();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Resultados</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
<style>
body { background:#0b1220; color:#e8eefc; }
.card { background:#111a2e; border:1px solid #213055; }
</style>
</head>
<body class="p-3">
<div class="container" style="max-width:720px;">
<div class="card p-4 text-center">
<h2 class="mb-3">Â¡Fin del juego!</h2>
<p class="lead">Has acertado <strong><%= score %></strong> de <strong><%= total %></strong>.</p>
<div class="d-flex gap-2 justify-content-center mt-3">
<a class="btn btn-primary" href="<%= request.getContextPath() %>/trivia?reset=1">Jugar otra vez</a>
<a class="btn btn-outline-light" href="<%= request.getContextPath() %>/">Volver al inicio</a>
</div>
</div>
</div>
</body>
</html>