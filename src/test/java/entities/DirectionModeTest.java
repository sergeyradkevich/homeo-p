package entities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionModeTest {

    private DirectionMode direction;
    public static final int SEVEN_DAILY_INTAKE_AMOUNT = 7;

    @Before
    public void setUp() {
        direction = new DirectionMode(DirectionModeType.DECREASINGLY);
    }

    /*
    delta 1, limit 2: 5 + 1 = 6
	        1st day - 7 times a day,
            2nd day - 6 times a day,
            and so on until 2 times a day on the 6th day
    */
    @Test
    public void durationOfDecreasingInDaysUntil2AsLimit_And_1AsDelta() {
        direction.setLimit(2);
        direction.setDelta(1);

        assertEquals(6, direction.calcDecreasingDaysAmountUntilLimitInclusive(SEVEN_DAILY_INTAKE_AMOUNT));
    }

    /*
    delta 2, limit 2: 3 + 1 = 4
	        1st day - 7 times a day,
            2nd day - 5 times a day,
            3rd day - 3 times a day,
            4th day - 2 times a day
    */
    @Test
    public void durationOfDecreasingInDaysUntil2AsLimit_And_2AsDelta() {
        direction.setLimit(2);
        direction.setDelta(2);

        assertEquals(4, direction.calcDecreasingDaysAmountUntilLimitInclusive(SEVEN_DAILY_INTAKE_AMOUNT));
    }

    /*
        delta 2, limit 3: 3 + 0 = 3
	        1st day - 7 times a day,
            2nd day - 5 times a day,
            3rd day - 3 times a day,
    */
    @Test
    public void durationOfDecreasingInDaysUntil3AsLimit_And_2AsDelta() {
        direction.setLimit(3);
        direction.setDelta(2);

        assertEquals(3, direction.calcDecreasingDaysAmountUntilLimitInclusive(SEVEN_DAILY_INTAKE_AMOUNT));
    }

    /*
        delta 4, limit 2: 2 + 1 = 3
	        1st day - 7 times a day,
            2nd day - 5 times a day,
            3rd day - 3 times a day,
    */
    @Test
    public void durationOfDecreasingInDaysUntil2AsLimit_And_4AsDelta() {
        direction.setLimit(2);
        direction.setDelta(4);

        assertEquals(3, direction.calcDecreasingDaysAmountUntilLimitInclusive(SEVEN_DAILY_INTAKE_AMOUNT));
    }

    /*
    delta 2, limit 2: 2 + 1 = 3
        1st day - 6 times a day,
        2nd day - 4 times a day,
        3rd day - 2 times a day,
*/
    @Test
    public void durationOfDecreasingInDaysUntil2AsLimit_And_2AsDelta_EvenDailyIntake() {
        direction.setLimit(2);
        direction.setDelta(2);

        assertEquals(3, direction.calcDecreasingDaysAmountUntilLimitInclusive(6));
    }



    // should validate passing arg value?

}
