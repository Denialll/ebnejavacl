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
@Table(name = "questions")
public class Question{

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "link", length = 700, nullable = false)
    String link;

    @Column(name = "file_name")
    String fileName;

    String cache;

    @Column(name = "document_id")
    String documentId;
}