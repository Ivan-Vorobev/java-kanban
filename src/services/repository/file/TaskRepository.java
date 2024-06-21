package services.repository.file;

import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class TaskRepository {
    private final File file;
    private final String firstRow = "type,id,name,status,description,epic,duration,startTime,endTime";
    private final Map<String, Integer> keys;
    private boolean writeModeAppend;

    public TaskRepository(File file, boolean rewriteData) {
        this.file = file;
        writeModeAppend = rewriteData;
        this.keys = new HashMap<>();
        String[] columns = firstRow.split(",");
        for (int i = 0; i < columns.length; i++) {
            keys.put(columns[i], i);
        }
    }

    public void add(Task task) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(TaskType.TASK.name());
        joiner.add(task.getId().toString());
        joiner.add(task.getTitle());
        joiner.add(task.getStatus().name());
        joiner.add(task.getDescription());
        joiner.add("");
        joiner.add(Long.toString(task.getDuration().toMinutes()));
        joiner.add(prepareStartTime(task));
        joiner.add("");
        write(joiner.toString());
    }

    public void add(Subtask subtask) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(TaskType.SUBTASK.name());
        joiner.add(subtask.getId().toString());
        joiner.add(subtask.getTitle());
        joiner.add(subtask.getStatus().name());
        joiner.add(subtask.getDescription());
        joiner.add(subtask.getEpicId().toString());
        joiner.add(Long.toString(subtask.getDuration().toMinutes()));
        joiner.add(prepareStartTime(subtask));
        joiner.add("");
        write(joiner.toString());
    }

    public void add(Epic epic) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(TaskType.EPIC.name());
        joiner.add(epic.getId().toString());
        joiner.add(epic.getTitle());
        joiner.add(epic.getStatus().name());
        joiner.add(epic.getDescription());
        joiner.add("");
        joiner.add(Long.toString(epic.getDuration().toMinutes()));
        joiner.add(prepareStartTime(epic));
        joiner.add(prepareEndTime(epic));
        write(joiner.toString());
    }

    private String prepareStartTime(Task task) {
        return task.getStartTime() == null ? "" : task.getStartTime().toString();
    }

    private String prepareEndTime(Task task) {
        if (task.getStartTime() == null) {
            return "";
        }

        return task.getEndTime().toString();
    }

    public List<Task> findAllTasks() {
        List<Task> result = new ArrayList<>();

        for (String row : read(TaskType.TASK)) {
            String[] columns = row.split(String.valueOf(','), keys.size());
            Task task = new Task(
                    Integer.valueOf(columns[keys.get("id")]),
                    columns[keys.get("name")],
                    columns[keys.get("description")],
                    Status.valueOf(columns[keys.get("status")]),
                    !columns[keys.get("startTime")].isEmpty()
                            ? Instant.from(LocalDateTime.parse(columns[keys.get("startTime")]))
                            : null,
                    Integer.valueOf(columns[keys.get("duration")])
            );
            result.add(task);
        }

        return result;
    }

    public List<Subtask> findAllSubtasks() {
        List<Subtask> result = new ArrayList<>();

        for (String row : read(TaskType.SUBTASK)) {
            String[] columns = row.split(String.valueOf(','), keys.size());
            Subtask subtask = new Subtask(
                    columns[keys.get("name")],
                    columns[keys.get("description")],
                    Status.valueOf(columns[keys.get("status")]),
                    !columns[keys.get("startTime")].isEmpty()
                            ? Instant.from(LocalDateTime.parse(columns[keys.get("startTime")]))
                            : null,
                    Integer.valueOf(columns[keys.get("duration")])
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

        for (String row : read(TaskType.EPIC)) {
            String[] columns = row.split(String.valueOf(','), keys.size());
            Epic epic = new Epic(
                    columns[keys.get("name")],
                    columns[keys.get("description")],
                    Status.valueOf(columns[keys.get("status")]),
                    !columns[keys.get("startTime")].isEmpty()
                            ? Instant.from(LocalDateTime.parse(columns[keys.get("startTime")]))
                            : null,
                    Integer.valueOf(columns[keys.get("duration")])
            );
            epic.setId(Integer.valueOf(columns[keys.get("id")]));
            Instant endTime = !columns[keys.get("endTime")].isEmpty()
                    ? Instant.from(LocalDateTime.parse(columns[keys.get("endTime")]))
                    : null;
            epic.setEndTime(endTime);
            if (epicLists.get(epic.getId()) != null) {
                epic.getSubtaskIds().addAll(epicLists.get(epic.getId()));
            }
            result.add(epic);
        }

        return result;
    }

    private List<String> read(TaskType taskType) {
        List<String> result = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            if (fileReader.ready()) {
                fileReader.readLine();
            }

            while (fileReader.ready()) {
                String row = fileReader.readLine().strip();
                if (row.startsWith(taskType.name())) {
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
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, writeModeAppend)) {
            if (!writeModeAppend) {
                fileWriter.write(firstRow + "\n");
            }
            fileWriter.write(row + "\n");
        } catch (FileNotFoundException exception) {
            throw new ManagerSaveException("Файл не найден или не удалось создать", exception);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл", exception);
        }
        writeModeAppend = true;
    }
}
