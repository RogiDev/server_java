//import java.io.*;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Client {
//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        /**
//         * Client wishes to initiate a connection with a remote/local server
//         * We need:
//         * 1. address of the server
//         * 2. port
//         *
//         * There are 2 kinds of sockets:
//         * ServerSocket- listens and accepts connections
//         * Operational socket (client socket) - 2 way pipeline to read/write messages
//         */
//        Socket clientSocket = new Socket("127.0.0.1",8010);
//        System.out.println("Client: Socket created");
//
//        InputStream inputStream = clientSocket.getInputStream();
//        OutputStream outputStream = clientSocket.getOutputStream();
//        ObjectInputStream fromServer = new ObjectInputStream(inputStream);
//        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
//
//        // create 2d array
//        int[][] source = {
//                {0,1,1},
//                {0,1,0},
//                {0,1,1},
//        };
//        // send "matrix" command
//        toServer.writeObject("matrix");
//        toServer.writeObject(source);
//
//        // send "getNeighbors" command and then send an index
//        toServer.writeObject("getNeighbors");
//        Index sourceIndex = new Index(1,1);
//        toServer.writeObject(sourceIndex);
//        List<Index> neighbors = new ArrayList<>((List<Index>)fromServer.readObject());
//        System.out.println("Client: neighbors are: "+ neighbors);
//
//        toServer.writeObject("getReachables");
//        toServer.writeObject(sourceIndex);
//        List<Index> reachableIndices = new ArrayList<>((List<Index>)fromServer.readObject());
//        System.out.println("Client: neighbors are: "+ reachableIndices);
//
//        toServer.writeObject("stop");
//        // orderly shutdown all streams
//        System.out.println("Closing streams");
//        fromServer.close();
//        toServer.close();
//        clientSocket.close();
//    }
//}
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket =new Socket("127.0.0.1",8010);
        System.out.println("client: Created Socket");

        ObjectOutputStream toServer=new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream fromServer=new ObjectInputStream(socket.getInputStream());

        // sending #1 matrix
        int[][] source = {
                {0, 1, 0},
                {1, 0, 1},
                {1, 0, 1},
                {1,0,1,0,1}
        };

        //TODO: create a loop to get commands from user.
        // upon sending an index: input should be of the type (rowNumber,columnNumber)

        //send "matrix" command then write 2d array to socket
        toServer.writeObject("matrix");
        //TODO: check that matrix is no bigger than  20X20 & jagged array (number of columns is same in all rows)
        toServer.writeObject(source);

        //send "neighbors" command then write an index to socket
        toServer.writeObject("getNeighbors");
        // TODO: get index from command prompt as: (rowNumber,columnNumber)
        toServer.writeObject(new Index(1,1));

        // get neighboring indices as list
        List<Index> AdjacentIndices =
                new ArrayList<Index>((List<Index>) fromServer.readObject());
        System.out.println("from client - Neighboring Indices are: "+ AdjacentIndices);

        //send "reachables" command then write an index to socket
        toServer.writeObject("getReachables");
        toServer.writeObject(new Index(1,1));

        // get reachable indices as list
        List<Index> reachables =
                new ArrayList<Index>((List<Index>) fromServer.readObject());
        System.out.println("from client - Reachable Indices are:  "+ reachables);

        toServer.writeObject("stop");
        System.out.println("client: Close all streams");
        fromServer.close();
        toServer.close();
        socket.close();
        System.out.println("client: Closed operational socket");
    }
}

