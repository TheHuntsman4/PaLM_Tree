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

public class Prototype {
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

    private static List<String> responses = new ArrayList<>();
    private static JTextArea responseArea;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Language Model Prototype");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1800, 1600); // Set window size to 800x600
        frame.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); 
        frame.add(leftPanel, BorderLayout.WEST); 

        JButton defaultBtn = new JButton("Default Request");
        JButton customBtn = new JButton("Custom Request");
        JButton customWithTokensBtn = new JButton("Custom Request with Number of Tokens");

        
        int buttonWidth = 320;
        int buttonHeight  = 200;
        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        Font buttonFont = new Font("Arial",Font.PLAIN,24);
        defaultBtn.setFont(buttonFont);
        defaultBtn.setPreferredSize(buttonSize);
        defaultBtn.setMaximumSize(buttonSize);
        customBtn.setFont(buttonFont);
        customBtn.setPreferredSize(buttonSize);
        customBtn.setMaximumSize(buttonSize);
        customWithTokensBtn.setFont(buttonFont);
        customWithTokensBtn.setPreferredSize(buttonSize);
        customWithTokensBtn.setMaximumSize(buttonSize);

        leftPanel.add(defaultBtn);
        leftPanel.add(customBtn);
        leftPanel.add(customWithTokensBtn);

        frame.add(leftPanel, BorderLayout.WEST);

        responseArea = new JTextArea();
        responseArea.setFont(new Font("Arial", Font.PLAIN, 32));
        frame.add(new JScrollPane(responseArea), BorderLayout.CENTER);

        defaultBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processRequest(new Request());
            }
        });

        customBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String request = JOptionPane.showInputDialog(frame, "Enter your custom request:");
                if (request != null && !request.isEmpty()) {
                    processRequest(new CustomRequest(request));
                }
            }
        });

        customWithTokensBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String request = JOptionPane.showInputDialog(frame, "Enter your custom request:");
                if (request != null && !request.isEmpty()) {
                    int numberOfTokens = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of tokens:"));
                    processRequest(new Request(request, numberOfTokens));
                }
            }
        });

        frame.setVisible(true);
    }

    private static void processRequest(Request request) {
        String apiResponse = APITest(request.getRequestString());
        String output = extractOutputField(apiResponse);
        responses.add(output);
        responseArea.append("API Response: " + output + "\n");
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