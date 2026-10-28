<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head><title>Session Demo</title></head>
<body>
    <h2>Compteur de session : ${count}</h2>
    <a href="${pageContext.request.contextPath}/session/increment">Incrémenter</a>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/session/reset">Réinitialiser</a>
</body>
</html>
