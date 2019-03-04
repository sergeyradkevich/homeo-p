package usecases;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class UseCaseRequest <T extends UseCaseRequest> {
    private Map<String, String> parameters = new HashMap<>();

    public String getParameter(String attribute) {
        return parameters.get(attribute);
    }

    public boolean isParameterMissing(String attribute) {
        return Objects.isNull(getParameter(attribute));
    }

    protected T buildParameterAndReturnSelf(String parameter, String value) {
        setParameter(parameter, value);
        return self();
    }

    abstract protected T self();

    protected void setParameter(String parameter, String value) {
        parameters.put(parameter, value);
    }
}
