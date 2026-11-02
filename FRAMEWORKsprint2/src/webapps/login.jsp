<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Connexion</title>
</head>
<body>
    <h1>Connexion</h1>
    <form method="post" action="${pageContext.request.contextPath}/login/check">
        <label>Identifiant <input type="text" name="login" required></label><br>
        <label>Mot de passe <input type="password" name="mdp" required></label><br>
        <button type="submit">Se connecter</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/public">Page publique</a></p>
</body>
</html>