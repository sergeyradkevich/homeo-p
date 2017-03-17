package entities;

import java.time.LocalDate;

public class Treatment extends Entity {
    private Drug drug;
    private Dosage dosage;
    private LocalDate startsOn;
    private TreatmentPeriod period;
    private LocalDate stopsOn;

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public void setDosage(Dosage dosage) {
        this.dosage = dosage;
    }

    public void setStartsOn(LocalDate startsOn) {
        this.startsOn = startsOn;
    }

    public void setPeriod(TreatmentPeriod period) {
        this.period = period;
    }

    public void setStopsOn(LocalDate stopsOn) {
        this.stopsOn = stopsOn;
    }

    public LocalDate getStopsOn() {
        return stopsOn;
    }
}
