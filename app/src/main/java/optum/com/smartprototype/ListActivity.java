package optum.com.smartprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.hl7.fhir.dstu3.model.Patient;
import java.util.ArrayList;
import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.client.HAPIClient;
import optum.com.smartprototype.client.OnTokenCheck;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.search.OnSearchComplete;
import optum.com.smartprototype.search.SearchPaging;
import optum.com.smartprototype.search.SearchForPatients;
import optum.com.smartprototype.scroll.EndOfPageScrollListener;
import optum.com.smartprototype.scroll.OnScrollToEndOfPage;

public class ListActivity extends AppCompatActivity  implements OnSearchComplete, OnTokenCheck, OnScrollToEndOfPage {

    public static final int TOKEN_CODE = 12;
    public static final String PATIENT = "patient";

    private ListView mListView;
    ArrayAdapter<String> patientAdapter = null;

    private Client mClient;
    private boolean canLoadMorePatients = false;

    private ArrayList<String> patientStringArrayList = new ArrayList<>();
    private ArrayList<Patient> allPatients = new ArrayList<>();
    private org.hl7.fhir.dstu3.model.Bundle mBundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setOnScrollListener(new EndOfPageScrollListener(this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent patientActivity = new Intent(getApplicationContext(), PatientActivity.class);
            patientActivity.putExtra(PATIENT, allPatients.get(i));
            startActivity(patientActivity);
            }
        });

        switch (getIntent().getIntExtra(MainActivity.SERVER_TYPE, -1)){
            case 1:
                mClient = HAPIClient.getInstance();
                break;
            case 2:
                mClient = SMARTClient.getInstance();
                break;
            default:
                mClient = HAPIClient.getInstance();
                break;
        } mClient.doesClientHaveAValidToken(this);
    }

    public void onTokenCheck(boolean isTokenValid) {
        if(isTokenValid){
            SearchForPatients patientAsyncTask = new SearchForPatients(this, mClient);
            patientAsyncTask.execute();
        }else{
            Intent tokenActivity = new Intent(this, TokenActivity.class);
            startActivityForResult(tokenActivity, TOKEN_CODE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TOKEN_CODE) {
            if (resultCode == RESULT_OK) {
                SearchForPatients patientAsyncTask = new SearchForPatients(this, mClient);
                patientAsyncTask.execute();
            }else Toast.makeText(this, "Error Happened Man!", Toast.LENGTH_SHORT).show();
        }
    }

    private void pageForPatients(){
        if(mBundle!=null) {
            SearchPaging patientAsyncTask = new SearchPaging(this, mClient, mBundle);
            patientAsyncTask.execute();
        }
    }

    public void onScrollToEndOfPage() {
        if(canLoadMorePatients){
            canLoadMorePatients = false;
            pageForPatients();
        }
    }

    public void onSearchComplete(org.hl7.fhir.dstu3.model.Bundle bundle) {

        if(bundle!=null){
            mBundle = bundle;
            canLoadMorePatients = true;
            for (org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent component : bundle.getEntry()){
                Patient temp = (Patient) component.getResource();
                allPatients.add(temp);
                patientStringArrayList.add("Patient " + allPatients.size() + ": " + ((temp.getName().size() > 0) ? temp.getName().get(0).getFamily() + ", " + temp.getName().get(0).getGivenAsSingleString() : "Name not found"));
            }

            if(patientAdapter == null){
                patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientStringArrayList);
                mListView.setAdapter(patientAdapter);
            }else patientAdapter.notifyDataSetChanged();

        }else{
            Toast.makeText(this, "No more patients found", Toast.LENGTH_SHORT).show();
            canLoadMorePatients = false;
        }
    }
}
