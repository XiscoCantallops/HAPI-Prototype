package optum.com.smartprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;
import java.util.ArrayList;

import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.client.HAPIClient;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.search.OnSearchComplete;
import optum.com.smartprototype.search.SearchForMedicationRequestsWithSubject;

public class PatientActivity extends AppCompatActivity implements OnSearchComplete{

    private ListView mListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Patient mPatient = (Patient)getIntent().getSerializableExtra(ListActivity.PATIENT);

        getSupportActionBar().setTitle(mPatient.getName().get(0).getFamily() + ", " + mPatient.getName().get(0).getGivenAsSingleString());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.mListView);

        Client mClient;
        SharedPreferences prefs = this.getSharedPreferences("com.optum.smartprototype", Context.MODE_PRIVATE);
        switch (prefs.getInt(MainActivity.SERVER_TYPE, -1)){
            case 1:
                mClient = HAPIClient.getInstance();
                break;
            case 2:
                mClient = SMARTClient.getInstance();
                break;
            default:
                mClient = HAPIClient.getInstance();
                break;
        }

        SearchForMedicationRequestsWithSubject medicationAsyncTask = new SearchForMedicationRequestsWithSubject(mPatient.getIdElement().getIdPart(), this, mClient);
        medicationAsyncTask.execute();
    }


    public void onSearchComplete(org.hl7.fhir.dstu3.model.Bundle bundle) {
        int numberOfMedications = 0;
        if(bundle!=null){
            ArrayList<String> observationStringArrayList = new ArrayList<>();
            for (org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent component : bundle.getEntry()) {
                MedicationRequest temp = (MedicationRequest) component.getResource();
                try {
                    numberOfMedications++;
                    observationStringArrayList.add(numberOfMedications+": "+temp.getMedicationCodeableConcept().getText());
                } catch (FHIRException e) {
                    e.printStackTrace();
                }
            }

            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, observationStringArrayList);
            mListView.setAdapter(listAdapter);
        }
    }
}
