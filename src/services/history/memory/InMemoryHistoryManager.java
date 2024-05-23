package services.history.memory;

import models.Task;
import services.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Node prev;
        Node next;
        Task task;

        Node(Task task) {
            this.task = task;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(task, node.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task);
        }
    }

    private final HashMap<Integer, Node> history;
    private int nodeCnt = 0;
    private Node lastNode;
    private Node startNode;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    private void linkLast(Node node) {
        if (node == null) {
            return;
        }

        if (lastNode != null) {
            lastNode.next = node;
            node.prev = lastNode;
        } else {
            startNode = node;
        }

        lastNode = node;
        nodeCnt++;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(nodeCnt);
        Node node = startNode;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (nodeCnt == 1 && node.equals(lastNode) && node.equals(startNode)) {
            startNode = null;
            lastNode = null;
        } else if (node.equals(lastNode)) {
            lastNode = node.prev;
            if (lastNode != null) {
                lastNode.next = null;
            }
        } else if (node.equals(startNode)) {
            startNode = node.next;
            if (startNode != null) {
                startNode.prev = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;
        nodeCnt--;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Node node = new Node(task);

        removeNode(history.remove(task.getId()));
        history.put(task.getId(), node);
        linkLast(node);
    }

    @Override
    public void remove(int id) {
        removeNode(history.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
