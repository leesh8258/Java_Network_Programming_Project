import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnterFrame extends JFrame {
    public EnterFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Omok Game");
        setVisible(true);
        setSize(300, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel enterPanel = new JPanel();
        enterPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(enterPanel);
        enterPanel.setLayout(null);

        JLabel nickNameLabel = new JLabel("NickName");
        nickNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        nickNameLabel.setBounds(25, 50, 80, 25);
        enterPanel.add(nickNameLabel);

        JTextField nickNameField = new JTextField();
        nickNameField.setBounds(125, 50, 150, 25);
        enterPanel.add(nickNameField);
        nickNameField.setColumns(10);

        JLabel portLabel = new JLabel("Port");
        portLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        portLabel.setBounds(45, 100, 80, 25);
        enterPanel.add(portLabel);

        JTextField portField = new JTextField();
        portField.setBounds(125, 100, 150, 25);
        enterPanel.add(portField);
        portField.setColumns(10);

        JButton gameExitButton = new JButton("EXIT");
        gameExitButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        gameExitButton.setBounds(35, 150, 100, 35);
        enterPanel.add(gameExitButton);
        gameExitButton.addActionListener(e -> System.exit(0));

        JButton gameStartButton = new JButton("게임시작");
        gameStartButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        gameStartButton.setBounds(155, 150, 100, 35);
        enterPanel.add(gameStartButton);

        gameStartButton.addActionListener(e -> {
            String nickname = nickNameField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            new Client("localhost", port, nickname);
            setVisible(false);
        });
    }
}
