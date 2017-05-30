package optum.com.smartprototype.search;

import android.os.AsyncTask;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.rest.gclient.IClientExecutable;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import optum.com.smartprototype.client.Client;

/**
 * Created by gedison on 5/26/17.
 */

public abstract class GenericSearch extends AsyncTask<Void, Object, Bundle>{

    protected Client mClient;
    private OnSearchComplete parent;
    private Exception exception = null;

    protected abstract IClientExecutable<?, Bundle> getClientExecutable();

    protected GenericSearch(OnSearchComplete parent, Client client){
        this.parent = parent;
        this.mClient = client;
    }

    protected Bundle doInBackground(Void... voids) {
        try {
            IClientExecutable executable = getClientExecutable();
            return (executable!=null) ? getClientExecutable().execute() : null;
        }catch (Exception e){
            exception = e;
            return null;
        }
    }

    protected void onPostExecute(Bundle response) {
        mClient = null;
        if(exception != null)parent.onSearchError(exception);
        else parent.onSearchComplete(response);
    }
}
