package com.viettran.reading_story_web.entity.mysql;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.viettran.reading_story_web.entity.base.BaseEntity;
import com.viettran.reading_story_web.utils.ListToJsonConverterUtil;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "reading-history")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ReadingHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @JsonBackReference
    @ManyToOne
    User user;

    @JsonBackReference
    @ManyToOne
    Story story;

    @Convert(converter = ListToJsonConverterUtil.class)
    @Column(columnDefinition = "json")
    @Builder.Default
    List<String> chaptersRead = new ArrayList<>(); // Lưu dưới dạng ["1", "2", "3"]
}
