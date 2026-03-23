package com.example.StarterPack.utils.validation;

import java.lang.reflect.Field;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {
    private String passwordFieldName;
    private String passwordMatchFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        System.out.println("-----------------PASSWORDMATCHVALIDATOR---------------------");
        passwordFieldName = constraintAnnotation.passwordField();// ở annotaton khai báo là gì thì sẽ nhân giá trị đó, 
                // ví dụ ở createUserRequest passwordField là password1 thì passwordFieldName là password1, 
                // nói chung là nhận tên field muốn được so sánh
        System.out.println("constraintAnnotation.passwordField(): "+ passwordFieldName);
                passwordMatchFieldName = constraintAnnotation.passwordConfirmationField();// tương tự
        System.out.println("constraintAnnotation.passwordConfirmationField(): "+ passwordMatchFieldName);
    }
    @Override
    public boolean isValid(Object arg0, ConstraintValidatorContext arg1) {
        // arg0: createUserRequest request
        try {
            System.out.println("-------------------------");
            Class<?> clazz = arg0.getClass();
            // passwordMatchFieldName,passwordFieldName là tên 2 field cần so sánh
            Field passwordField = clazz.getDeclaredField(passwordFieldName);// request.password
            Field passwordMatchField = clazz.getDeclaredField(passwordMatchFieldName);// request.confirmPassword
            System.out.println("clazz.getDeclaredField(passwordFieldName): "+ clazz.getDeclaredField(passwordFieldName));
            System.out.println("clazz.getDeclaredField(passwordMatchFieldName): "+ clazz.getDeclaredField(passwordFieldName));
            passwordField.setAccessible(true);
            passwordMatchField.setAccessible(true);

            // lấy giá trị
            String password = (String) passwordField.get(arg0);
            String passwordMatch = (String) passwordMatchField.get(arg0);
            System.out.println("password: "+password);
            System.out.println("password: "+password);
            // so sánh
            return password != null && password.equals(passwordMatch);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            return false;
        }   
    }
    
}
