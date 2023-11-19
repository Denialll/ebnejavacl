package com.damian.yandexdisk.store.repositories;

import com.damian.yandexdisk.store.entities.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LectureRepo extends JpaRepository<Lecture, Long> {

    @Transactional
    void deleteByFileName(String fileName);

    Optional<Lecture> findById(long id);
}
