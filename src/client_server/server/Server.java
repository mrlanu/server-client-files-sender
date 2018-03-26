package client_server.server;

import client_server.Connection;
import client_server.ConsoleHelper;
import client_server.Message;
import client_server.MessageType;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static Path directory;
    private static int port;

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        ConsoleHelper.writeMessage("--->>> Please enter a Server's port : ");
        port = ConsoleHelper.readInt();

        ConsoleHelper.writeMessage("--->>> Please enter a directory : ");
        directory = Paths.get(ConsoleHelper.readString());

        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            ConsoleHelper.writeMessage
                    (String.format("\n--->>> The Server has started on the port : %s, directory - %s", port, directory));
            while (true){
                new Handler(serverSocket.accept()).start();
            }

        } catch (IOException e) {
            ConsoleHelper.writeMessage(e.getMessage());
        }
    }

    public static List<Path> getFilesList(Path path){

        List<Path> result = new ArrayList<>();

        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path file : directoryStream) {
                    if (Files.isRegularFile(file)){
                        result.add(file);
                    }
                }
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Error - " + e);
            }
        }else {
            ConsoleHelper.writeMessage("--->>> That is not directory.");
        }
        return result;
    }

    private static class Handler extends Thread{

        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {

            ConsoleHelper.writeMessage("\n--->>> Got a new connection with address - " + socket.getRemoteSocketAddress());
            Connection connection = null;

            try {
                connection = new Connection(socket);
                serverMainLoop(connection);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error - " + e);
            }
        }

        public void serverMainLoop(Connection connection) throws IOException, ClassNotFoundException {

            while (true){
                Message message = connection.receive();

                if (message.getMessageType() == MessageType.FILES_LIST){
                    List<String> filesNameList = new ArrayList<>();
                    getFilesList(directory).forEach(p -> filesNameList.add(p.getFileName().toString()));
                    connection.send(new Message(MessageType.FILES_LIST, filesNameList));

                }else if (message.getMessageType() == MessageType.FILE_BY_NAME){
                    String fileName = message.getFileName();
                    Path fileForSending = getFilesList(directory).stream()
                            .filter(p -> p.getFileName().toString().equals(fileName))
                            .findFirst()
                            .get();

                    try(FileInputStream fis = new FileInputStream(fileForSending.toFile())) {
                        byte[] buffer = new byte[(int) fileForSending.toFile().length()];
                        fis.read(buffer);
                        connection.send(new Message(MessageType.FILE_BY_NAME, fileForSending.getFileName().toString(), buffer));
                    }

                }else {
                    ConsoleHelper.writeMessage("--->>> Can't recognize this command.");
                }
            }
        }
    }
}
