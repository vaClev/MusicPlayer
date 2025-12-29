package org.example.vasilev.musicpro.services.music;

import com.google.gson.Gson;
import org.example.vasilev.musicpro.utils.GsonFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class APIClient
{
    private final String baseUrl; // передам в конструктор из файла properties http://localhost:5098/api/
    private final Gson gson;

    public APIClient(String baseUrl)
    {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.gson = GsonFactory.createSimpleGson();
    }

    /// Временно конструктор для отладки
    public APIClient()
    {
        this("http://localhost:5098/");
    }

    /**
     * Выполнить GET запрос к API
     *
     * @param endpoint     эндпоинт API (без базового URL) например - music
     * @param responseType класс для десериализации ответа
     * @return результат запроса
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, Class<T> responseType)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                String url = buildUrl(endpoint);
                String json = sendGetRequest(url);
                return gson.fromJson(json, responseType);
            } catch (Exception e)
            {
                throw new RuntimeException("GET request failed for endpoint: " + endpoint, e);
            }
        });
    }

    /// построение полного пути для отправки запроса
    private String buildUrl(String endpoint)
    {
        String cleanEndpoint = endpoint.startsWith("/") ? endpoint.substring(1) : endpoint;
        return baseUrl + cleanEndpoint;
    }

    /// Отправка запроса
    private String sendGetRequest(String urlString) throws IOException
    {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try
        {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            // настройка параметров запос
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
            {
                throw new IOException("HTTP GET Request Failed with Error code: " + responseCode);
            }

            //Вычитываем данные из ответа
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

            return response.toString();
        }
        finally
        {
            // Закрываем ресурсы в правильном порядке
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }

            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }


    /**
     * Выполнить GET запрос к API (для списков)
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, java.lang.reflect.Type responseType)
    {
        return CompletableFuture.supplyAsync(() -> {
            try
            {
                String url = buildUrl(endpoint);
                String json = sendGetRequest(url);

                return gson.fromJson(json, responseType);
            }
            catch (Exception e)
            {
                throw new RuntimeException("GET request failed for endpoint: " + endpoint, e);
            }
        });
    }
}
