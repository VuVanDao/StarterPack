package com.example.StarterPack.utils.validation;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueValidator.class) // Khi dùng → sẽ gọi UniqueValidator để kiểm tra
@Target({ElementType.FIELD}) // Áp dụng cho FIELD (từng thuộc tính) 
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface Unique {
    String message() default "Field must be unique";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String columnName();
    String tableName();
}
