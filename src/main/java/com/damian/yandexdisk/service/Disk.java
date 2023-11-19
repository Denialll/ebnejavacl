package com.damian.yandexdisk.service;

import com.damian.yandexdisk.store.entities.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class Disk {

    @Value(value = "${Authorization}")
    @NonFinal
    String authorization;
    RestTemplate restTemplate = new RestTemplate();

    LectureService lectureService;
    MaterialService materialService;
    PracticeService practiceService;
    TaskService taskService;
    QuestionService questionService;

    ObjectMapper objectMapper;

    // Метод, который добавляет файлы и здиска в бд
    public void checkNewFiles() {

        JsonNode json = connectDisk("");

        if (json == objectMapper.nullNode()) {
            System.out.println("Ошибка в методе check");
            return;
        }

        json = json.get("_embedded").get("items");

        if (!json.isEmpty()) {

            json.forEach((e) -> {

                if (e.get("type").asText().equals("dir")) {

                    String dir = e.get("name").asText();
                    String url = "/" + e.get("name").asText();

                    System.out.println(url);

                    JsonNode jsonSecond = connectDisk(url);

                    if (jsonSecond == objectMapper.nullNode()) {
                        System.out.println("Ошибка в методе check");
                        return;
                    }

                    jsonSecond = jsonSecond.get("_embedded").get("items");

                    for (JsonNode el : jsonSecond) {

                        String name = el.get("name").asText();
                        String file = el.get("file").asText();
                        String cache = el.get("md5").asText();
                        Long id = Long.parseLong(name.substring(0, 1));

                        switch (dir) {
                            case "1)Lectures": {

                                Optional<Lecture> lecture = lectureService.fetchById(id);

                                if (lecture.isEmpty()) {
                                    lectureService.saveLecture(
                                            id,
                                            Integer.parseInt(name.substring(2, 6)),
                                            name,
                                            file,
                                            cache,
                                            null
                                    );
                                } else {
                                    String documentId = cache.equals(lecture.get().getCache()) ? lecture.get().getDocumentId() : null;

                                    lectureService.saveLecture(
                                            id,
                                            Integer.parseInt(name.substring(2, 6)),
                                            name,
                                            file,
                                            cache,
                                            documentId
                                    );
                                }

                                break;
                            }
                            case "2)Practices": {
                                Optional<Practice> practice = practiceService.fetchById(id);

                                if (practice.isEmpty()) {
                                    practiceService.savePractice(
                                            id,
                                            Integer.parseInt(name.substring(2, 6)),
                                            name,
                                            file,
                                            cache,
                                            null
                                    );
                                } else {
                                    String documentId = cache.equals(practice.get().getCache()) ? practice.get().getDocumentId() : null;

                                    practiceService.savePractice(
                                            id,
                                            Integer.parseInt(name.substring(2, 6)),
                                            name,
                                            file,
                                            cache,
                                            documentId
                                    );
                                }
                                break;
                            }
                            case "3)Tasks": {

                                Optional<Task> task = taskService.fetchById(id);

                                int year = Integer.parseInt(name.substring(name.indexOf("-") + 5, name.indexOf(".")));
                                int month = Integer.parseInt(name.substring(name.indexOf("-") + 3, name.indexOf(".") - 4));
                                int dayOfMonth = Integer.parseInt(name.substring(name.indexOf("-") + 1, name.indexOf(".") - 6));

                                if (task.isEmpty()) {

                                    taskService.saveTask(
                                            id,
                                            name,
                                            new Date(new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis()),
                                            file,
                                            cache,
                                            null
                                    );
                                } else {
                                    String documentId = cache.equals(task.get().getCache()) ? task.get().getDocumentId() : null;

                                    taskService.saveTask(
                                            id,
                                            name,
                                            new Date(new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis()),
                                            file,
                                            cache,

                                            documentId
                                    );
                                }
                                break;
                            }
                            case "4)Materials": {

                                Optional<Material> material = materialService.fetchById(id);

                                if (material.isEmpty()) {
                                    materialService.saveMaterial(
                                            id,
                                            name,
                                            file,
                                            Long.valueOf(name.substring(2, 3)),
                                            cache,
                                            null
                                    );
                                } else {
                                    String documentId = cache.equals(material.get().getCache()) ? material.get().getDocumentId() : null;

                                    materialService.saveMaterial(
                                            id,
                                            name,
                                            file,
                                            Long.valueOf(name.substring(2, 3)),
                                            cache,
                                            documentId
                                    );
                                }
                                break;
                            }
                            case "5)Questions": {

                                Optional<Question> question = questionService.fetchById(id);

                                if (question.isEmpty()) {
                                    questionService.saveQuestion(
                                            id,
                                            name,
                                            file,
                                            cache,
                                            null
                                    );
                                } else {
                                    String documentId = cache.equals(question.get().getCache()) ? question.get().getDocumentId() : null;

                                    questionService.saveQuestion(
                                            id,
                                            name,
                                            file,
                                            cache,
                                            documentId
                                    );
                                }
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    //    Метод, который сравнивает базу данных и яндекс диск, и удает файлы из бд, если их нет на диске
    @Transactional
    public void removeOldFiles() {
        JsonNode json = connectDisk("");

        if (json == objectMapper.nullNode()) {
            System.out.println("Ошибка в методе remove");
            return;
        }

        json = json.get("_embedded").get("items");

        if (!json.isEmpty()) {

            json.forEach((e) -> {

                if (e.get("type").asText().equals("dir")) {

                    String dir = e.get("name").asText();

                    switch (dir) {

                        case "1)Lectures": {

                            List<String> lectures = lectureService.fetchAll().stream().map(Lecture::getFileName).collect(Collectors.toList());

                            String url = "/" + e.get("name").asText();

                            JsonNode jsonSecond = connectDisk(url);

                            if (jsonSecond == objectMapper.nullNode()) {
                                System.out.println("Ошибка в методе remove");
                                return;
                            }

                            jsonSecond = jsonSecond.get("_embedded").get("items");

                            for (JsonNode el : jsonSecond) {
                                for (String lecture : lectures) {
                                    if (el.get("name").asText().equals(lecture)) {
                                        lectures.remove(lecture);
                                        break;
                                    }
                                }

                                if (!lectures.isEmpty()) {
                                    lectures.forEach(lectureService::removeLecture);
                                }
                            }
                        }
                        case "2)Practices": {

                            List<String> practices = practiceService.fetchAll().stream().map(Practice::getFileName).collect(Collectors.toList());

                            String url = "/" + e.get("name").asText();

                            JsonNode jsonSecond = connectDisk(url);

                            if (jsonSecond == objectMapper.nullNode()) {
                                System.out.println("Ошибка в методе remove");
                                return;
                            }

                            jsonSecond = jsonSecond.get("_embedded").get("items");

                            for (JsonNode el : jsonSecond) {
                                for (String practice : practices) {
                                    if (el.get("name").asText().equals(practice)) {
                                        practices.remove(practice);
                                        break;
                                    }
                                }

                                if (!practices.isEmpty()) {
                                    practices.forEach(practiceService::removePractice);
                                }
                            }
                        }
                        case "3)Tasks": {

                            List<String> tasks = taskService.fetchAll().stream().map(Task::getFileName).collect(Collectors.toList());

                            String url = "/" + e.get("name").asText();

                            JsonNode jsonSecond = connectDisk(url);

                            if (jsonSecond == objectMapper.nullNode()) {
                                System.out.println("Ошибка в методе remove");
                                return;
                            }

                            jsonSecond = jsonSecond.get("_embedded").get("items");

                            for (JsonNode el : jsonSecond) {
                                for (String task : tasks) {
                                    if (el.get("name").asText().equals(task)) {
                                        tasks.remove(task);
                                        break;
                                    }
                                }

                                if (!tasks.isEmpty()) {
                                    tasks.forEach(taskService::removeTask);
                                }
                            }
                        }
                        case "4)Materials": {

                            List<String> materials = materialService.fetchAll().stream().map(Material::getFileName).collect(Collectors.toList());

                            String url = "/" + e.get("name").asText();

                            JsonNode jsonSecond = connectDisk(url);

                            if (jsonSecond == objectMapper.nullNode()) {
                                System.out.println("Ошибка в методе remove");
                                return;
                            }

                            jsonSecond = jsonSecond.get("_embedded").get("items");

                            for (JsonNode el : jsonSecond) {
                                for (String material : materials) {
                                    if (el.get("name").asText().equals(material)) {
                                        materials.remove(material);
                                        break;
                                    }
                                }

                                if (!materials.isEmpty()) {
                                    materials.forEach(materialService::removeMaterial);
                                }
                            }
                        }
                        case "5)Questions": {

                            List<String> questions = questionService.fetchAll().stream().map(Question::getFileName).collect(Collectors.toList());

                            String url = "/" + e.get("name").asText();

                            JsonNode jsonSecond = connectDisk(url);

                            if (jsonSecond == objectMapper.nullNode()) {
                                System.out.println("Ошибка в методе remove");
                                return;
                            }

                            jsonSecond = jsonSecond.get("_embedded").get("items");

                            for (JsonNode el : jsonSecond) {
                                for (String question : questions) {
                                    if (el.get("name").asText().equals(question)) {
                                        questions.remove(question);
                                        break;
                                    }
                                }

                                if (!questions.isEmpty()) {
                                    questions.forEach(questionService::removeQuestion);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    //    Метод, который подключается к яндекс диску и присылает файлы по какому-то пути


    @SneakyThrows
    public JsonNode connectDisk(String prefix) {

        RequestEntity<Void> request = RequestEntity.get(URI.create("https://cloud-api.yandex.net/v1/disk/resources?path=ЧАТ-БОТ" + prefix))
                .header("Authorization", authorization)
                .build();

        ResponseEntity<JsonNode> json;

//        Проверка на подключение, если оно удачное то возращается тело ответа, иначе nullNode
        try {
            json = restTemplate.exchange(request, JsonNode.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Плохой запрос");
            System.out.println(request);
            e.printStackTrace();
            return objectMapper.nullNode();
        }

        return objectMapper.readTree(Objects.requireNonNull(json.getBody()).toString());
    }

//    public byte[] downloadFile(JsonNode json) {
//
//        byte[] file = new byte[1024 * 12];
//
//        BufferedInputStream in;
//        try {
//
//            in = new BufferedInputStream(new URL(json.get("file").asText()).openStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            in.read(file, 0, file.length);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return file;
//    }
}


