package doubles;

import usecases.UseCaseRequest;
import usecases.UseCaseValidator;

import java.util.ArrayList;
import java.util.List;

public class AlwaysValidUseCaseValidatorStub implements UseCaseValidator {

    @Override
    public void validate(UseCaseRequest request) {}

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public List<String> errors() {
        return new ArrayList<>();
    }
}