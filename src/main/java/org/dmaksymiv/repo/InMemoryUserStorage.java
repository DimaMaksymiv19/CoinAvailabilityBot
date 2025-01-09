package org.dmaksymiv.repo;

import java.util.HashSet;
import java.util.Set;

public class InMemoryUserStorage implements UserRepository {

    private static final InMemoryUserStorage INSTANCE = new InMemoryUserStorage();

    private final Set<Long> chatIds = new HashSet<>();

    private InMemoryUserStorage() {
    }

    public static InMemoryUserStorage getInstance() {
        return INSTANCE;
    }

    @Override
    public void addChatId(long chatId) {
        chatIds.add(chatId);
    }

    @Override
    public Set<Long> getChatIds() {
        return new HashSet<>(chatIds);
    }

    @Override
    public void removeChatId(long chatId) {
        chatIds.remove(chatId);
    }
}