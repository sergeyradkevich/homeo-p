package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValidationRule {
    private final String attribute;
    private boolean isViolated = false;
    private Collection<Function<String, Boolean>> checks = new ArrayList<>();
    private Predicate precondition;
    private Collection<ValidationRule> subrules = new ArrayList<>();

    public ValidationRule(String attribute) {
        this.attribute = attribute;
    }

    public static ValidationRule of(String attribute) {
        return new ValidationRule(attribute);
    }

    public ValidationRule check(Function<String, Boolean> check) {
        checks.add(check);
        return this;
    }

    public ValidationRule precondition(Predicate<Object> condition) {
        this.precondition = condition;
        return this;
    }

    public ValidationRule subrule(ValidationRule subrule) {
        subrules.add(subrule);
        return this;
    }

    public void validate() {
        if (Objects.nonNull(precondition)) {
            if (!precondition.test(attribute)) return;
        }

        checks.forEach(check -> {
            if (!check.apply(attribute))
                violate();
        });

        if (nonViolated())
            subrules.forEach(rule -> rule.validate());
    }

    private boolean nonViolated() {
        return !isViolated;
    }

    private void violate() {
        isViolated = true;
    }

}
