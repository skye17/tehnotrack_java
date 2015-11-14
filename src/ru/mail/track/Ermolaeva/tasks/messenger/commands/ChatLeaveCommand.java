package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatCommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;


public class ChatLeaveCommand extends ChatCommand<ChatCommandMessage> {
    public ChatLeaveCommand(MessageStore messageStore) {
        super(messageStore);
        commandType = CommandType.CHAT_LEAVE;
        description = "/chat_leave <chat_id> - leave chat <chat_id> (only if participant)";
    }


    @Override
    protected Result executeCommandChecked(Session session, ChatCommandMessage commandMessage) throws DataAccessException {
        Chat chat = messageStore.getChatById(commandMessage.getChatId());
        if (chat.getUsersList().size() == 2) {
            return new CommandResult("You can't leave the dialogue.", true);
        }
        messageStore.removeUserFromChat(session.getCurrentUserId(), commandMessage.getChatId());
        return new CommandResult("You've successfully left the chat " + commandMessage.getChatId());
    }

}
