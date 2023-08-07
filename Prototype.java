import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Prototype {
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

    private static JTextArea responseTextArea;
    private static List<String> responses = new ArrayList<>();
    private static List<String> requestedInputs = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create the JFrame to hold the UI components
        JFrame frame = new JFrame("PaLM Prototype");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new BorderLayout());

        // Create a JTextArea for displaying the responses
        responseTextArea = new JTextArea();
        responseTextArea.setEditable(false);
        JScrollPane responseScrollPane = new JScrollPane(responseTextArea);

        // Create a JPanel for the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

        // Add buttons to the buttonPanel
        JButton defaultButton = new JButton("Default Request");
        defaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processRequest(new Request());
            }
        });

        JButton customButton = new JButton("Custom Request");
        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customRequest = JOptionPane.showInputDialog(frame, "Enter your custom request:");
                processRequest(new CustomRequest(customRequest));
            }
        });

        JButton customWithTokensButton = new JButton("Custom Request with Tokens");
        customWithTokensButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customRequest = JOptionPane.showInputDialog(frame, "Enter your custom request:");
                String tokens = JOptionPane.showInputDialog(frame, "Enter the number of tokens:");
                try {
                    int numberOfTokens = Integer.parseInt(tokens);
                    processRequest(new Request(customRequest, numberOfTokens));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number of tokens!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton printButton = new JButton("Print All Conversations");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printRequestsAndResponses(requestedInputs, responses);
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exiting...");
                frame.dispose();
            }
        });

        buttonPanel.add(defaultButton);
        buttonPanel.add(customButton);
        buttonPanel.add(customWithTokensButton);
        buttonPanel.add(printButton);
        buttonPanel.add(exitButton);

        // Add components to the JFrame
        frame.add(responseScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }

    private static void processRequest(Request request) {
        String apiResponse = APITest(request.getRequestString());
        String output = extractOutputField(apiResponse);
        requestedInputs.add(request.getRequestString());
        responses.add(output);
        responseTextArea.append("API Response for request '" + request.getRequestString() + "':\n" + output + "\n\n");
    }

    private static void printRequestsAndResponses(List<String> requests, List<String> responses) {
        responseTextArea.append("\n--------------------- All Requests and Responses ---------------------\n");
        for (int i = 0; i < responses.size(); i++) {
            responseTextArea.append("--------------------- Conversation - " + (i + 1) + ": ---------------------\n");
            responseTextArea.append("Request " + (i + 1) + ": " + requests.get(i) + "\n");
            responseTextArea.append("Response " + (i + 1) + ": " + responses.get(i) + "\n");
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