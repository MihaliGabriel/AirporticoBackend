package main.controller;

import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import main.config.security.JWTGenerator;
import main.model.User;
import main.service.CustomOAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
@CrossOrigin(origins = {"https://localhost:4200", "http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
public class OAuthController {
    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
    private JWTGenerator jwtGenerator;
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    public OAuthController(CustomOAuth2UserService customOAuth2UserService, JWTGenerator jwtGenerator) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/login/oauth2/code/google")
    public ResponseEntity<Object> handleGoogleIdToken(@RequestHeader(value = "Authorization") String authorizationHeader) throws GeneralSecurityException, IOException {
        String tokenString = authorizationHeader.substring("Bearer ".length());

        //validating the Google OpenID token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList("110579548427-f4eugf3jlfbu3u9r3f4g67ssr2r8ti3i.apps.googleusercontent.com"))
                .build();

        GoogleIdToken idToken = verifier.verify(tokenString);
        IdToken.Payload payload;
        if (idToken != null) {
            payload = idToken.getPayload();
            logger.info("ID token payload: {}", payload);
        } else {
            logger.error("Invalid ID token");
            return ResponseEntity.badRequest().body("Invalid ID token from Google");
        }

        JSONObject jsonObject = new JSONObject(payload);

        //username will be the email. iss is the provider that sent the access token
        String username = jsonObject.getString("email") + "_google_auth";
        String email = jsonObject.getString("email");
        String provider = jsonObject.getString("iss");

        //add the user that logged in with OAuth in the database for future logins without OAuth.
        User user = customOAuth2UserService.loadUserinDb(username, provider, email);

        //add roles to user
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //generate a JWT token for the user
        String jwtToken = jwtGenerator.generateToken(username);

        //response for the front end
        Map<String, Object> responseMap = new HashMap<>();
          responseMap.put("username", username);
          responseMap.put("token", jwtToken);
          responseMap.put("roles", authorities);
          responseMap.put("id", user.getId());

        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/login/oauth2/code/facebook")
    public ResponseEntity<Object> handleFacebookIdToken(@RequestHeader(value = "Authorization") String authorizationHeader) throws IOException {
        String tokenString = authorizationHeader.substring("Bearer ".length());
        String appAccessToken = "7547482238604403|e345468a49182c71970bab71b75a2405"; // App Access Token

        String url = String.format(
                "https://graph.facebook.com/debug_token?input_token=%s&access_token=%s",
                tokenString, appAccessToken);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        logger.info("Response Code: {}", responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        logger.info("Response after reading facebook token: {}", response);

        JSONObject jsonObject = new JSONObject(response.toString());

        String provider = "facebook";
        JSONObject data = jsonObject.getJSONObject("data");
        String userId = data.getString("user_id");
        String username = userId + "_facebook";
        String email = ""; //facebook OAuth doesn't provide the email in the access token.

        User user = customOAuth2UserService.loadUserinDb(username, provider, email);

        //add roles to user
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //generate a JWT token for the user
        String jwtToken = jwtGenerator.generateToken(username);

        //response for the front end
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("username", username);
        responseMap.put("token", jwtToken);
        responseMap.put("roles", authorities);
        responseMap.put("id", user.getId());

        return ResponseEntity.ok(responseMap);
    }
}
