import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.itu.myframework.annotation.Auth;
import mg.itu.myframework.model.MapSession;
import mg.itu.myframework.util.SessionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class Sprint10AuthTest {
    public static void main(String[] args) throws Exception {
        testAuthRetention();
        testSessionSynchronizationAndIsolation();
        System.out.println("Sprint 10 authentication tests: OK");
    }

    private static void testAuthRetention() throws Exception {
        Method method = ProtectedController.class.getDeclaredMethod("protectedPage");
        if (!method.isAnnotationPresent(Auth.class)) {
            throw new AssertionError("@Auth doit etre visible a l'execution");
        }
    }

    private static void testSessionSynchronizationAndIsolation() {
        Map<String, Object> contextAttributes = new HashMap<>();
        ServletContext context = proxy(ServletContext.class, (proxy, method, args) -> {
            if (method.getName().equals("getAttribute")) return contextAttributes.get(args[0]);
            if (method.getName().equals("setAttribute")) {
                contextAttributes.put((String) args[0], args[1]);
                return null;
            }
            return defaultValue(method.getReturnType());
        });

        SessionState firstState = new SessionState("first");
        SessionState secondState = new SessionState("second");
        firstState.attributes.put("user", "first-user");
        HttpServletRequest firstRequest = request(context, firstState);
        HttpServletRequest secondRequest = request(context, secondState);

        MapSession first = SessionUtil.setMapSession(firstRequest, null);
        MapSession second = SessionUtil.setMapSession(secondRequest, null);
        assertEquals("first-user", first.get("user"), "copie de la session");
        assertNull(second.get("user"), "isolation des sessions");

        first.add("role", "admin");
        first.remove("user");
        SessionUtil.rectifySession(firstRequest, first);
        assertNull(firstState.attributes.get("user"), "suppression dans HttpSession");
        assertEquals("admin", firstState.attributes.get("role"), "ajout dans HttpSession");
        assertNull(secondState.attributes.get("role"), "absence de partage entre sessions");
    }

    private static HttpServletRequest request(ServletContext context, SessionState state) {
        HttpSession session = proxy(HttpSession.class, (proxy, method, args) -> {
            if (method.getName().equals("getId")) return state.id;
            if (method.getName().equals("getAttributeNames")) {
                return java.util.Collections.enumeration(new java.util.ArrayList<>(state.attributes.keySet()));
            }
            if (method.getName().equals("getAttribute")) return state.attributes.get(args[0]);
            if (method.getName().equals("setAttribute")) {
                state.attributes.put((String) args[0], args[1]);
                return null;
            }
            if (method.getName().equals("removeAttribute")) {
                state.attributes.remove(args[0]);
                return null;
            }
            return defaultValue(method.getReturnType());
        });
        return proxy(HttpServletRequest.class, (proxy, method, args) -> {
            if (method.getName().equals("getSession")) return session;
            if (method.getName().equals("getServletContext")) return context;
            return defaultValue(method.getReturnType());
        });
    }

    private static class SessionState {
        private final String id;
        private final Map<String, Object> attributes = new HashMap<>();

        private SessionState(String id) {
            this.id = id;
        }
    }

    private static class ProtectedController {
        @Auth
        public void protectedPage() {
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(label + " attendu=" + expected + ", obtenu=" + actual);
        }
    }

    private static void assertNull(Object value, String label) {
        if (value != null) {
            throw new AssertionError(label + " doit etre null, obtenu=" + value);
        }
    }
}