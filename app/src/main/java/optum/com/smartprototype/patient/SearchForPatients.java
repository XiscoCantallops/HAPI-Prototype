package optum.com.smartprototype.patient;

import android.os.AsyncTask;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import optum.com.smartprototype.client.Client;

/**
 * Created by gedison on 5/26/17.
 */

public class SearchForPatients extends AsyncTask<Void, Object, List<Patient>>{

    private OnSearchForPatientsComplete parent;
    private Client mClient;

    public SearchForPatients(OnSearchForPatientsComplete parent, Client mClient){
        this.parent = parent;
        this.mClient = mClient;
    }

    protected List<Patient> doInBackground(Void... voids) {
        try {
            Bundle response = mClient.getClient()
                    .search()
                    .forResource(Patient.class)
                    .where(Patient.FAMILY.isMissing(false))
                    .returnBundle(Bundle.class).execute();

            List<Patient> ret = new LinkedList<>();
            for (Bundle.BundleEntryComponent component : response.getEntry())
                ret.add((Patient) component.getResource());
            return ret;
        }catch (Exception e){
            return null;
        }
    }

    protected void onPostExecute(List<Patient> patients) {
        mClient = null;
        parent.onSearchForPatientsComplete(patients);
    }
}
