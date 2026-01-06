package com.vietanh.webmanh.dbs.mongo.models;

import java.util.ArrayList;
import java.util.List;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "reading_history") // chọn mongodb vì query mạnh trên array
public class ReadingHistory {
    @Id
    String id;

    @Indexed
    Integer userId;

    @Indexed
    Integer comicId;

    @Builder.Default
    List<String> readChapters = new ArrayList<>();
}