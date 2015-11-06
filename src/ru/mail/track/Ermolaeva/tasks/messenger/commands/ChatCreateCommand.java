package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.command_message.ChatCreateMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;

import java.util.ArrayList;
import java.util.List;

public class ChatCreateCommand extends MessengerCommand<ChatCreateMessage> {
    private MessageStore messageStore;
    private Store userStore;

    public ChatCreateCommand(MessageStore messageStore, Store userStore) {
        commandType = CommandType.CHAT_CREATE;
        description = "/chat_create <user_id list> - create chat(or dialogue) with users from <user_id list>";
        this.messageStore = messageStore;
        this.userStore = userStore;
    }

    @Override
    protected Result executeCommand(Session session, ChatCreateMessage commandMessage) {
        List<Long> usersId = commandMessage.getUserIdList();
        for (Long userId : usersId) {
            if (userStore.getUserById(userId) == null) {
                return new CommandResult("No user with id " + userId, true);
            }
        }

        if (usersId.size() == 1) {
            if (usersId.get(0).equals(session.getCurrentUserId())) {
                return new CommandResult("You can't create chat with yourself!", true);
            }
            for (Long id : messageStore.getChatsByUserId(session.getCurrentUserId())) {
                Chat chat = messageStore.getChatById(id);
                List<Long> participants = chat.getUsersList();
                if (participants.size() == 2
                        && participants.contains(usersId.get(0))) {
                    return new CommandResult(chat.getInfo());
                }
            }
            Chat dialogue = new Chat();
            messageStore.addChat(dialogue);
            List<Long> usersList = new ArrayList<>();
            usersList.add(session.getCurrentUserId());
            usersList.add(usersId.get(0));
            for (Long user : usersList) {
                messageStore.addUserToChat(user, dialogue.getId());
            }
            return new CommandResult(dialogue.getInfo());
        } else {
            Chat chat = new Chat();
            List<Long> usersList = new ArrayList<>();
            for (Long id : usersId) {
                if (!usersList.contains(id)) {
                    usersList.add(id);
                }
            }
            usersList.add(session.getCurrentUserId());

            messageStore.addChat(chat);
            for (Long user : usersList) {
                messageStore.addUserToChat(user, chat.getId());
            }
            return new CommandResult(chat.getInfo());
        }
    }
}