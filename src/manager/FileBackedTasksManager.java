package manager;

import exceptions.ManagerSaveException;
import tasks.*;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    Path filePath;

    public FileBackedTasksManager(Path filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args) {
        try {
            FileBackedTasksManager fileBackedTasksManager
                    = new FileBackedTasksManager(Path.of("src/manager/files/recovery.csv"));

            Task task = new Task("Задача 1", Status.NEW, "Купить алкоголь");
            Task task1 = new Task("Задача 2", Status.NEW, "Выпить ново-пассит");
            fileBackedTasksManager.addTask(task);
            fileBackedTasksManager.addTask(task1);
            Epic epic = new Epic("Эпик 1", "Покупка продуктов");
            Subtask subtask = new Subtask("подзадача 1.", Status.NEW, "Купить гречку");
            Subtask subtask1 = new Subtask("подзадача 2.", Status.NEW, "Купить мясо");
            Subtask subtask2 = new Subtask("подзадача 3.", Status.NEW, "Купить мясо");

            fileBackedTasksManager.addEpic(epic);

            fileBackedTasksManager.addSubtask(subtask, epic);
            fileBackedTasksManager.addSubtask(subtask1, epic);
            fileBackedTasksManager.addSubtask(subtask2, epic);

            Epic epic2 = new Epic("Эпик 2", "Подготовка к литературе");
            fileBackedTasksManager.addEpic(epic2);

            fileBackedTasksManager.getTaskById(1);
            fileBackedTasksManager.getTaskById(2);
            fileBackedTasksManager.getEpicById(3);
            fileBackedTasksManager.getSubtaskById(4);
            fileBackedTasksManager.getSubtaskById(5);
            fileBackedTasksManager.getSubtaskById(6);
            System.out.println(fileBackedTasksManager.historyManager.getHistory());

            loadFromFile(new File("src/manager/files/recovery.csv"));

            System.out.println(fileBackedTasksManager.historyManager.getHistory());

        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }

    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))) {

            String header = "id,type,name,status,description,epic" + "\n";
            writer.write(header);

            writer.write(Transformer.allTasksToString(this) + "\n"
                    + Transformer.historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    private static FileBackedTasksManager loadFromFile(File file) {
        Path filePath = Path.of(file.toString());
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(filePath);

        List<String> csvToString = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            while (reader.ready()) {
                String line = reader.readLine();
                csvToString.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        int nextId = 0;

        for (int i = 1; i < csvToString.size() - 2; i++) {
            Task task = Transformer.taskFromString(csvToString.get(i));

            if (csvToString.get(i).contains(String.valueOf(TasksTypes.SUBTASK))) {
                fileBackedTasksManager.addBackedSubtask((Subtask) task);
                if (task.getId() > nextId) {
                    nextId = task.getId() + 1;
                }
            } else if (csvToString.get(i).contains(String.valueOf(TasksTypes.EPIC))) {
                fileBackedTasksManager.addBackedEpic((Epic) task);
                fileBackedTasksManager.updateEpic((Epic) task);
                if (task.getId() > nextId) {
                    nextId = task.getId() + 1;
                }
            } else if (csvToString.get(i).contains(String.valueOf(TasksTypes.TASK))) {
                fileBackedTasksManager.addBackedTask(task);
                if (task.getId() > nextId) {
                    nextId = task.getId() + 1;
                }
            }
        }


        InMemoryTaskManager.setNextId(nextId);

        String history = csvToString.get(csvToString.size() - 1);
        List<Integer> historyRecs = Transformer.historyFromString(history);
        for (
                Integer id : historyRecs) {
            if (fileBackedTasksManager.tasks.containsKey(id)) {
                fileBackedTasksManager.getTaskById(id);
            } else if (fileBackedTasksManager.epics.containsKey(id)) {
                fileBackedTasksManager.getEpicById(id);
            } else if (fileBackedTasksManager.subtasks.containsKey(id)) {
                fileBackedTasksManager.getSubtaskById(id);
            }
        }
        return fileBackedTasksManager;
    }

    @Override
    public void addSubtask(Subtask subtask, Epic epic) {
        super.addSubtask(subtask, epic);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, Subtask oldSubtask) {
        super.updateSubtask(subtask, oldSubtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int taskId) {
        super.removeEpicById(taskId);
        save();
    }

    @Override
    public void removeSubtaskById(int taskId) {
        super.removeSubtaskById(taskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void addBackedTask(Task task) {
        super.addBackedTask(task);
        save();
    }

    @Override
    public void addBackedEpic(Epic epic) {
        super.addBackedEpic(epic);
        save();
    }

    @Override
    public void addBackedSubtask(Subtask subtask) {
        super.addBackedSubtask(subtask);
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int taskId) {
        Epic epic = super.getEpicById(taskId);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int taskId) {
        Subtask subtask = super.getSubtaskById(taskId);
        save();
        return subtask;
    }

}
