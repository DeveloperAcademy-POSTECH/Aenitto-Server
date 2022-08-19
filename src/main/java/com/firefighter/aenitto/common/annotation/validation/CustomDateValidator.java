package com.firefighter.aenitto.common.annotation.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class CustomDateValidator implements ConstraintValidator<CustomDate, String> {
    private static final String REGEX = "^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$";
    private static final String REGEX_ERROR_MESSAGE = "\"yyyy.mm.dd\" 형식으로 작성해주세요.\n현재 입력 값: {value}";
    private static final String YEAR_ERROR_MESSAGE = "년도는 2000 ~ {currentYear} 사이의 값을 입력해주세요. \n현재 입력 값: {value}";
    private static final String INVALID_YMD_ERROR_MESSAGE = "존재하지 않는 연/월/일 값입니다. \n현재 입력 값: {value}";


    @Override
    public void initialize(CustomDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) { return true; }

        HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
        hibernateContext.disableDefaultConstraintViolation();

        if (!Pattern.matches(REGEX, value)) {
            hibernateContext
                    .addMessageParameter("value", value)
                    .buildConstraintViolationWithTemplate(REGEX_ERROR_MESSAGE)
                    .addConstraintViolation();
            return false;
        }
        String[] split = value.split("\\.");
        if (split.length != 3) {
            hibernateContext
                    .addMessageParameter("value", value)
                    .buildConstraintViolationWithTemplate(REGEX_ERROR_MESSAGE)
                    .addConstraintViolation();
            return false;
        }
        final int year = Integer.valueOf(split[0]);
        final int month = Integer.valueOf(split[1]);
        final int date = Integer.valueOf(split[2]);

        try {
            LocalDate.of(year, month, date);
        } catch (DateTimeException e) {
            hibernateContext
                    .addMessageParameter("value", value)
                    .buildConstraintViolationWithTemplate(INVALID_YMD_ERROR_MESSAGE)
                    .addConstraintViolation();
            return false;
        }

        if (year < 2000 || year > LocalDate.now().getYear() + 1) {
            hibernateContext
                    .addMessageParameter("currentYear", LocalDate.now().getYear() + 1)
                    .addMessageParameter("value", value)
                    .buildConstraintViolationWithTemplate(YEAR_ERROR_MESSAGE)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
