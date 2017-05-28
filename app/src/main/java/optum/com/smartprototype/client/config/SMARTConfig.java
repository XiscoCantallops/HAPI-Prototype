package optum.com.smartprototype.client.config;

/**
 * Created by gedison on 5/26/2017.
 */

public class SMARTConfig{
/*
    public static final String BASE_URL =       "https://auth.hspconsortium.org";
    public static final String TOKEN_URL =      BASE_URL+"/token";
    public static final String AUTHORIZE_URL =  BASE_URL+"/authorize";
    public static final String DATA_URL =       "https://api-stu3.hspconsortium.org/asdf/data";

    public static final String STATE =           "3F9463CA";
    public static final String SCOPE =           "openid user/*.* profile";
    public static final String CLIENT_ID =       "10bbccc3-afea-47da-9914-f2b9683fedc1";
    public static final String REDIRECT_URI =    "http://smartapp/callback";
    public static final String RESPONSE_TYPE =   "code";
    public static final String GRANT_TYPE =      "authorization_code";
*/

    public static final String BASE_URL =       "http://ec2-52-27-185-80.us-west-2.compute.amazonaws.com:8080/hspc-reference-authorization";
    public static final String TOKEN_URL =      BASE_URL+"/token";
    public static final String AUTHORIZE_URL =  BASE_URL+"/authorize";
    public static final String DATA_URL =       "http://ec2-52-27-185-80.us-west-2.compute.amazonaws.com:8080/tortuga-1/baseDstu3";

    public static final String STATE =           "3F9463CA";
    public static final String SCOPE =           "openid user/*.* profile";
    public static final String CLIENT_ID =       "a6d594f6-fd9e-40c1-8357-576379f71a6c";
    public static final String REDIRECT_URI =    "http://smartapp/callback";
    public static final String RESPONSE_TYPE =   "code";
    public static final String GRANT_TYPE =      "authorization_code";

}
