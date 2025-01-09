package org.dmaksymiv.notification;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.TimeZone;

public class SchedulerConfig {

    private static final String timeZoneId = "Etc/GMT+8";
    private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);

    public static void startDailyNotification() {
        try {
            JobDetail job = JobBuilder.newJob(DailyNotificationJob.class)
                    .withIdentity("dailyNotificationJob", "group1")
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("dailyTrigger", "group1")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(9, 0)
                            .inTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZoneId))))
                    .build();

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            log.info("Notification job scheduled successfully job: {}", job.getKey());
        } catch (SchedulerException e) {
            log.error("Error during scheduling job. Error message: {}", e.getMessage());
        }
    }
}
