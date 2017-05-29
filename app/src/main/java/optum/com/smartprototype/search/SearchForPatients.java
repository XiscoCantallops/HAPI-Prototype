package optum.com.smartprototype.search;

import android.os.AsyncTask;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.LinkedList;
import java.util.List;

import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.search.OnSearchComplete;

/**
 * Created by gedison on 5/26/17.
 */

public class SearchForPatients extends AsyncTask<Void, Object, Bundle>{

    private OnSearchComplete parent;
    private Client mClient;
    private int count = 15;

    public SearchForPatients(OnSearchComplete parent, Client mClient){
        this.parent = parent;
        this.mClient = mClient;
    }

    protected Bundle doInBackground(Void... voids) {
        try {
            Bundle response = mClient.getClient()
                    .search()
                    .forResource(Patient.class)
                    .count(count)
                    .where(Patient.FAMILY.isMissing(false))
                    .returnBundle(Bundle.class).execute();


            return response;
        }catch (Exception e){
            return null;
        }
    }

    protected void onPostExecute(Bundle response) {
        mClient = null;
        parent.onSearchComplete(response);
    }
}
