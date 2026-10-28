package service;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Repository<T> {
    private HashMap<String, T> store = new HashMap<>();

    private String findId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        return (String) field.get(entity);
                    } catch (IllegalAccessException e) {
                        System.out.println("Could not read @Id field: " + e.getMessage());
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public T save(T entity) {
        String id = findId(entity);
        if (id == null) {
            System.out.println("No @Id field found on entity.");
            return entity;
        }
        store.put(id, entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    public T findById(String id) {
        return store.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean delete(String id) {
        if (store.containsKey(id)) {
            store.remove(id);
            return true;
        }
        return false;
    }

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) return 0;
        double sum = 0;
        for (N val : values) {
            sum += val.doubleValue();
        }
        return sum / values.length;
    }
}
