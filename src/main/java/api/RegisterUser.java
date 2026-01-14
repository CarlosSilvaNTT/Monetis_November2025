package api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterUser {
    public static Map<String, Object> register() throws IOException, InterruptedException {
        String url = "https://monetis-delta.vercel.app/api/users/register";

        Map<String, Object> body = new HashMap<>();
        body.put("name", "Testing");
        body.put("surname", "Account");
        body.put("email", "testing@example.com");
        body.put("phone_number", "123123123");
        body.put("street_address", "Some Random street");
        body.put("postal_code", "1231-123");
        body.put("city", "Lisbon");
        body.put("country", "PT");
        body.put("password", "testingPassword!1");
        body.put("confirmPassword", "testingPassword!1");

        String response = ApiClient.post(url, body);
        return ApiClient.parseJson(response);
    }
}
