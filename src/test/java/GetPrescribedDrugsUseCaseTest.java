import entities.Drug;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

}

class Gateway {

    private Set<Drug> entities = new HashSet<>();

    public Drug save(Drug drug) {
        persist(drug);
        return drug;
    }

    public List<Drug> findAll() {
        return entities.stream()
                .collect(Collectors.toList());
    }

    private void persist(Drug drug) {
        try {
            entities.add((Drug) drug.clone());
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

