import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<MyTask> tasks;
    private ServerSocket server;
    private String db;

    public static void main(String[] args) throws IOException {
        Server _server = new Server();
        _server.waitingConnection();
    }

    public Server() throws IOException {
        this.server = new ServerSocket(8082);
        this.tasks = new ArrayList<>();
        this.db = "";
        log("Server is running ...");
    }

    public void waitingConnection() throws IOException {
        while (true) {
            Socket socket = server.accept();
            try {
                this.addTask(new MyTask(socket));
            } catch (Exception e) {
                socket.close();
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            }
        }
    }

    private synchronized void addTask(MyTask task) {
        this.tasks.add(task);
    }

    private synchronized void removeTask(MyTask task) {
        this.tasks.remove(task);
    }

    private synchronized void broadCast(MyTask fromTask, String message) {
        for (int i = 0; i < this.tasks.size(); i++) {
            MyTask task = this.tasks.get(i);
            if (!task.equals(fromTask)) {
                try {
                    task.send(message);
                } catch (Exception e) {
                    this.tasks.remove(i--);
                    task.close();
                    log(e.getMessage());
                    log(e.getStackTrace());
                }
            }
        }
    }

    private void log(Object s) {
        System.out.println(s);
    }

    public class MyTask extends Thread {
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public MyTask(Socket _socket) throws IOException {
            this.socket = _socket;
            in = new Scanner(this.socket.getInputStream());
            out = new PrintWriter(this.socket.getOutputStream(), true);
            this.start();
        }

        @Override
        public void run() {
            this.doMission();
        }

        private void doMission() {
            if (db.length() > 0) {
                this.out.println(db);
            }
            try {
                System.out.println("Connected: " + this.socket);

                while (true) {
                    String returnStr = "";

                    String inStr = in.nextLine();
                    broadCast(this, inStr);
                    db += inStr + "\n";
                    out.println(returnStr);
                    in.reset();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            } finally {
                try {
                    this.close();
                    System.out.println("Closed socket" + this.socket);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println(e.getStackTrace());
                }
            }
        }

        private void send(String message) {
            this.out.println(message);
        }

        private void close() {
            try {
                this.socket.close();
                removeTask(this);
            } catch (IOException e) {
                log(e.getMessage());
                log(e.getStackTrace());
            }
        }
    }
}
