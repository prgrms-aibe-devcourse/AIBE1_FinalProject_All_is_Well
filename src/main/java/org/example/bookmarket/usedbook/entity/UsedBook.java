package org.example.bookmarket.usedbook.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bookmarket.book.entity.Book;
import org.example.bookmarket.category.entity.Category;
import org.example.bookmarket.user.entity.User;
import org.example.bookmarket.common.TimeEntity;

import java.util.List;

@Entity
@Table(name = "used_books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsedBook extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String conditionGrade;
    private boolean hasWriting;
    private boolean hasStains;
    private boolean hasTears;
    private boolean hasWaterDamage;
    private boolean likeNew;
    private String detailedCondition;

    private Integer sellingPrice;


    private Integer aiSuggestedMinPrice;
    private Integer aiSuggestedMaxPrice;

    @ElementCollection(fetch = FetchType.LAZY) // 결함 키워드 리스트를 저장하기 위한 설정
    @CollectionTable(name = "used_book_defects", joinColumns = @JoinColumn(name = "used_book_id"))
    @Column(name = "defect")
    private List<String> aiDetectedDefects;


    private String status;

    @OneToMany(mappedBy = "usedBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsedBookImage> images;
}