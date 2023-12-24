package MainApp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

            temperature = json.getJsonObject("main").getJsonNumber("temp").doubleValue() - 273.15; // Convert Kelvin to Celsius
            humidity = json.getJsonObject("main").getInt("humidity");
            windSpeed = json.getJsonObject("wind").getJsonNumber("speed").doubleValue();
        }
    }

    public void displayWeatherInfo() {
        System.out.println("City: " + this.place);
        System.out.println("Temperature: " + this.temperature + " °C");
        System.out.println("Humidity: " + this.humidity + "%");
        System.out.println("Wind Speed: " + this.windSpeed + " m/s");
    }

    public static void main(String[] args) {
        WeatherApp weatherApp = new WeatherApp();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter city name: ");
            weatherApp.place = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        weatherApp.getWeatherInfo();
        weatherApp.displayWeatherInfo();
    }
}
