package com.mymoney.walletsync.repository;

import com.mymoney.walletsync.model.common.entity.Category;
import com.mymoney.walletsync.model.common.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCategoryType(CategoryType categoryType);

}

