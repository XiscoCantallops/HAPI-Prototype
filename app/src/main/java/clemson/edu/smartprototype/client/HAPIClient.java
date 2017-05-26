package clemson.edu.smartprototype.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;


/**
 * Created by gedison on 5/26/17.
 */

public class HAPIClient {

    private static HAPIClient hapiClientInstance = null;
    private IGenericClient genericClient;

    protected HAPIClient(){
        FhirContext ctx = FhirContext.forDstu3();
        genericClient = ctx.newRestfulGenericClient(Config.DATA_URL);

        //BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);
        //genericClient.registerInterceptor(authInterceptor);
    }

    public static HAPIClient getInstance(){
        if(hapiClientInstance==null) hapiClientInstance = new HAPIClient();
        return hapiClientInstance;
    }

    public IGenericClient getClient(){
        return genericClient;
    }



}
