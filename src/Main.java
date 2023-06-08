import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getHistory();

        Task task = new Task("Задача 1", "Купить алкоголь", Status.NEW);
        Task task1 = new Task("Задача 2", "Выпить ново-пассит", Status.NEW);
        manager.addTask(task);
        manager.addTask(task1);

        Epic epic = new Epic("Эпик 1", "Покупка продуктов");
        Subtask subtask = new Subtask("подзадача 1.", "Купить гречку", Status.NEW);
        Subtask subtask1 = new Subtask("подзадача 2.", "Купить мясо", Status.NEW);
        Subtask subtask2 = new Subtask("подзадача 3.", "Купить мясо", Status.NEW);
        manager.addEpic(epic);
        manager.addSubtask(subtask, epic);
        manager.addSubtask(subtask1, epic);
        manager.addSubtask(subtask2, epic);

        Epic epic2 = new Epic("Эпик 2", "Подготовка к литературе");
        manager.addEpic(epic2);

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);

        System.out.println(historyManager.getHistory());

        manager.getTaskById(1);
        manager.getEpicById(3);

        System.out.println(historyManager.getHistory());

        manager.removeTaskById(2);

        System.out.println(historyManager.getHistory());

    }
}
