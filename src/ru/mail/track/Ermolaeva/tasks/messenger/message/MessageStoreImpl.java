package ru.mail.track.Ermolaeva.tasks.messenger.message;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class MessageStoreImpl implements MessageStore {
    private List<Message> messages;

    public MessageStoreImpl() {
        messages = new ArrayList<>();
    }


    @Override
    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public Collection<Message> getMessageHistory() {
        return messages;
    }

    @Override
    public Collection<Message> getMessageHistory(int messagesNumber) {
        int size = messages.size();
        if (size < messagesNumber) {
            return messages;
        }
        return messages.subList(size - messagesNumber, size);
    }

    @Override
    public Collection<Message> getMessagesByKeyword(String keyword) {
        ArrayList<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message.getMessage().contains(keyword)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Collection<Message> getMessagesByPattern(String regex, boolean caseFlag) {
        ArrayList<Message> result = new ArrayList<>();
        if (caseFlag) {
            for (Message message : messages) {
                if (Pattern.compile(regex).matcher(message.getMessage()).find(Pattern.CASE_INSENSITIVE)) {
                    result.add(message);
                }
            }
        } else {
            for (Message message : messages) {
                if (Pattern.compile(regex).matcher(message.getMessage()).find()) {
                    result.add(message);
                }
            }
        }
        return result;
    }

    @Override
    public int size() {
        return messages.size();
    }
}
