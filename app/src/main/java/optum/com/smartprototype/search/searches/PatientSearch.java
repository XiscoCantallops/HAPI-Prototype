package optum.com.smartprototype.search.searches;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.rest.gclient.IClientExecutable;
import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.search.GenericSearch;
import optum.com.smartprototype.search.OnSearchComplete;

/**
 * Created by gedison on 5/29/2017.
 */

public class PatientSearch extends GenericSearch {

    public PatientSearch(OnSearchComplete parent, Client client) {
        super(parent, client);
    }

    protected IClientExecutable<?, Bundle> getClientExecutable() {
        return mClient.getClient()
                .search()
                .forResource(Patient.class)
                .count(15)
                .where(Patient.FAMILY.isMissing(false))
                .returnBundle(org.hl7.fhir.dstu3.model.Bundle.class);
    }
}
