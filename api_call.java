import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class api_call {
    public static void main (String args[]){
        APITest test_api = new APITest(); 
        String out = test_api.APITest("lemonade recipe");
        System.out.println(out);
    }
}

public class APITest {
    public static String APITest(String text) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta2/models/text-bison-001:generateText?key=AIzaSyBWnPy4-R6VLvU-jiTvBo-aucC1Tj3f0_Y";
        String requestBody = "{ \"prompt\": { \"text\": \"" + text + "\" } }";

        try {
            String jsonResponse = sendAPIRequest(apiUrl, requestBody);

            return jsonResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
    }

    private static String sendAPIRequest(String apiUrl, String requestBody) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);


        try (OutputStream outputStream = conn.getOutputStream()) {
            outputStream.write(requestBody.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new IOException("API request failed with response code: " + responseCode);
        }
    }
}
