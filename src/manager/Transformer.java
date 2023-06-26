package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

    public static Task taskFromString(String value) {
        String[] fields = value.split(",");

        int id = Integer.parseInt(fields[0]);
        TasksTypes type = TasksTypes.valueOf(fields[1]);
        String title = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        if (type.equals(TasksTypes.TASK)) {
            return new Task(id, title, status, description);
        } else if (type.equals(TasksTypes.EPIC)) {
            return new Epic(id, title, status, description);
        } else if (type.equals(TasksTypes.SUBTASK)) {
            int epicId = Integer.parseInt(fields[5]);

            return new Subtask(id, title, status, description, epicId);
        } else {
            throw new IllegalArgumentException("Ошибка формата задачи");
        }
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        if (manager.getHistory() == null) {
            return "";
        }
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();

        String[] historyRecs = value.split(",");

        for (String id : historyRecs) {
            history.add(Integer.parseInt(id));
        }
        return history;
    }

    public static String allTasksToString(TaskManager taskManager) {
        StringBuilder sb = new StringBuilder();
        List<Task> allTasks = new ArrayList<>();

        allTasks.addAll(taskManager.getAllTasks());
        allTasks.addAll(taskManager.getAllSubtasks());
        allTasks.addAll(taskManager.getAllEpics());

        for (Task task : allTasks) {
            sb.append(task.toString()).append("\n");
        }
        return sb.toString();
    }
}
