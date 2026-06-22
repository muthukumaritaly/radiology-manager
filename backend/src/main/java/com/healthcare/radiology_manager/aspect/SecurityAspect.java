package com.healthcare.radiology_manager.aspect;

import com.healthcare.radiology_manager.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class SecurityAspect {

    private static final String ROLE_HEADER = "X-User-Role";

    @Before("@within(com.healthcare.radiology_manager.aspect.RequireRole) || @annotation(com.healthcare.radiology_manager.aspect.RequireRole)")
    public void checkRole(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = method.getDeclaringClass().getAnnotation(RequireRole.class);
        }

        if (requireRole != null) {
            String requiredRole = requireRole.value();
            String roleHeader = request.getHeader(ROLE_HEADER);
            String userRole = (roleHeader != null) ? roleHeader.trim() : "";

            if (userRole.isEmpty() || !requiredRole.equalsIgnoreCase(userRole)) {
                throw new AccessDeniedException(
                    "Access Denied: Only " + requiredRole + " role can access this endpoint. " +
                    "Please provide HTTP header '" + ROLE_HEADER + ": " + requiredRole + "'."
                );
            }
        }
    }
}
