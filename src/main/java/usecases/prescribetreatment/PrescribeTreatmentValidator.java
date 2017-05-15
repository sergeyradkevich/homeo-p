package usecases.prescribetreatment;

import usecases.BaseUseCaseValidator;
import usecases.ValidationRule;

import java.util.function.Consumer;

class PrescribeTreatmentValidator extends BaseUseCaseValidator {

    @Override
    protected void fillRequiredAttributes() {
        addAttribute("startDate", "Start Date");
        addAttribute("periodAmount", "Amount of Treatment Period");
        addAttribute("periodUnit", "Unit of Treatment Period");
        addAttribute("drugId", "Drug Id");
        addAttribute("dosageId", "Dosage Id");
    }

    @Override
    protected void initializeRules() {
        Consumer<String> starDateRule = new ValidationRule<String>()
                .add(this::requireNonEmpty)
                .add(this::checkDateFormat);

        Consumer<String> periodAmountRule = new ValidationRule<String>()
                .add(this::requireNonEmpty)
                .add(this::checkIntegerFormat)
                .add(this::requirePositiveNumber)
                .add(this::requireNonZero);

        addRule("startDate", starDateRule);
        addRule("periodAmount", periodAmountRule);
        addRule("periodUnit", this::requireNonEmpty);
        addRule("drugId", this::requireNonEmpty);
        addRule("dosageId", this::requireNonEmpty);
    }
}
