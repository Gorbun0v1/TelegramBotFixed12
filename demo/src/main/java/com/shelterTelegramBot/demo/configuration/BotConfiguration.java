package com.shelterTelegramBot.demo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfiguration {
    @Value("${telegram.bot.token}")
    String token;
    @Value("${telegram.bot.name}")
    String name;
}
