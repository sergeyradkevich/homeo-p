package entities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DosageTest {

    private Dose dose;
    private Dosage dosage;

    @Before
    public void setUp() {
        dose = new Dose();
        dose.setQuantity(3);
        dose.setForm("pill");

        dosage = new Dosage();
        dosage.setDose(dose);
        dosage.setDailyIntakeAmount(2);
    }

    @Test
    public void dosageHasDoseAndFrequency() {
        assertEquals(3, dosage.getDose().getQuantity());
        assertEquals(2, dosage.getDailyIntakeAmount());
    }

    @Test
    public void totalDailyDose() {
        assertEquals(6, dosage.totalDailyDose());
    }

    @Test
    public void regimenText() {
        assertEquals("3 pill 2 times a day", dosage.regimen());
    }

    // todo: DosageForm entity instead of a text in Dose 
    // todo: Singular to plural dosage form
    // https://github.com/atteo/evo-inflector
}