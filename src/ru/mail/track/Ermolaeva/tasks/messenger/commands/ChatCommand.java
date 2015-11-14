package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatCommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;


public abstract class ChatCommand<T extends ChatCommandMessage> extends MessengerCommand<T> {
    protected MessageStore messageStore;
    protected boolean needPermissionCheck = true;

    public ChatCommand(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    @Override
    protected Result executeCommand(Session session, T commandMessage) throws DataAccessException {
        Long userId = session.getCurrentUserId();
        if (needPermissionCheck) {
            Long chatId = commandMessage.getChatId();
            Chat chat = messageStore.getChatById(chatId);
            if (chat == null) {
                return new CommandResult("No chat with id: " + chatId);
            } else {
                if (!messageStore.getChatsByUserId(userId).contains(commandMessage.getChatId())) {
                    return new CommandResult("You don't participate in the chat " + commandMessage.getChatId());
                } else {
                    return executeCommandChecked(session, commandMessage);
                }
            }
        }
        return executeCommandChecked(session, commandMessage);
    }

    protected abstract Result executeCommandChecked(Session session, T commandMessage) throws DataAccessException;
}
