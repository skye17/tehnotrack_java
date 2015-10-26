package ru.mail.track.Ermolaeva.tasks.messenger.message;


import java.util.Collection;

public interface MessageStore {
    void addMessage(Message message);

    Collection<Message> getMessageHistory();

    Collection<Message> getMessageHistory(int messagesNumber);

    Collection<Message> getMessagesByKeyword(String keyword);

    Collection<Message> getMessagesByPattern(String pattern, boolean caseFlag);

    int size();

}
