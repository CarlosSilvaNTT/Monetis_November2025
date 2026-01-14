
package api;

import java.net.http.*;
import java.net.URI;
import java.io.IOException;
import java.net.http.HttpRequest.BodyPublishers;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Base64;
import java.util.Map;

public class ApiClient {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();


    // Credenciais para Basic Auth
    static Dotenv dotenv = Dotenv.load();
    private static final String USER = dotenv.get("USER");  //"testing@example.com";
    private static final String PASSWORD = dotenv.get("PASSWORD"); //"testingPassword!1";


    // Método para gerar o header Authorization
    private static String getBasicAuthHeader() {
        String auth = USER + ":" + PASSWORD;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }



    // Generic GET method
    public static String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", getBasicAuthHeader())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Generic POST method with JSON body
    public static String post(String url, Map<String, Object> body) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", getBasicAuthHeader())
                .POST(BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Parse JSON response into Map
    public static Map<String, Object> parseJson(String json) throws IOException {
        return mapper.readValue(json, Map.class);
    }
}
