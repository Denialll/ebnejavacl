package com.damian.yandexdisk.service;

import com.damian.yandexdisk.store.entities.Lecture;
import com.damian.yandexdisk.store.entities.Practice;
import com.damian.yandexdisk.store.repositories.LectureRepo;
import com.damian.yandexdisk.store.repositories.PracticeRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PracticeService {

    PracticeRepo practiceRepo;

    public void savePractice(Long id, int year, String fileName, String link, String cache, String documentId) {

        practiceRepo.save(Practice.builder()
                .id(id)
                .year(year)
                .fileName(fileName)
                .link(link)
                .cache(cache)
                .documentId(documentId)
                .build());
    }

    public List<Practice> fetchAll() {

        return practiceRepo.findAll();
    }

    public void removePractice(String fileName) {

        practiceRepo.deleteByFileName(fileName);
    }

    public Optional<Practice> fetchById(Long id) {

        return practiceRepo.findById(id);
    }
}