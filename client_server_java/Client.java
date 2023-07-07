import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Client {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "0123456789abcdef";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to the server.");

            // Create AES cipher for encryption and decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            // Create input and output streams for communication
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            // Create data input and output streams for convenience
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // Exchange messages
            while (true) {
                // Read message from the client console
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Client: ");
                String clientMessage = consoleReader.readLine();

                // Encrypt the message
                String encryptedClientMessage = encryptMessage(clientMessage, cipher);

                // Send the encrypted message to the server
                dataOutputStream.writeUTF(encryptedClientMessage);
                dataOutputStream.flush();

                // Read message from the server
                String encryptedMessage = dataInputStream.readUTF();

                // Decrypt the message
                String decryptedMessage = decryptMessage(encryptedMessage, cipher);

                System.out.println("Server: " + decryptedMessage);
            }
        } catch (IOException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encryptMessage(String message, Cipher cipher) {
        try {
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return new String(encryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String decryptMessage(String encryptedMessage, Cipher cipher) {
        try {
            byte[] decryptedBytes = cipher.doFinal(encryptedMessage.getBytes());
            return new String(decryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
