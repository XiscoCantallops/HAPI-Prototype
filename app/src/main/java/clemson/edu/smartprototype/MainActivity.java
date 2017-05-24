package clemson.edu.smartprototype;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;

public class MainActivity extends AppCompatActivity {

    private static String BASE_URL = "https://auth.hspconsortium.org";
    private static String TOKEN_URL = BASE_URL+"/token";
    private static String AUTHORIZE_URL = BASE_URL+"/authorize";
    private static String DATA_URL = BASE_URL+"/hspc/data";

    private static String STATE = "3F9463CA";
    private static String SCOPE = "openid user/*.* profile";
    private static String CLIENT_ID = "980b1773-e620-41c2-b413-82ac9d46cc4b";
    private static String REDIRECT_URI = "http://smartapp/callback";
    private static String RESPONSE_TYPE = "code";
    private static String GRANT_TYPE = "authorization_code";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onResume() {
        super.onResume();

        if(getIntent()!=null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();
            if(uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
            }else {
                String state = uri.getQueryParameter("state");
                if(state.equals(STATE)) {
                    String code = uri.getQueryParameter("code");
                    new DownloadFilesTask().execute(code);
                }
            }
        }
    }

    public String httpPostRequest(String code) {
        String returnToken = null;
        try {
            URL url = new URL(TOKEN_URL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("client_id", CLIENT_ID)
                    .appendQueryParameter("code", code)
                    .appendQueryParameter("state", STATE)
                    .appendQueryParameter("response_type", RESPONSE_TYPE)
                    .appendQueryParameter("redirect_uri", REDIRECT_URI)
                    .appendQueryParameter("grant_type", GRANT_TYPE);
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)response.append(inputLine);
            in.close();

            JSONObject obj = new JSONObject(response.toString());
            returnToken = obj.getString("access_token");
            System.out.println("Tokens for days: "+returnToken);
            return returnToken;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void myClick(View v){
        String uri = Uri.parse(AUTHORIZE_URL)
                .buildUpon()
                .appendQueryParameter("state", STATE)
                .appendQueryParameter("response_type", RESPONSE_TYPE)
                .appendQueryParameter("scope", SCOPE)
                .appendQueryParameter("aud", DATA_URL)
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .build().toString();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(browserIntent);
    }
    IGenericClient genericClient;
    public void test(String token){
        System.out.println("AT TEST: "+token);
        FhirContext ctx = null;
        ctx = FhirContext.forDstu3();
        System.out.println("AT TEST: "+token);
        IRestfulClientFactory clientFactory = ctx.getRestfulClientFactory();

        BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);
        genericClient = ctx.newRestfulGenericClient(DATA_URL);
        genericClient.registerInterceptor(authInterceptor);


        Thread searchThread = new Thread() {
            @Override
            public void run() {
                final IBaseBundle results =  genericClient.search()
                        .forResource(Patient.class)
                        .limitTo(100)
                        .where(Patient.FAMILY.matches().value("Smith"))
                        .returnBundle(org.hl7.fhir.instance.model.api.IBaseBundle.class)
                        .encodedXml().execute();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(results.toString());
                        /*
                        if (results.getEntries().size()>0) {
                            for(int i=0; i<results.size(); i++){
                                BundleEntry result = results.getEntries().get(i);
                                Patient practitioner = (Patient) result.getResource();
                                System.out.println(practitioner.getGender()+" "+practitioner.getName());

                            }
                        }*/
                    }
                });
            }
        };
        searchThread.start();
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... codes) {
            String token = httpPostRequest(codes[0]);
            test(token);
            return token;
        }
    }
}
