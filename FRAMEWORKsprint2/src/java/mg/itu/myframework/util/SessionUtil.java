package mg.itu.myframework.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.itu.myframework.model.MapSession;

/**
 * Stocke un MapSession comme attribut de HttpSession sous une clé interne.
 * Le même objet est retourné pour toutes les requêtes de la même session HTTP,
 * ce qui garantit l'isolation entre clients et la persistance des attributs
 * sans synchronisation supplémentaire.
 */
public final class SessionUtil {
    private static final String MAP_SESSION_KEY = "mg.itu.myframework.MapSession";

    private SessionUtil() {}

    public static MapSession getMapSessionContext(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        Object stored = httpSession.getAttribute(MAP_SESSION_KEY);
        if (stored instanceof MapSession) {
            return (MapSession) stored;
        }
        MapSession mapSession = new MapSession();
        httpSession.setAttribute(MAP_SESSION_KEY, mapSession);
        return mapSession;
    }
}
