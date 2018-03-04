import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookClient {

  public static String parseTitle(String line){
    Pattern P1 = Pattern.compile("\"([^\"]*)\"");
    Matcher m1 = P1.matcher(line);
    String title = null;
    while (m1.find()) {
      //System.out.println(m1.group(0));
      title = m1.group(1);
    }
    return title;
  }

  public static void main (String[] args) throws IOException {
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

    Scanner sc = null;
    Socket clientSocket = null;
    DataOutputStream outToServer = null;
    DataInputStream fromServer = null;
    PrintStream pout = null;

    try {
        sc = new Scanner(new FileReader(commandFile));
        clientSocket = new Socket("localhost",tcpPort);
        //create output stream to write to socket
        System.out.println("connecting to server");
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        pout = new PrintStream(clientSocket.getOutputStream());


        while(sc.hasNextLine()) {
          String cmd = sc.nextLine();
          String[] tokens = cmd.split(" ");
          System.out.println(cmd);

          if (tokens[0].equals("setmode")) {
            // Default: UDP
            //TODO

          }
          else if (tokens[0].equals("borrow")) {
            System.out.println("borrowing...");
            String title = parseTitle(cmd);
            System.out.println(title);
            pout.println(1 + " " + tokens[1] + " " + title + "\n");
            pout.flush();
            int retValue = sc.nextInt();
            //outToServer.writeBytes(cmd);
            // TODO appropriate responses form the server

          } else if (tokens[0].equals("return")) {
            //outToServer.writeBytes(cmd);

            // TODO appropriate responses form the server

          } else if (tokens[0].equals("inventory")) {
            //outToServer.writeBytes(cmd);
            // appropriate responses form the server


          } else if (tokens[0].equals("list")) {
            //outToServer.writeBytes(cmd);

            // appropriate responses form the server

          } else if (tokens[0].equals("exit")) {
            //outToServer.writeBytes(cmd);

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
    }finally {
        outToServer.close();
        clientSocket.close();
    }
  }
}
