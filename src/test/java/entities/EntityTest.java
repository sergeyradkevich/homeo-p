package entities;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntityTest {

    @Test
    public void entitiesAreEqualWhenIdsAreTheSame() {
        Drug one = new Drug();
        Drug another = new Drug();

        one.setId("1");
        another.setId("1");

        assertTrue(one.equals(another));
    }

    @Test
    public void entitiesAreNotEqualWhenIdsAreDifferent() {
        Drug one = new Drug();
        Drug another = new Drug();

        one.setId("1");
        another.setId("2");

        assertFalse(one.equals(another));
    }

    @Test
    public void entityEqualsToItself() {
        Drug entity = new Drug();
        entity.setId("1");

        assertTrue(entity.equals(entity));
    }

    @Test
    public void entitiesDoNotEqualsWhenNull() {
        assertFalse(new Drug().equals(null));
    }

    @Test
    public void entitiesDoNotEqualsWhenIdsAreNull() {
        assertFalse(new Drug().equals(new Drug()));

        Drug one = new Drug();
        Drug another = new Drug();

        one.setId("1");

        assertFalse(another.equals(one));
    }

}
