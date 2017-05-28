package optum.com.smartprototype.patient;

import android.os.AsyncTask;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.LinkedList;
import java.util.List;

import optum.com.smartprototype.client.Client;

/**
 * Created by gedison on 5/26/17.
 */

public class SearchForPatientsPaging extends AsyncTask<Void, Object, List<Patient>>{

    private OnSearchForPatientsComplete parent;
    private Client mClient;
    private Bundle response;

    public SearchForPatientsPaging(OnSearchForPatientsComplete parent, Client mClient, Bundle previousResponse){
        this.parent = parent;
        this.mClient = mClient;
        this.response = previousResponse;
    }

    protected List<Patient> doInBackground(Void... voids) {
        try {
            if (response.getLink(IBaseBundle.LINK_NEXT) != null) {
                response = mClient.getClient().loadPage().next(response).execute();

                List<Patient> ret = new LinkedList<>();
                for (Bundle.BundleEntryComponent component : response.getEntry()) ret.add((Patient) component.getResource());
                return ret;

            } else return null;
        }catch (Exception e) {
            System.out.println("Exception: "+e.toString());
            return null;
        }
    }

    protected void onPostExecute(List<Patient> patients) {
        mClient = null;
        if(patients == null)response = null;
        parent.onSearchForPatientsComplete(patients, response);
    }
}
