package managers.utilityClasses;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Enums.Status;
import tasks.Enums.TasksTypes;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Transformer {

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

    public static String historyToString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();

        if (history.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (Task task : history) {
            sb.append(task.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (!value.isBlank()) {
            String[] historyRecs = value.split(",");

            for (String id : historyRecs) {
                history.add(Integer.parseInt(id));
            }
        }
        return history;
    }

    public static Task taskFromString(String value) {
        if (!value.isBlank()) {
            String[] fields = value.split(",");

            int id = Integer.parseInt(fields[0]);
            TasksTypes type = TasksTypes.valueOf(fields[1]);
            String title = fields[2];
            Status status = Status.valueOf(fields[3]);
            String description = fields[4];
                LocalDateTime startTime = null;
                long duration = 0;

            if (!type.equals(TasksTypes.EPIC)) {
                if (!fields[5].equals("null")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
                    startTime = LocalDateTime.parse(fields[5], formatter);
                }
                duration = Long.parseLong(fields[6]);
            }

            if (type.equals(TasksTypes.TASK)) {
                return new Task(id, title, status, description,startTime,duration);
            } else if (type.equals(TasksTypes.EPIC)) {
                return new Epic(id, title, status, description);
            } else if (type.equals(TasksTypes.SUBTASK)) {
                int epicId = Integer.parseInt(fields[7]);
                return new Subtask(id, title, status, description, startTime, duration, epicId);
            } else {
                throw new IllegalArgumentException("Ошибка формата задачи");
            }
        }
        throw new IllegalArgumentException("Ошибка формата задачи");
    }
}

