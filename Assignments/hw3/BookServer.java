import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookServer {


    static HashMap<String, Integer> libraryInventory;
    static HashSet<Student> studentDatabase;
    static HashMap<Integer, String> recIDMap;
    private static Integer currRecID;




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

    private static String borrow(String student, String title){
        String checkedOut = "Request Failed - Book not available";
        String bookNotAvail = "Request Failed - We do not have this book";
        //check to see if the book exists
        int quantity = 0;
        System.out.println(libraryInventory.values());
        //System.out.println(currRecID++);
        if(!libraryInventory.containsKey(title)) {
            return bookNotAvail;
        }else { //if book exists
            quantity = libraryInventory.get(title);
            if (quantity==0) {
                return checkedOut;
            }
            if(!studentDatabase.contains(student)) { // if new student, add to database
                Student temp = new Student(student);
                System.out.println("Added " + temp.name + " to the database");
                //temp.addBook(title);
                studentDatabase.add(temp);
            }
            //update all student and library records
            currRecID++;
            System.out.println(currRecID);
            recIDMap.put(currRecID, title);
            libraryInventory.put(title, quantity-1);
            for (Student s : studentDatabase) {
                if (s.name.equals(student)) {
                    System.out.println("added book for " + s.name);
                    s.addBook(currRecID,title);
                }
            }

        }

        StringBuilder ret = new StringBuilder("Your request has been approved, ");
        ret.append("<" + currRecID + "> ");
        ret.append("<" + student + "> ");
        ret.append("<"+ title + ">");
        return ret.toString();

    }
    private static String list(String name){
        //if record doesn't exist return string
        StringBuilder ret = new StringBuilder("");
        System.out.println(name);
        if(!studentDatabase.contains(name)){
            System.out.println("AAAAAAA");
            return ("No record found for <" + name + ">");
        }else{
            System.out.println("BBBBBBB");
            //find the student's records and return them
            for (Student s : studentDatabase) {
                if (s.name.equals(name)) {
                    for(int key: s.books.keySet()) {
                        int id = key;
                        String book = s.books.get(key);
                        ret.append("<"+key+"> " + "<"+book+">\n");
                    }
                }
            }
        }

        return ret.toString();
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
            //DataOutputStream outToClient = null;
            PrintStream outToClient = null;
            Scanner sc = null;
            Scanner parseCmd = null;

            try {
                //create input stream attached to socket
                sc = new Scanner(theClient.getInputStream ());
                inFromClient = new BufferedReader(new InputStreamReader(theClient.getInputStream()));
                //create output stream, attached to socket
                outToClient = new PrintStream(theClient.getOutputStream());
                //outToClient = new DataOutputStream(theClient.getOutputStream());
                System.out.println("handling client command");
                while(sc.hasNext()){
                    String command = sc.nextLine();
                    parseCmd = new Scanner(command);
                    String cmdType = parseCmd.next();

                    //TODO process different commands


                    if(cmdType.equals("borrow")){
                        String title = parseTitle(command);
                        String name = parseCmd.next();
                        String respond = borrow(name, title);
                        outToClient.println(respond);
                        outToClient.flush();
                        //send response to client after processing
                    }else if(cmdType.equals("inventory")){

                    }else if(cmdType.equals("return")){

                    }else if(cmdType.equals("list")){
                        String name = parseCmd.next();
                        String response = list(name);
                        outToClient.println(response);

                    }else if(cmdType.equals("exit")){

                    }else if(cmdType.equals("setmode")){

                    }
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
    libraryInventory = library.getInventory();
    studentDatabase = new HashSet<>();
    recIDMap = new HashMap<>();
    currRecID = 0;

    //creating TCP socket, listening for multiple threads
    try{
      ServerSocket listener = new ServerSocket(tcpPort);
      Socket serverSocket;
      while((serverSocket = listener.accept())!= null){
          System.out.println("New Client... Creating new thread");
        Thread t = new ServerThread(serverSocket);
        t.start();
      }
    }catch (IOException e){
        System.err.print("Server Aborted: " + e);
    }
    // TODO: handle request from clients
  }


}
