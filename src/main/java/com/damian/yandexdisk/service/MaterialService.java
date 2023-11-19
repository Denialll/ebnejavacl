package com.damian.yandexdisk.service;

import com.damian.yandexdisk.store.entities.Lecture;
import com.damian.yandexdisk.store.entities.Material;
import com.damian.yandexdisk.store.entities.Task;
import com.damian.yandexdisk.store.repositories.LectureRepo;
import com.damian.yandexdisk.store.repositories.MaterialRepo;
import com.damian.yandexdisk.store.repositories.TaskRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MaterialService {

    MaterialRepo materialRepo;
    TaskRepo taskRepo;

    public void saveMaterial(Long id, String name, String link, Long taskId, String cache, String documentId) {

        Optional<Task> task = taskRepo.findById(taskId);

        if (task.isPresent()) {
            try {
                materialRepo.save(Material.builder()
                        .id(id)
                        .fileName(name)
                        .link(link)
                        .task(task.get())
                        .cache(cache)
                        .documentId(documentId)
                        .build());
            } catch (NullPointerException e) {
                System.out.println("Task не сохранился");
                e.printStackTrace();
            }
        }
    }

    public List<Material> fetchAll() {

        return materialRepo.findAll();
    }

    public void removeMaterial(String fileName) {

        materialRepo.deleteByFileName(fileName);
    }

    public Optional<Material> fetchById(Long id) {

        return materialRepo.findById(id);
    }
}