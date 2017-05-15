package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ValidationRule<T> implements Consumer<T> {
    private Collection<Consumer<T>> checks = new ArrayList<>();

    public ValidationRule<T> add(Consumer<T> check) {
        checks.add(check);
        return this;
    }

    @Override
    public void accept(T t) {
        checks.forEach(check -> check.accept(t));
    }
}
