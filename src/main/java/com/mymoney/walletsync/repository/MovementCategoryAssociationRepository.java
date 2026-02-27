package com.mymoney.walletsync.repository;

import com.mymoney.walletsync.model.common.entity.MovementCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementCategoryAssociationRepository extends JpaRepository<MovementCategoryAssociation, Long> {
}
