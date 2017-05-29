package optum.com.smartprototype.search;

import android.os.AsyncTask;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.search.OnSearchComplete;

/**
 * Created by gedison on 5/26/17.
 */

public class SearchPaging extends AsyncTask<Void, Object, Bundle>{

    private OnSearchComplete parent;
    private Client mClient;
    private Bundle response;

    public SearchPaging(OnSearchComplete parent, Client mClient, Bundle previousResponse){
        this.parent = parent;
        this.mClient = mClient;
        this.response = previousResponse;
    }

    protected Bundle doInBackground(Void... voids) {
        try {
            if (response.getLink(IBaseBundle.LINK_NEXT) != null) {
                response = mClient.getClient().loadPage().next(response).execute();
                return response;
            } else return null;
        }catch (Exception e) {
            System.out.println("Exception: "+e.toString());
            return null;
        }
    }

    protected void onPostExecute(Bundle response) {
        mClient = null;
        parent.onSearchComplete(response);
    }
}
