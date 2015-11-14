package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;

public class ChatSendMessage extends ChatCommandMessage {
    private String messageToChat;

    public ChatSendMessage() {
        super(CommandType.CHAT_SEND);
    }

    public String getMessageToChat() {
        return messageToChat;
    }

    public void setMessageToChat(String messageToChat) {
        this.messageToChat = messageToChat;
    }

}
