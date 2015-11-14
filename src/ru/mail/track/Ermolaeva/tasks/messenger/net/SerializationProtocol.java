package ru.mail.track.Ermolaeva.tasks.messenger.net;


import java.io.*;


public class SerializationProtocol implements Protocol {

    @Override
    public SocketMessage decode(byte[] bytes) {
        if (bytes != null) {
            try (ByteArrayInputStream byteInStream = new ByteArrayInputStream(bytes);
                 ObjectInputStream in = new ObjectInputStream(byteInStream)) {
                return (SocketMessage) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new IllegalArgumentException("Can't decode message");
            }
        }
        return null;
    }

    @Override
    public byte[] encode(SocketMessage message) {
        if (message != null) {
            try (ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                 ObjectOutputStream out = new ObjectOutputStream(byteOutStream)) {
                out.writeObject(message);
                out.flush();
                return byteOutStream.toByteArray();
            } catch (IOException io) {
                //
                throw new IllegalArgumentException("Can't encode message");
            }
        }
        return null;
    }
}
