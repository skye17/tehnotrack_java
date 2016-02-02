package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.net.SocketMessage;


public interface Result {
    SocketMessage getMessage();

    boolean getStatus();
}
