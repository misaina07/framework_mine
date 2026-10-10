<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Nouvel employe</title>
</head>
<body>
    <h1>Nouvel employe</h1>
    <form method="post" action="${pageContext.request.contextPath}/emp/save-object">
        <label>Nom <input type="text" name="nom" required></label><br>
        <label>Age <input type="number" name="age" required></label><br>
        <label>Email <input type="email" name="email" required></label><br>
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>