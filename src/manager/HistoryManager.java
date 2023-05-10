package manager;
import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public interface HistoryManager {


    void add(Task task);

    LinkedList<Task> getHistory();
}
