package optum.com.smartprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.hl7.fhir.dstu3.model.Patient;
import java.util.List;
import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.client.OnTokenCheck;
import optum.edu.smartprototype.R;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.patient.OnSearchForPatientsComplete;
import optum.com.smartprototype.patient.SearchForPatients;

public class MainActivity extends AppCompatActivity implements OnSearchForPatientsComplete, OnTokenCheck {

    private ListView mListView;
    private Client mClient = SMARTClient.getInstance();

    public static final int TOKEN_CODE = 12;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.mListView);
    }

    public void getToken(View v){
        mClient.doesClientHaveAValidToken(this);
    }


    public void onSearchForPatientsComplete(List<Patient> patients) {
        String[] patientStringArray = new String[patients.size()];
        for(int i=0; i<patients.size(); i++) patientStringArray[i] = "Patient "+i+": "+ ((patients.get(i).getName().size()>0) ? patients.get(i).getName().get(0).getFamily()+", "+patients.get(i).getName().get(0).getGivenAsSingleString() : "Name not found");

        ArrayAdapter<String> patientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, patientStringArray);
        mListView.setAdapter(patientAdapter);
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
            }else{
                Toast.makeText(this, "Token could not be generated", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
