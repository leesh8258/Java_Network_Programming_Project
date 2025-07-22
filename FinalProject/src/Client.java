import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Stack;

public class Client extends JFrame {
    private final Stack<Integer> boardStack_X = new Stack<>();
    private final Stack<Integer> boardStack_Y = new Stack<>();
    private int current_player = 1; //흑돌 부터 하도록
    private int player; //돌 색깔
    private final String player_nickname;
    private final int[][] board = new int[15][15];
    private JPanel panel;
    private JTextField textField;
    private JTextArea textArea;
    private JButton backButton;
    private JButton readyButton;
    private Timer timer;
    private Graphics graphics;
    private final Rule rule_omok;
    private final Rule rule_samsam;
    private final Rule rule_sasa;
    private final Rule rule_jangmok;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    public Client(String ip, int port, String player_nickname) {
        try {
            Socket socket = new Socket(ip, port);
            this.player_nickname = player_nickname;
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            rule_omok = omok.getInstance();
            rule_samsam = samsam.getInstance();
            rule_sasa = sasa.getInstance();
            rule_jangmok = jangmok.getInstance();
            createGUI();
            new Thread(this::receiveToServer).start();
            sendToServer("[JOIN]" + player_nickname);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void createGUI() {
        setTitle("오목");
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBounds(100, 100, 900, 625);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        panel = new JPanel();
        panel.setBounds(45, 45, 501, 501);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblNewLabel = new JLabel("board");
        lblNewLabel.setIcon(new ImageIcon("src/images/board.PNG")); //바둑판 이미지(원본크기 501*501)생성
        lblNewLabel.setBounds(0, 0, 501, 501);
        panel.add(lblNewLabel);

        //마우스 클릭시 이벤트 바둑판 위 에서만 작동 하도록
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                //플레이어 턴, 마우스 클릭한 곳이 바둑판 인지 확인
                if(x >= 30 && x <= 470 && y >= 30 && y <=470 && player == current_player) {
                    x = calculate(x);
                    y = calculate(y);
                    if(stoneClick(x, y))
                        sendToServer("[STONE]" + x + " " + y); //바둑판 좌표로 변경한 후 서버 전달
                }
            }
        });

        timer = new Timer(15);
        Thread timerThread = new Thread(timer);
        timer.setSecond(-1);
        timerThread.start();
        contentPane.add(timer);

        //채팅창 GUI 구현
        //scrollPane이 레이아웃이 null에서 작동안함 따로 Jpanel공간에 채팅창을 만들어 부착
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBounds(576, 230, 284, 240);
        contentPane.add(textPanel);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        //textPanel의 SOUTH에 input칸과 버튼칸을 만들기 위함
        JPanel southPanel = new JPanel(new BorderLayout());
        textPanel.add(southPanel, BorderLayout.SOUTH);

        textField = new JTextField();
        textField.setColumns(20);
        southPanel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("보내기");
        southPanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(e -> {
            if (!textField.getText().isEmpty()) {
                sendToServer("[CHAT]" + "[" + player_nickname + "] >> " + textField.getText() + "\n");
                textField.setText(null);
            }
        });

        backButton = new JButton("무르기");
        backButton.setBounds(576, 523, 92, 23);
        contentPane.add(backButton);
        backButton.setEnabled(false);
        backButton.addActionListener(e -> {
            sendToServer("[BACK]");
            backButton.setEnabled(false);
        });

        readyButton = new JButton("준비");
        readyButton.setBounds(680, 523, 88, 23);
        contentPane.add(readyButton);

