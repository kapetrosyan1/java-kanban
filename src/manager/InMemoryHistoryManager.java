package manager;
import tasks.Task;
import java.util.*;

class InMemoryHistoryManager implements HistoryManager {
     public static class CustomLinkedList<T> {
         Map<Integer, Node> hashTable = new HashMap<>();
         private Node head;
         private Node tail;


         public void linkLast(Task task) {
             if (task != null) {
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
         }

         public void removeNode(Node node) {
             if (node != null) {
                 Node prev = node.getPrev();
                 Node next = node.getNext();

                 if (head == node) {
                     hashTable.remove(node.getData().getId());
                     head = next;
                     return;
                 } else if (tail == node) {
                     tail = prev;
                     hashTable.remove(node.getData().getId());
                     return;
                 }

                 prev.setNext(next);
                 prev.setPrev(prev);
                 hashTable.remove(node.getData().getId());
             }
         }

         public List<Task> getTasks() {
             List<Task> taskList = new ArrayList<>();
             Node nextElement = head.getNext();

             taskList.add(head.getData());

             while (nextElement != null) {
                 taskList.add(nextElement.getData());
                 nextElement = nextElement.getNext();
             }

             return taskList;
         }
     }
     static CustomLinkedList<Task> history = new CustomLinkedList<>();


     @Override
    public void add(Task task) {
        if (task != null) {
            history.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {

         history.removeNode(history.hashTable.get(id));
         }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
}