package com.shelterTelegramBot.demo.repository;

import com.shelterTelegramBot.demo.entity.ShelterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<ShelterEntity, Long> {
}
