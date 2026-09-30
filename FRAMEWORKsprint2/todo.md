# Sprint 6 - Web API REST avec JSON

## Objectif

Le framework devait pouvoir distinguer deux types de méthodes de contrôleur :

- une méthode qui retourne une vue `ModelAndView` et qui doit ouvrir une page JSP ;
- une méthode d'API qui retourne une valeur Java et qui doit produire une réponse JSON.

Une annotation de méthode `@WebApi` a donc été ajoutée. Le framework vérifie cette annotation au moment de l'invocation de la méthode du contrôleur.

## 1. Annotation `WebApi`

Fichier : `src/java/mg/itu/myframework/annotation/WebApi.java`

```java
package mg.itu.myframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebApi {
}
```

`@Target(ElementType.METHOD)` limite l'annotation aux méthodes.

`@Retention(RetentionPolicy.RUNTIME)` permet au framework de tester l'annotation avec la reflection pendant l'exécution.

## 2. Dépendances Jackson

Les fichiers suivants ont été ajoutés dans `lib/` :

- `jackson-annotations-2.17.2.jar`
- `jackson-core-2.17.2.jar`
- `jackson-databind-2.17.2.jar`

`ObjectMapper` utilise ces trois bibliothèques pour convertir les objets Java en JSON.

Import utilisé dans le framework :

```java
import com.fasterxml.jackson.databind.ObjectMapper;
```

## 3. Traitement JSON dans le Front Controller

Fichier : `src/java/mg/itu/myframework/controller/FrontControllerServlet.java`

Après l'appel reflection de la méthode du contrôleur, le framework vérifie `@WebApi` :

```java
Object result = mapping.getMethode().invoke(controllerInstance);

if (mapping.getMethode().isAnnotationPresent(WebApi.class)) {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");

    PrintWriter jsonWriter = res.getWriter();
    if (result instanceof String) {
        jsonWriter.println((String) result);
    } else {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonWriter.println(objectMapper.writeValueAsString(result));
    }
} else if (result instanceof ModelAndView) {
    ModelAndView modelAndView = (ModelAndView) result;
    for (Map.Entry<String, Object> entry : modelAndView.getData().entrySet()) {
        req.setAttribute(entry.getKey(), entry.getValue());
    }
    req.getRequestDispatcher(modelAndView.getView()).forward(req, res);
}
```

Ainsi, une méthode annotée retourne du JSON et une méthode qui retourne `ModelAndView` continue de retourner une page JSP.

## 4. Correction du chemin de requête

Le chemin ne dépend plus d'un index fixe dans l'URL complète. Il est calculé par rapport au contexte Tomcat :

```java
String path = req.getRequestURI().substring(req.getContextPath().length());
return path.isEmpty() ? "/" : path;
```

Cela permet de fonctionner avec le contexte `/my-framework` :

```text
http://localhost:8080/my-framework/api/employes
                -> /api/employes
```

## 5. Contrôleur de test

Fichier : `src/java/controller/EmpController.java`

Une méthode API a été ajoutée :

```java
import mg.itu.myframework.annotation.WebApi;
import java.util.Arrays;
import java.util.List;

@WebApi
@UrlMapping(url = "/api/employes")
public List<String> apiEmployes() {
    return Arrays.asList("Rakoto", "Rabe", "Randria");
}
```

Réponse attendue :

```json
["Rakoto","Rabe","Randria"]
```

La méthode existante `/emp/liste` n'est pas annotée `@WebApi`. Elle continue donc à utiliser `ModelAndView` et `liste.jsp`.

## 6. Configuration Tomcat

Fichier : `src/webapps/WEB-INF/web.xml`

Le `FrontControllerServlet` est enregistré comme servlet principale :

```xml
<servlet>
    <servlet-name>frontController</servlet-name>
    <servlet-class>mg.itu.myframework.controller.FrontControllerServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>frontController</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

Sans ce fichier, Tomcat ne savait pas quelle servlet devait recevoir les URLs de l'application.

## 7. Compilation Windows

Le script `run.bat` :

1. supprime l'ancien dossier `out` ;
2. compile les fichiers Java avec les JAR de `lib` ;
3. copie les ressources web ;
4. prépare `out/WEB-INF/classes` pour Tomcat ;
5. crée `out/lib/my-framework.jar`.

Commande à lancer depuis `FRAMEWORKsprint2` :

```bat
run.bat
```

L'application web doit finalement contenir cette structure :

```text
out/
  WEB-INF/
    web.xml
    classes/
      controller/
      mg/itu/myframework/
  liste.jsp
```

## 8. Création et déploiement du WAR

Après le build :

```powershell
jar cf .\out\my-framework.war -C .\out .
Copy-Item .\out\my-framework.war `
  "C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\my-framework.war" `
  -Force
```

Tomcat déploie ensuite automatiquement le contexte :

```text
/my-framework
```

## 9. Tests réalisés

### Test de l'API JSON

```powershell
curl.exe -i http://localhost:8080/my-framework/api/employes
```

Résultat obtenu :

```text
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8

["Rakoto","Rabe","Randria"]
```

### Test de la vue JSP

```powershell
curl.exe -i http://localhost:8080/my-framework/emp/liste
```

Résultat obtenu : HTTP `200`, avec la page `liste.jsp` et les employés `Rakoto`, `Rabe` et `Randria`.

### Test d'une URL inconnue

```powershell
curl.exe -i http://localhost:8080/my-framework/inconnue
```

Résultat obtenu : HTTP `200`, avec le message indiquant que l'URL n'est pas accessible et la liste des mappings disponibles.

### Vérification du code

Les fichiers Java modifiés ne présentent aucune erreur de diagnostic :

- `WebApi.java` ;
- `FrontControllerServlet.java` ;
- `EmpController.java`.

## 10. Base de données

Ce sprint ne contient aucun accès à une base de données : pas de schéma SQL, de driver JDBC, de configuration de connexion ou de code DAO. Les employés utilisés pour le test sont donc stockés en mémoire dans le contrôleur.

Il n'y avait pas de base à réinsérer pour cette fonctionnalité.