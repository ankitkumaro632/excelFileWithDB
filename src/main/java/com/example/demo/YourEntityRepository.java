package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface YourEntityRepository extends JpaRepository<YourEntity, Long> {
}