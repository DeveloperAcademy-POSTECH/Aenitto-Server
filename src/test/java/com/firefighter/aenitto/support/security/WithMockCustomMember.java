package com.firefighter.aenitto.support.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import javax.management.relation.Role;
import java.lang.annotation.*;
import java.util.UUID;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockCustomMemberSecurityContextFactory.class)
public @interface WithMockCustomMember {
    String name() default "userName";
}
