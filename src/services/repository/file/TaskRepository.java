package services.repository.file;

import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TaskRepository {
    private final File file;
    private final String firstRow = "type,id,name,status,description,epic";
    private final Map<String, Integer> keys;

    public TaskRepository(File file) {
        this.file = file;
        this.keys = new HashMap<>();
        String[] columns = firstRow.split(",");
        for (int i = 0; i < columns.length; i++) {
            keys.put(columns[i], i);
        }
    }

    public void add(Task task) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(Type.TASK.name());
        joiner.add(task.getId().toString());
        joiner.add(task.getTitle());
        joiner.add(task.getStatus().name());
        joiner.add(task.getDescription());
        joiner.add("");
        write(joiner.toString());
    }

    public void add(Subtask subtask) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(Type.SUBTASK.name());
        joiner.add(subtask.getId().toString());
        joiner.add(subtask.getTitle());
        joiner.add(subtask.getStatus().name());
        joiner.add(subtask.getDescription());
        joiner.add(subtask.getEpicId().toString());
        write(joiner.toString());
    }

    public void add(Epic epic) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(Type.EPIC.name());
        joiner.add(epic.getId().toString());
        joiner.add(epic.getTitle());
        joiner.add(epic.getStatus().name());
        joiner.add(epic.getDescription());
        joiner.add("");
        write(joiner.toString());
    }

    public List<Task> findAllTasks() {
        List<Task> result = new ArrayList<>();

        for (String row : read(Type.TASK)) {
            String[] columns = row.split(",");
            Task task = new Task(
                    Integer.valueOf(columns[keys.get("id")]),
                    columns[keys.get("name")],
                    columns[keys.get("description")],
                    Status.valueOf(columns[keys.get("status")])
            );
            result.add(task);
        }

        return result;
    }

    public List<Subtask> findAllSubtasks() {
        List<Subtask> result = new ArrayList<>();

        for (String row : read(Type.SUBTASK)) {
            String[] columns = row.split(",");
            Subtask subtask = new Subtask(
                    columns[keys.get("name")],
                    columns[keys.get("description")],
                    Status.valueOf(columns[keys.get("status")])
            );
            subtask.setId(Integer.valueOf(columns[keys.get("id")]));
            subtask.setEpicId(Integer.valueOf(columns[keys.get("epic")]));
            result.add(subtask);
        }

        return result;
    }

    public List<Epic> findAllEpics() {
        List<Epic> result = new ArrayList<>();
        List<Subtask> subtasks = findAllSubtasks();
        Map<Integer, List<Integer>> epicLists = new HashMap<>();

        for (Subtask subtask : subtasks) {
            List<Integer> epicList = epicLists.get(subtask.getEpicId());
            if (epicList == null) {
                epicList = new ArrayList<>();
                epicLists.put(subtask.getEpicId(), epicList);
            }

            epicList.add(subtask.getId());
        }

        for (String row : read(Type.EPIC)) {
            String[] columns = row.split(",");
            Epic epic = new Epic(
                    columns[keys.get("name")],
                    columns[keys.get("description")],
                    Status.valueOf(columns[keys.get("status")])
            );
            epic.setId(Integer.valueOf(columns[keys.get("id")]));
            if (epicLists.get(epic.getId()) != null) {
                epic.getSubtaskIds().addAll(epicLists.get(epic.getId()));
            }
            result.add(epic);
        }

        return result;
    }

    private List<String> read(Type type) {
        List<String> result = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            if (fileReader.ready()) {
                fileReader.readLine();
            }

            while (fileReader.ready()) {
                String row = fileReader.readLine().strip();
                if (row.startsWith(type.name())) {
                    result.add(row);
                }
            }

        } catch (FileNotFoundException exception) {
            throw new ManagerSaveException("Файл не найден или не удалось создать", exception);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл", exception);
        }

        return result;
    }

    private void write(String row) {
        boolean append = file.isFile();
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, append)) {
            if (!append) {
                fileWriter.write(firstRow + "\n");
            }
            fileWriter.write(row + "\n");
        } catch (FileNotFoundException exception) {
            throw new ManagerSaveException("Файл не найден или не удалось создать", exception);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл", exception);
        }
    }
}
