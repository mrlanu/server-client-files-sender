package client_server;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

    private MessageType messageType;
    private String fileName;
    private byte[] buffer;
    private List<String> filesNameList;

    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.fileName = null;
        this.buffer = null;
        this.filesNameList = null;
    }

    public Message(MessageType messageType, String fileName) {
        this.messageType = messageType;
        this.fileName = fileName;
        this.buffer = null;
        this.filesNameList = null;
    }

    public Message(MessageType messageType, List<String> filesNameList) {
        this.messageType = messageType;
        this.filesNameList = filesNameList;
        this.buffer = null;
        this.fileName = null;
    }

    public Message(MessageType messageType, String fileName, byte[] buffer) {
        this.messageType = messageType;
        this.fileName = fileName;
        this.buffer = buffer;
        this.filesNameList = null;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getFilesNameList() {
        return filesNameList;
    }

    public byte[] getBuffer() {
        return buffer;
    }
}
