import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Prototype {
    static class Request {
        public String request;
        public int numberOfTokens;

        Request() {
            this.request = "tell me about yourself";
        }

        Request(String request) {
            this.request = request;
        }

        Request(String request, int numberOfTokens) {
            this.request = request;
            this.numberOfTokens = numberOfTokens;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Select a request:");
            System.out.println("1. Default Request");
            System.out.println("2. Custom Request");
            System.out.println("3. Custom Request with number of tokens");
            System.out.println("4. Exit");
            System.out.print("Enter your choice (1/2/3/4): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            String request;
            int numberOfTokens;

            switch (choice) {
                case 1:
                    Request defaultRequest = new Request();
                    request = defaultRequest.request;
                    break;
                case 2:
                    System.out.print("Enter your custom request: ");
                    request = scanner.nextLine();
                    Request customRequest = new Request(request);
                    break;
                case 3:
                    System.out.print("Enter your custom request: ");
                    request = scanner.nextLine();
                    System.out.print("Enter the number of tokens: ");
                    numberOfTokens = scanner.nextInt();
                    Request customRequestWithTokens = new Request(request, numberOfTokens);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
                    continue;
            }

            System.out.println("Final String: " + request);

            // Call the API and print the response
            String apiResponse = APITest(request);
            System.out.println("API Response: " + apiResponse);
        }
    }

    public static String APITest(String text) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta2/models/text-bison-001:generateText?key=AIzaSyBWnPy4-R6VLvU-jiTvBo-aucC1Tj3f0_Y";
        String requestBody = "{ \"prompt\": { \"text\": \"" + text + "\" } }";

        try {
            String jsonResponse = sendAPIRequest(apiUrl, requestBody);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray candidatesArray = jsonObject.getJSONArray("candidates");
            JSONObject candidateObject = candidatesArray.getJSONObject(0);
            String output = candidateObject.getString("output");
            return output;
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
            StringBuilder response = new StringBuilder();

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

