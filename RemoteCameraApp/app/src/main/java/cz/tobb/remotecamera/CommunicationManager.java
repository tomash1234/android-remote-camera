package cz.tobb.remotecamera;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class CommunicationManager {

    private volatile boolean serverRunning;
    private ServerListener serverListener;
    public static final int PORT = 6660;

    public CommunicationManager(ServerListener serverListener, final int port) {
        this.serverListener = serverListener;
        Thread thread = new Thread(){
            @Override
            public void run() {
                initServer(port);
            }
        };
        thread.start();
    }

    public void stop(){
        serverRunning = false;
    }

    private void initServer(int port){
        serverRunning = true;
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while (serverRunning) {
                Socket socket = serverSocket.accept();


                System.out.println("New client connected");
                InputStream inputStream = socket.getInputStream();
                byte[] data = new byte[5];
                int read =  inputStream.read(data);
                System.out.println("Received: " + read + ", " + Arrays.toString(data));


                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println(new Date().toString());
            }
        }catch (IOException ex){
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
