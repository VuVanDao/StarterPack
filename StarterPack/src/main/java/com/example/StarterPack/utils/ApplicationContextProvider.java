package com.example.StarterPack.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    // Lưu ApplicationContext dưới dạng static để có thể truy cập toàn cục
    private static ApplicationContext context;

    // Lấy bean theo kiểu class (dùng khi không thể @Autowired, ví dụ static context)
    public static <T> T bean(Class<T> beanType) {
        return context.getBean(beanType);
    }

    // Lấy bean theo tên
    public static Object bean(String name) {
        return context.getBean(name);
    }

    // Spring sẽ tự động gọi method này khi khởi tạo context
    // -> dùng để gán ApplicationContext vào biến static
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    
}
