package optum.com.smartprototype;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.hl7.fhir.dstu3.model.Patient;

import java.util.List;

import optum.com.smartprototype.config.SMARTConfig;
import optum.com.smartprototype.token.GetToken;
import optum.edu.smartprototype.R;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.patient.OnSearchForPatientsComplete;
import optum.com.smartprototype.patient.SearchForPatients;
import optum.com.smartprototype.token.OnTokenComplete;

public class MainActivity extends AppCompatActivity implements OnSearchForPatientsComplete, OnTokenComplete {

    private ListView mListView;
    private boolean thing = true;

    static final int TOKEN_REQUEST = 8080;
    static final String TOKEN_KEY = "TOKEN_KEY";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("At Main");

        mListView = (ListView) findViewById(R.id.mListView);

        //SearchForPatients patientAsyncTask = new SearchForPatients(this);
        //patientAsyncTask.execute();
    }

    protected void onResume() {
        super.onResume();

        if(getIntent()!=null && getIntent().getAction()!=null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            thing = false;
            Uri uri = getIntent().getData();
            if(uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
                System.out.println(error);
            }else {
                System.out.println(uri.toString());

                String state = uri.getQueryParameter("state");
                if(state.equals(SMARTConfig.STATE)) {
                    String code = uri.getQueryParameter("code");
                    GetToken tokenAsyncTask = new GetToken(this);
                    tokenAsyncTask.execute(code);
                }
            }
        }
    }

    public void getToken(View v){
        String uri = Uri.parse(SMARTConfig.AUTHORIZE_URL)
                .buildUpon()
                .appendQueryParameter("state",          SMARTConfig.STATE)
                .appendQueryParameter("response_type",  SMARTConfig.RESPONSE_TYPE)
                .appendQueryParameter("scope",          SMARTConfig.SCOPE)
                .appendQueryParameter("aud",            SMARTConfig.DATA_URL)
                .appendQueryParameter("client_id",      SMARTConfig.CLIENT_ID)
                .appendQueryParameter("redirect_uri",   SMARTConfig.REDIRECT_URI)
                .build().toString();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(browserIntent);
    }

    public void onTokenComplete(String token) {
        System.out.println("AT TOKEN: "+token);
        SearchForPatients patientAsyncTask = new SearchForPatients(this, SMARTClient.getInstance(token));
        patientAsyncTask.execute();
    }


    public void onSearchForPatientsComplete(List<Patient> patients) {

        String[] patientStringArray = new String[patients.size()];
        for(int i=0; i<patients.size(); i++) patientStringArray[i] = "Patient "+i+": "+ ((patients.get(i).getName().size()>0) ? patients.get(i).getName().get(0).getFamily()+", "+patients.get(i).getName().get(0).getGivenAsSingleString() : "Name not found");

        ArrayAdapter<String> patientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, patientStringArray);
        mListView.setAdapter(patientAdapter);
    }


}
