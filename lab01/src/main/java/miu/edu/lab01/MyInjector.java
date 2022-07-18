package miu.edu.lab01;

import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyInjector {
    Map<Class<?>, Object> map = new HashMap<>();
    public MyInjector() {
        Reflections reflections = new Reflections("miu.edu.lab01");
        reflections.getTypesAnnotatedWith(MyBean.class).forEach(aClass -> {
            try {
                Object t = aClass.newInstance();
                Arrays.stream(aClass.getFields()).filter(f -> f.isAnnotationPresent(MyAutowired.class))
                        .toList()
                        .forEach(field -> {
                            Class<?> innerClass = field.getType();
                            try {
                                Object innerInstance = innerClass.newInstance();
                                map.put(innerClass, innerInstance);
                                field.set(t, innerInstance);
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                map.put(aClass, t);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public Object getBean(Class<?> clazz) throws NotFoundBeanException {
        try {
            return map.get(clazz);
        } catch (Exception e) {
            throw new NotFoundBeanException("Error");
        }
    }
}
