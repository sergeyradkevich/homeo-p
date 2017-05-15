package usecases;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;

public abstract class BaseUseCaseValidator implements UseCaseValidator {
    private Map<String, String> attributes = new HashMap<>();
    private Map<String, Consumer<String>> rules = new HashMap<>();
    private List<String> errors = new ArrayList<>();
    private UseCaseRequest request;

    public BaseUseCaseValidator() {
        fillRequiredAttributes();
        initializeRules();
    }

    @Override
    public void validate(UseCaseRequest request) {
        this.request = request;
        errors.clear();
        rules.forEach((attribute, rule) -> rule.accept(attribute));
    }

    @Override
    public boolean isValid() {
        return errors.isEmpty();
    }

    // todo: List of attribute + its errors
    @Override
    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }

    protected abstract void fillRequiredAttributes();
    protected abstract void initializeRules();

    protected void addAttribute(String attribute, String attributeName) {
        attributes.put(attribute, attributeName);
    }

    protected void addRule(String attribute, Consumer<String> rule) {
        rules.put(attribute, rule);
    }

    protected void requireNonEmpty(String attribute) {
        String attrValue = request.getParameter(attribute);
        if (Objects.isNull(attrValue) || attrValue.isEmpty()) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' must be present", attrName);
        }
    }

    protected void checkDateFormat(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            LocalDate.parse(attrValue, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' is malformed: '%s'. Accepted format is 'yyyy-MM-dd'", attrName, attrValue);
        }
    }

    protected void checkIntegerFormat(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            Integer.parseInt(attrValue);
        } catch (NumberFormatException e) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' is malformed: '%s'", attrName, attrValue);
        }
    }

    protected void requirePositiveNumber(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            long result = Long.parseLong(attrValue);
            if (result < 0) {
                String attrName = attributes.get(attribute);
                failCheck("'%s' must be a positive value", attrName);
            }
        } catch (NumberFormatException e) {
            skipCheck();
        }
    }

    protected void requireNonZero(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            long result = Long.parseLong(attrValue);
            if (result == 0) {
                String attrName = attributes.get(attribute);
                failCheck("'%s' must be greater than zero", attrName);
            }
        } catch (NumberFormatException e) {
            skipCheck();
        }
    }

    private void failCheck(String message, Object... args) {
        errors.add(String.format(message, args));
    }

    private void skipCheck() {}
}
