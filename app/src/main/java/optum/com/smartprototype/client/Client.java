package optum.com.smartprototype.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Created by gedison on 5/27/2017.
 */

public abstract class Client {
    protected static final FhirContext ctx = FhirContext.forDstu3();

    public abstract IGenericClient getClient();
    public abstract void doesClientHaveAValidToken(OnTokenCheck parent);
    public abstract void registerToken(String token);
}
