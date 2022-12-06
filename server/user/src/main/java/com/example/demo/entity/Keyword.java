package com.example.demo.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "keyword_id")
    private Long id;
    private String word;

//    @BatchSize(size = 100)
//    @OneToMany(mappedBy ="deal", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    @OrderBy("id asc")
//    private List<DealKeyword> keywords;
}