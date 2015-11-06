package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.command_message.ChatListMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.List;


public class ChatListCommand extends MessengerCommand<ChatListMessage> {
    private MessageStore messageStore;

    public ChatListCommand(MessageStore messageStore) {
        commandType = CommandType.CHAT_LIST;
        description = "/chat_list -  get list of user's chats' id";
        this.messageStore = messageStore;
    }

    @Override
    protected Result executeCommand(Session session, ChatListMessage commandMessage) {
        List<Long> id = messageStore.getChatsByUserId(session.getCurrentUserId());
        return new CommandResult(id);
    }

}
