package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatCommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;


public class ChatJoinCommand extends ChatCommand<ChatCommandMessage> {
    public ChatJoinCommand(MessageStore messageStore) {
        super(messageStore);
        commandType = CommandType.CHAT_JOIN;
        description = "/chat_join <chat_id> - join chat <chat_id> if not already a participant";
        needPermissionCheck = false;
    }

    @Override
    protected Result executeCommandChecked(Session session, ChatCommandMessage commandMessage) throws DataAccessException {
        Long chatId = commandMessage.getChatId();
        Chat chat = messageStore.getChatById(chatId);
        if (chat == null) {
            return new CommandResult("No chat with id: " + chatId);
        } else {
            if (chat.getUsersList().contains(session.getCurrentUserId())) {
                return new CommandResult("You already participate in the chat!", true);
            } else {
                chat = messageStore.addUserToChat(session.getCurrentUserId(), chatId);
                return new CommandResult(chat.getInfo());
            }
        }

    }
}
