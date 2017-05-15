package usecases.prescribetreatment;

import usecases.UseCaseRequest;

public class PrescribeTreatmentRequest extends UseCaseRequest<PrescribeTreatmentRequest> {

    public PrescribeTreatmentRequest addStartDate(String value) {
        return buildParameterAndReturnSelf("startDate", value);
    }
    public PrescribeTreatmentRequest addPeriodAmount(String value) {
        return buildParameterAndReturnSelf("periodAmount", value);
    }
    public PrescribeTreatmentRequest addPeriodUnit(String value) {
        return buildParameterAndReturnSelf("periodUnit", value);
    }
    public PrescribeTreatmentRequest addDrugId(String value) {
        return buildParameterAndReturnSelf("drugId", value);
    }
    public PrescribeTreatmentRequest addDosageId(String value) {
        return buildParameterAndReturnSelf("dosageId", value);
    }

    String startDate() {
        return getParameter("startDate");
    }
    String periodAmount() {
        return getParameter("periodAmount");
    }
    String periodUnit() {
        return getParameter("periodUnit");
    }
    String drugId() {
        return getParameter("drugId");
    }
    String dosageId() {
        return getParameter("dosageId");
    }

    @Override
    protected PrescribeTreatmentRequest self() {
        return this;
    }
}
