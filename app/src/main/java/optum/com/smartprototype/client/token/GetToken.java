package optum.com.smartprototype.client.token;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import optum.com.smartprototype.client.config.SMARTConfig;

/**
 * Created by gedison on 5/26/2017.
 */

public class GetToken extends AsyncTask<String, Integer, String> {

    private OnTokenComplete parent;

    public GetToken(OnTokenComplete parent){
        this.parent = parent;
    }

    private String httpPostRequest(String code) {
        String returnToken = null;
        try {
            System.out.println("HTTP POST REQUEST");
            URL url = new URL(SMARTConfig.TOKEN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("client_id",      SMARTConfig.CLIENT_ID)
                    .appendQueryParameter("code",           code)
                    .appendQueryParameter("state",          SMARTConfig.STATE)
                    .appendQueryParameter("response_type",  SMARTConfig.RESPONSE_TYPE)
                    .appendQueryParameter("redirect_uri",   SMARTConfig.REDIRECT_URI)
                    .appendQueryParameter("grant_type",     SMARTConfig.GRANT_TYPE);
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
            System.out.println("I've got tokens for days motherfucker: "+returnToken);
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

    protected String doInBackground(String... codes) {
        return httpPostRequest(codes[0]);
    }

    protected void onPostExecute(String token) {
        parent.onTokenComplete(token);
    }
}
