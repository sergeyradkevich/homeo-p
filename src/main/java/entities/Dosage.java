package entities;

public class Dosage extends Entity {
    private Dose dose;
    private int dailyIntakeAmount;
    private String treatmentId;

    public Dose getDose() {
        return dose;
    }

    public void setDose(Dose dose) {
        this.dose = dose;
    }

    public int getDailyIntakeAmount() {
        return dailyIntakeAmount;
    }

    public void setDailyIntakeAmount(int amount) {
        this.dailyIntakeAmount = amount;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', intakes='%s', dose='%s'}",
                this.getClass().getName(), getId(), getDailyIntakeAmount(), getDose());
    }

    public int totalDailyDose() {
        return dose.getQuantity() * getDailyIntakeAmount();
    }

    public String regimen() {
        return String.format("%d %s %d times a day", dose.getQuantity(), dose.getForm(), getDailyIntakeAmount());
    }
}
