package entities;

import values.TreatmentPeriod;

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

    public Drug getDrug() {
        return drug;
    }

    public void setDosage(Dosage dosage) {
        this.dosage = dosage;
    }

    public Dosage getDosage() {
        return dosage;
    }

    public void setStartsOn(LocalDate startsOn) {
        this.startsOn = startsOn;
    }

    public LocalDate getStartsOn() {
        return startsOn;
    }

    public void setPeriod(TreatmentPeriod period) {
        this.period = period;
    }

    public TreatmentPeriod getPeriod() {
        return period;
    }

    public void setStopsOn(LocalDate stopsOn) {
        this.stopsOn = stopsOn;
    }

    public LocalDate getStopsOn() {
        return stopsOn;
    }

    public boolean doesOverlap(Treatment other) {
        if (!getDrug().equals(other.getDrug())) return false;
        if (getStartsOn().isAfter(other.getStopsOn()) || getStopsOn().isBefore(other.getStartsOn())) return false;
        return true;
    }
}
