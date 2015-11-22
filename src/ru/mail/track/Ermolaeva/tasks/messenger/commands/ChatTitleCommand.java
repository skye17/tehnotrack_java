package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatTitleMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;


public class ChatTitleCommand extends ChatCommand<ChatTitleMessage> {
    public ChatTitleCommand(MessageStore messageStore) {
        super(messageStore);
        commandType = CommandType.CHAT_TITLE;
        description = "/chat_title <chat_id> <chat_title> - update title of chat(only for participants)";
    }

    @Override
    protected Result executeCommandChecked(Session session, ChatTitleMessage commandMessage) throws DataAccessException {
        Chat chat = messageStore.getChatById(commandMessage.getChatId());
        chat.setTitle(commandMessage.getChatTitle());
        messageStore.updateChat(chat);
        return new CommandResult(chat.getInfo());
    }
}
