package client_server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
        System.out.print(message);
    }

    public static String readString(){
        String s;
        while (true) {
            try {
                s = reader.readLine();
                break;
            } catch (IOException e) {
                System.out.println("Error during enter the text. Please, try again...");
            }
        }
        return s;
    }

    public static int readInt(){
        int result;
        while (true){
            try {
                result = Integer.parseInt(readString());
                break;
            }catch (NumberFormatException e){
                System.out.println("Error during a number enter. Try again...");
            }
        }

        return result;
    }
}
