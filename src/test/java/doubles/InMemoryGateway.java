package doubles;

import entities.Entity;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryGateway<T extends Entity> {

    private Set<T> entities = new HashSet<>();

    public T save(T entity) {
        persist(entity);
        return entity;
    }

    public T findById(String id) {
        Optional<T> result = entities.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .map(this::clone);

        return result.isPresent()? result.get() : null;
    }

    public List<T> findAll() {
        return entities.stream()
                .map(this::clone)
                .sorted(Comparator.comparing(T::getId))
                .collect(Collectors.toList());
    }

    private void persist(T entity) {
        entity.setId(UUID.randomUUID().toString());
        T clone = clone(entity);
        entities.add(clone);
    }

    @SuppressWarnings("unchecked")
    private T clone(T entity) {
        try {
            return (T) entity.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnCloneable();
        }
    }
}

class UnCloneable extends RuntimeException {}
