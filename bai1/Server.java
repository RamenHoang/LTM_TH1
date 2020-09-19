import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(8080)) {
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

                    returnStr += "Chu hoa: " + this.manipulateString(inStr, 0) + "\n";
                    returnStr += "Chu thuong: " + this.manipulateString(inStr, 1) + "\n";
                    returnStr += "Vua hoa vua thuong: " + this.manipulateString(inStr, 2) + "\n";
                    returnStr += this.lengthAndVowel(inStr);

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

        /**
         * @param inStr string to manipulate
         * @param type  0: to upper; 1: to lower; 2: do both
         * @return string after manipulated
         */
        private String manipulateString(String inStr, int type) {
            String returnStr = "";
            char tempCh;

            for (int i = 0; i < inStr.length(); i++) {
                tempCh = inStr.charAt(i);
                switch (type) {
                    case 0:
                        tempCh = (tempCh >= 97 && tempCh <= 122) ? (char) (tempCh - 32) : tempCh;
                        break;
                    case 1:
                        tempCh = (tempCh >= 65 && tempCh <= 90) ? (char) (tempCh + 32) : tempCh;
                        break;
                    case 2:
                        if (tempCh >= 97 && tempCh <= 122)
                            tempCh = (char) (tempCh - 32);
                        else if (tempCh >= 65 && tempCh <= 90)
                            tempCh = (char) (tempCh + 32);
                        break;
                }
                returnStr += tempCh;
            }
            return returnStr;
        }

        private String lengthAndVowel(String inStr) {
            String returnStr = "";
            
            int countWord = 0;
            String[] words = inStr.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].length() > 0)
                    countWord++;
            }
            returnStr += "So tu: " + countWord;

            int countVowel = 0;

            for (int i = 0; i < inStr.length(); i++) {
                String currentChar = inStr.charAt(i) + "";
                currentChar = currentChar.toLowerCase();
                if (currentChar.equals("a") || currentChar.equals("u") || currentChar.equals("e") || currentChar.equals("o") || currentChar.equals("i"))
                    countVowel++;
            }
                
            returnStr += "\nSo nguyen am: " + countVowel;

            return returnStr;
        }
    }
}
