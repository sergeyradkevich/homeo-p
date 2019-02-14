package testsetup;

import com.google.inject.AbstractModule;
import context.GetDirectionalModeFactory;
import usecases.directionusage.GetDirectionalMode;

public class TreatmentDirectionalModeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GetDirectionalMode.class).to(GetDirectionalModeFactory.class);
    }
}
