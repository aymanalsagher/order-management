package com.github.aymanalsagher.orderservice.repository;

import com.github.aymanalsagher.orderservice.model.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Ordering, Long> {
}