        readyButton.addActionListener(e -> {
            sendToServer("[READY]");
            readyButton.setEnabled(false);
        });

    }

    //돌 놓을자리 조건 체크
    private boolean stoneClick(int x, int y) {
        if (x == -1 || y == -1) return false;

        if (current_player == 1) {
            if (rule_samsam.checkRule(current_player, board, y, x)) {
                textArea.append("33 규칙에 의해 둘 수 없는 자리 입니다.\n");
                return false;
            }
            if (rule_sasa.checkRule(current_player, board, y, x)) {
                textArea.append("44 규칙에 의해 둘 수 없는 자리 입니다.\n");
                return false;
            }
            if (rule_jangmok.checkRule(current_player, board, y, x)) {
                textArea.append("장목 규칙에 의해 둘 수 없는 자리 입니다.\n");
                return false;
            }
        }
        return true;
    }

    private void drawCircle(String msg) {
        String[] temp = msg.split(" ");
        int x = Integer.parseInt(temp[0]);
        int y = Integer.parseInt(temp[1]);

        boardStack_Y.push(y);
        boardStack_X.push(x);

        graphics = panel.getGraphics();

        if(board[y][x] == 0) {
            if (current_player == 1) {
                board[y][x] = 1;
                graphics.setColor(Color.BLACK);
                graphics.fillOval(returnToOriginal(x) - 14, returnToOriginal(y) - 14, 25, 25);
            }

            if (current_player == 2) {
                board[y][x] = 2;
                graphics.setColor(Color.WHITE);
                graphics.fillOval(returnToOriginal(x) - 14, returnToOriginal(y) - 14, 25, 25);
            }


            if (rule_omok.checkOmok(current_player, board, y, x)) {
                sendToServer("[WIN]" + current_player);
                gameSet();
            } else {
                current_player = 3 - current_player;
                timerTimeSet();
                backButton.setEnabled(true);
                sendToServer("[countDown]");
            }

        }
        backButton.setEnabled(true);
    }

    //게임 승리시
    private void winSystem(String msg) {
        JOptionPane.showMessageDialog(this, msg + "WIN!");
        backButton.setEnabled(false);
        timer.setSecond(-1);
    }

    //게임 초기세팅
    private void gameSet() {
        current_player = 1;
        player = 0;
        for(int y =0; y < 15; y++) {
            for(int x =0; x < 15; x++) {
                board[y][x] = 0;
            }
        }
        readyButton.setEnabled(true);
    }

    //2명의 플레이어가 무르기 요청시 실행
    private void requestBack() {
        int y = boardStack_Y.pop();
        int x = boardStack_X.pop();
        board[y][x] = 0;
        current_player = 3 - current_player;
        repaint();
    }
    //플레이어 색깔 정하기
    private void setColor(String msg) {
        player = Integer.parseInt(msg);
    }

    private void chatSystem(String msg) {
        textArea.append(msg);
    }

    //서버로부터 메세지 받기
    private void receiveToServer() {
        while(true) {
            try {
                String msg = dataInputStream.readUTF();

                if(msg.startsWith("[STONE]")) {
                    msg = msg.substring(7);
                    drawCircle(msg); //바둑알 그리기
                }

                if(msg.startsWith("[READY]")) {
                    msg = msg.substring(7);
                    setColor(msg); //플레이어 색깔 결정 (흑돌, 백돌)
                    timerTimeSet();
                    repaint();
                }

                if(msg.startsWith("[BACK]")) {
                    if(!boardStack_X.empty()) {
                        textArea.append("무르기[2/2]\n");
                        requestBack(); //무르기 함수
                        timerTimeSet();
                    }
                    else {
                        backButton.setEnabled(true);
                    }
                }

                if(msg.startsWith("[WIN]")) {
                    msg = msg.substring(5);
                    winSystem(msg); //게임승리시 설정
                }

                if(msg.startsWith("[CHAT]") || msg.startsWith("[JOIN]")) {
                    msg = msg.substring(6);
                    chatSystem(msg); //채팅창 기능
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //서버에게 메세지 보내기
    private void sendToServer(String msg) {
        try {
            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //바둑판 좌표에서 frame 좌표로 변경
    private int returnToOriginal(int a) {
        return (a * 30) + 40;
    }

    //frame 클릭 좌표에서 바둑판 좌표로 변경
    private int calculate(int x) {
        int A = (x - 40) / 30;
        int B = (x - 40) % 30;

        if( B > 10 && B < 20)
            return -1;

        else if(B >= 20)
            A += 1;

        return A;
    }

    //창이 최소화, 초기화 될 시 바둑판 그림 다시그려주기
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        graphics = panel.getGraphics();
        for(int y =0; y < 15; y++) {
            for(int x =0; x < 15; x++) {
                if(board[y][x] != 0) {
                    if(board[y][x] == 1)
                        graphics.setColor(Color.BLACK);

                    if(board[y][x] == 2)
                        graphics.setColor(Color.WHITE);

                    graphics.fillOval(returnToOriginal(x) -14 , returnToOriginal(y) - 14, 25, 25);
                }
            }
        }
    }

    private void timerTimeSet() {
        if(current_player == player)
            timer.setSecond(15);

        else
            timer.setSecond(-1);
    }

    //내부 클래스로 타이머 구현
    private class Timer extends JLabel implements Runnable {
        int second;
        int width = 250, height = 50;
        int x = 600, y = 100;
        Random random = new Random();

        public Timer(int second) {
            setOpaque(true);
            setBounds(x, y, width, height);
            setForeground(Color.blue);
            setFont(new Font("맑은고딕", Font.PLAIN, 50));
            setHorizontalAlignment(JLabel.CENTER);
            this.second = second;
        }

        public void setSecond(int second) {
            this.second = second;
            if(second >= 10)
                setText("00: " +second);
            if(second < 10 && second > 0)
                setText("00:0" +second);
            if(second == -1)
                setText("00:00");

        }
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (second > 0) {
                    second -= 1;
                    if (second >= 10)
                        setText("00: " + second);
                    if (second < 10)
                        setText("00:0" + second);
                }

                if (second == 0) {
                    int x;
                    int y;
                    while(true) {
                        int randomX = random.nextInt(15);
                        int randomY = random.nextInt(15);

                        if (board[randomY][randomX] == 0 && stoneClick(randomX, randomY)) {
                            x = randomX;
                            y = randomY;
                            break;
                        }
                    }

                    sendToServer("[STONE]" + x + " " + y);
                }

            }
        }
    }
    public static void main(String[] args){
        new EnterFrame();
    }
}
