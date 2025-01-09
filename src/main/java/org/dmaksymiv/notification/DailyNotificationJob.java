package org.dmaksymiv.notification;

import io.github.cdimascio.dotenv.Dotenv;
import org.dmaksymiv.bot.CoinAvailabilityBot;
import org.dmaksymiv.repo.InFileUserStorage;
import org.dmaksymiv.repo.UserRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DailyNotificationJob implements Job {


    private static final Logger log = LoggerFactory.getLogger(DailyNotificationJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        String coinsList = CoinAvailabilityBot.getUnavailableCoins();
        UserRepository repository = new InFileUserStorage();

        Set<Long> chatIds = repository.getChatIds();
        Dotenv dotenv = Dotenv.load();
        String botToken = dotenv.get("BOT_TOKEN");

        CoinAvailabilityBot bot = CoinAvailabilityBot.getInstance(botToken);
        for (long chatId : chatIds) {
            bot.sendResponse(chatId, coinsList);
        }
        log.info("Scheduled message successfully sent to {} users", chatIds.size());
    }
}
