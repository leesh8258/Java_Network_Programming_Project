import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private int playerCount = 0;
    private int backCount = 0;
    private final ServerSocket serverSocket;
    private final List<ServerThread> clientList = new CopyOnWriteArrayList<>();
    private final List<String> NickNameList = new CopyOnWriteArrayList<>();
    public Server(int port) throws IOException {
       serverSocket = new ServerSocket(port);
       threadLoop();
    }

    public void threadLoop(){
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket);
                clientList.add(serverThread);
                new Thread(serverThread).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) throws IOException {
        new Server(5000);
    }

    private class ServerThread implements Runnable {
        private final Socket socket;
        private final DataInputStream dataInputStream;
        private final DataOutputStream dataOutputStream;
        public ServerThread(Socket socket) throws IOException {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }

        //여기서 레디버튼 둘다 입력 들어오면 send로 각각 플레이어1 플레이어2 라고 보내기
        @Override
        public void run() {
            String msg;

            while(true) {
                try {
                    msg = dataInputStream.readUTF();

                    if(msg.startsWith("[READY]")) {
                        playerCount++;
                        if(playerCount == 2) {
                            int color = 1;
                            for(ServerThread client : clientList) {
                                client.send(msg + color);
                                color++;
                            }
                            playerCount = 0;
                        }
                    }

                    else if(msg.startsWith("[BACK]")) {
                        backCount++;
                        if(backCount == 1) {
                            for(ServerThread client : clientList) {
                                client.send("[CHAT]" + "무르기[1/2]" + "\n");
                            }
                        }
                        if(backCount == 2) {
                            for(ServerThread client : clientList) {
                                client.send(msg);
                            }
                            backCount = 0;
                        }
                    }

                    else if(msg.startsWith("[countDown]")) {
                        backCount = 0;
                    }

                    else if(msg.startsWith("[WIN]")) {
                        msg = msg.substring(5);
                        for(ServerThread client : clientList) {
                            client.send("[WIN]"+NickNameList.get(Integer.parseInt(msg) - 1));
                        }
                    }

                    else if(msg.startsWith("[JOIN]")) {
                        msg = msg.substring(6);
                        NickNameList.add(msg);
                        for(ServerThread client : clientList) {
                            client.send("[JOIN]" +"[" + msg + "] 님이 입장했습니다." + "\n" );
                        }
                    }

                    else {
                        for (ServerThread client : clientList) {
                            client.send(msg);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void send(String msg) {
            try {
                dataOutputStream.writeUTF(msg);
            } catch (IOException e) {
                clientList.remove(this);
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
