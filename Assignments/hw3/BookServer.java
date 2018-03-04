import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookServer {

  public static void initLibrary(File fileName, Library library) throws FileNotFoundException {
    // parse the inventory file
    Scanner sc = new Scanner(fileName);

    //initialize the library inventory
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      Pattern P1 = Pattern.compile("\"([^\"]*)\"");
      Pattern P2 = Pattern.compile("\\d+");
      Matcher m2 = P2.matcher(line);
      Matcher m1 = P1.matcher(line);
      //add book and quantity to library inventory
      while (m1.find() && m2.find()) {
          //System.out.println(m1.group(0));
        String book = m1.group(1);
        int quantity = Integer.parseInt(m2.group(0));
        library.getInventory().put(book, quantity);
      }
    }
  }

  //Create a new server thread for each new command
    public static class ServerThread extends Thread{
        Socket theClient;

        public ServerThread(Socket s){
            this.theClient = s;
        }

        @Override

        //Handle all types of commands here
        public void run() {
            BufferedReader inFromClient = null;
            DataOutputStream outToClient = null;

            try {
                //create input stream attached to socket
                inFromClient = new BufferedReader(new InputStreamReader(theClient.getInputStream()));
                //create output stream, attached to socket
                outToClient = new DataOutputStream(theClient.getOutputStream());
                //read in line from socket
                System.out.println("handling client command");
                while(inFromClient.read()!=-1){
                    String clientCommand = inFromClient.readLine();
                    //TODO Testing here if server is receiving commands from client
                    System.out.println(clientCommand + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

  public static void main (String[] args) throws IOException {
    int tcpPort;
    int udpPort;
    if (args.length != 1) {
      System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
      System.exit(-1);
    }
    String fileName =args[0];
    tcpPort = 7000;
    udpPort = 8000;
    Library library = new Library();
    initLibrary(new File(fileName), library);

    //creating TCP socket, listening for multiple threads
    try{
      ServerSocket listener = new ServerSocket(tcpPort);
      Socket server;
      while((server = listener.accept())!= null){
          System.out.println("New Client... Creating new thread");
        Thread t = new ServerThread(server);
        t.start();
      }
    }catch (IOException e){
        System.err.print("Server Aborted: " + e);
    }
    // TODO: handle request from clients
  }


}
