package optum.com.smartprototype.patient;

import org.hl7.fhir.dstu3.model.Patient;

import java.util.List;

/**
 * Created by gedison on 5/26/17.
 */

public interface OnSearchForPatientsComplete {
    public void onSearchForPatientsComplete(List<Patient> patients);
}