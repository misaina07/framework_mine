<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Liste des employés</title>
</head>
<body>
    <h1>Liste des employés</h1>
    <ul>
<%
        List<String> employes = (List<String>) request.getAttribute("employes");
        if (employes != null) {
            for (String employe : employes) {
%>
        <li><%= employe %></li>
<%
            }
        }
%>
    </ul>
</body>
</html>
