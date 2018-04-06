package testsetup;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import doubles.DosageInMemoryGateway;
import doubles.DrugInMemoryGateway;
import doubles.TreatmentInMemoryGateway;
import usecases.DosageGateway;
import usecases.DrugGateway;
import usecases.TreatmentGateway;

public class CommonTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TreatmentGateway.class).to(TreatmentInMemoryGateway.class).in(Singleton.class);
        bind(DosageGateway.class).to(DosageInMemoryGateway.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    DrugGateway drugGateway(TreatmentGateway treatmentGateway) {
        return new DrugInMemoryGateway((TreatmentInMemoryGateway) treatmentGateway);
    }
    
}
