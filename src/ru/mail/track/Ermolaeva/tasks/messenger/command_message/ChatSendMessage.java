package ru.mail.track.Ermolaeva.tasks.messenger.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class ChatSendMessage extends CommandMessage {
    private String messageToChat;
    private Long chatId;

    public ChatSendMessage() {
        super(CommandType.CHAT_SEND);
    }

    public String getMessageToChat() {
        return messageToChat;
    }

    public void setMessageToChat(String messageToChat) {
        this.messageToChat = messageToChat;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
