package usecases.prescribetreatment;

public class PrescribeTreatmentException extends RuntimeException {

    public PrescribeTreatmentException() {
        super();
    }

    public PrescribeTreatmentException(String message) {
        super(message);
    }
}
