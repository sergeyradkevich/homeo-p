package entities;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntityTest {

    @Test
    public void entitiesAreEqualWhenIdsAreTheSame() {
        Entity one = new Entity();
        Entity another = new Entity();

        one.setId("1");
        another.setId("1");

        assertTrue(one.equals(another));
    }

    @Test
    public void entitiesAreNotEqualWhenIdsAreDifferent() {
        Entity one = new Entity();
        Entity another = new Entity();

        one.setId("1");
        another.setId("2");

        assertFalse(one.equals(another));
    }

    @Test
    public void entityEqualsToItself() {
        Entity entity = new Entity();
        entity.setId("1");

        assertTrue(entity.equals(entity));
    }

    @Test
    public void entitiesDoNotEqualsWhenNull() {
        Entity entity = new Entity();
        entity.setId("1");

        assertFalse(entity.equals(null));
    }

    @Test
    public void entitiesDoNotEqualsWhenIdsAreNull() {
        assertFalse(new Entity().equals(new Entity()));

        Entity one = new Entity();
        Entity another = new Entity();

        one.setId("1");

        assertFalse(another.equals(one));
    }

}
