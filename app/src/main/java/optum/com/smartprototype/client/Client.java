package optum.com.smartprototype.client;

import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Created by gedison on 5/27/2017.
 */

public interface Client {
    IGenericClient getClient();
    void doesClientHaveAValidToken(OnTokenCheck parent);
    void registerToken(String token);
}
