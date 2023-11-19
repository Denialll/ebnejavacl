package com.damian.yandexdisk.service;

import com.damian.yandexdisk.store.entities.Lecture;
import com.damian.yandexdisk.store.repositories.LectureRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LectureService {

    LectureRepo lectureRepo;

    public void saveLecture(Long id, int year, String fileName, String link, String cache, String documentId) {

        lectureRepo.save(Lecture.builder()
                .id(id)
                .year(year)
                .fileName(fileName)
                .link(link)
                .cache(cache)
                .documentId(documentId)
                .build());
    }

    public List<Lecture> fetchAll() {

        return lectureRepo.findAll();
    }

    public void removeLecture(String fileName) {

        lectureRepo.deleteByFileName(fileName);
    }

    public Optional<Lecture> fetchById(long id) {

        return lectureRepo.findById(id);
    }
}
