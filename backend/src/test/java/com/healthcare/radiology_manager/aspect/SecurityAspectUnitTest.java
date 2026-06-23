package com.healthcare.radiology_manager.aspect;

import com.healthcare.radiology_manager.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAspectUnitTest {

    private SecurityAspect securityAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        securityAspect = new SecurityAspect();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("SecurityAspect: RequestAttributes is null - returns immediately")
    void checkRole_requestAttributesNull_returns() {
        RequestContextHolder.setRequestAttributes(null);
        assertThatCode(() -> securityAspect.checkRole(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("SecurityAspect: Annotation on method - Access Granted")
    void checkRole_annotationOnMethod_accessGranted() throws Exception {
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        Method method = MockClass.class.getMethod("adminMethod");
        when(methodSignature.getMethod()).thenReturn(method);
        when(httpServletRequest.getHeader("X-User-Role")).thenReturn("ADMIN");

        assertThatCode(() -> securityAspect.checkRole(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("SecurityAspect: Annotation on method - Access Denied on role mismatch")
    void checkRole_annotationOnMethod_accessDenied_roleMismatch() throws Exception {
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        Method method = MockClass.class.getMethod("adminMethod");
        when(methodSignature.getMethod()).thenReturn(method);
        when(httpServletRequest.getHeader("X-User-Role")).thenReturn("USER");

        assertThatThrownBy(() -> securityAspect.checkRole(joinPoint))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access Denied: Only ADMIN role can access this endpoint");
    }

    @Test
    @DisplayName("SecurityAspect: Annotation on method - Access Denied on missing header")
    void checkRole_annotationOnMethod_accessDenied_missingHeader() throws Exception {
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        Method method = MockClass.class.getMethod("adminMethod");
        when(methodSignature.getMethod()).thenReturn(method);
        when(httpServletRequest.getHeader("X-User-Role")).thenReturn(null);

        assertThatThrownBy(() -> securityAspect.checkRole(joinPoint))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access Denied: Only ADMIN role can access this endpoint");
    }

    @Test
    @DisplayName("SecurityAspect: Annotation on class - Access Granted")
    void checkRole_annotationOnClass_accessGranted() throws Exception {
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        Method method = MockAdminClass.class.getMethod("someMethod");
        when(methodSignature.getMethod()).thenReturn(method);
        when(httpServletRequest.getHeader("X-User-Role")).thenReturn("ADMIN");

        assertThatCode(() -> securityAspect.checkRole(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("SecurityAspect: No Annotation - Access Granted")
    void checkRole_noAnnotation_accessGranted() throws Exception {
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        Method method = MockClass.class.getMethod("noAnnotationMethod");
        when(methodSignature.getMethod()).thenReturn(method);

        assertThatCode(() -> securityAspect.checkRole(joinPoint)).doesNotThrowAnyException();
    }

    static class MockClass {
        @RequireRole("ADMIN")
        public void adminMethod() {}

        public void noAnnotationMethod() {}
    }

    @RequireRole("ADMIN")
    static class MockAdminClass {
        public void someMethod() {}
    }
}
