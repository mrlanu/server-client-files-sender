package client_server.client;

import client_server.Connection;
import client_server.ConsoleHelper;
import client_server.Message;
import client_server.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.util.List;

public class Client {

    private String address;
    private int port;
    private Path directory;
    private Connection connection;

    public Client() {
        this.address = getServerAddress();
        this.port = getServerPort();
        this.directory = Paths.get(getDirectoryForStore());
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public void run(){
        SocketThread socketThread = new SocketThread();
        socketThread.start();
    }

    protected String getServerAddress(){
        ConsoleHelper.writeMessage("--->>> Enter the server's address : ");
        return ConsoleHelper.readString();
    }

    protected int getServerPort(){
        ConsoleHelper.writeMessage("--->>> Enter the server's port : ");
        return ConsoleHelper.readInt();
    }

    protected String getDirectoryForStore(){
        ConsoleHelper.writeMessage("--->>> Enter a directory for store the files : ");
        return ConsoleHelper.readString();
    }

    public class SocketThread extends Thread{

        @Override
        public void run() {
            try {
                Socket socket = new Socket(address, port);
                connection = new Connection(socket);
                ConsoleHelper.writeMessage(String.format("\n--->>> Have connected to address : %s, port : %d\n", address, port));
                clientMainLoop();

            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error - " + e);
            }
        }

        public void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                sendCommand();
                Message message = connection.receive();

                if (message.getMessageType() == MessageType.FILES_LIST) {
                    List<String> list = message.getFilesNameList();
                    list.forEach(System.out::println);

                }else if (message.getMessageType() == MessageType.FILE_BY_NAME){
                    try (FileOutputStream fos = new FileOutputStream(Paths.get(directory + "/" + message.getFileName()).toFile())){
                        fos.write(message.getBuffer());
                        ConsoleHelper.writeMessage
                                (String.format("\n --->>> File name : %s downloaded to directory - %s\n", message.getFileName(), directory));
                    }
                }
            }
        }

        public void sendCommand() throws IOException {

            ConsoleHelper.writeMessage("\n--->>> What would you like to do ? \n");
            ConsoleHelper.writeMessage("\n -> 1 - Get files list from server \n");
            ConsoleHelper.writeMessage("\n -> 2 - Download file by name \n");

            switch (ConsoleHelper.readInt()){
                case 1 : connection.send(new Message(MessageType.FILES_LIST));
                    break;

                case 2 : ConsoleHelper.writeMessage("--->>> Enter a file name for download : ");
                    String fileName = ConsoleHelper.readString();
                    connection.send(new Message(MessageType.FILE_BY_NAME, fileName));
                    break;

                default: break;
            }
        }
    }
}
