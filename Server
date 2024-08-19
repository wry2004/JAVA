import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*;
import static java.lang.Thread.sleep;
import java.lang.Math;
import java.util.concurrent.CountDownLatch;

class gui extends JFrame{
    public gui(String title){
        super(title);
        initgui();
    }
    private void initgui(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(300,200);
        JButton B=new JButton("关闭服务器");
        B.setBounds(100,80,100,30);
        B.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(B);
        setVisible(true);
    }
}
class poke{
    public int[] card;
    public int arr;
    public poke()
    {
        card=new int[52];
        arr=0;
        for(int i=1;i<=13;i++)
        {
            for (int j = 0; j <= 3; j++)
            {
                card[i * 4 - j - 1] = i;
            }
        }
    }
    public void shuffle(int[] inputList)
    {
        Random random = new Random();
        for(int loop=0;loop<52;loop++){
            int randomIndex=random.nextInt(52-loop);
            int temp=inputList[loop];
            inputList[loop]=inputList[loop+randomIndex];
            inputList[loop+randomIndex]=temp;
        }
    }
    public int divcard(){
        arr++;
        return card[arr-1];
    }
}
class player{
    public String ID;
    public Boolean giveup;
    public int numofcard;
    public int[] cardofplayer;
    public int score;
    public player(){
        ID="无玩家";
        numofcard=0;
        score=10;
        giveup=false;
        cardofplayer=new int[3];
    }
    public void getplayer(String a){
        ID=a;
    }
    public void getcard(int a){
        if(numofcard>=3){
            System.out.println("failed!");
            return;
        }
        cardofplayer[numofcard]=a;
        numofcard++;
    }
}
class game{
    int jackpot=6;
    public int judsame(player p){
        if(p.cardofplayer[0]==p.cardofplayer[1]&&p.cardofplayer[1]==p.cardofplayer[2])
        {
            return 3;
        }
        if(p.cardofplayer[0]== p.cardofplayer[1]||p.cardofplayer[1]== p.cardofplayer[2]||p.cardofplayer[0]==p.cardofplayer[2])
        {
            return 2;
        }
        return 1;
    }
    public void result(player[] p,int n,int flag){
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n-i-1;j++)
            {
                if(judsame(p[j])<judsame(p[j+1])||((p[j].cardofplayer[0]+p[j].cardofplayer[1]+p[j].cardofplayer[2])<(p[j+1].cardofplayer[0]+p[j+1].cardofplayer[1]+p[j+1].cardofplayer[2])&&judsame(p[j+1])==judsame(p[j])))
                {
                    player tmp=p[j];
                    p[j]=p[j+1];
                    p[j+1]=tmp;
                }
            }
        }
        for(int i=0;i<n;i++)
        {
            int j=i+1;
            if(flag==1){
                System.out.println("N0."+j+"---"+p[i].ID+"   "+p[i].cardofplayer[0]+" "+p[i].cardofplayer[1]+" "+p[i].cardofplayer[2]);
            }
        }
    }
}
class Task implements Runnable {
    private Socket s;
    static CountDownLatch latch = new CountDownLatch(2);
    static CountDownLatch latch2=new CountDownLatch(2);
    public int cnt=0;
    public int order;
    public player p;
    public int[] adda=new int[2];
    public Task(int[] adda,Socket socket,int a,player p) {
        this.s = socket;
        this.order=a;
        this.p=p;
        this.adda=adda;
    }

