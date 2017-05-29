package optum.com.smartprototype.search;

import android.os.AsyncTask;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import optum.com.smartprototype.client.Client;

/**
 * Created by gedison on 5/26/17.
 */

public class SearchForMedicationRequestsWithSubject extends AsyncTask<Void, Object, Bundle>{

    private OnSearchComplete parent;
    private Client mClient;
    private String id;

    public SearchForMedicationRequestsWithSubject(String id, OnSearchComplete parent, Client client){
        this.id = id;
        this.parent = parent;
        mClient = client;
    }

    protected Bundle doInBackground(Void... voids) {
        try {
            Bundle response = mClient.getClient()
                    .search()
                    .forResource(MedicationRequest.class)
                    .count(15)
                    .where(MedicationRequest.SUBJECT.hasId("Patient/"+id))
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
