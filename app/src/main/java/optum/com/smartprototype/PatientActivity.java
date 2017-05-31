package optum.com.smartprototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;
import java.util.ArrayList;

import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.client.HAPIClient;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.search.GenericSearch;
import optum.com.smartprototype.search.searches.MedicationRequestWithSubjectSearch;
import optum.com.smartprototype.search.OnSearchComplete;

import static optum.com.smartprototype.ListActivity.TOKEN_CODE;

public class PatientActivity extends AppCompatActivity implements OnSearchComplete{

    private ListView mListView;
    private Patient mPatient;
    private Client mClient;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPatient = (Patient)getIntent().getSerializableExtra(ListActivity.PATIENT);

        getSupportActionBar().setTitle(mPatient.getName().get(0).getFamily() + ", " + mPatient.getName().get(0).getGivenAsSingleString());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.mListView);


        SharedPreferences prefs = this.getSharedPreferences("optum.com.smartprototype", Context.MODE_PRIVATE);
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

        loadMedications();
    }

    public void loadMedications(){
        GenericSearch medicationAsyncTask = new MedicationRequestWithSubjectSearch(this, mClient, mPatient.getIdElement().getIdPart());
        medicationAsyncTask.execute();
    }

    public void loadToken(){
        Intent tokenActivity = new Intent(this, TokenActivity.class);
        startActivityForResult(tokenActivity, TOKEN_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TOKEN_CODE) {
            if (resultCode == RESULT_OK) {
                loadMedications();
            }else Toast.makeText(this, "Error Happened Man!", Toast.LENGTH_SHORT).show();
        }
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

    public void onSearchError(Exception e) {
        if(e instanceof AuthenticationException){
            loadToken();
        }else{
            System.out.println("Other Error: "+e.getLocalizedMessage()+" "+e.getClass().toString());
        }
    }
}
