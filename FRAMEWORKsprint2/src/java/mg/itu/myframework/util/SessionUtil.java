package mg.itu.myframework.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.itu.myframework.model.MapSession;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionUtil {
    private static final String MAP_SESSIONS_ATTRIBUTE = SessionUtil.class.getName() + ".mapSessions";

    private SessionUtil() {
    }

    public static MapSession getMapSessionContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return getMapSessions(request.getServletContext()).get(session.getId());
    }

    public static void setMapSessionContext(HttpServletRequest request, MapSession mapSession) {
        HttpSession session = request.getSession();
        getMapSessions(request.getServletContext()).put(session.getId(), mapSession);
    }

    public static MapSession setMapSession(HttpServletRequest request, MapSession mapSession) {
        HttpSession session = request.getSession();
        if (mapSession == null) {
            mapSession = new MapSession();
        }
        Enumeration<String> names = session.getAttributeNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            mapSession.setAttribute(name, session.getAttribute(name));
        }
        setMapSessionContext(request, mapSession);
        return mapSession;
    }

    public static void rectifySession(HttpServletRequest request, MapSession mapSession) {
        if (mapSession == null) {
            return;
        }
        HttpSession session = request.getSession();
        Enumeration<String> names = session.getAttributeNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            if (!mapSession.getAttributes().containsKey(name)) {
                session.removeAttribute(name);
            }
        }
        for (Map.Entry<String, Object> entry : mapSession.getAttributes().entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
        setMapSessionContext(request, mapSession);
    }

    public static boolean verifyMapSessionObject(HttpServletRequest request, Object[] args) {
        if (args == null) {
            return false;
        }
        for (Object argument : args) {
            if (argument instanceof MapSession) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, MapSession> getMapSessions(ServletContext context) {
        Map<String, MapSession> sessions = (Map<String, MapSession>) context.getAttribute(MAP_SESSIONS_ATTRIBUTE);
        if (sessions == null) {
            synchronized (context) {
                sessions = (Map<String, MapSession>) context.getAttribute(MAP_SESSIONS_ATTRIBUTE);
                if (sessions == null) {
                    sessions = new ConcurrentHashMap<>();
                    context.setAttribute(MAP_SESSIONS_ATTRIBUTE, sessions);
                }
            }
        }
        return sessions;
    }
}
