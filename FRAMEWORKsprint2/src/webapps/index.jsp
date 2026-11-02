<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        main { max-width: 760px; margin: auto; }
        section { margin: 24px 0; }
        ul { line-height: 2; }
        a { color: #1558a6; }
    </style>
</head>
<body>
<main>
    <h1>Accueil du framework</h1>
    <p>Selectionnez la fonctionnalite a ouvrir :</p>

    <section>
        <h2>Authentification</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/login.jsp">Se connecter</a></li>
            <li><a href="${pageContext.request.contextPath}/public">Page publique</a></li>
            <li><a href="${pageContext.request.contextPath}/profile">Profil authentifie</a></li>
            <li><a href="${pageContext.request.contextPath}/user-area">Espace utilisateur</a></li>
            <li><a href="${pageContext.request.contextPath}/admin">Espace administrateur</a></li>
            <li><a href="${pageContext.request.contextPath}/management">Espace admin ou manager</a></li>
            <li><a href="${pageContext.request.contextPath}/logout">Se deconnecter</a></li>
        </ul>
    </section>

    <section>
        <h2>Employes</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/emp/liste">Liste des employes</a></li>
            <li><a href="${pageContext.request.contextPath}/emp/new">Nouveau formulaire simple</a></li>
            <li><a href="${pageContext.request.contextPath}/emp/new-object">Formulaire objet</a></li>
            <li><a href="${pageContext.request.contextPath}/api/employes">API employes</a></li>
        </ul>
    </section>

    <section>
        <h2>Session</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/session/increment">Incrementer le compteur</a></li>
            <li><a href="${pageContext.request.contextPath}/session/reset">Reinitialiser le compteur</a></li>
        </ul>
    </section>
</main>
</body>
</html>
