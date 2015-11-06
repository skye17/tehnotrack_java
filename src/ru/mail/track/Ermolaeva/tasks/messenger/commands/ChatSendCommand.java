package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.command_message.ChatSendMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.ChatMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;


public class ChatSendCommand extends MessengerCommand<ChatSendMessage> {
    public static final String MESSAGE_ADDED = "Message successfully added to chat ";
    public static final String NOT_PARTICIPANT = "You don't participate in the chat ";
    private MessageStore messageStore;

    public ChatSendCommand(MessageStore messageStore) {
        commandType = CommandType.CHAT_SEND;
        description = "/chat_send <id> <message> - send <message> to chat with <id>";
        this.messageStore = messageStore;
    }

    @Override
    protected Result executeCommand(Session session, ChatSendMessage commandMessage) {
        Long chatId = commandMessage.getChatId();
        String messageText = commandMessage.getMessageToChat();
        if (messageStore.getChatsByUserId(session.getCurrentUserId()).contains(chatId)) {
            ChatMessage message = new ChatMessage(messageText);
            message.setSender(session.getCurrentUserId());
            message.setChatId(chatId);
            messageStore.addMessage(message);
            messageStore.addMessage(message.getId(), chatId);
            return new CommandResult(MESSAGE_ADDED + chatId);
        } else {
            return new CommandResult(NOT_PARTICIPANT + chatId, true);
        }
    }

}