    @Override
    public void run() {
        try {
            System.out.println("客户端"+order+":" + s.getInetAddress().getLocalHost() + "已连接到服务器");
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String mess = br.readLine();
            System.out.println("客户端"+order+"：" + mess+"就绪");
            p.ID = mess;
            cnt++;
            p.score -= 3;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            sleep(1000);
            bw.write("本局将消耗3点筹码，您还会剩余"+(p.score)+"点筹码"+'\n');
            bw.flush();
            bw.write("          手牌1---"+p.cardofplayer[0]+'\n');
            bw.flush();
            bw.write("          手牌2---"+p.cardofplayer[1]+'\n');
            bw.flush();
            bw.write("          手牌3---"+p.cardofplayer[2]+'\n');
            bw.flush();
            bw.write("选择想增加的下注，加注范围0到3,输入0到3间的数字下注(否则视为不下注)\n");
            bw.flush();
            mess = br.readLine();
            adda[order-1] = Integer.parseInt(mess);
            if (adda[order-1] < 0 || adda[order-1] > 3) {
                adda[order-1] = 0;
            }
            bw.write(p.ID);
            bw.flush();
            bw.write("加注"+adda[order-1]+"注！\n");
            bw.flush();
            latch.countDown();
            bw.write("正在等待对手..."+"\n");
            bw.flush();
            latch.await();
            mess=br.readLine();
            if("g".equalsIgnoreCase(mess.trim())){
                p.giveup=true;
                bw.write("您放弃了游戏，失去全部底注！\n");
                bw.flush();
            }
            latch2.countDown();
            latch2.await();
            sleep(5000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Server {
    private static List<Socket> clientSockets = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        String choice;
        int sign = 0;
        int maxadd = 0;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new gui("关闭服务器设置");
            }
        });
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("服务器启动，监听端口：" + 8888);
            int cnt = 1;
            int num = 2;
            int[] adda = new int[2];
            player[] p = new player[num];
            for (int i = 0; i < num; i++) {
                p[i] = new player();
            }
            poke c = new poke();
            c.shuffle(c.card);
            for (int i = 0; i < num; i++) {
                for (int j = 0; j < 3; j++) {
                    p[i].getcard(c.divcard());
                }
            }
            while (cnt <= 2) {
                Socket socket = serverSocket.accept();
                clientSockets.add(socket);
                Task t = new Task(adda,socket, cnt, p[cnt - 1]);
                Thread TR = new Thread(t);
                TR.start();
                TR.setPriority(Thread.MAX_PRIORITY);
                cnt++;
                int flag = 1;
                while (flag == 1) {
                    flag = 0;
                    for (int i = 0; i < cnt - 1; i++) {
                        if (p[i].ID.equals("无玩家")) {
                            flag = 1;
                        }
                    }
                }
                if (cnt > 2) {
                    Task.latch.await();
                    maxadd = Math.max(adda[0], adda[1]);
                    for(Socket client:clientSockets){
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        bw.write("根据两位玩家的加注最大值，本局需要加注"+maxadd+"注，如果放弃将结束游戏，失去底注！\n放弃游戏输入‘g’（其他输入则视为继续）\n");
                        bw.flush();
                    }
                    Task.latch2.await();
                    for(int i=0;i<num;i++){
                        if(p[i].giveup){
                            cnt=i+1000;
                        }
                    }
                    if(cnt>100){
                        break;
                    }
                    for (Socket client : clientSockets) {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        Task.latch2.await();
                        game g = new game();
                        g.result(p, num, 0);
                        g.jackpot=g.jackpot+2*maxadd;
                        for (int i = 0; i < num; i++) {
                            if (sign == 0) {
                                p[i].score -= maxadd;
                            }
                            if (i == 0 && sign == 0) {
                                p[i].score += g.jackpot;
                            }
                        }
                        sign = 1;
                        sleep(1000);
                        bw.write("所有玩家均加注"+maxadd+"注，总奖池来到"+g.jackpot+"分");
                        bw.flush();
                        sleep(1000);
                        bw.write("\n最终结果：\n");
                        bw.flush();
                        for (int i = 0; i < num; i++) {
                            int j = i + 1;
                            bw.write("\nN0." + j + "---" + p[i].ID + "   " + p[i].cardofplayer[0] + " " + p[i].cardofplayer[1] + " " + p[i].cardofplayer[2] + '\n');
                            bw.flush();
                            if (i == 0) {
                                bw.write("恭喜" + p[i].ID + "赢得了比赛，获得了" + g.jackpot + "点筹码，总分为" + p[i].score + "点" + '\n');
                                bw.flush();
                            } else {
                                bw.write(p[i].ID + "不要失望，还剩" + p[i].score + "点" + '\n');
                                bw.flush();
                            }
                        }
                        bw.write("\n输入'r'查看对局记录\n");
                        bw.flush();
                    }
                }
            }
            if(cnt>100){
                sign=1;
                for (Socket client : clientSockets) {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    Task.latch2.await();
                    game g = new game();
                    g.result(p, num, 0);
                    sleep(1000);
                    bw.write("有人放弃了比赛\n");
                    bw.flush();
                    sleep(1000);
                    bw.write("\n最终结果：\n");
                    bw.flush();
                    for (int i = 0; i < num; i++) {
                        bw.write('\n'+p[i].ID + "   " + p[i].cardofplayer[0] + " " + p[i].cardofplayer[1] + " " + p[i].cardofplayer[2] + '\n');
                        bw.flush();
                        if (i != (cnt-1000)) {
                            if(sign==1) {
                                sign=0;
                                p[i].score += 6;
                            }
                            bw.write("恭喜" + p[i].ID + "赢得了比赛，获得了" + 6 + "点筹码，总分为" + p[i].score + "点" + '\n');
                            bw.flush();
                        } else {
                            bw.write(p[i].ID + "因放弃游戏而输，但不要失望，还剩" + p[i].score + "点" + '\n');
                            bw.flush();
                        }
                    }
                    bw.write("\n输入'r'查看对局记录\n");
                    bw.flush();
                }
                String filename = "C:\\Users\\WU\\Desktop\\对局记录（网络存储）.txt";
                try {
                    FileWriter writer = new FileWriter(filename, false);
                    for(int i=0;i<num;i++){
                        if(p[i].giveup){
                            writer.write("N0." + (i+1) + "---" + p[i].ID + "   " + p[i].cardofplayer[0] + " " + p[i].cardofplayer[1] + " " + p[i].cardofplayer[2] +"（放弃比赛）" +'\n');
                        }
                        else {
                            writer.write("N0." + (i+1) + "---" + p[i].ID + "   " + p[i].cardofplayer[i] + " " + p[i].cardofplayer[1] + " " + p[i].cardofplayer[2] + '\n');
                        }
                    }
                    writer.close();
                } catch (IOException iox) {
                    System.out.println("创建文件" + filename + "失败");
                }
            }
            else {
                String filename = "C:\\Users\\WU\\Desktop\\对局记录（网络存储）.txt";
                try {
                    FileWriter writer = new FileWriter(filename, false);
                    writer.write("N0." + 1 + "---" + p[0].ID + "   " + p[0].cardofplayer[0] + " " + p[0].cardofplayer[1] + " " + p[0].cardofplayer[2] + '\n');
                    writer.write("N0." + 2 + "---" + p[1].ID + "   " + p[1].cardofplayer[0] + " " + p[1].cardofplayer[1] + " " + p[1].cardofplayer[2] + '\n');
                    writer.close();
                } catch (IOException iox) {
                    System.out.println("创建文件" + filename + "失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
