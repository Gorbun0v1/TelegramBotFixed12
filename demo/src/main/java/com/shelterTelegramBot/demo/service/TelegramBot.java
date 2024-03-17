package com.shelterTelegramBot.demo.service;

import com.shelterTelegramBot.demo.configuration.BotConfiguration;
import com.shelterTelegramBot.demo.entity.PetEntity;
import com.shelterTelegramBot.demo.entity.ShelterEntity;
import com.shelterTelegramBot.demo.entity.UserEntity;
import com.shelterTelegramBot.demo.repository.*;
import com.shelterTelegramBot.demo.repository.PetRepository;
import com.shelterTelegramBot.demo.utils.ButtonsNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
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
import java.util.Optional;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final UserRepository userRepository;
    private final BotConfiguration configuration;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;
    private final RuleRepository ruleRepository;

    public TelegramBot(UserRepository userRepository,
                       BotConfiguration configuration,
                       ShelterRepository shelterRepository,
                       PetRepository petRepository,
                       RuleRepository ruleRepository) {
        super(configuration.getToken());
        this.userRepository = userRepository;
        this.configuration = configuration;
        this.shelterRepository = shelterRepository;
        this.petRepository = petRepository;
        this.ruleRepository = ruleRepository;
    }

    /**
     * Основной метод взаимодействия бота с пользователем
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();
            String text = update.getMessage().getText();
            UserEntity userEntity = new UserEntity().setChatId(chatId).setName(name);
            if (userRepository.findByChatId(chatId).isPresent()) {
                sendMessage(chatId, "И снова здравствуйте!" + userEntity.getName());
            } else {
                sendMessage(chatId, "Здравствуйте!" + userEntity.getName());
                userRepository.save(userEntity);
            }
            setStarMenuBot(chatId, "Главное меню");
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            String callBackData = update.getCallbackQuery().getData();
            if (callBackData.equals(ButtonsNames.INFO_ABOUT_SHELTER_BUTTON_DATA)) {
                setSheltersMenuBot(chatId, "Приюты: ");
            }
            else if (callBackData.contains("SHELTERS")) {
                Long shelterId = Long.parseLong(callBackData.split("_")[0]);
                setShelterInfoMenu(chatId, "Что Вы хотите узнать?", shelterId);
            } else if (callBackData.contains("INFO")) {
                getInfoByShelterId(chatId, callBackData);
            }
            if (callBackData.equals(ButtonsNames.GET_PET_FROM_SHELTER_BUTTON_DATA)) {
                getAllPetsMenu(chatId, "Наши питомцы");
            } else if (callBackData.contains("PETS")) {
                Long petId = Long.parseLong(callBackData.split("_")[0]);
                setPetInfoMenu(chatId, "Что хотите узнать?", petId);
            } else if (callBackData.contains("PET_INFO")) {
                getInfoByPetId(chatId, callBackData);
            }
        }
    }

    /**
     * Меню отображения животных
     * @param chatId ID чата
     * @param text текст кнопок
     */
    private void getPetMenu(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.PET_NAME_BUTTON_NAME, ButtonsNames.PET_NAME_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_AGE_BUTTON_NAME, ButtonsNames.PET_AGE_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 1);
    }

    /**
     * Метод вывода всех животных приюта
     * <br/>
     * По циклу <b>информация из репозитория БД</b> {@link PetRepository} преобразуется в сущность {@link PetEntity}, где затем кладется в коллекцию {@link List}
     * <br/>
     * В цикле путем добавления из БД информации, создается меню <b>кнопок</b> {@code setKeyboard(chatId, text, lists, 2);}
     * @param chatId ID чата
     * @param text текст кнопок с информацией о питомце
     */
    private void getAllPetsMenu(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        for (PetEntity petEntity : petRepository.findAll()) {
            lists.add(List.of(petEntity.getBreed() + " " + " возраст: " + petEntity.getAge() + " года ", petEntity.getId() + "_PETS_" + "BUTTON"));
        }
        setKeyboard(chatId, text, lists, 2);
    }

    /**
     * Метод в котором информация о питомце ищется путем резделения строки и поиску по информации ID {@code Long petId = Long.parseLong(callBackData.split("_")[0])}
     *
     * @param chatId ID чата
     * @param callBackData аргумент (нажатая кнопка)
     */
    private void getInfoByPetId(Long chatId, String callBackData) {
        Long petId = Long.parseLong(callBackData.split("_")[0]);
//        System.out.println(petId.getClass().getName());
        Optional<PetEntity> pet = petRepository.findById(petId);
        if (pet.isPresent()) {
            if (callBackData.contains(ButtonsNames.PET_NAME_BUTTON_DATA)) {
                sendMessage(chatId, pet.get().getName());
            } else if (callBackData.contains(ButtonsNames.PET_AGE_BUTTON_DATA)) {
                sendMessage(chatId, String.valueOf(pet.get().getAge()));
            } else if (callBackData.contains(ButtonsNames.PET_BREED_BUTTON_DATA)) {
                sendMessage(chatId, pet.get().getBreed());
            } else if (callBackData.contains(ButtonsNames.PET_COMMENT_BUTTON_DATA)) {
                sendMessage(chatId, pet.get().getComment());
            }
        }
    }

    /**
     * Метод создания кнопок
     * @param chatId ID чата
     * @param text текст информации о питомце
     * @param petId ID питомца
     */
    private void setPetInfoMenu(Long chatId, String text, Long petId) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.PET_NAME_BUTTON_NAME, petId + "_" + ButtonsNames.PET_NAME_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_AGE_BUTTON_NAME, petId + "_" + ButtonsNames.PET_AGE_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_BREED_BUTTON_NAME, petId + "_" + ButtonsNames.PET_BREED_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.PET_COMMENT_BUTTON_NAME, petId + "_" + ButtonsNames.PET_COMMENT_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 2);
    }

    /**
     * Метод в котором информация о приюте ищется путем резделения строки и поиску по информации ID {@code Long petId = Long.parseLong(callBackData.split("_")[0])}
     * @param chatId ID чата
     * @param callBackData аргумент (нажатая кнопка)
     */
    private void getInfoByShelterId(Long chatId, String callBackData) {
        Long shelterId = Long.parseLong(callBackData.split("_")[0]);
        Optional<ShelterEntity> shelter = shelterRepository.findById(shelterId);
        if (shelter.isPresent()) {
            if (callBackData.contains(ButtonsNames.SCHEDULE_BUTTON_DATA)) {
                sendMessage(chatId, shelter.get().getSchedule());
            } else if (callBackData.contains(ButtonsNames.DRIVING_DIRECTIONS_BUTTON_DATA)) {
                sendMessage(chatId, shelter.get().getDrivingDirections());
            } else if (callBackData.contains(ButtonsNames.GUARD_DETAILS_BUTTON_DATA)) {
                sendMessage(chatId, shelter.get().getGuardDetails());
            } else if (callBackData.contains(ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_DATA)) {
                sendMessage(chatId, shelter.get().getSafetyPrecautions());
            }
        }
    }

    /**
     * Метод создания клавиатуры бота
     * @param chatId ID чата
     * @param text текс с информацией кнопки
     * @param buttonsInfo список кнопок
     * @param amountOfRows количество строк кнопок
     */
    private void setKeyboard(Long chatId, String text, List<List<String>> buttonsInfo, int amountOfRows) {
        //Создание кнопок
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(); //разметка кнопок
        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //список строк
        List<InlineKeyboardButton> row = new ArrayList<>(); //первый ряд кнопок
        for (int i = 0; i < buttonsInfo.size(); i++) {
            if (amountOfRows == row.size()) {
                rows.add(row);
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonsInfo.get(i).get(0));
            button.setCallbackData(buttonsInfo.get(i).get(1)); // Узнать информацию о приюте
            row.add(button);
        }
        //добавление в ряд
        rows.add(row);
        //добавляю все ряды в Markup
        keyboard.setKeyboard(rows);
        sendMessageWithKeyboard(chatId, text, keyboard);
    }

    /**
     * Метод вывода всех животных приюта
     * <br/>
     * По циклу <b>информация из репозитория БД</b> {@link ShelterRepository} преобразуется в сущность {@link ShelterEntity}, где затем кладется в коллекцию {@link List}
     * <br/>
     * В цикле путем добавления из БД информации, создается меню <b>кнопок</b> {@code setKeyboard(chatId, text, lists, 1);}
     * @param chatId
     * @param text
     */
    private void setSheltersMenuBot(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        for (ShelterEntity shelterEntity : shelterRepository.findAll()) {
            lists.add(List.of(shelterEntity.getName(), shelterEntity.getId() + "_SHELTERS_" + "BUTTON"));
        }
        setKeyboard(chatId, text, lists, 1);
    }
    /**
     * Метод создания кнопок
     * @param chatId ID чата
     * @param text текст информации о приюте
     * @param shelterId ID приюта
     */
    private void setShelterInfoMenu(Long chatId, String text, Long shelterId) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.SCHEDULE_BUTTON_NAME, shelterId + "_" + ButtonsNames.SCHEDULE_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.DRIVING_DIRECTIONS_BUTTON_NAME, shelterId + "_" + ButtonsNames.DRIVING_DIRECTIONS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.GUARD_DETAILS_BUTTON_NAME, shelterId + "_" + ButtonsNames.GUARD_DETAILS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_NAME, shelterId + "_" + ButtonsNames.SAFETY_PRECAUTIONS_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 2);
    }

    /**
     * Метод создания стартового меню с кнопками
     * @param chatId ID чата
     * @param text текст информации кнопок
     */
    private void setStarMenuBot(Long chatId, String text) {
        List<List<String>> lists = new ArrayList<>();
        lists.add(List.of(ButtonsNames.INFO_ABOUT_SHELTER_BUTTON_NAME, ButtonsNames.INFO_ABOUT_SHELTER_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.GET_PET_FROM_SHELTER_BUTTON_NAME, ButtonsNames.GET_PET_FROM_SHELTER_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.SEND_REPORT_PETS_BUTTON_NAME, ButtonsNames.SEND_REPORT_PETS_BUTTON_DATA));
        lists.add(List.of(ButtonsNames.CALL_VOLUNTEER_BUTTON_NAME, ButtonsNames.CALL_VOLUNTEER_BUTTON_DATA));
        setKeyboard(chatId, text, lists, 2);
    }

    /**
     * Метод создания кливиатуры
     * @param chatId ID чата
     * @param text Текст кнопок
     * @param markup разметка кнопок
     */
    private void sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markup);
        executeMessage(chatId, text, sendMessage);
    }


    private void executeMessage(Long chatId, String text, SendMessage sendMessage) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Long chatId, String text) {
        executeMessage(chatId, text, new SendMessage());
    }

    @Override
    public String getBotUsername() {
        return configuration.getName();
    }

}
