package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatCreateMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.AbstractUserDao;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.ArrayList;
import java.util.List;

public class ChatCreateCommand extends ChatCommand<ChatCreateMessage> {
    private AbstractUserDao userStore;

    public ChatCreateCommand(MessageStore messageStore, AbstractUserDao userStore) {
        super(messageStore);
        needPermissionCheck = false;
        commandType = CommandType.CHAT_CREATE;
        description = "/chat_create <user_id list> - create chat(or dialogue) with users from <user_id list>";
        this.userStore = userStore;
    }

    @Override
    protected Result executeCommandChecked(Session session, ChatCreateMessage commandMessage) throws DataAccessException {
        List<Long> usersId = commandMessage.getUserIdList();
        for (Long userId : usersId) {
            if (userStore.getById(userId) == null) {
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

            Long chatId = messageStore.addChatToStore(new Chat());
            List<Long> usersList = new ArrayList<>();
            usersList.add(session.getCurrentUserId());
            usersList.add(usersId.get(0));
            messageStore.addUsersToChat(usersList, chatId);
            return new CommandResult(messageStore.getChatById(chatId).getInfo());
        } else {

            List<Long> usersList = new ArrayList<>();
            usersId.stream().filter(id -> !usersList.contains(id)).forEach(usersList::add);
            usersList.add(session.getCurrentUserId());

            Long chatId = messageStore.addChatToStore(new Chat());
            for (Long user : usersList) {
                messageStore.addUserToChat(user, chatId);
            }

            return new CommandResult(messageStore.getChatById(chatId).getInfo());
        }
    }

}