package com.damian.yandexdisk.service;

import com.damian.yandexdisk.store.entities.Lecture;
import com.damian.yandexdisk.store.entities.Task;
import com.damian.yandexdisk.store.repositories.LectureRepo;
import com.damian.yandexdisk.store.repositories.TaskRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskService {

    TaskRepo taskRepo;

    public void saveTask(Long id, String fileName, Date deadline, String link, String cache, String documentId) {

        taskRepo.save(Task.builder()
                .id(id)
                .fileName(fileName)
                .deadline(deadline)
                .link(link)
                .cache(cache)
                .documentId(documentId)
                .build());
    }

    public List<Task> fetchAll() {

        return taskRepo.findAll();
    }

    public void removeTask(String fileName) {

        taskRepo.deleteByFileName(fileName);
    }

    public Optional<Task> fetchById(Long id) {

        return taskRepo.findById(id);
    }
}