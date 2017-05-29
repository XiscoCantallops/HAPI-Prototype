package optum.com.smartprototype;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.client.OnTokenCheck;
import optum.com.smartprototype.client.SMARTClient;
import optum.com.smartprototype.client.config.SMARTConfig;
import optum.com.smartprototype.client.token.GetToken;
import optum.com.smartprototype.client.token.OnTokenComplete;

public class TokenActivity extends AppCompatActivity implements OnTokenComplete, OnTokenCheck {

    private Client mClient = SMARTClient.getInstance();
    private boolean onFirstRun = true;
    private boolean getTokenAutomatically = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        if(getTokenAutomatically){
            findViewById(R.id.tokenButton).setVisibility(View.INVISIBLE);
        }
    }

    protected void onResume() {
        super.onResume();

        if(getIntent()!=null && getIntent().getAction()!=null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            setContentView(R.layout.activity_authorization);

            Uri uri = getIntent().getData();
            if(uri.getQueryParameter("error") != null) onTokenComplete(null);
            else {
                getIntent().setAction("");
                String state = uri.getQueryParameter("state");
                if(state.equals(SMARTConfig.STATE)) {
                    GetToken tokenAsyncTask = new GetToken(this);
                    tokenAsyncTask.execute(uri.getQueryParameter("code"));
                }
            }
        }else if(!onFirstRun){
            mClient.doesClientHaveAValidToken(this);
        }else{
            onFirstRun = false;
            if(getTokenAutomatically)getToken(null);
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
        if(token!=null)mClient.registerToken(token);
        finish();
        return;
    }

    public void onTokenCheck(boolean isTokenValid) {
        if(isTokenValid){
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }else{
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }
}
