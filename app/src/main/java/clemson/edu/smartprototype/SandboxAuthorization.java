package clemson.edu.smartprototype;

/**
 * Created by gedison on 4/2/2017.
 */

public class SandboxAuthorization {


    //https://persona-auth.hspconsortium.org/authorize?
    // state=3F9463CA&
    // response_type=code&
    // aud=https%3A%2F%2Fpersona-api.hspconsortium.org%2Fhspc%2Fdata&
    // scope=user%2F*.*+openid+profile&
    // redirect_uri=smartapp%3A%2F%2Fcallback&
    // client_id=3cb9c849-3c07-4d52-869b-a38f4ce86402
    private String dataURL ="https://persona-auth.hspconsortium.org/hspc/data";
    private String baseURL ="https://persona-auth.hspconsortium.org/authorize";
    private String scope = "openid user/*.* profile";
    private String client_id = "3cb9c849-3c07-4d52-869b-a38f4ce86402";
    private String redirect = "smartapp://callback";
    private boolean keychain = false;
    private boolean verbose = false;

}
