package cz.tobb.remotecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class CommunicationManager {

    public static final int MAX_IMAGE_SIZE = 256 * 256;
    public static final int IMAGE_CHANNELS = 3;
    private volatile boolean serverRunning;
    private ServerListener serverListener;
    public static final int PORT = 6660;

    private volatile Bitmap bitmapToSend;

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


                System.out.println("SERVER: New client connected");
                InputStream inputStream = socket.getInputStream();
                byte[] data = new byte[5];
                int read =  inputStream.read(data);
                System.out.println("SERVER: Received: " + read + ", " + Arrays.toString(data));

                OutputStream output = socket.getOutputStream();
                synchronized (this){
                    serverListener.requestData();
                    this.wait(3000);
                }
                if(bitmapToSend == null){
                    System.out.println("SERVER picture problem");
                }else{
                    sendResponse(output, bitmapToSend);
                    bitmapToSend = null;
                }


            }
        }catch (IOException ex){
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void sendResponse(OutputStream outputStream, Bitmap bitmap) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width > MAX_IMAGE_SIZE || height > MAX_IMAGE_SIZE){
            throw new IllegalArgumentException("Length of picture size cannot be greater than " + MAX_IMAGE_SIZE
                    + ", current picture size " + width + "x" + height);
        }
        byte[] byteDimensions = new byte[]{
                (byte)(width/ 256),  (byte) (width % 256),
                (byte)(height / 256),  (byte) (height % 256)
        };
        byte[] data = new byte[width * height * IMAGE_CHANNELS];
        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int index = (i * width + j) * IMAGE_CHANNELS;
                if(IMAGE_CHANNELS == 3){
                    int color = bitmap.getPixel(j, i);
                    data[index + 0] = (byte) Color.red(color);
                    data[index + 1] = (byte) Color.green(color);
                    data[index + 2] = (byte) Color.blue(color);
                }
            }
        }
        System.out.println("SERVER: header info");
        outputStream.write(byteDimensions);
        System.out.println("SERVER: Data " + data.length);
        outputStream.write(data);
    }

    public void sendImage(File file) {
        bitmapToSend = BitmapFactory.decodeFile(file.getAbsolutePath());
        bitmapToSend = Bitmap.createScaledBitmap(bitmapToSend, bitmapToSend.getWidth() / 4, bitmapToSend.getHeight() / 4, true);
        synchronized (this){
            this.notify();
        }
    }
}
