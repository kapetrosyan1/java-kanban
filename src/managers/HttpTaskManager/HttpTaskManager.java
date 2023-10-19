package managers.HttpTaskManager;

import Server.HttpTaskServer;
import Server.KV.KVTaskClient;
import com.google.gson.reflect.TypeToken;
import managers.fileBackedTasksManager.FileBackedTasksManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final String taskKey = "tasks";
    private final String epicKey = "epics";
    private final String subtaskKey = "subtasks";
    private final String historyKey = "history";
    private final KVTaskClient kvTaskClient;

    public HttpTaskManager(String URL) {
        super(null);
        kvTaskClient = new KVTaskClient(URL);
        load();
    }

    public HttpTaskManager(String URL, boolean isLoad) {
        super(null);
        kvTaskClient = new KVTaskClient(URL);
        if (isLoad) {
            load();
        }
    }

    @Override
    public void save() {
        String jsonTasks = HttpTaskServer.GSON.toJson(getAllTasks());
        kvTaskClient.put(taskKey, jsonTasks);

        String jsonSubtasks = HttpTaskServer.GSON.toJson(getAllSubtasks());
        kvTaskClient.put(subtaskKey, jsonSubtasks);


        String jsonEpics = HttpTaskServer.GSON.toJson(getAllEpics());
        kvTaskClient.put(epicKey, jsonEpics);

        String jsonHistory = HttpTaskServer.GSON.toJson(getHistory());
        kvTaskClient.put(historyKey, jsonHistory);
    }

    public void load() {
        String taskString = kvTaskClient.load(taskKey);

        if (taskString != null) {
            List<Task> fromJsonTasks = HttpTaskServer.GSON.fromJson(taskString, new TypeToken<ArrayList<Task>>() {
            }.getType());
            fromJsonTasks.forEach(t -> tasks.put(t.getId(), t));
        }

        String epicString = kvTaskClient.load(epicKey);

        if (epicString != null) {
            List<Epic> fromJsonEpics = HttpTaskServer.GSON.fromJson(epicString, new TypeToken<ArrayList<Epic>>() {
            }.getType());
            fromJsonEpics.forEach(e -> epics.put(e.getId(), e));
        }

        String subtaskString = kvTaskClient.load(subtaskKey);

        if (subtaskString != null) {
            List<Subtask> fromJsonSubtasks = HttpTaskServer.GSON.fromJson(subtaskString, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            fromJsonSubtasks.forEach(s -> subtasks.put(s.getId(), s));
        }

        String historyString = kvTaskClient.load(historyKey);

        if (historyString != null) {
            List<Task> fromJsonHistory = HttpTaskServer.GSON.fromJson(historyString, new TypeToken<ArrayList<Task>>() {
            }.getType());

            for (Task task : fromJsonHistory) {
                if (tasks.containsKey(task.getId())) {
                    getTaskById(task.getId());
                } else if (epics.containsKey(task.getId())) {
                    getEpicById(task.getId());
                } else if (subtasks.containsKey(task.getId())) {
                    getSubtaskById(task.getId());
                }
            }
        }
    }
}
