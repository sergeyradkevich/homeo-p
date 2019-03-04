package usecases.prescribetreatment;

import entities.DirectionMode;
import entities.DirectionModeType;
import usecases.BaseUseCaseValidator;
import usecases.ValidationRule;

import java.util.function.Function;

public class PrescribeTreatmentValidator extends BaseUseCaseValidator {

    @Override
    protected void fillAttributes() {
        addAttribute("startDate", "Start Date");
        addAttribute("periodAmount", "Amount of Treatment Period");
        addAttribute("periodUnit", "Unit of Treatment Period");
        addAttribute("drugId", "Drug Id");
        addAttribute("dosageId", "Dosage Id");
        addAttribute("directionModeType", "Direction Mode Type");
        addAttribute("directionModeTaken", "Amount of Taken for the Periodical Direction");
    }

    @Override
    protected void initializeRules() {
        ValidationRule starDateRule = ValidationRule.of("startDate")
                .check(this::requireNonEmpty)
                .check(this::checkDateFormat);

        ValidationRule periodAmountRule = ValidationRule.of("periodAmount")
                .check(this::requireNonEmpty)
                .check(this::checkIntegerFormat)
                .check(this::requirePositiveNumber)
                .check(this::requireNonZero);

        Function<String, Boolean> directionModeTypeWithinValidRange =
                (attribute) -> assertTruthCondition(attribute, DirectionModeType::isExistent);
        ValidationRule directionModeTypeRule = ValidationRule.of("directionModeType")
                .check(this::requireNonEmpty)
                .check(directionModeTypeWithinValidRange)
                .subrule(ValidationRule.of("directionModeTaken")
                            .precondition(this::isPeriodicalDirectionMode)
                            .check(this::requireNonEmpty)
                            .check(this::checkIntegerFormat)
                            .check(this::requirePositiveNumber)
                            .check(this::requireNonZero))
                .subrule(ValidationRule.of("directionModeInterval")
                            .precondition(this::isPeriodicalDirectionMode)
                            .check(this::requireNonEmpty)
                            .check(this::checkIntegerFormat)
                            .check(this::requirePositiveNumber)
                            .check(this::requireNonZero));

        addRule(starDateRule);
        addRule(periodAmountRule);
        addRule(ValidationRule.of("periodUnit").check(this::requireNonEmpty));
        addRule(ValidationRule.of("drugId").check(this::requireNonEmpty));
        addRule(ValidationRule.of("dosageId").check(this::requireNonEmpty));
        addRule(directionModeTypeRule);
    }

    private boolean isPeriodicalDirectionMode(Object __) {
        String attrValue = request.getParameter("directionModeType");

        DirectionModeType type  = DirectionModeType.valueOf(attrValue);
        DirectionMode mode = new DirectionMode(type);

        if (mode.isDaily()) return false;

        return mode.isPeriodically();
    }

}
