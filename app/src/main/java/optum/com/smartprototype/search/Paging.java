package optum.com.smartprototype.search;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import ca.uhn.fhir.rest.gclient.IClientExecutable;
import optum.com.smartprototype.client.Client;

/**
 * Created by gedison on 5/29/2017.
 */

public class Paging extends GenericSearch {

    private Bundle previousBundle;

    public Paging(OnSearchComplete parent, Client client, Bundle bundle) {
        super(parent, client);
        previousBundle = bundle;
    }

    protected IClientExecutable<?, Bundle> getClientExecutable() {
        if (previousBundle.getLink(IBaseBundle.LINK_NEXT) != null) {
            return mClient.getClient().loadPage().next(previousBundle);
        } else return null;
    }
}
