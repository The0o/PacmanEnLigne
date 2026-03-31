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
    <title>Historique des scores</title>
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #14213d, #1d3557);
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
        .stats {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-bottom: 18px;
        }
        .stat {
            background: rgba(15, 23, 42, 0.28);
            border-radius: 10px;
            padding: 10px 14px;
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
                <a class="button" href="<%= request.getContextPath() %>/leaderboard.jsp">Leaderboard public</a>
            </div>

            <h1>Mon historique</h1>
            <div class="stats">
                <div class="stat" id="currentUser">Utilisateur: ...</div>
                <div class="stat" id="bestScore">Meilleur score: ...</div>
                <div class="stat" id="totalScores">Nombre de parties: ...</div>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Score</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody id="historyBody">
                    <tr>
                        <td colspan="2">Chargement...</td>
                    </tr>
                </tbody>
            </table>

            <div class="message" id="message"></div>
        </div>
    </div>

    <script>
        function formatDateHumain(value) {
            if (!value) {
                return "-";
            }

            const normalized = value.replace(" ", "T");
            const dotIndex = normalized.indexOf(".");
            const base = dotIndex >= 0 ? normalized.substring(0, dotIndex) : normalized;
            const date = new Date(base);

            if (isNaN(date.getTime())) {
                return value;
            }

            return new Intl.DateTimeFormat("fr-FR", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit"
            }).format(date);
        }

        async function chargerHistorique() {
            const body = document.getElementById("historyBody");
            const message = document.getElementById("message");

            try {
                const response = await fetch("<%= request.getContextPath() %>/api/scores/history");
                const data = await response.json();

                if (!response.ok) {
                    body.innerHTML = "<tr><td colspan='2'>Impossible de charger l'historique.</td></tr>";
                    message.textContent = data.error || "Erreur serveur.";
                    return;
                }

                document.getElementById("currentUser").textContent = "Utilisateur: " + (data.currentUser || "-");
                document.getElementById("bestScore").textContent = "Meilleur score: " + (data.bestScore ?? "-");
                document.getElementById("totalScores").textContent = "Nombre de parties: " + (data.totalScores ?? 0);

                if (!data.items || data.items.length === 0) {
                    body.innerHTML = "<tr><td colspan='2'>Aucun score pour le moment.</td></tr>";
                    return;
                }

                body.innerHTML = data.items.map(item => {
                    const createdAt = formatDateHumain(item.createdAt);
                    return "<tr><td>" + item.score + "</td><td>" + createdAt + "</td></tr>";
                }).join("");
            } catch (error) {
                body.innerHTML = "<tr><td colspan='2'>Erreur de chargement.</td></tr>";
                message.textContent = "La requete vers le serveur a echoue.";
            }
        }

        chargerHistorique();
    </script>
</body>
</html>
