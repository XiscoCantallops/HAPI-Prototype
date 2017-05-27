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

import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.client.OnTokenCheck;
import optum.com.smartprototype.config.SMARTConfig;
import optum.com.smartprototype.token.GetToken;
import optum.edu.smartprototype.R;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.patient.OnSearchForPatientsComplete;
import optum.com.smartprototype.patient.SearchForPatients;
import optum.com.smartprototype.token.OnTokenComplete;

public class MainActivity extends AppCompatActivity implements OnSearchForPatientsComplete, OnTokenComplete, OnTokenCheck {

    private ListView mListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.mListView);

        System.out.println("MAIN");
    }

    protected void onResume() {
        super.onResume();
        System.out.println("RESUME");
        if(getIntent()!=null && getIntent().getAction()!=null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();
            if(uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
                System.out.println(error);
            }else {
                getIntent().setAction("");
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
        System.out.println("GET TOKEN");
        Client mClient = SMARTClient.getInstance();
        mClient.doesClientHaveAValidToken(this);
    }

    public void onSearchForPatientsComplete(List<Patient> patients) {
        System.out.println("SEARCH COMPLETE");
        String[] patientStringArray = new String[patients.size()];
        for(int i=0; i<patients.size(); i++) patientStringArray[i] = "Patient "+i+": "+ ((patients.get(i).getName().size()>0) ? patients.get(i).getName().get(0).getFamily()+", "+patients.get(i).getName().get(0).getGivenAsSingleString() : "Name not found");

        ArrayAdapter<String> patientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, patientStringArray);
        mListView.setAdapter(patientAdapter);
    }

    public void onTokenComplete(String token) {
        System.out.println("TOKEN COMPLETE");
        System.out.println("AT TOKEN: "+token);
        Client mClient = SMARTClient.getInstance();
        mClient.registerToken(token);

        SearchForPatients patientAsyncTask = new SearchForPatients(this, SMARTClient.getInstance());
        patientAsyncTask.execute();
    }

    public void onTokenCheck(boolean isTokenValid) {
        System.out.println("TOKEN CHECK");
        if(isTokenValid){
            System.out.println("TOKEN VALID");
            SearchForPatients patientAsyncTask = new SearchForPatients(this, SMARTClient.getInstance());
            patientAsyncTask.execute();
        }else{
            System.out.println("TOKEN FAILED");
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
    }
}
