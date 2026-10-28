package util;

import model.annotation.Audit;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class AuditLogger {

    // Call this at the start of any service method to log it via reflection
    public static void log(Object service, String methodName) {
        try {
            for (Method method : service.getClass().getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(Audit.class)) {
                    Audit audit = method.getAnnotation(Audit.class);
                    String action = audit.value().isEmpty() ? methodName : audit.value();
                    System.out.println("[AUDIT] " + LocalDateTime.now() + " | Action: " + action);
                }
            }
        } catch (Exception e) {
            System.out.println("[AUDIT] Could not log: " + e.getMessage());
        }
    }
}
