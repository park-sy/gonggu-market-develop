package com.gonggu.deal.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deal_id")
    private Long id;
    private String title;
    private String content;
    private Long price;
    private Long unitPrice;
    private Integer quantity;
    private Integer unitQuantity;
    private Integer totalCount;
    private String unit;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer nowCount;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private String url;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean deletion;

    @BatchSize(size = 100)
    @JsonManagedReference
    @OneToMany(mappedBy ="deal", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<DealImage> images;

    @BatchSize(size = 100)
    @OneToMany(mappedBy ="deal", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<DealKeyword> keywords;

    @BatchSize(size = 100)
    @OneToMany(mappedBy ="deal", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<DealMember> members;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @ManyToOne(fetch = FetchType.EAGER ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id")
    private Category category;

    public DealEditor.DealEditorBuilder toEditor(){
        return DealEditor.builder().content(content);
    }

    public void edit(DealEditor dealEditor){
        content = builder().content;
        deletion = builder().deletion;
    }

    public void editCount(Integer amount){
        this.nowCount = amount;
    }
}
