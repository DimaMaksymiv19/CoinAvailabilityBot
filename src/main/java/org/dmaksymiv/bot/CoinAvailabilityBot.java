package org.dmaksymiv.bot;

import org.dmaksymiv.repo.InFileUserStorage;
import org.dmaksymiv.repo.UserRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoinAvailabilityBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(CoinAvailabilityBot.class);

    // Singleton instance
    private static volatile CoinAvailabilityBot instance;

    // Token for the bot
    private static String token;

    private CoinAvailabilityBot(String token) {
        super(token);
    }

    // Factory method to get the singleton instance
    public static CoinAvailabilityBot getInstance(String botToken) {
        if (instance == null) {
            synchronized (CoinAvailabilityBot.class) {
                if (instance == null) {
                    token = botToken; // Assign botToken only during initialization
                    instance = new CoinAvailabilityBot(botToken);
                }
            }
        }
        return instance;
    }


    public  String getToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            UserRepository repository = new InFileUserStorage();

            if (userMessage.equalsIgnoreCase("/start")) {
                sendResponse(chatId, "Привіт! \nНадішли команду /check, щоб отримати список монет з датами їх появи у продажу.\nНадішли команду /subscribe, щоб отримувати автоматичне сповіщення про майбутні монети\nНадішли команду /unsubscribe, щоб скасувати автоматичне сповіщення про майбутні монети");
            } else if (userMessage.equalsIgnoreCase("/check")) {
                String response = getUnavailableCoins();
                sendResponse(chatId, response);
            } else if (userMessage.equalsIgnoreCase("/subscribe")) {
                repository.addChatId(chatId);
                sendResponse(chatId, "Ви підписані на щоденні сповіщення.");
            } else if (userMessage.equalsIgnoreCase("/unsubscribe")) {
                repository.removeChatId(chatId);
                sendResponse(chatId, "Ви відписались від щоденних сповіщень.");
            } else {
                sendResponse(chatId, "Невідома команда. Спробуй /check.");
            }
        }
    }

    public void sendResponse(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
            log.debug("Response sent. Message: {}", text);
        } catch (TelegramApiException e) {
            log.error("Error during sending response. Error message: {}", e.getMessage());
        }
    }

    public static String getUnavailableCoins() {
        String url = "https://coins.bank.gov.ua/catalog.html";
        List<String> unavailableCoins = new ArrayList<>();

        try {
            Document document = Jsoup.connect(url).get();
            Elements items = document.select(".col_product.col_product_bank");
            log.debug("Items count: {}", items.size());
            int itemNumber = 0;

            for (Element item : items) {
                String title = item.select(".model_product").text();
                String status = item.select(".label3.product_label").text();
                String price = item.select(".new_price").text();

                if (status.contains("у продажу") || status.contains("наступний продаж у")) {
                    itemNumber++;
                    unavailableCoins.add(String.format("%s. \"%s\" - %s, %s\n", itemNumber, title, status, price));
                }
            }
            log.debug("Unavailable coins count: {}", unavailableCoins.size());
        } catch (IOException e) {
            log.error("Error during getting unavailable coins. Error message: {}", e.getMessage());
            return "Не вдалося отримати дані з сайту.";
        }

        if (unavailableCoins.isEmpty()) {
            return "Немає монет з датами появи.";
        } else {
            return String.join("\n", unavailableCoins);
        }
    }

    @Override
    public String getBotUsername() {
        return "CoinAvailabilityBot";
    }
}