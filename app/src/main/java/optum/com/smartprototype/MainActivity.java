package optum.com.smartprototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    public static final String SERVER_TYPE = "server-type";

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void startPatientListActivity(){
        Intent listActivityIntent = new Intent(this, ListActivity.class);
        startActivity(listActivityIntent);
    }

    public void useHAPIServer(View v){
        SharedPreferences prefs = this.getSharedPreferences("optum.com.smartprototype", Context.MODE_PRIVATE);
        prefs.edit().putInt(SERVER_TYPE, 1).apply();
        startPatientListActivity();
    }

    public void useSMARTServer(View v){
        SharedPreferences prefs = this.getSharedPreferences("optum.com.smartprototype", Context.MODE_PRIVATE);
        prefs.edit().putInt(SERVER_TYPE, 2).apply();
        startPatientListActivity();
    }

}
