package Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.interfaces.TaskManager;
import managers.utilityClasses.LocalDateTimeAdapter;
import managers.utilityClasses.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    public final TaskManager manager;
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer(String URL) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handle);
        manager = Managers.getDefault(URL);
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handle);
        this.manager = manager;
    }

    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = extractPath(exchange);
            String query = extractQuery(exchange);

            if (query != null && query.equals("Запрошенный ресурс не найден")) {
                writeResponse(exchange, query, 404);
                return;
            }

            String response;

            switch (method) {
                case "GET":
                    switch (path) {
                        case "":
                            response = GSON.toJson(manager.getPrioritizedTasks());
                            writeResponse(exchange, response, 200);
                            return;

                        case "task/":
                            if (query == null) {
                                response = GSON.toJson(manager.getAllTasks());
                                writeResponse(exchange, response, 200);
                            } else if (parseTaskId(query) == -1) {
                                response = "Запрошенный ресурс не найден";
                                writeResponse(exchange, response, 404);
                                return;
                            } else {
                                Task task = manager.getTaskById(parseTaskId(query));
                                if (task != null) {
                                    response = GSON.toJson(task);
                                    writeResponse(exchange, response, 200);
                                } else {
                                    writeResponse(exchange, "Задача с id " + parseTaskId(query) +
                                            " не найдена", 404);
                                }
                            }
                            return;

                        case "subtask/":
                            if (query == null) {
                                response = GSON.toJson(manager.getAllSubtasks());
                                writeResponse(exchange, response, 200);
                            } else if (parseTaskId(query) == -1) {
                                response = "Запрошенный ресурс не найден";
                                writeResponse(exchange, response, 404);
                            } else {
                                Subtask subtask = manager.getSubtaskById(parseTaskId(query));
                                if (subtask != null) {
                                    response = GSON.toJson(subtask);
                                    writeResponse(exchange, response, 200);
                                } else {
                                    writeResponse(exchange, "Подзадача с id " + parseTaskId(query) +
                                            " не найдена", 404);
                                }
                            }
                            return;

                        case "epic/":
                            if (query == null) {
                                response = GSON.toJson(manager.getAllEpics());
                                writeResponse(exchange, response, 200);
                            } else {
                                if (parseTaskId(query) == -1) {
                                    response = "Запрошенный ресурс не найден";
                                    writeResponse(exchange, response, 404);
                                } else {
                                    Epic epic = manager.getEpicById(parseTaskId(query));
                                    if (epic != null) {
                                        response = GSON.toJson(epic);
                                        writeResponse(exchange, response, 200);
                                    } else {
                                        writeResponse(exchange, "Эпик с id " + parseTaskId(query) +
                                                " не найден", 404);
                                    }
                                }
                            }
                            return;

                        case "subtask/epic/":
                            if (query == null || parseTaskId(query) == -1) {
                                response = "Запрошенный ресурс не найден";
                                writeResponse(exchange, response, 404);
                                return;
                            } else {
                                if (manager.getEpicSubtasks(parseTaskId(query)) == null) {
                                    writeResponse(exchange, "Эпик с id " + parseTaskId(query) +
                                            " не найден", 404);
                                    return;
                                }
                                response = GSON.toJson(manager.getEpicSubtasks(parseTaskId(query)));
                                writeResponse(exchange, response, 200);
                            }
                            return;

                        case "history":
                            response = GSON.toJson(manager.getHistory());
                            writeResponse(exchange, response, 200);
                            return;

                        default:
                            writeResponse(exchange, "Запрошенный ресурс не найден", 404);
                            return;
                    }

                case "POST":
                    switch (path) {
                        case "task/":
                            Task task = GSON.fromJson(getRequestBody(exchange), Task.class);

                            if (task.getTitle() == null || task.getDescription() == null) {
                                writeResponse(exchange, "Не удалось создать задачу", 400);
                                return;
                            }

                            if (task.getId() == 0) {
                                manager.addTask(task);
                                writeResponse(exchange, "Задача успешно добавлена", 201);
                            } else {
                                manager.updateTask(task);
                                writeResponse(exchange, "Задача успешно обновлена", 201);
                            }
                            return;

                        case "epic/":
                            Epic epic = GSON.fromJson(getRequestBody(exchange), Epic.class);

                            if (epic.getTitle() == null || epic.getDescription() == null) {
                                writeResponse(exchange, "Не удалось создать эпик", 400);
                                return;
                            }

                            if (epic.getId() == 0) {
                                manager.addEpic(epic);
                                writeResponse(exchange, "Эпик успешно добавлен", 201);
                            } else {
                                manager.updateEpic(epic);
                                writeResponse(exchange, "Эпик успешно обновлен", 201);
                            }
                            return;

                        case "subtask/":

                            Subtask subtask = GSON.fromJson(getRequestBody(exchange), Subtask.class);

                            if (subtask.getTitle() == null || subtask.getDescription() == null || subtask.getEpicId() == 0) {
                                writeResponse(exchange, "Не удалось создать подзадачу", 400);
                                return;
                            }

                            if (subtask.getId() == 0) {
                                manager.addSubtask(subtask);
                                writeResponse(exchange, "Подзадача успешно добавлена", 201);
                            } else {
                                manager.updateSubtask(subtask);
                                writeResponse(exchange, "Подзадача успешно обновлена", 201);
                            }
                            return;

                        default:
                            writeResponse(exchange, "Запрошенный ресурс не найден", 404);
                            return;
                    }

                case "DELETE":
                    switch (path) {
                        case "task/":
                            if (query == null) {
                                manager.clearTasks();
                                writeResponse(exchange, "Задачи успешно удалены", 200);
                            } else if (parseTaskId(query) == -1) {
                                writeResponse(exchange, "Запрошенный ресурс не найден", 404);
                            } else {
                                manager.removeTaskById(parseTaskId(query));
                                writeResponse(exchange, "Задача с id " + parseTaskId(query) +
                                        " успешно удалена", 200);
                            }
                            return;

                        case "epic/":
                            if (query == null) {
                                manager.clearEpics();
                                writeResponse(exchange, "Эпики успешно удалены", 200);
                            } else if (parseTaskId(query) == -1) {
                                writeResponse(exchange, "Запрошенный ресурс не найден", 404);
                            } else {
                                manager.removeEpicById(parseTaskId(query));
                                writeResponse(exchange, "Эпик с id " + parseTaskId(query) +
                                        " успешно удален", 200);
                            }
                            return;

                        case "subtask/":
                            if (query == null) {
                                manager.clearSubtasks();
                                writeResponse(exchange, "Подзадачи успешно удалены", 200);
                            } else if (parseTaskId(query) == -1) {
                                writeResponse(exchange, "Запрошенный ресурс не найден", 404);
                            } else {
                                manager.removeSubtaskById(parseTaskId(query));
                                writeResponse(exchange, "Подзадача с id " + parseTaskId(query) +
                                        " успешно удалена", 200);
                            }
                            return;

                        default:
                            writeResponse(exchange, "Запрошенный ресурс не найден", 404);
                            return;
                    }

                default:
                    writeResponse(exchange, "Запрошенный метод не поддерживается", 501);
            }
        } catch (IOException exception) {
            throw new RuntimeException("При выполнении запроса возникла ошибка");
        }
    }

    public void start() {
        System.out.println("Task-сервер запущен на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Task-сервер, запущенный на порту " + PORT + ", остановлен");
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {

        byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
        if (exchange.getRequestMethod().equals("GET") && responseCode == 200) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
        }
        exchange.sendResponseHeaders(responseCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String extractPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().substring(7);
    }

    private String extractQuery(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return null;
        }

        if (Pattern.matches("^id=\\d+$", query)) {
            return query.substring(3);
        }

        return "Запрошенный ресурс не найден";
    }

    private int parseTaskId(String queryString) {
        try {
            return Integer.parseInt(queryString);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }
}