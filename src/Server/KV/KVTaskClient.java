package Server.KV;

import exceptions.FailedRequestException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String URL;
    private final String apiToken;
    private final HttpClient httpClient;

    public KVTaskClient(String URL) {
        this.URL = URL;
        httpClient = HttpClient.newHttpClient();
        apiToken = registerAPIToken(URL);
    }

    private String registerAPIToken(String URL) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "register"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Регистрация не удалась. Код ошибки: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new FailedRequestException("Не получается сделать запрос");
        }
    }

    public String load(String KVServerKey) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "load/" + KVServerKey + "?API_TOKEN=" + apiToken))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return null;
            }
            if (response.statusCode() != 200) {
                throw new RuntimeException("Не удается загрузить данные. Код ошибки: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new FailedRequestException("Не получается сделать запрос");
        }
    }

    public void put(String KVServerKey, String value) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "save/" + KVServerKey + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Не удалось сохранить данные. Код ошибки: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new FailedRequestException("Не получается сделать запрос");
        }
    }
}