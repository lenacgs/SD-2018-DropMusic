package Dropbox_RESTAPI;
import java.io.Serializable;
import java.util.Scanner;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import uc.sd.apis.DropBoxApi2;


public class DropboxConnection implements Serializable{
    OAuthService service;
    Scanner in;

    // Access codes #1: per application used to get access codes #2
    private static final String API_APP_KEY = "m8rp7duib3txihe";
    private static final String API_APP_SECRET = "k0a57qnvqp4dgg1";

    // Access codes #2: per user per application
    private static final String API_USER_TOKEN = "";

    public DropboxConnection() {
        service = new ServiceBuilder()
                .provider(DropBoxApi2.class)
                .apiKey(API_APP_KEY)
                .apiSecret(API_APP_SECRET)
                .callback("https://eden.dei.uc.pt/~fmduarte/echo.php")
                .build();

        in = new Scanner(System.in);
        System.out.println("service created");

        try {
            if (API_USER_TOKEN.equals("")) {
                System.out.println("API_USER_TOKEN=\"\"");
                System.out.println("Authorize scribe here:");
                System.out.println(service.getAuthorizationUrl(null));
                System.out.println("Press enter when done.\n>>>");
                Verifier verifier = new Verifier(in.nextLine());
                Token accessToken = service.getAccessToken(null, verifier);
                System.out.println("Define API_USER_TOKEN: " + accessToken.getToken());
                //System.out.println("Define API_USER_SECRET: " + accessToken.getSecret());
                System.exit(0);
            }

            Token accessToken = new Token(API_USER_TOKEN, "");
        } catch (OAuthException exc) {
            exc.printStackTrace();
        }
    }



}
