package org.dmaksymiv.repo;

import java.util.Set;

public interface UserRepository {
    void addChatId(long chatId);

    Set<Long> getChatIds();

    void removeChatId(long chatId);
}
