package repository;

import java.util.*;

public abstract class Repository<T> {
    protected Map<String, T> items = new HashMap<>();

    public T save(T item, String id) {
        items.put(id, item);
        return item;
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<T> findAllById(List<String> ids) {
        List<T> result = new ArrayList<>();
        for (String id : ids) {
            T item = items.get(id);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    public void delete(String id) {
        items.remove(id);
    }

    public void deleteAll() {
        items.clear();
    }

    public long count() {
        return items.size();
    }

    public boolean exists(String id) {
        return items.containsKey(id);
    }
}