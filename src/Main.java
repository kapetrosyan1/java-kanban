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
        manager.addEpic(epic);
        manager.addSubtask(subtask, epic);
        manager.addSubtask(subtask1, epic);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        System.out.println(historyManager.getHistory());

        Epic epic2 = new Epic("Эпик 2", "Подготовка к литературе");
        Subtask subtask3 = new Subtask("подзадача 3 1.", "Прочитать Войну и Мир", Status.IN_PROGRESS);
        manager.addEpic(epic2);
        manager.addSubtask(subtask3, epic2);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);
        manager.getEpicById(6);
        manager.getSubtaskById(7);

        Task task2 = new Task("задача 3", "Выпить ново-пассит", Status.NEW);
        Task task3 = new Task("задача 4", "Выпить ново-пассит", Status.NEW);
        Task task4 = new Task("задача 5", "Выпить ново-пассит", Status.NEW);
        Task task5 = new Task("задача 6", "Выпить ново-пассит", Status.NEW);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        manager.addTask(task5);
        manager.getTaskById(8);
        manager.getTaskById(9);
        manager.getTaskById(10);
        manager.getTaskById(11);
        manager.getTaskById(12);

        System.out.println(historyManager.getHistory());

    }
}
