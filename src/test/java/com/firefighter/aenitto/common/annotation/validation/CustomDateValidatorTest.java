package com.firefighter.aenitto.common.annotation.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomDateValidatorTest {
    private CustomDateValidator validator;
    private ConstraintValidatorContext context;
    private HibernateConstraintValidatorContext hibernateContext;

    @BeforeEach
    void init() {
        validator = new CustomDateValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
        hibernateContext = mock(HibernateConstraintValidatorContext.class);

        when(context.unwrap(any()))
                .thenReturn(hibernateContext);
        doNothing().when(hibernateContext).disableDefaultConstraintViolation();
        when(hibernateContext.addMessageParameter(anyString(), any()))
                .thenReturn(hibernateContext);
        when(hibernateContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mock(HibernateConstraintViolationBuilder.class));
    }

    @DisplayName("Regex String 테스트 - 성공")
    @Test
    void regexStringTest() throws NoSuchFieldException, IllegalAccessException {
        // given
        Class<CustomDateValidator> clazz = CustomDateValidator.class;
        Field regex = clazz.getDeclaredField("REGEX");
        regex.setAccessible(true);
        String regexString = (String) regex.get(clazz);

        final String ymd1 = "2022.01.23";
        final String ymd2 = "202.11.23";
        final String ymd3 = "2022.11.22";
        final String ymd4 = "2022.111.12";

        // when, then
        assertThat(Pattern.matches(regexString, ymd1)).isTrue();
        assertThat(Pattern.matches(regexString, ymd2)).isFalse();
        assertThat(Pattern.matches(regexString, ymd3)).isTrue();
        assertThat(Pattern.matches(regexString, ymd4)).isFalse();
    }

    @DisplayName("Valid 테스트 - 실패 (regex 형식 틀림)")
    @Test
    void valid_fail_regex() {
        // given
        final String value = "1234-00-00";

        // when
        boolean valid = validator.isValid(value, context);

        // then
        assertThat(valid).isFalse();
    }

    @DisplayName("Valid 테스트 - 실패 (Year 범위 이탈)")
    @Test
    void valid_fail_year() {
        // given
        final String value = "2025.1.1";

        // when
        boolean valid = validator.isValid(value, context);

        // then
        assertThat(valid).isFalse();
    }

    @DisplayName("Valid 테스트 - 실패 (Month 범위 이탈)")
    @Test
    void valid_fail_month() {
        // given
        final String value = "2021.21.1";

        // when
        boolean valid = validator.isValid(value, context);

        // then
        assertThat(valid).isFalse();
    }

    @DisplayName("Valid 테스트 - 실패 (Date 범위 이탈)")
    @Test
    void valid_fail_date() {
        // given
        final String value = "2021.1.50";

        // when
        boolean valid = validator.isValid(value, context);

        // then
        assertThat(valid).isFalse();
    }

    @DisplayName("Valid 테스트 - 실패 (존재하지 않는 일자)")
    @Test
    void valid_fail_illegal_ymd() {
        // given
        final String value = "2021.2.30";

        // when
        boolean valid = validator.isValid(value, context);

        // then
        assertThat(valid).isFalse();
    }
}
