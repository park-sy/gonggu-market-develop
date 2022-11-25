package com.gonggu.deal.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class DealImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fileName;
//    private String newFileName;
//    private String filePath;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean thumbnail;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "deal_id")
    private Deal deal;

    public DealImage(Deal deal, String o) {
        this.fileName = o;
        this.deal = deal;
    }
}
