package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatCommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.List;


public class ChatListCommand extends ChatCommand<ChatCommandMessage> {

    public ChatListCommand(MessageStore messageStore) {
        super(messageStore);
        needPermissionCheck = false;
        commandType = CommandType.CHAT_LIST;
        description = "/chat_list -  get list of user's chats' id";
        this.messageStore = messageStore;
    }

    @Override
    protected Result executeCommandChecked(Session session, ChatCommandMessage commandMessage) throws DataAccessException {
        List<Long> id = messageStore.getChatsByUserId(session.getCurrentUserId());
        return new CommandResult(id);
    }
}
