package clemson.edu.smartprototype.patient;

import android.os.AsyncTask;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.LinkedList;
import java.util.List;

import clemson.edu.smartprototype.client.HAPIClient;

/**
 * Created by gedison on 5/26/17.
 */

public class SearchForPatients extends AsyncTask<Void, Object, List<Patient>>{

    private OnSearchForPatientsComplete parent;

    public SearchForPatients(OnSearchForPatientsComplete parent){
        this.parent = parent;
    }

    protected List<Patient> doInBackground(Void... voids) {
        Bundle response = HAPIClient.getInstance().getClient()
                .search()
                .forResource(Patient.class)
                .where(Patient.FAMILY.isMissing(false))
                .returnBundle(Bundle.class).execute();

        List<Patient> ret = new LinkedList<>();
        for(Bundle.BundleEntryComponent component : response.getEntry())ret.add((Patient)component.getResource());
        return ret;
    }

    protected void onPostExecute(List<Patient> patients) {
        parent.onSearchForPatientsComplete(patients);
    }
}
