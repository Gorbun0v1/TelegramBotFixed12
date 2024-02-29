package com.shelterTelegramBot.demo.service;

import com.shelterTelegramBot.demo.configuration.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    public TelegramBot(BotConfiguration configuration) {
        super(configuration.getToken());
        this.configuration = configuration;
    }

    private final BotConfiguration configuration;
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                sendMessage(chatId, "Добро пожаловать " + name);
                setStarMenuBot(chatId, "General Menu");
            }
        }
    }

    //**** ВЫНЕСЫ В ОТДЕЛЬНЫЙ МЕТОД ОТПРАВКУ СООБЩЕНИЯ SEND MESSAGE

    private void setKeyboard(Long chatId, String text, List<List<String>> buttonsInfo, int amountOfRows) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(); //разметка кнопок
        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //список строк
        List<InlineKeyboardButton> row = new ArrayList<>(); //первый ряд кнопок
        for (int i = 0; i < buttonsInfo.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonsInfo.get(i).get(0));
            button.setCallbackData(buttonsInfo.get(i).get(1)); // Узнать информацию о приюте
            row.add(button);
            //****ДОБАВИТЬ РЯД ПОДУМАЙ АНТОН
        }
        rows.add(row);
        keyboard.setKeyboard(rows);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        sendMessage.setReplyMarkup(keyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void setStarMenuBot(Long chatId, String text) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(); //разметка кнопок

        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //список строк
        List<InlineKeyboardButton> row = new ArrayList<>(); //первый ряд кнопок
        List<InlineKeyboardButton> row2 = new ArrayList<>(); //первый ряд кнопок

        InlineKeyboardButton getInfoAboutShelter = new InlineKeyboardButton( );
        getInfoAboutShelter.setText("Информацию о приюте");
        getInfoAboutShelter.setCallbackData("INFO_ABOUT_SHELTER_BUTTON"); // Узнать информацию о приюте

        InlineKeyboardButton getPetFromShelter = new InlineKeyboardButton( );
        getPetFromShelter.setText("Взять животное");
        getPetFromShelter.setCallbackData("GET_PET_FROM_SHELTER_BUTTON"); // Узнать информацию о приюте

        InlineKeyboardButton sendReportPets = new InlineKeyboardButton( );
        sendReportPets.setText("Отчет о питомце");
        sendReportPets.setCallbackData("SEND_REPORT_PETS_BUTTON"); // Узнать информацию о приюте

        InlineKeyboardButton callVolunteer = new InlineKeyboardButton( );
        callVolunteer.setText("Позвать волонтера");
        callVolunteer.setCallbackData("CALL_VOLUNTEER_BUTTON"); // Узнать информацию о приюте

        row.add(getInfoAboutShelter);
        row.add(getPetFromShelter);
        row2.add(sendReportPets);
        row2.add(callVolunteer);

        rows.add(row);
        rows.add(row2);

        keyboard.setKeyboard(rows);



        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);



        sendMessage.setReplyMarkup(keyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }



    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return configuration.getName();
    }

}
