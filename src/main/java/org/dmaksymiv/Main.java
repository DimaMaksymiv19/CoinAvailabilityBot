package org.dmaksymiv;

import io.github.cdimascio.dotenv.Dotenv;
import org.dmaksymiv.bot.CoinAvailabilityBot;
import org.dmaksymiv.notification.SchedulerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.load();
            String botToken = dotenv.get("BOT_TOKEN");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(CoinAvailabilityBot.getInstance(botToken));
        } catch (TelegramApiException e) {
            log.error("Error during bot registration. Error message: {}", e.getMessage());
        }
        SchedulerConfig.startDailyNotification();
    }

}
