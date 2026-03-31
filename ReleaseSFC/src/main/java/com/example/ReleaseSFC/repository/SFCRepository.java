package com.example.ReleaseSFC.repository;

import com.example.ReleaseSFC.entity.SFC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SFCRepository extends JpaRepository<SFC, Long> {
}