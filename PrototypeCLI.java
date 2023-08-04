import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrototypeCLI {
    static class Request {
        public String request = "tell me about yourself";
        public int numberOfTokens;

        Request() {
            this.request = "how to write a hello world program in java";
        }

        Request(String request) {
            this.request = request;
        }

        Request(String request, int numberOfTokens) {
            this.request = request;
            this.numberOfTokens = numberOfTokens;
        }

        public String getRequestString() {
            return request;
        }
    }

    static class CustomRequest extends Request {
        public CustomRequest(String request) {
            super(request);
        }

        @Override
        public String getRequestString() {
            return "Custom Request: " + super.getRequestString();
        }
    }

    public static void main(String[] args) {
        List<String> responses = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Select a request:");
            System.out.println("1. Default Request");
            System.out.println("2. Custom Request");
            System.out.println("3. Custom Request with number of tokens");
            System.out.println("4. Print the response until now");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1/2/3/4): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            String request = "hello PaLM";
            int numberOfTokens;

            switch (choice) {
                case 1:
                    Request defaultRequest = new Request();
                    request = defaultRequest.getRequestString();
                    break;
                case 2:
                    System.out.print("Enter your custom request: ");
                    request = scanner.nextLine();
                    Request customRequest = new CustomRequest(request);
                    request = customRequest.getRequestString();
                    break;
                case 3:
                    System.out.print("Enter your custom request: ");
                    request = scanner.nextLine();
                    System.out.print("Enter the number of tokens: ");
                    numberOfTokens = scanner.nextInt();
                    Request customRequestWithTokens = new Request(request, numberOfTokens);
                    request = customRequestWithTokens.getRequestString();
                    break;
                case 4:
                    printResponses(responses);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
                    continue;
            }

            System.out.println("Final String: " + request);
            String apiResponse = APITest(request);
            String output = extractOutputField(apiResponse);
            responses.add(output);
            System.out.println("API Response: " + output);
        }
    }

    private static void printResponses(List<String> responses) {
        System.out.println("\n--- All Responses ---");
        for (int i = 0; i < responses.size(); i++) {
            System.out.println("Response " + (i + 1) + ": " + responses.get(i));
        }
    }

    private static String APITest(String text) {
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

    public static String extractOutputField(String jsonString) {
        int startIndex = jsonString.indexOf("\"output\":");
        if (startIndex != -1) {
            startIndex += "\"output\":".length();
            int endIndex = jsonString.indexOf("\"safetyRatings\":", startIndex);
            if (endIndex != -1) {
                String outputField = jsonString.substring(startIndex, endIndex).trim();
                // Remove the surrounding quotes and escape sequences
                outputField = outputField.replaceAll("\\\\n", "\n");
                outputField = outputField.replaceAll("\"", "");
                return outputField;
            }
        }
        return "Output field not found";
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