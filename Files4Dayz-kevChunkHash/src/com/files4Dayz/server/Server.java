package com.files4Dayz.server;
import com.files4Dayz.application.FileInfo;
import com.files4Dayz.security.AsciiArmor;
import com.files4Dayz.security.XorCipher;
import org.mindrot.jbcrypt.BCrypt;

import java.net.*;
import java.io.*;
import static com.files4Dayz.security.Checksum.findchecksum;
import static com.files4Dayz.security.XorCipher.encryptDecrypt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private DataInputStream dataReadIn;
    private DataOutputStream dataSendOut;
    private FileOutputStream fileToSave;
    private ServerSocket server;
    private Socket client;
    private int failTime;
    private boolean successVerification = false;
    private boolean successFileTransfer = true;
    private boolean keepConnection = true;
    private String userName;
    private String password;
    private final String key = "key.txt";

    public Server(int port) throws IOException {
        readCredentialsFromFile();
        if (!(port == 0)) {
            try {
                server = new ServerSocket(port);
                failTime = 3;
                System.out.println("Server established. Waiting for client to send file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readCredentialsFromFile() throws IOException {
        final String credentials = "credentials.txt";
        BufferedReader br = new BufferedReader(new FileReader(new File(credentials)));
        userName = br.readLine();
        password = br.readLine();
        br.close();
    }

    public void runAsServer() throws IOException {
        try {
            client = server.accept();
            if (client != null) {
                System.out.println("Got a caller"); }
                verifyCredentials();
            if (successVerification) {
                System.out.println("Login success");
                } else {
                keepConnection = false;
                System.out.println("Login failed. Server will be closed");
                }
        } catch (IOException e) {
            e.printStackTrace(); }

    }

    private void verifyCredentials() throws IOException{
        String userNameClient = null;
        String passwordClient = null;
        dataReadIn = new DataInputStream(client.getInputStream());
        dataSendOut = new DataOutputStream(client.getOutputStream());

        // verify user name and password
        while (failTime > 0) {
            userNameClient = dataReadIn.readUTF();
            passwordClient = dataReadIn.readUTF();
            if (!userNameClient.equals(userName) || !BCrypt.checkpw(passwordClient, password)) {
                if (failTime > 1) {
                    dataSendOut.writeUTF("Wrong combination");
                    System.out.println("shits wrong lmao");
                }
                failTime -= 1;
            } else {
                successVerification = true;
                System.out.println("Verification success");
                break;
            }
        }
        // if user name is correct, reset failTime
        if (successVerification) {
            dataSendOut.writeUTF("Correct");
            failTime = 3;
        }

        if (failTime == 0) {
            System.out.println("Too many fails. Server will close");
            dataSendOut.writeUTF("Close");
            client.close();
            System.exit(0);
        }
    }
    public FileInfo saveFile() throws IOException {

        // save as fileName sent from client
        String fileName = dataReadIn.readUTF();
        int chunkSize = dataReadIn.readInt();
        fileToSave = new FileOutputStream(fileName);
        boolean isArmored = false;
        byte[] originalChunk = null;
        if (dataReadIn.readUTF().equals("armored")) {
            isArmored = true;
            originalChunk = new byte[1368];
        } else {
            originalChunk = new byte[1024];
        }

        // decode
        // byte[] data = decode(buffer);
        int read = 0;
        int size = 0;
        while (chunkSize > 0 && (read = dataReadIn.read(originalChunk)) > 0) {
        	size++;
            // base 64 decode
            //decode(originalChunk);
            // decrypt
            //encryptDecrypt(originalChunk, key);
            byte[] chunkAfterRemoveArmor = null;
            if (isArmored) {
                chunkAfterRemoveArmor = AsciiArmor.removeArmor(originalChunk);
                chunkAfterRemoveArmor = XorCipher.encryptDecrypt(chunkAfterRemoveArmor, new File(key));
                System.out.println("Server receives " + chunkAfterRemoveArmor.length);
                System.out.println("Dearmored");
            } else {
                originalChunk = XorCipher.encryptDecrypt(originalChunk, new File(key));
            }
            String hashedValueFromClient = dataReadIn.readUTF();
            System.out.println("Cipher checksum " + hashedValueFromClient);
            hashedValueFromClient = XorCipher.encryptDecrypt(hashedValueFromClient, new File(key));
            System.out.println("Original checksum " + hashedValueFromClient);
            System.out.println("Successful read of hash");
            if (isArmored) {
            	System.out.println(findchecksum(chunkAfterRemoveArmor));
            } else {
            	System.out.println(findchecksum(originalChunk));
            }
            int failedAttempts = 0;
            if (checkHash(isArmored ? chunkAfterRemoveArmor : originalChunk, hashedValueFromClient)) {
                fileToSave.write(isArmored ? chunkAfterRemoveArmor : originalChunk, 0, isArmored ? chunkAfterRemoveArmor.length : read);
                System.out.println("correct");
                dataSendOut.writeUTF("correct");
                dataSendOut.flush();
                chunkSize -= 1;
            } else {
                failedAttempts++;
                if (failedAttempts == 3) {
                    dataSendOut.writeUTF("closing");
                    System.exit(0);
                }
               dataSendOut.writeUTF("wrong");
               dataSendOut.flush();
               System.out.println("hash wrong");
                failTime -= 1;
                if (failTime == 0) {
                    successFileTransfer = false;
                    break;
                }
            }
            System.out.println("Receive " + size + " KB" );
        }
        fileToSave.close();
        //dataReadIn.close();
      
        if (successFileTransfer) {
        	 System.out.println("File successfully received!");
            return new FileInfo(fileName);
        } else {
            System.out.println("Exceed fail time limit! Connection is terminated.");
            client.close();
            return null;
        }
    }
private boolean checkHash(byte[] originalChunk, String hashedValueFromClient) {
        String hashedValueComputed = findchecksum(originalChunk);
        return hashedValueComputed.equals(hashedValueFromClient);
    }
}
