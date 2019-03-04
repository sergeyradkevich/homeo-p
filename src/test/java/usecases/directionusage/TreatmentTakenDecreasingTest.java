package usecases.directionusage;

import org.junit.Test;

public class TreatmentTakenDecreasingTest {

    @Test
    public void stub() {

    }
}


/*

    Vocara:
    9 drops, 7 times a day
    decreasing 1 intake daily until 2 times.


    DecreaseMode
    delta
    limit

    The source amount for subtraction a delta is Dosage.dailyIntakeAmount (int)
    Criteria for delta:
    - 1 by default
    - cannot be greater than input dailyIntakeAmount
    - interface to increase delta instead of passing/setting concrete value?

    Criteria for limit:
    - 1 by default
    - cannot be less than 1 and greater than dailyIntakeAmount
    - is there any schema to reach the limit by decreasing dailyIntakeAmount by delta, like
    7 minus 3 => 4, but limit is 3. Does it mean that the next decrease by 3 should yield 3 in result?

    General criteria:
    - Can be not just “-1” (or “-2”) decreasing, but some schema like 9, 7, 5, 3
    decreasing mode may switch to periodical
    - decreasing mode may switch to periodical, for example "Then 9 drops, 2 times a day 3 days is taken, then a pause for 3 days"
*/