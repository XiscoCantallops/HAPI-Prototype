package optum.com.smartprototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        SharedPreferences prefs = this.getSharedPreferences("com.optum.smartprototype", Context.MODE_PRIVATE);
        prefs.edit().putInt(SERVER_TYPE, 1).apply();
        startPatientListActivity(1);
    }

    public void useSMARTServer(View v){
        SharedPreferences prefs = this.getSharedPreferences("com.optum.smartprototype", Context.MODE_PRIVATE);
        prefs.edit().putInt(SERVER_TYPE, 2).apply();
        startPatientListActivity(2);
    }

}
