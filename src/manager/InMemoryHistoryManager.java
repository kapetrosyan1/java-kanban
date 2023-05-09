package manager;

import tasks.Task;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private static LinkedList<Task> history = new LinkedList<>();
    @Override
    public void add(Task task) {
        if (task != null) {
            history.addLast(task);
            if(history.size() > 10) {
                history.removeFirst();
            }
        }
    }

    @Override
    public LinkedList<Task> getHistory() {
        return history;
    }
}
/*Привет, у нас в 4 спринте еще не было связанных списков, но как я понял, должно быть примерно как-то так.
Попробовал вызвать 11 задач, вроде сработало корректно.
 */