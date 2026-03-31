<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/api/auth/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Accueil Pacman</title>
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #081c15, #1b4332);
            color: #f1f5f9;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .card {
            width: 560px;
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(255, 255, 255, 0.14);
            border-radius: 16px;
            padding: 32px;
            box-shadow: 0 16px 40px rgba(0, 0, 0, 0.25);
        }
        h1 {
            margin-top: 0;
        }
        code {
            background: rgba(15, 23, 42, 0.35);
            padding: 2px 6px;
            border-radius: 6px;
        }
        .actions {
            margin-top: 24px;
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }
        .button {
            display: inline-block;
            padding: 12px 16px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 700;
        }
        .primary {
            background: #52b788;
            color: #081c15;
        }
        .secondary {
            background: rgba(255, 255, 255, 0.12);
            color: #f1f5f9;
        }
    </style>
</head>
<body>
    <div class="card">
        <h1>Bienvenue <%= session.getAttribute("username") %></h1>
        <p>La connexion JSP fonctionne maintenant cote serveur web.</p>
        <p>
            Session active:
            <code>userId=<%= session.getAttribute("userId") %></code>
            <code>role=<%= session.getAttribute("role") %></code>
        </p>
        <p>Le client Pacman desktop peut continuer a utiliser l'API JSON existante, mais pour la demonstration web vous pouvez montrer cette page JSP.</p>
        <div class="actions">
            <a class="button secondary" href="<%= request.getContextPath() %>/history.jsp">Mon historique</a>
            <a class="button secondary" href="<%= request.getContextPath() %>/leaderboard.jsp">Leaderboard public</a>
            <a class="button primary" href="<%= request.getContextPath() %>/api/auth/logout">Se deconnecter</a>
            <a class="button secondary" href="<%= request.getContextPath() %>/api/me">Tester /api/me</a>
        </div>
    </div>
</body>
</html>
