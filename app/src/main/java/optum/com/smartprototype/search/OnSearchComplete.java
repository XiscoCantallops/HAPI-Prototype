package optum.com.smartprototype.search;

import org.hl7.fhir.dstu3.model.Bundle;

/**
 * Created by gedison on 5/26/17.
 */

public interface OnSearchComplete {
    void onSearchComplete(Bundle bundle);
}
