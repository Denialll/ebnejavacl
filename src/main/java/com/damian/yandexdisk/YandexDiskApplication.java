package com.damian.yandexdisk;

import com.damian.yandexdisk.service.Disk;
import com.damian.yandexdisk.service.LectureService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class YandexDiskApplication implements CommandLineRunner {

    Disk disk;

    public static void main(String[] args) {
        SpringApplication.run(YandexDiskApplication.class, args);
    }

    @Override
    public void run(String... args) {

        TimerTask task = new TimerTask() {

            @SneakyThrows
            @Override
            public void run() {
                System.out.println("remove");
                disk.removeOldFiles();
                System.out.println("check");
                disk.checkNewFiles();
            }
        };

        Timer timer = new Timer();
        long delay = (1000 * 60 * 60 * 3);
        timer.schedule(task, 0, delay);

    }
}
