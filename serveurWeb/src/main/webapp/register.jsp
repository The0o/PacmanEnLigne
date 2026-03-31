<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Inscription Pacman</title>
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #14213d, #264653);
            color: #f1f5f9;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .card {
            width: 360px;
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(255, 255, 255, 0.14);
            border-radius: 16px;
            padding: 28px;
            box-shadow: 0 16px 40px rgba(0, 0, 0, 0.25);
            backdrop-filter: blur(8px);
        }
        h1 {
            margin: 0 0 18px;
            text-align: center;
        }
        label {
            display: block;
            margin: 14px 0 6px;
            font-weight: 600;
        }
        input {
            width: 100%;
            box-sizing: border-box;
            padding: 12px;
            border-radius: 10px;
            border: 1px solid #94a3b8;
            font-size: 14px;
        }
        button {
            width: 100%;
            margin-top: 18px;
            padding: 12px;
            border: 0;
            border-radius: 10px;
            background: #2a9d8f;
            color: #f8fafc;
            font-weight: 700;
            cursor: pointer;
        }
        .message {
            margin: 12px 0;
            padding: 10px 12px;
            border-radius: 10px;
            font-size: 14px;
            background: rgba(220, 38, 38, 0.18);
            border: 1px solid rgba(248, 113, 113, 0.35);
        }
        .links {
            margin-top: 16px;
            text-align: center;
        }
        a {
            color: #8ecae6;
            text-decoration: none;
        }
        .hint {
            margin-top: 10px;
            font-size: 13px;
            color: #cbd5e1;
        }
    </style>
</head>
<body>
    <div class="card">
        <h1>Inscription</h1>

        <% if (request.getAttribute("error") != null) { %>
            <div class="message"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/api/users">
            <label for="username">Username</label>
            <input
                id="username"
                name="username"
                type="text"
                value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                required>

            <label for="password">Mot de passe</label>
            <input id="password" name="password" type="password" required>

            <div class="hint">Le mot de passe doit contenir au moins 6 caracteres.</div>

            <button type="submit">Creer le compte</button>
        </form>

        <div class="links">
            <a href="<%= request.getContextPath() %>/api/auth/login">Retour a la connexion</a>
        </div>
    </div>
</body>
</html>
