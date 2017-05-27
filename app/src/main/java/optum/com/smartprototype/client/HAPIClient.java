package optum.com.smartprototype.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import optum.com.smartprototype.config.HAPIConfig;


/**
 * Created by gedison on 5/26/17.
 */

public class HAPIClient implements Client{

    private static HAPIClient hapiClientInstance = null;
    private IGenericClient genericClient;

    protected HAPIClient(){
        FhirContext ctx = FhirContext.forDstu3();
        genericClient = ctx.newRestfulGenericClient(HAPIConfig.DATA_URL);
    }

    public static Client getInstance(){
        if(hapiClientInstance==null) hapiClientInstance = new HAPIClient();
        return hapiClientInstance;
    }

    public IGenericClient getClient(){
        return genericClient;
    }

    public void doesClientHaveAValidToken(OnTokenCheck parent){
        parent.onTokenCheck(true);
    }

    public void registerToken(String token){

    }
}
