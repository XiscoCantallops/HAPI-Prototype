package optum.com.smartprototype.client;

/**
 * Created by gedison on 5/26/2017.
 */

import android.os.AsyncTask;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import optum.com.smartprototype.client.config.SMARTConfig;

public class SMARTClient extends Client{

    private static SMARTClient smartClientInstance = null;
    private static IGenericClient genericClient;

    private SMARTClient(){
        genericClient = ctx.newRestfulGenericClient(SMARTConfig.DATA_URL);
    }

    public void registerToken(String token){
        if(smartClientInstance!=null){
            BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);
            genericClient.registerInterceptor(authInterceptor);
        }
    }

    public static SMARTClient getInstance(){
        if(smartClientInstance==null) smartClientInstance = new SMARTClient();
        return smartClientInstance;
    }

    public IGenericClient getClient(){
        return genericClient;
    }

    public void doesClientHaveAValidToken(OnTokenCheck parent){
        CheckToken tokenAsyncTask = new CheckToken(parent);
        tokenAsyncTask.execute();
    }

    private class CheckToken extends AsyncTask<Void, Object, Boolean> {

        private OnTokenCheck parent;

        private CheckToken(OnTokenCheck parent){
            this.parent = parent;
        }

        protected Boolean doInBackground(Void... voids) {
            try {
                Bundle response = genericClient
                        .search()
                        .forResource(Patient.class)
                        .returnBundle(Bundle.class).execute();
                return true;
            }catch(AuthenticationException e) {
                return false;
            }catch (Exception e){
                return true;
            }
        }

        protected void onPostExecute(Boolean isTokenValid) {
            parent.onTokenCheck(isTokenValid);
        }
    }
}

