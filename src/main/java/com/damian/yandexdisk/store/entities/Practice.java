package com.damian.yandexdisk.store.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "practices")
public class Practice {

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "link", length = 700, nullable = false)
    String link;

    @Column(name = "file_name")
    String fileName;

    int year;

    String cache;

    @Column(name = "document_id")
    String documentId;
}