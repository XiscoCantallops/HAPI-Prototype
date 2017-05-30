package optum.com.smartprototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.client.HAPIClient;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.search.GenericSearch;
import optum.com.smartprototype.search.OnSearchComplete;
import optum.com.smartprototype.search.Paging;
import optum.com.smartprototype.search.searches.PatientSearch;
import optum.com.smartprototype.scroll.EndOfPageScrollListener;
import optum.com.smartprototype.scroll.OnScrollToEndOfPage;

public class ListActivity extends AppCompatActivity  implements OnSearchComplete, OnScrollToEndOfPage {

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

        loadPatients();
    }

    public void loadPatients(){
        GenericSearch patientSearch = new PatientSearch(this, mClient);
        patientSearch.execute();
    }

    public void loadToken(){
        Intent tokenActivity = new Intent(this, TokenActivity.class);
        startActivityForResult(tokenActivity, TOKEN_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TOKEN_CODE) {
            if (resultCode == RESULT_OK) {
                loadPatients();
            }else Toast.makeText(this, "Error Happened Man!", Toast.LENGTH_SHORT).show();
        }
    }

    private void pageForPatients(){
        if(mBundle!=null) {
            GenericSearch patientAsyncTask = new Paging(this, mClient, mBundle);
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

    public void onSearchError(Exception e) {
        if(e instanceof AuthenticationException){
            loadToken();
        }else{
            System.out.println("Other Error: "+e.getLocalizedMessage()+" "+e.getClass().toString());
        }
    }
}
