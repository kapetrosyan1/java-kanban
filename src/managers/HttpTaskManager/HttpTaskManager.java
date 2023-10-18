package managers.HttpTaskManager;

import Server.KV.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.fileBackedTasksManager.FileBackedTasksManager;
import managers.utilityClasses.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final String taskKey = "tasks";
    private final String epicKey = "epics";
    private final String subtaskKey = "subtasks";
    private final String historyKey = "history";
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String URL) {
        super(Path.of("resources/recovery.csv"));
        kvTaskClient = new KVTaskClient(URL);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
            load();
        }

    public HttpTaskManager(String URL, boolean isLoad) {
        super(null);
        kvTaskClient = new KVTaskClient(URL);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        if (isLoad) {
            load();
        }
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(getAllTasks());
        kvTaskClient.put(taskKey, jsonTasks);

        String jsonSubtasks = gson.toJson(getAllSubtasks());
        kvTaskClient.put(subtaskKey, jsonSubtasks);


        String jsonEpics = gson.toJson(getAllEpics());
        kvTaskClient.put(epicKey, jsonEpics);

        String jsonHistory = gson.toJson(getHistory());
        kvTaskClient.put(historyKey, jsonHistory);
    }

    public void load() {
        String taskString = kvTaskClient.load(taskKey);

        if (taskString != null) {
            List<Task> fromJsonTasks = gson.fromJson(taskString, new TypeToken<ArrayList<Task>>() {
            }.getType());
            fromJsonTasks.forEach(t -> tasks.put(t.getId(), t));
        }

        String epicString = kvTaskClient.load(epicKey);

        if (epicString != null) {
            List<Epic> fromJsonEpics = gson.fromJson(epicString, new TypeToken<ArrayList<Epic>>() {
            }.getType());
            fromJsonEpics.forEach(e -> epics.put(e.getId(), e));
        }

        String subtaskString = kvTaskClient.load(subtaskKey);

        if (subtaskString != null) {
            List<Subtask> fromJsonSubtasks = gson.fromJson(subtaskString, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            fromJsonSubtasks.forEach(s -> subtasks.put(s.getId(), s));
        }

        String historyString = kvTaskClient.load(historyKey);

        if (historyString != null) {
            List<Integer> fromJsonHistory = gson.fromJson(historyString, new TypeToken<ArrayList<Integer>>() {
            }.getType());

            for (Integer id : fromJsonHistory) {
                if (tasks.containsKey(id)) {
                    getTaskById(id);
                } else if (epics.containsKey(id)) {
                    getEpicById(id);
                } else if (subtasks.containsKey(id)) {
                    getSubtaskById(id);
                }
            }
        }
    }
}
