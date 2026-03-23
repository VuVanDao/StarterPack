package com.example.StarterPack.utils.validation;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueValidator implements ConstraintValidator<Unique, String>{
    private final JdbcClient jdbcClient;
    private String tableName;
    private String columnName;

    public UniqueValidator(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void initialize(Unique constraintAnnotation) {
        System.out.println("-------------------------UNIQUE---------------------");
        tableName = constraintAnnotation.tableName();
        // ở annotaton khai báo là gì thì sẽ nhân giá trị đó, 
        // ví dụ ở createUserRequest email là email1 thì columnName là email1, 
        // nói chung là nhận tên field muốn được check unique
        columnName = constraintAnnotation.columnName();
        System.out.println("constraintAnnotation.tableName(): "+ constraintAnnotation.tableName());
        System.out.println("constraintAnnotation.columnName(): "+ constraintAnnotation.columnName());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        // value = request.getEmail()
        return jdbcClient.sql("SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?")
            .param(value)
            .query(Integer.class)
                .single() == 0;
        // 0 → chưa tồn tại → VALID ✅
        // >0 → đã tồn tại → INVALID ❌
    }
}
