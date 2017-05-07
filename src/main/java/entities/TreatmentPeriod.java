package entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TreatmentPeriod {
    private ChronoUnit unit;
    private int amount;

    public TreatmentPeriod() {}

    public TreatmentPeriod(int amount, ChronoUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public void setUnit(ChronoUnit unit) {
        this.unit = unit;
    }

    public ChronoUnit getUnit() {
        return unit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDate calcEnd(LocalDate startsOn) {
        return startsOn.plus(getAmount(), getUnit()).minusDays(1);
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) return false;

        TreatmentPeriod other = (TreatmentPeriod) obj;
        return Objects.equals(this.getAmount(), other.getAmount()) &&
                Objects.equals(this.getUnit(), other.getUnit());
    }

    @Override
    public String toString() {
        return String.format("{amount='%d', unit='%s'}", getAmount(), getUnit().name());
    }
}
