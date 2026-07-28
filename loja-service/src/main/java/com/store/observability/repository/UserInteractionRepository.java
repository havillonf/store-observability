package com.store.observability.repository;

import com.store.observability.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    long countByAction(String action);

    @Query("SELECT COALESCE(SUM(u.value), 0.0) FROM UserInteraction u WHERE u.action = 'checkout'")
    double sumCheckoutValue();
}
