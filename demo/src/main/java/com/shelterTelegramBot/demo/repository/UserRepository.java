package com.shelterTelegramBot.demo.repository;

import com.shelterTelegramBot.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity getByChatId(Long chatId);

    Optional<UserEntity> findByChatId(Long chatId);
}
