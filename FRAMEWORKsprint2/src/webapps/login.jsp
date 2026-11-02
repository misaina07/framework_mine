<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion</title>
</head>
<body>
    <h1>Connexion</h1>
    <form method="get" action="${pageContext.request.contextPath}/login">
        <label for="login">Login</label>
        <input id="login" name="login" type="text" required>
        <label for="mdp">Mot de passe</label>
        <input id="mdp" name="mdp" type="password" required>
        <button type="submit">Se connecter</button>
    </form>
    <p><a href="${pageContext.request.contextPath}/public">Page publique</a></p>
</body>
</html>
