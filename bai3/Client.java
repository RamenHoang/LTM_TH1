import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JOptionPane;

public class Client extends JFrame implements ActionListener {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    private String nickname;

    private JLabel nicknameL;
    private JTextArea enteredText;
    private JTextField typedText;
    private JScrollPane scrollPane;

    public Client() {
        try {
            this.initSocket();
            this.initLayout();
            this.listenFromServer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            this.log(e.getStackTrace());
        }
    }

    private void initLayout() {
        //
        this.nickname = randomName();
        this.nicknameL = new JLabel(this.nickname);
        this.enteredText = new JTextArea(25, 32);
        this.typedText = new JTextField(32);
        this.scrollPane = new JScrollPane(this.enteredText);

        this.enteredText.setEditable(false);
        this.enteredText.setBackground(Color.WHITE);
        this.enteredText.setLineWrap(true);

        this.typedText.addActionListener(this);

        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.setTitle("Chat with Server: Chat room");
        this.add(this.nicknameL);
        this.add(this.scrollPane);
        this.add(this.typedText);
        this.setSize(430, 500);
        this.setResizable(false);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setVisible(true);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JFrame login = new JFrame();
        login.setBounds(400, 400, 200, 150);
        login.setLayout(new FlowLayout(FlowLayout.CENTER));
        login.add(new JLabel("Your name"));
        login.setResizable(false);
        JTextField input = new JTextField(15);
        JButton submit = new JButton("Let's chat!");
        login.add(input);
        login.add(submit);
        login.setVisible(true);
        submit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String name = input.getText();
                if (name.length() > 0) {
                    try {
                        nickname = name;
                        nicknameL.setText(nickname);
                        login.setVisible(false);
                        login.dispose();
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(login, exc.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(login, "Please complete all field!");
                    input.requestFocus();
                }
            }

        });
    }

    private void initSocket() throws IOException {
        try {
            this.socket = new Socket("localhost", 8082);
            this.in = new Scanner(this.socket.getInputStream());
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (Exception e) {
            this.log(e.getMessage());
            this.log(e.getStackTrace());
            this.socket.close();
        }
    }

    private void listenFromServer() {
        try {
            String response = "";
            while (in.hasNextLine()) {
                response = in.nextLine();
                this.enteredText.insert(response + "\n", this.enteredText.getText().length());
                in.reset();
            }
            this.log(in.hasNextLine());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            this.log(e.getStackTrace());
        }
    }

    private void sendTextToServer() {
        try {
            String text = "";
            text = this.typedText.getText();
            this.out.println("[" + this.nickname + "]: " + text);
            this.enteredText.insert("[You]: " + text, this.enteredText.getText().length());
        } catch (Exception e) {
            this.log(e.getMessage());
            this.log(e.getStackTrace());
        }
    }

    private void log(Object s) {
        System.out.println(s);
    }

    private String randomName() {
        String s = "Stranger-";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            s += random.nextInt(9);
        }
        return s;
    }

    public static void main(String[] args) {
        Client newClient = new Client();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // send data to server
        this.sendTextToServer();
        typedText.setText("");
        typedText.requestFocusInWindow();
    }
}