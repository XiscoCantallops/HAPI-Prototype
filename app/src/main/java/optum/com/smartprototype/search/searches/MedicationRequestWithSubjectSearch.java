package optum.com.smartprototype.search.searches;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.rest.gclient.IClientExecutable;
import optum.com.smartprototype.client.Client;
import optum.com.smartprototype.search.GenericSearch;
import optum.com.smartprototype.search.OnSearchComplete;

/**
 * Created by gedison on 5/29/2017.
 */

public class MedicationRequestWithSubjectSearch extends GenericSearch {

    private String mId;

    public MedicationRequestWithSubjectSearch(OnSearchComplete parent, Client client, String id) {
        super(parent, client);
        mId = id;
    }

    protected IClientExecutable<?, Bundle> getClientExecutable() {
        return  mClient.getClient()
                    .search()
                    .forResource(MedicationRequest.class)
                    .count(15)
                    .where(MedicationRequest.SUBJECT.hasId("Patient/" +  mId))
                    .returnBundle(Bundle.class);
    }
}
