import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpServer {
    public static final int port = 8080;

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        // All writes are performed while synchronized on 'os'.
        private final PrintWriter os;

        // Socket reads do not need to be synchronized as each clients gets its
        // own thread.
        private final BufferedReader is;
        private final InputStreamReader inputStreamReader;
        private final BufferedWriter bufferedWriter;
        private String clientId;

        public ClientHandler(Socket socket)
                throws IOException {
            this.socket = socket;


            this.inputStreamReader = new InputStreamReader(socket.getInputStream());
            this.os = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            this.is = new BufferedReader(inputStreamReader);
            this.bufferedWriter = new BufferedWriter(this.os);
        }

        @Override
        public void run() {
            try {
                // First line the client sends us is the client ID.
                for (String line = is.readLine(); line != null; line = is.readLine()) {
//                    bufferedWriter.write(line);
//                    bufferedWriter.newLine();
//                    bufferedWriter.flush();
                    sendMessage(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("Client " + clientId + " terminated.");
                try {
                    socket.close();
                    this.inputStreamReader.close();
                    this.os.close();
                    this.is.close();
                    this.bufferedWriter.close();
                } catch (IOException ioe) {
                    // TODO Auto-generated catch block
                    ioe.printStackTrace();
                }
            }
        }

        public void sendMessage( String message) {
            try {
                synchronized (os) {
                    os.println(message);
                    os.flush();
                }
            } catch (Exception e) {
                // We shutdown this client on any exception.
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ioe) {
                    // TODO Auto-generated catch block
                    ioe.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        for (Socket socket = ss.accept(); socket != null; socket = ss.accept()) {
            Runnable handler = new ClientHandler(socket);
            new Thread(handler).start();
        }

    }
}