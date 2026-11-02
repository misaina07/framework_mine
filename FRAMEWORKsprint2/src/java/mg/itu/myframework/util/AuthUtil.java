package mg.itu.myframework.util;

import java.util.ArrayList;
import java.util.List;

public final class AuthUtil {
    private AuthUtil() {
    }

    public static List<String> getRolesFromAuthAnnotation(String roleNames) {
        List<String> roles = new ArrayList<>();
        if (roleNames == null || roleNames.trim().isEmpty()) {
            return roles;
        }
        for (String role : roleNames.split(",")) {
            String trimmedRole = role.trim();
            if (!trimmedRole.isEmpty()) {
                roles.add(trimmedRole);
            }
        }
        return roles;
    }

    public static boolean verifyRoleAccess(Object roleAuth, String roleNames) {
        List<String> roles = getRolesFromAuthAnnotation(roleNames);
        if (roles.isEmpty()) {
            return true;
        }
        return roleAuth != null && roles.contains(roleAuth.toString());
    }
}
