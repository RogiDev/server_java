import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles Matrix-related tasks
 */
public class MatrixIHandler implements IHandler {
    private Matrix matrix;
    private Index startIndex;
    private Index endIndex;
    private volatile boolean doWork = true;

    @Override
    public void resetMembers() {
        this.matrix = null;
        this.startIndex = null;
        this.endIndex = null;
        this.doWork = true;
    }

    @Override
    public void handle(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException {
        /*
        Send data as bytes.
        Read data as bytes then transform to meaningful data
        ObjectInputStream and ObjectOutputStream can read and write both primitives and objects
         */
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);

        boolean doWork = true;
        // handle client's tasks
        while(doWork){
            /*
            Use-cases:
            if client send the command "matrix"
            - Client sends 2d array -> server will create a Matrix object.
            if client send the command "getNeighbors"
            - Client sends an Index and wishes to get an List of neighbors
            if client send the command "getReachables"
            - Client sends an Index and wishes to get an List of reachable indices
            //TODO:
            - Client sends a start & end index and wishes to get all possible routes between them
             */

            switch (objectInputStream.readObject().toString()){
                case "matrix":{
                    // client will now send a 2d array. handler will create a matrix object
                    int[][] tempArray = (int[][])objectInputStream.readObject();
                    System.out.println("Server: Got 2d array");
                    this.matrix = new Matrix(tempArray);
                    this.matrix.printMatrix();
                    break;
                }

                case "getNeighbors":{
                    // handler will receive an index, then compute its neighbors
                    Index tempIndex = (Index)objectInputStream.readObject();
                    if(this.matrix!=null){
                        List<Index> neighbors = new ArrayList<>(this.matrix.getNeighbors(tempIndex));
                        System.out.println("Server: neighbors of "+ tempIndex + ":  " + neighbors);
                        // send to socket's outputstream
                        objectOutputStream.writeObject(neighbors);
                    }
                    break;
                }

                case "getReachables":{
                    // handler will receive an index, then compute its neighbors
                    Index tempIndex = (Index)objectInputStream.readObject();
                    if(this.matrix!=null){
                        List<Index> reachables = new ArrayList<>(this.matrix.getReachables(tempIndex));
                        System.out.println("Server: neighbors of "+ tempIndex + ":  " + reachables);
                        // send to socket's outputstream
                        objectOutputStream.writeObject(reachables);
                    }
                }

                case "stop":{
                    doWork = false;
                    break;
                }
            }
        }





    }


}
