package com.damian.yandexdisk.store.repositories;

import com.damian.yandexdisk.store.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface TaskRepo extends JpaRepository<Task, Long> {
    void deleteByFileName(String fileName);
}
