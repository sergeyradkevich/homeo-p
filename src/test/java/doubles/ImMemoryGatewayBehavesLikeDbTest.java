package doubles;

import entities.Drug;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImMemoryGatewayBehavesLikeDbTest {

    private InMemoryGateway<Drug> gateway = new InMemoryGateway<>();

    @Test
    public void updateOfSavedEntityHasNoEffectOnDataInMemory() {
        Drug drug = new Drug("Arsen Alb");
        gateway.save(drug);

        drug.setName("Arsen Alb +++");
        Drug persisted = gateway.findAll().get(0);

        assertEquals("Arsen Alb", persisted.getName());
    }

    @Test
    public void updateOfOneRetrievedEntityHasNoEffectOnDataInMemory() {
        Drug origin = gateway.save(new Drug("Arsen Alb"));

        Drug persisted = gateway.findById(origin.getId());
        persisted.setName("Arsen Alb ---");

        assertEquals("Arsen Alb", gateway.findById(origin.getId()).getName());
    }

    @Test
    public void updateOfRetrievedEntitiesHasNoEffectOnDataInMemory() {
        gateway.save(new Drug("Arsen Alb"));

        Drug persisted = gateway.findAll().get(0);
        persisted.setName("Arsen Alb ---");

        assertEquals("Arsen Alb", gateway.findAll().get(0).getName());
    }
}
