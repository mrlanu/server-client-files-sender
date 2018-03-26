package client_server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {

    final private Socket socket;
    final private ObjectOutputStream out;
    final private ObjectInputStream in;

    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }

    public Message receive() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    public void send(Message message) throws IOException {
        out.writeObject(message);
    }

    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
