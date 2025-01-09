package org.dmaksymiv.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InFileUserStorage implements UserRepository {
    private static final String FILE_PATH = "src/main/resources/subscribers.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(InFileUserStorage.class);

    @Override
    public Set<Long> getChatIds() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                return objectMapper.readValue(file, new TypeReference<>() {
                });
            }
        } catch (IOException e) {
            log.error("Can't read storage file. Error message: {}", e.getMessage());
        }
        return new HashSet<>();
    }

    @Override
    public void addChatId(long chatId) {
        Set<Long> chatIds = getChatIds();
        if (chatIds.add(chatId)) {
            saveChatIds(chatIds);
            log.info("Chat ID - {} saved in file successfully", chatId);
        }
    }

    @Override
    public void removeChatId(long chatId) {
        Set<Long> chatIds = getChatIds();
        if (chatIds.remove(chatId)) {
            saveChatIds(chatIds);
            log.info("Chat ID - {} removed form file successfully", chatId);
        }
    }

    private static void saveChatIds(Set<Long> chatIds) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), chatIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
