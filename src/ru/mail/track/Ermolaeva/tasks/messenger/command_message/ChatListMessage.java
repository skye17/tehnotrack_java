package ru.mail.track.Ermolaeva.tasks.messenger.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class ChatListMessage extends CommandMessage {
    public ChatListMessage() {
        super(CommandType.CHAT_LIST);
    }
}
