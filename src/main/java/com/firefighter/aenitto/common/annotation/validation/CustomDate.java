package com.firefighter.aenitto.common.annotation.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = CustomDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomDate {
    String message() default "";
}
