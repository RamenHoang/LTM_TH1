import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(8081)) {
            System.out.println("Server is running ...");

            while (true) {
                Socket socket = server.accept();
                try {
                    new MyTask(socket);
                } catch (Exception e) {
                    socket.close();
                    System.out.println(e.getMessage());
                    System.out.println(e.getStackTrace());
                }
            }
        }
    }

    public static class MyTask extends Thread {
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public MyTask(Socket _socket) {
            this.socket = _socket;
            this.start();
        }

        @Override
        public void run() {
            this.doMission();
        }

        private void doMission() {
            try {
                System.out.println("Connected: " + this.socket);

                while (true) {
                    in = new Scanner(this.socket.getInputStream());
                    out = new PrintWriter(this.socket.getOutputStream(), true);

                    String returnStr = "";

                    String inStr = in.nextLine();
                    try {
                        returnStr = Evaluation.evaluate(inStr) + "";
                    } catch(UnsupportedOperationException e) {
                        returnStr = e.getMessage();
                    }

                    out.println(returnStr);
                    in.reset();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            } finally {
                try {
                    this.socket.close();
                    System.out.println("Closed socket" + this.socket);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println(e.getStackTrace());
                }
            }
        }
    }
}
