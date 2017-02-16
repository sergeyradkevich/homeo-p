import entities.Drug;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetPrescribedDrugsUseCaseTest {

    private Gateway gateway = new Gateway();
    private GetPrescribedDrugsUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetPrescribedDrugsUseCase(gateway);
    }

    @Test
    public void givenNoDrugsReturnsNoneDrugs() {
        List<Drug> drugs = useCase.getPrescribedDrugs();
        assertTrue(drugs.isEmpty());
    }

    @Test
    public void givenOnePrescribedDrugReturnsOneDrug() {
        Drug drug = new Drug("Arsen Alb");
        gateway.save(drug);

        List<Drug> drugs = useCase.getPrescribedDrugs();

        assertEquals("Arsen Alb", drugs.get(0).getName());
        assertEquals(1, drugs.size());
    }

    //todo: introduce equality based on identity, not names
    @Test
    public void givenManyPrescribedDrugsReturnsThem() {
        String[] names = {"Arsen Alb", "Arsen Alb 1", "Arsen Alb 2"};

        Stream.of(names)
                .map(name -> (new Drug(name)))
                .forEach(drug -> gateway.save(drug));

        List<Drug> drugs = useCase.getPrescribedDrugs();

        List<String> persistedNames = drugs.stream()
                .map(drug -> drug.getName())
                .sorted()
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(names), persistedNames);
    }

    @Test
    public void ImMemoryGatewayBehavesLikeDb_updateOfSavedEntityHasNoEffectOnDataInMemory() {
        Drug drug = new Drug("Arsen Alb");
        gateway.save(drug);

        drug.setName("Arsen Alb +++");
        Drug persisted = gateway.findAll().get(0);

        assertEquals("Arsen Alb", persisted.getName());
    }

    @Test
    public void ImMemoryGatewayBehavesLikeDb_updateOfRetrievedEntityHasNoEffectOnDataInMemory() {
        gateway.save(new Drug("Arsen Alb"));

        Drug persisted = gateway.findAll().get(0);
        persisted.setName("Arsen Alb ---");

        assertEquals("Arsen Alb", gateway.findAll().get(0).getName());
    }

}

class Gateway {

    private Set<Drug> entities = new HashSet<>();

    public Drug save(Drug drug) {
        persist(drug);
        return drug;
    }

    public List<Drug> findAll() {
        return entities.stream()
                .map(e -> clone(e))
                .collect(Collectors.toList());
    }

    private void persist(Drug drug) {
       Drug clone = clone(drug);
       entities.add(clone);
    }

    private Drug clone(Drug drug) {
        try {
            return (Drug) drug.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new UnCloneable();
        }
    }


}

class GetPrescribedDrugsUseCase {
    private Gateway gateway;

    public GetPrescribedDrugsUseCase(Gateway gateway) {
        this.gateway = gateway;
    }

    public List<Drug> getPrescribedDrugs() {
        return gateway.findAll();
    }
}

class UnCloneable extends RuntimeException {}

