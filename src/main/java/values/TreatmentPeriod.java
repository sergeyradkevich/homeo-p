package values;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TreatmentPeriod {
    private ChronoUnit unit;
    private int amount;

    public TreatmentPeriod(int amount, ChronoUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public LocalDate calcEnd(LocalDate startsOn) {
        return startsOn.plus(amount, unit).minusDays(1);
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) return false;

        TreatmentPeriod other = (TreatmentPeriod) obj;
        return Objects.equals(this.amount, other.amount) &&
                Objects.equals(this.unit, other.unit);
    }

    @Override
    public String toString() {
        return String.format("{amount='%d', unit='%s'}", amount, unit.name());
    }
}
