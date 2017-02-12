import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class GetPrescribedDrugsUseCaseTest {

    @Test
    public void givenNoDrugsReturnsEmpty() {
        GetPrescribedDrugsUseCase useCase = new GetPrescribedDrugsUseCase();
        List<String> drugs = useCase.getPrescribedDrugs();
        assertTrue(drugs.isEmpty());
    }
}

class GetPrescribedDrugsUseCase {
    public List<String> getPrescribedDrugs() {
      return Collections.emptyList();
    }
}

