<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Leaderboard public</title>
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #081c15, #2d6a4f);
            color: #f8fafc;
            min-height: 100vh;
            padding: 32px 16px;
            box-sizing: border-box;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
        }
        .card {
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(255, 255, 255, 0.14);
            border-radius: 16px;
            padding: 24px;
            box-shadow: 0 16px 40px rgba(0, 0, 0, 0.22);
        }
        h1 {
            margin-top: 0;
        }
        .actions {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-bottom: 20px;
        }
        .button {
            display: inline-block;
            padding: 10px 14px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 700;
            background: rgba(255, 255, 255, 0.12);
            color: #f8fafc;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            text-align: left;
            padding: 12px 10px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.12);
        }
        th {
            color: #cbd5e1;
        }
        .message {
            margin-top: 16px;
            color: #cbd5e1;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <div class="actions">
                <a class="button" href="<%= request.getContextPath() %>/home.jsp">Accueil</a>
                <a class="button" href="<%= request.getContextPath() %>/history.jsp">Mon historique</a>
            </div>

            <h1>Leaderboard public</h1>

            <table>
                <thead>
                    <tr>
                        <th>Rang</th>
                        <th>Username</th>
                        <th>Meilleur score</th>
                    </tr>
                </thead>
                <tbody id="leaderboardBody">
                    <tr>
                        <td colspan="3">Chargement...</td>
                    </tr>
                </tbody>
            </table>

            <div class="message" id="message"></div>
        </div>
    </div>

    <script>
        async function chargerLeaderboard() {
            const body = document.getElementById("leaderboardBody");
            const message = document.getElementById("message");

            try {
                const response = await fetch("<%= request.getContextPath() %>/api/leaderboard");
                const data = await response.json();

                if (!response.ok) {
                    body.innerHTML = "<tr><td colspan='3'>Impossible de charger le leaderboard.</td></tr>";
                    message.textContent = data.error || "Erreur serveur.";
                    return;
                }

                if (!data.items || data.items.length === 0) {
                    body.innerHTML = "<tr><td colspan='3'>Aucun score disponible.</td></tr>";
                    return;
                }

                body.innerHTML = data.items.map(item =>
                    "<tr><td>" + item.rank + "</td><td>" + item.username + "</td><td>" + item.bestScore + "</td></tr>"
                ).join("");
            } catch (error) {
                body.innerHTML = "<tr><td colspan='3'>Erreur de chargement.</td></tr>";
                message.textContent = "La requete vers le serveur a echoue.";
            }
        }

        chargerLeaderboard();
    </script>
</body>
</html>
