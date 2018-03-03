import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
public class BookClient {
  public static void main (String[] args) {
    String hostAddress;
    int tcpPort;
    int udpPort;
    int clientId;

    if (args.length != 2) {
      System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
      System.out.println("\t(1) <command-file>: file with commands to the server");
      System.out.println("\t(2) client id: an integer between 1..9");
      System.exit(-1);
    }

    String commandFile = args[0];
    clientId = Integer.parseInt(args[1]);
    hostAddress = "localhost";
    tcpPort = 7000;// hardcoded -- must match the server's tcp port
    udpPort = 8000;// hardcoded -- must match the server's udp port


    try {
        Scanner sc = new Scanner(new FileReader(commandFile));
        Socket clientSocket = new Socket("localhost",tcpPort);
        //create output stream to write to socket
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());


        while(sc.hasNextLine()) {
          String cmd = sc.nextLine();
          String[] tokens = cmd.split(" ");

          if (tokens[0].equals("setmode")) {
            // Default: UDP
            //TODO

          }
          else if (tokens[0].equals("borrow")) {
            for(String word:tokens){
              outToServer.writeBytes(word);
            }

            // TODO appropriate responses form the server

          } else if (tokens[0].equals("return")) {
            outToServer.writeBytes(tokens[0]);
            outToServer.writeBytes(tokens[1]);

            // TODO appropriate responses form the server

          } else if (tokens[0].equals("inventory")) {
            outToServer.writeBytes(tokens[0]);
            // appropriate responses form the server


          } else if (tokens[0].equals("list")) {
            outToServer.writeBytes(tokens[0]);

            // appropriate responses form the server

          } else if (tokens[0].equals("exit")) {
            outToServer.writeBytes(tokens[0]);

          } else {
            System.out.println("ERROR: No such command");
          }
        }
    } catch (FileNotFoundException e) {
	e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
