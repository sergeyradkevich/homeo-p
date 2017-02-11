import org.junit.Test;
import static org.junit.Assert.*;

public class HomeoPTest {
    @Test public void testEnv() {
        HomeoP h = new HomeoP();
        assertTrue(h.doesWork());
    }
}
