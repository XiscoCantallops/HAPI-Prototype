package optum.com.smartprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    public static final String SERVER_TYPE = "server-type";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void startPatientListActivity(int serverType){
        Intent listActivityIntent = new Intent(this, ListActivity.class);
        listActivityIntent.putExtra(SERVER_TYPE, serverType);
        startActivity(listActivityIntent);
    }

    public void useHAPIServer(View v){
        startPatientListActivity(1);
    }

    public void useSMARTServer(View v){
        startPatientListActivity(2);
    }

}
