# TODO : Sprint 10, Authentification

## Objectif

Pouvoir protéger une méthode de contrôleur avec `@Auth`. Si l'utilisateur n'est pas connecté, la méthode n'est pas exécutée. Le framework doit aussi offrir une session simple à manipuler (`MapSession`) sans toucher directement à `HttpSession`.

---

## Étape 1 : Créer l'annotation `@Auth`

- [x] Créer `mg.itu.framework.annotation.Auth`
- [x] `@Target(ElementType.METHOD)`
- [x] `@Retention(RetentionPolicy.RUNTIME)`, sinon la réflexion ne la verra pas
- [x] Aucun attribut pour l'instant

## Étape 2 : Rendre la clé d'authentification configurable

- [x] Dans le `web.xml` du projet de test, ajouter :

  ```xml
  <context-param>
      <param-name>sessionAuth</param-name>
      <param-value>user</param-value>
  </context-param>
  ```

- [x] Dans `AppStartUpListener.contextInitialized`, lire ce paramètre avec `context.getInitParameter("sessionAuth")`
- [x] Le stocker dans le contexte : `context.setAttribute("sessionAuth", sessionAuth)`
- [x] Ne pas coder `"user"` en dur dans le framework : le nom doit venir du `web.xml`

## Étape 3 : Créer la classe `MapSession`

Dans `mg.itu.framework.model`.

- [x] Attribut `Map<String, Object> attributes` initialisé dans le constructeur
- [x] Méthodes :
  - `setAttribute` / `add`
  - `getAttribute` / `get`
  - `removeAttribute` / `remove`
  - `clearAttributes`
  - `getAttributes` / `setAttributes`
  - `getAttributeKeys` / `getKeys`

## Étape 4 : Créer `SessionUtil` (synchronisation avec `HttpSession`)

Dans `mg.itu.framework.util`.

- [x] Stocker dans le `ServletContext` une `ConcurrentHashMap<String, MapSession>` (clé = `session.getId()`), créée à la demande dans un bloc `synchronized`
- [x] `getMapSessionContext(req)` : retourne la `MapSession` de la session courante, ou `null`
- [x] `setMapSessionContext(req, ms)` : l'enregistre dans cette map
- [x] `setMapSession(req, ms)` :
  - crée une `MapSession` si `ms` est `null`
  - y copie tous les attributs de la `HttpSession`
  - la range dans le contexte
- [x] `rectifySession(req, ms)` : remet les modifications dans la vraie `HttpSession`
  - supprimer de la `HttpSession` les clés absentes de la `MapSession`
  - ajouter ou mettre à jour les autres
- [x] `verifyMapSessionObject(req, args)` : retourne `true` si un des arguments de la méthode est un `MapSession`

## Étape 5 : Injecter `MapSession` dans les paramètres de méthode

Dans `FrontControllerServlet.getArgs` :

- [x] Ajouter un cas `parameterType == MapSession.class`
- [x] Appeler `SessionUtil.setMapSession(req, SessionUtil.getMapSessionContext(req))`, puis passer la `MapSession` en argument
- [x] Après `method.invoke(...)`, si `verifyMapSessionObject` est vrai, appeler `rectifySession`
  → c'est ce qui rend les modifications du développeur persistantes dans la vraie session

## Étape 6 : Vérifier `@Auth` avant d'invoquer la méthode

Dans `FrontControllerServlet.invokeMethod`, avant l'appel :

- [x] Récupérer la `MapSession` de la session courante
- [x] Lire `sessionAuth` (la clé) dans le `ServletContext`
- [x] Regarder si `mapSession.get(clé) != null`
- [x] Si la méthode a `@Auth` et que l'utilisateur n'est pas authentifié, afficher un message et faire `return`, **sans exécuter la méthode**

## Étape 7 : Écrire le projet de test

- [x] `login(String login, String mdp, MapSession session)` : si les identifiants sont corrects, `session.add("user", ...)`
- [x] `logout(MapSession session)` : `session.remove("user")`
- [x] Une méthode **sans** `@Auth` (page publique)
- [x] Une méthode **avec** `@Auth` (page protégée)
- [x] Un formulaire de login (JSP)

## Étape 8 : Tests à valider

- [ ] Accès à la page protégée sans login : refusé
- [ ] Login avec mauvais mot de passe puis page protégée : refusé
- [ ] Login correct puis page protégée : accepté
- [ ] Logout puis page protégée : refusé
- [ ] Deux navigateurs (ou une navigation privée) : l'authentification de l'un n'affecte pas l'autre
- [ ] La page publique reste accessible tout le temps

---

## Pièges fréquents

1. **Oublier `RetentionPolicy.RUNTIME`** : `isAnnotationPresent(Auth.class)` renvoie toujours `false` et la sécurité ne s'applique jamais.
2. **Ne pas appeler `rectifySession` après l'invocation** : le login semble marcher, mais la clé n'arrive jamais dans la vraie session.
3. **Écrire la clé de session en dur** : le framework doit permettre de changer le nom via le `web.xml`.
4. **Vérifier `@Auth` après `invoke`** : la méthode serait déjà exécutée. La vérification doit venir avant.
5. **Faire un `login` sans paramètre `MapSession`** : il n'a alors aucun moyen d'écrire dans la session.

---

## Pour aller plus loin (bonus)

- [ ] Renvoyer un **401** ou une **redirection vers la page de login** au lieu d'un simple texte
- [ ] Ajouter un attribut `@Auth(role = "admin")` pour gérer des rôles
- [ ] Nettoyer la map des `MapSession` avec un `HttpSessionListener` (`sessionDestroyed`), pour éviter une fuite mémoire
- [ ] Permettre `@Auth` sur la **classe** entière, pas seulement sur une méthode
