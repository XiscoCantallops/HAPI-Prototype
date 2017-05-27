package optum.com.smartprototype.client;

/**
 * Created by gedison on 5/26/2017.
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import optum.com.smartprototype.config.SMARTConfig;

public class SMARTClient implements Client{

    private static SMARTClient smartClientInstance = null;
    private IGenericClient genericClient;

    protected SMARTClient(String token){
        FhirContext ctx = FhirContext.forDstu3();
        genericClient = ctx.newRestfulGenericClient(SMARTConfig.DATA_URL);
        if(token!=null) {
            BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);
            genericClient.registerInterceptor(authInterceptor);
        }
    }

    public static SMARTClient getInstance(String token){
        if(smartClientInstance==null) smartClientInstance = new SMARTClient(token);
        return smartClientInstance;
    }

    public static SMARTClient getInstance(){
        if(smartClientInstance==null) smartClientInstance = new SMARTClient(null);
        return smartClientInstance;
    }

    public IGenericClient getClient(){
        return genericClient;
    }



}

