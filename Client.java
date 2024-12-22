import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    public String IDofClient;
    int cnt = 0;
    int sign = 0;
    private static final long serialVersionUID = 1L;
    private JTextField messageField;
    private JTextArea chatArea;
    private PrintWriter out;
    private BufferedReader in;
    private String filename;
    public Client(String title) {
        super(title);
        initGUI();
    }
    public void initGUI() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);
        JButton sendButton = new JButton("确认");
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        JLabel fontLabel = new JLabel("字体: ");
        controlPanel.add(fontLabel);
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontBox = new JComboBox<>(fonts);
        fontBox.setSelectedItem("Serif");
        controlPanel.add(fontBox);
        JLabel sizeLabel = new JLabel("大小: ");
        controlPanel.add(sizeLabel);
        Integer[] sizes = {12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
        JComboBox<Integer> sizeBox = new JComboBox<>(sizes);
        sizeBox.setSelectedItem(18);
        controlPanel.add(sizeBox);
        JLabel colorLabel = new JLabel("文本颜色: ");
        controlPanel.add(colorLabel);
        String[] colors = {"黑色", "红色", "蓝色", "绿色"};
        JComboBox<String> colorBox = new JComboBox<>(colors);
        controlPanel.add(colorBox);
        JLabel bgColorLabel = new JLabel("背景颜色: ");
        controlPanel.add(bgColorLabel);
        String[] colors2 = {"白色", "红色", "蓝色", "绿色"};
        JComboBox<String> bgColorBox = new JComboBox<>(colors2);
        controlPanel.add(bgColorBox);
        JButton applyButton = new JButton("应用");
        controlPanel.add(applyButton);
        add(controlPanel, BorderLayout.NORTH);
        fontBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFont(fontBox, sizeBox);
            }
        });
        sizeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFont(fontBox, sizeBox);
            }
        });
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateColors(colorBox, bgColorBox);
            }
        });
        setVisible(true);
        connectToServer();
    }
    private void updateFont(JComboBox<String> fontBox, JComboBox<Integer> sizeBox) {
        String selectedFont = (String) fontBox.getSelectedItem();
        int selectedSize = (Integer) sizeBox.getSelectedItem();
        Font font = new Font(selectedFont, Font.PLAIN, selectedSize);
        chatArea.setFont(font);
    }
    private void updateColors(JComboBox<String> colorBox, JComboBox<String> bgColorBox) {
        String selectedColor = (String) colorBox.getSelectedItem();
        String selectedBgColor = (String) bgColorBox.getSelectedItem();
        Color textColor;
        switch (selectedColor) {
            case "红色":
                textColor = Color.RED;
                break;
            case "蓝色":
                textColor = Color.BLUE;
                break;
            case "绿色":
                textColor = Color.GREEN;
                break;
            default:
                textColor = Color.BLACK;
        }
        Color bgColor;
        switch (selectedBgColor) {
            case "红色":
                bgColor = Color.RED;
                break;
            case "蓝色":
                bgColor = Color.BLUE;
                break;
            case "绿色":
                bgColor = Color.GREEN;
                break;
            default:
                bgColor = Color.WHITE;
        }
        chatArea.setForeground(textColor);
        chatArea.setBackground(bgColor);
    }
    private void connectToServer() {
        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 8888;
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(new Runnable() {
                public void run() {
                    String message;
                    appendToChatArea("\n欢迎体验德州扑克小游戏，规则如下：\n 3张相同的牌 > 2张相同的牌 > 牌都不同； 都不同则比较三张牌总数；\n获得全部手牌后可结合牌型选择加注，两位玩家都加注后，以加注最大值为标准，所有玩家必须达到标准，不然放弃游戏并失去底注；\n结束后输入‘r’查看对局记录\n");
                    appendToChatArea("\n每名玩家初始点数10分，对局底注3分\n");
                    appendToChatArea("请输入ID：");
                    try {
                        while ((message = in.readLine()) != null) {
                            appendToChatArea(message);
                            if (filename != null) {
                                updateFile(message);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage() {
        String message = messageField.getText();
        if (sign == 1 && "r".equalsIgnoreCase(message.trim())) {
            readFile();
        } else {
            out.println(message);
            if (cnt == 0) {
                cnt = 1;
                sign = 1;
                IDofClient = message;
                filename = "C:\\Users\\WU\\Desktop\\对局记录（用户" + " ' " + IDofClient + " ' " + "本地存储）.txt";
            }
            appendToChatArea(message);
            if (filename != null) {
                updateFile(message);
            }
            messageField.setText("");
        }
    }
    private void updateFile(String message) {
        try {
            FileWriter writer = new FileWriter(filename, true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException iox) {
            appendToChatArea("更新文件" + filename + "失败");
        }
    }
    private void readFile() {
        try {
            appendToChatArea("-----------------------------------游戏记录-------------------------------------------");
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = in.readLine();
            while (line != null) {
                appendToChatArea(line);
                line = in.readLine();
            }
            in.close();
            appendToChatArea("-------------------------------------------------------------------------------------");
        } catch (IOException iox) {
            appendToChatArea("读取" + filename + "失败，查看了不存在的文件？");
        }
    }
    private void appendToChatArea(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatArea.append(message + "\n");
            }
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Client("德州扑克客户端");
            }
        });
    }
}
