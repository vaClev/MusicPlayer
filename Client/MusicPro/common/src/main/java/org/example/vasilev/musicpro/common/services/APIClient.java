package org.example.vasilev.musicpro.common.services;

import com.google.gson.Gson;
import org.example.vasilev.musicpro.common.utils.GsonFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class APIClient
{
    private final String baseUrl;
    private final Gson gson;
    private final ExecutorService downloadExecutor;

    public APIClient(String baseUrl, int threadsCount)
    {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.gson = GsonFactory.createSimpleGson();
        this.downloadExecutor = Executors.newFixedThreadPool(threadsCount); // Отдельный пул для загрузок
    }

    /// Построение URL с query параметрами
    public static String buildUrlWithParams(String basePath, Map<String, String> params)
    {
        if (params == null || params.isEmpty()) {
            return basePath;
        }

        StringBuilder urlBuilder = new StringBuilder(basePath);
        urlBuilder.append("?");

        List<String> paramPairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
            paramPairs.add(encodedKey + "=" + encodedValue);
        }

        urlBuilder.append(String.join("&", paramPairs));
        return urlBuilder.toString();
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
            // настройка параметров запроса
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

    /// //////////////////////////////////////////////////////////////////////////////////////////
    /// Скачивание файлов

    /// Асинхронное скачивание файла
    public CompletableFuture<File> downloadAsync(String endpoint, File destinationFile,
                                                 Consumer<Double> progressCallback)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            return downloadFileSync(endpoint, destinationFile, progressCallback);
        }, downloadExecutor);
    }

    ///Асинхронное скачивание без отслеживания прогресса
    public CompletableFuture<File> downloadAsync(String endpoint, File destinationFile)
    {
        return downloadAsync(endpoint, destinationFile, null);
    }

    /// Приватный синхронный метод скачивания
    private File downloadFileSync(String endpoint, File destinationFile, Consumer<Double> progressCallback)
    {
        HttpURLConnection connection = null;
        FileOutputStream fos = null;
        InputStream is = null;
        String downloadUrl = buildUrl(endpoint);

        try
        {
            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
            {
                throw new IOException("HTTP GET Request Failed with Error code: " + responseCode);
            }

            // Получаем размер файла для отслеживания прогресса
            long fileSize = connection.getContentLengthLong();
            long downloadedSize = 0;

            /// Скачиваем файл
            is = connection.getInputStream();
            fos = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[8192]; // Увеличенный буфер для производительности
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1)
            {
                fos.write(buffer, 0, bytesRead);
                downloadedSize += bytesRead;

                // Отправляем прогресс если есть колбэк
                if (progressCallback != null && fileSize > 0)
                {
                    double progress = (double) downloadedSize / fileSize * 100;
                    progressCallback.accept(Math.min(progress, 100.0)); // Ограничиваем 100%
                }
            }
            fos.flush();
            System.out.println("Файл скачан: " + destinationFile.getAbsolutePath());

            return destinationFile;
        }
        catch (Exception e)
        {
            // Удаляем частично скачанный файл при ошибке
            if (destinationFile.exists())
            {
                destinationFile.delete();
            }
            throw new RuntimeException("Ошибка скачивания файла: " + e.getMessage(), e);
        }
        finally // освобождение ресурсов
        {
            try
            {
                if (fos != null) fos.close();
                if (is != null) is.close();
                if (connection != null) connection.disconnect();
            }
            catch (Exception e)
            {
                System.err.println("Ошибка закрытия потоков: " + e.getMessage());
            }
        }
    }

    ///Закрытие ресурсов
    public void shutdown()
    {
        downloadExecutor.shutdown();
    }
}
