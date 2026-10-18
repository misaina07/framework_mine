import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.itu.myframework.model.MapFILE;
import mg.itu.myframework.util.ParameterBinder;
import model.Employe;

public class Sprint8MultipartTest {
    private static final byte[] FILE_BYTES = "contenu-cin".getBytes(StandardCharsets.UTF_8);

    public static void main(String[] args) throws Exception {
        HttpServletRequest request = multipartRequest();

        MapFILE extracted = ParameterBinder.extractMapFile(request, "rectoCIN");
        assertEquals("cin.png", extracted.getNom_du_fichier(), "nom du fichier");
        assertEquals("image/png", extracted.getType_fichier(), "type MIME");
        assertEquals(FILE_BYTES.length, extracted.getBytes().length, "taille du fichier");

        Method directMethod = TestController.class.getDeclaredMethod("direct", MapFILE.class);
        MapFILE direct = (MapFILE) ParameterBinder.bind(directMethod, request, null)[0];
        assertEquals("cin.png", direct.getNom_du_fichier(), "binding direct");

        Method objectMethod = TestController.class.getDeclaredMethod("object", Employe.class);
        Employe employe = (Employe) ParameterBinder.bind(objectMethod, request, null)[0];
        assertEquals("Rakoto", employe.getNom(), "champ objet nom");
        assertEquals(24, employe.getAge(), "champ objet age");
        assertEquals("rakoto@example.com", employe.getEmail(), "champ objet email");
        assertEquals("cin.png", employe.getRectoCIN().getNom_du_fichier(), "fichier objet");

        HttpServletRequest withoutFile = requestWith(null);
        if (ParameterBinder.extractMapFile(withoutFile, "rectoCIN") != null) {
            throw new AssertionError("Un fichier absent doit produire null avec extractMapFile");
        }

        System.out.println("Sprint 8 multipart tests: OK");
    }

    public static class TestController {
        public void direct(MapFILE file) {
        }

        public void object(Employe employe) {
        }
    }

    private static HttpServletRequest multipartRequest() {
        return requestWith(part());
    }

    private static HttpServletRequest requestWith(Part part) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getName().equals("getContentType")) {
                return "MULTIPART/FORM-DATA; boundary=test";
            }
            if (method.getName().equals("getPart")) {
                return part;
            }
            if (method.getName().equals("getParameter")) {
                String name = (String) args[0];
                return switch (name) {
                    case "nom" -> "Rakoto";
                    case "age" -> "24";
                    case "email" -> "rakoto@example.com";
                    default -> null;
                };
            }
            return defaultValue(method.getReturnType());
        };
        return proxy(HttpServletRequest.class, handler);
    }

    private static Part part() {
        InvocationHandler handler = (proxy, method, args) -> {
            return switch (method.getName()) {
                case "getSize" -> (long) FILE_BYTES.length;
                case "getSubmittedFileName" -> "cin.png";
                case "getContentType" -> "image/png";
                case "getInputStream" -> new ByteArrayInputStream(FILE_BYTES);
                default -> defaultValue(method.getReturnType());
            };
        };
        return proxy(Part.class, handler);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        if (type == char.class) return '\0';
        if (type == InputStream.class) return InputStream.nullInputStream();
        return null;
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " attendu=" + expected + ", obtenu=" + actual);
        }
    }
}