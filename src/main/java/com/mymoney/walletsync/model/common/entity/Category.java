package com.mymoney.walletsync.model.common.entity;

import com.mymoney.walletsync.model.common.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
}
