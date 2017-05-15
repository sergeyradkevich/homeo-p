package usecases;

import java.util.List;

public interface UseCaseValidator {
    void validate(UseCaseRequest request);
    boolean isValid();
    List<String> errors();
}
