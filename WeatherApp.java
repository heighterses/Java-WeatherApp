package MainApp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public class WeatherApp {

    private static final String API_KEY = "f6c37586c1df36f113bd0b8844029b93";

    private String place;
    private Double temperature;
    private Integer humidity;
    private Double windSpeed;

    public void getWeatherInfo() {
        try {
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + place + "&appid=" + API_KEY;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                parseWeatherInfo(response.toString());
            } else {
                System.out.println("Error: Unable to fetch weather information. HTTP response code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseWeatherInfo(String jsonResponse) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonResponse))) {
            JsonObject json = reader.readObject();

            temperature = json.getJsonObject("main").getJsonNumber("temp").doubleValue() - 273.15;
            humidity = json.getJsonObject("main").getInt("humidity");
            windSpeed = json.getJsonObject("wind").getJsonNumber("speed").doubleValue();
        }
    }

    public static void main(String[] args) {
        // GUI Implementations:

        WeatherApp weatherApp = new WeatherApp();

        // Frame Creation:
        JFrame frame = new JFrame("Cute Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 660);

        // Background Setting:
        ImageIcon backgroundImage = new ImageIcon("C:\\Users\\Abdullah\\Desktop\\project.jpeg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.add(backgroundLabel);

        // Heading label:
        JLabel headingLabel = new JLabel("Cute Weather App");
        headingLabel.setBounds(190, 20, 350, 60);
        headingLabel.setForeground(Color.WHITE);
        headingLabel.setFont(new Font("Viner Hand ITC", Font.BOLD, 36));
        backgroundLabel.add(headingLabel);

        // Input Label:
        JLabel nameLabel = new JLabel("Enter City Name:");
        nameLabel.setBounds(127, 75, 350, 60);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        backgroundLabel.add(nameLabel);

        // Input TextField:
        JTextField nameTextField = new JTextField();
        nameTextField.setBounds(260, 95, 220, 25);
        nameTextField.setFont(new Font("Arial", Font.PLAIN, 14));
        backgroundLabel.add(nameTextField);

        // Submit Button:
        JButton submitButton = new JButton("Get Weather");
        submitButton.setBounds(306, 129, 130, 28);
        submitButton.setForeground(Color.BLACK);
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        backgroundLabel.add(submitButton);

        // Temperature Label:
        JLabel temperatureLabel = new JLabel("Temperature (C):");
        temperatureLabel.setBounds(128, 146, 350, 60);
        temperatureLabel.setForeground(Color.WHITE);
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 16));
        backgroundLabel.add(temperatureLabel);

        // Temperature TextField:
        JTextField temperatureTextField = new JTextField();
        temperatureTextField.setBounds(260, 165, 220, 25);
        temperatureTextField.setFont(new Font("Arial", Font.PLAIN, 14));
        backgroundLabel.add(temperatureTextField);

        // Humidity Label:
        JLabel humidityLabel = new JLabel("Humidity(%):");
        humidityLabel.setBounds(160, 180, 350, 60);
        humidityLabel.setForeground(Color.WHITE);
        humidityLabel.setFont(new Font("Arial", Font.BOLD, 16));
        backgroundLabel.add(humidityLabel);

        // Humidity TextField:
        JTextField humidityTextField = new JTextField();
        humidityTextField.setBounds(260, 200, 220, 25);
        humidityTextField.setFont(new Font("Arial", Font.PLAIN, 14));
        backgroundLabel.add(humidityTextField);

        // WindSpeed Label:
        JLabel windSpeedLabel = new JLabel("Wind Speed (m/h):");
        windSpeedLabel.setBounds(113, 215, 350, 60);
        windSpeedLabel.setForeground(Color.WHITE);
        windSpeedLabel.setFont(new Font("Arial", Font.BOLD, 16));
        backgroundLabel.add(windSpeedLabel);

        // WindField TextField:
        JTextField windSpeedTextField = new JTextField();
        windSpeedTextField.setBounds(260, 235, 220, 25);
        windSpeedTextField.setFont(new Font("Arial", Font.PLAIN, 14));
        backgroundLabel.add(windSpeedTextField);

        // Frame Setting:
        frame.setLayout(null);
        frame.setVisible(true);

        // Event handling of submit button
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                weatherApp.place = nameTextField.getText();
                weatherApp.getWeatherInfo();

                temperatureTextField.setText(String.format("%.2f", weatherApp.temperature));
                humidityTextField.setText(String.valueOf(weatherApp.humidity));
                windSpeedTextField.setText(String.format("%.2f", weatherApp.windSpeed));

                // Insert data into the database
                try (Connection con = DBconnection.connect()) {
                    DBconnection.insertData(con, weatherApp.place, weatherApp.temperature, weatherApp.humidity, weatherApp.windSpeed);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
