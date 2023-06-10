package manager;

import tasks.Task;

public class Node {
    private Task data;
    private Node next;
    private Node prev;

    public Node(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public Task getData() {
        return data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
