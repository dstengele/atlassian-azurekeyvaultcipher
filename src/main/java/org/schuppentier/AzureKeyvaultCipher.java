package org.schuppentier;

import com.atlassian.db.config.password.Cipher;
import org.json.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AzureKeyvaultCipher implements Cipher {
    HttpClient client = HttpClient.newHttpClient();

    public String encrypt(String keyvaultSecretUrl) {
        return keyvaultSecretUrl;
    }

    public String decrypt(String keyvaultSecretUrl) {
        String accessToken = getAccessToken();

        return getSecret(keyvaultSecretUrl, accessToken);
    }

    private String getAccessToken() {
        JSONObject oauthTokenJson = getJsonObjectFromUri("http://169.254.169.254/metadata/identity/oauth2/token?api-version=2018-02-01&resource=https%3A%2F%2Fvault.azure.net", "Metadata", "true");
        if (oauthTokenJson == null) return null;

        return oauthTokenJson.getString("access_token");
    }

    private String getSecret(String secretUri, String accessToken) {
        URI secretUriObject;
        try {
            secretUriObject = new URI(secretUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        String newQuery = secretUriObject.getQuery();
        if (null == newQuery) {
            newQuery = "api-version=7.3";
        } else {
            newQuery += "&api-version=7.3";
        }

        try {
            secretUri = new URI(secretUriObject.getScheme(), secretUriObject.getAuthority(), secretUriObject.getPath(), newQuery, secretUriObject.getFragment()).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        JSONObject secretJson = getJsonObjectFromUri(secretUri, "Authorization", "Bearer " + accessToken);
        if (secretJson == null) return null;

        return secretJson.getString("value");
    }

    private JSONObject getJsonObjectFromUri(String uri, String... headers) {
        URI oauthTokenUri = URI.create(uri);
        HttpRequest oauthTokenRequest = HttpRequest.newBuilder(oauthTokenUri)
                .headers(headers)
                .build();

        HttpResponse<String> oauthTokenResponse;
        try {
            oauthTokenResponse = client.send(oauthTokenRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        String oauthTokenJsonString = oauthTokenResponse.body();

        return new JSONObject(oauthTokenJsonString);
    }
}
