package com.damian.yandexdisk.store.repositories;

import com.damian.yandexdisk.store.entities.Lecture;
import com.damian.yandexdisk.store.entities.Practice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticeRepo extends JpaRepository<Practice, Long> {
    void deleteByFileName(String fileName);
}
