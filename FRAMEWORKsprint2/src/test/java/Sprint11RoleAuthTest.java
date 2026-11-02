import controller.AuthController;
import mg.itu.myframework.annotation.Auth;
import mg.itu.myframework.model.MapSession;
import mg.itu.myframework.util.AuthUtil;

import java.lang.reflect.Method;

public class Sprint11RoleAuthTest {
    public static void main(String[] args) throws Exception {
        AuthController controller = new AuthController();
        MapSession session = new MapSession();

        String loginResult = controller.login("user", "user", session);
        assertEquals("Connexion utilisateur reussie", loginResult, "login utilisateur");
        assertEquals("user", session.getAttribute("user"), "cle utilisateur en session");
        assertEquals("user", session.getAttribute("role"), "role en session");

        Method profile = AuthController.class.getMethod("profile");
        Method userArea = AuthController.class.getMethod("userArea");
        Method adminArea = AuthController.class.getMethod("adminArea");
        Method managementArea = AuthController.class.getMethod("managementArea");
        assertAuthorized(profile, session, "profil utilisateur");
        assertAuthorized(userArea, session, "espace utilisateur");
        assertForbidden(adminArea, session, "espace administrateur pour USER");
        assertForbidden(managementArea, session, "espace management pour USER");

        controller.logout(session);
        assertForbidden(profile, session, "profil apres logout");

        session = new MapSession();
        controller.login("admin", "admin", session);
        assertAuthorized(adminArea, session, "espace administrateur pour ADMIN");
        assertAuthorized(managementArea, session, "espace management pour ADMIN");
        assertForbidden(userArea, session, "espace utilisateur pour ADMIN");

        session = new MapSession();
        controller.login("manager", "manager", session);
        assertForbidden(adminArea, session, "espace administrateur pour MANAGER");
        assertAuthorized(managementArea, session, "espace management pour MANAGER");

        String invalidLogin = controller.login("unknown", "wrong", new MapSession());
        assertEquals("Identifiants invalides", invalidLogin, "login invalide");
        assertEquals("admin, manager", AuthUtil.getRolesFromAuthAnnotation("admin, manager").get(0)
            + ", " + AuthUtil.getRolesFromAuthAnnotation("admin, manager").get(1), "roles avec espaces");

        System.out.println("Sprint 11 role authentication tests: OK");
    }

    private static void assertAuthorized(Method method, MapSession session, String label) {
        Auth auth = method.getAnnotation(Auth.class);
        if (auth == null || session.getAttribute("user") == null
                || !AuthUtil.verifyRoleAccess(session.getAttribute("role"), auth.role())) {
            throw new AssertionError(label + " doit etre autorise");
        }
    }

    private static void assertForbidden(Method method, MapSession session, String label) {
        Auth auth = method.getAnnotation(Auth.class);
        boolean authenticated = session.getAttribute("user") != null;
        boolean hasAccess = authenticated && AuthUtil.verifyRoleAccess(session.getAttribute("role"), auth.role());
        if (auth == null || hasAccess) {
            throw new AssertionError(label + " doit etre refuse");
        }
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " attendu=" + expected + ", obtenu=" + actual);
        }
    }
}
