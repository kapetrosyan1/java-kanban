package managers.inMemoryManagers;

import managers.interfaces.HistoryManager;
import managers.utilityClasses.Node;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> hashTable = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(hashTable.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node newElement = new Node(task);

        if (hashTable.containsKey(task.getId())) {
            removeNode(hashTable.get(task.getId()));
        }

        if (head == null) {
            head = newElement;
        } else {
            newElement.setPrev(tail);
            tail.setNext(newElement);
        }

        tail = newElement;

        hashTable.put(task.getId(), newElement);
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node prev = node.getPrev();
            Node next = node.getNext();

            if (head == node && tail == node) {
                hashTable.remove(node.getData().getId());
                head = null;
                tail = null;
                return;
            } else if (head == node) {
                hashTable.remove(node.getData().getId());
                head = next;
                next.setPrev(prev);
                return;
            } else if (tail == node) {
                hashTable.remove(node.getData().getId());
                tail = prev;
                tail.setNext(null);
                return;
            }

            prev.setNext(next);
            next.setPrev(prev);
            hashTable.remove(node.getData().getId());
        }
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        if (head != null) {
            Node nextElement = head.getNext();

            taskList.add(head.getData());

            while (nextElement != null) {
                taskList.add(nextElement.getData());
                nextElement = nextElement.getNext();
            }
            return taskList;
        }
        return taskList;
    }
}
