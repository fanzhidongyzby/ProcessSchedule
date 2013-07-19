/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package operatingsystem;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author fanzhidong
 */
public class Operator extends JPanel implements ActionListener
{
    public PCB newPcb =null;//记录新的进程
    public PCB wakePcb =null;//记录唤醒进程
    public static int[]r=new int[]{3,5,7,9};
    public static Schedule scdl=new Schedule(r);//创建调度对象
    public static LinkedList blockList=new LinkedList();
    public static ProcList pl=new ProcList();//进程列表
    public static LinkedList deadList=new LinkedList();//死亡进程队列

    public static Timer timerRandOper =null;//控制随机的操作，产生，杀死，阻塞进程 等
    public static Timer timerCrtProc =null;//控制创建进程的动画


    public Operator()
    {
        timerRandOper=new Timer(1000,this);
        timerRandOper.start();
        //仅仅是创建进程的时候开启，进程添加完毕后关闭，另外，出现同步的操作时关闭，run除外，保证抢断发生
        timerCrtProc=new Timer(25,this);
        timerCrtProc.start();//启动创建动画，动画结束时，将newPcb添加进就绪队列，同时置其null
        this.setLayout(null);
        this.add(pl);
    }

    public void creatProc()
    {
        if (newPcb != null ||wakePcb !=null || !Schedule.allowProc())//禁止同时创建进程,唤醒  ,发生拥塞
        {
            return ;
        }
        newPcb = new PCB();//应有内存的信息，待续
        newPcb.addr = (int) (Math.random() * 10000);
        newPcb.size = (int) (Math.random() * 100 + 20);
        pl.add(newPcb);//加入进程列表
        newPcb.setLocation(640, 15);
        this.add(newPcb);
    }
    public void wakeupProc()
    {
        if (newPcb != null ||wakePcb !=null ||  !Schedule.allowProc())//发生拥塞
        {
            return ;
        }
        int n=blockList.size();
        if(n==0)
            return ;
        int m=(int)(Math.random()*n);
        PCB pcb=(PCB)blockList.get(m);
        wakePcb=pcb;
        wakePcb.state='U';//唤醒状态
        blockList.remove(pcb);
        //移动后边的
        for(int i=m;i<blockList.size();i++)
        {
            PCB p=(PCB)blockList.get(i);
            int a=p.getX();
            int b=p.getY();
            p.setLocation(a-55, b);
        }
        pl.add(pcb);//加入进程列表
        scdl.loadProc(pcb);        
        wakePcb=null;
    }

    public static void killProc(PCB p)
    {
        Queue q=(Queue)Schedule.queueList.get(p.prior);
        q.remove(p);
        int a=50+40+deadList.size()*55;
        int b=100+400;
        p.setLocation(a, b);
        deadList.add(p);
    }


    public static void blockProc(PCB p)
    {
        Queue q=(Queue)Schedule.queueList.get(p.prior);
        q.remove(p);
        int a=50+40+blockList.size()*55;
        int b=100+315;
        p.setLocation(a, b);
        blockList.add(p);
    }

    public void cleanList(LinkedList l)//清理列表
    {
        if (l.size() > 8)
        {
            for (int i = 0; i < l.size(); i++)
            {
                PCB p = (PCB) l.get(i);
                this.remove(p);
            }
            l.clear();
        }
    }


    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==timerRandOper)
        {
            cleanList(Schedule.completeList);
            cleanList(deadList);
            int i=0;
            int m=(int)(Math.random()*3);
            if(m==0)
                i=1;
            switch (i)
            {
                case 0://创建进程
                    creatProc();
                    break;
                case 1://唤醒进程
                    wakeupProc();
                    break;
            }
        }
        else if(e.getSource()==timerCrtProc)
        {
            if(newPcb!=null)//有新进程创建，执行动画
            {
                //根据进程的优先级访问其应在优先级队列的元素个数，从而确定pcb最终位置，描绘沿途的点数组
                //getPriorListSize(int prior)
                //计算路径//确定创建进程的动画路径
                LinkedList ps=new LinkedList();
                ps.add(new Point(640,15));
                ps.add(new Point(640,135));
                ps.add(new Point(585,135));
                int n=scdl.getPriorListSize(newPcb.prior);
                ps.add(new Point(585,135+50*newPcb.prior));
                ps.add(new Point(90+n*55,135+50*newPcb.prior));
                boolean finish=newPcb.moveLines(ps);
                if(newPcb.getX()<90)//避免出现一直走不停地bug
                    finish=true;
                if(finish)//动画完成
                {
                    scdl.loadProc(newPcb);//加入就绪队列
                    newPcb=null;
                }
                repaint();
            }
        }
    }

    @Override
    public void paint(Graphics g)
    {
        int x = 50;
        int y = 100;

        //draw Title
        g.setColor(Color.black);
        g.drawString("操作系统进程调度的模拟：", x + 5, y - 50);
        g.setColor(Color.lightGray);
        //g.drawString("（由于演示系统开辟的定时器资源较多，有时程序运行时出现的刷新“小毛病”还请见谅  O(∩_∩)O~）",x+5,y-30);
        //g.drawString("                                                                                                                                                 **********  一 叶 孤 城",x+5,y-10);
        g.setColor(Color.blue);
        g.drawString("( E-mail me : fanzhidongyzby@163.com )" ,60, 70);
        //draw创建进程
        g.setColor(Color.BLACK);
        g.drawString("进程池：", x + 535, y - 60);
        g.drawLine(x + 580, y - 65, x + 640, y - 65);
        g.drawLine(x + 610, y - 65, x + 610, y + 45);
        g.drawLine(x + 555, y + 45, x + 610, y + 45);
        g.drawLine(x+555, y+45, x+555, y+495);

        //draw就绪队列
        int num = 4;
        g.setColor(Color.BLUE);
        g.drawString("就绪队列：", x + 5, y + 15);
        g.drawLine(x + 25, y + 25, x + 25, y + num * 50 + 15);
        for (int i = 0; i < num; i++)
        {
            g.setColor(Color.blue);
            g.drawString(String.valueOf(r[i]), x+15, y+45+50*i+5);
            g.drawLine(x + 25, y + 45 + 50 * i, x + 555, y + 45 + 50 * i);
        }

        //draw 阻塞队列
        g.setColor(Color.red);
        g.drawString("阻塞队列：", x + 5, y + 290);
        g.drawLine(x + 25, y + 300, x + 25, y + 350);
        g.drawLine(x + 25, y + 325, x + 555, y + 325);

        //draw 死亡队列
        g.setColor(Color.yellow);
        g.drawString("死亡队列：", x + 5, y + 375);
        g.drawLine(x + 25, y + 385, x + 25, y + 435);
        g.drawLine(x + 25, y + 410, x + 555, y + 410);

        //draw 完成队列
        g.setColor(Color.magenta);
        g.drawString("完成队列：", x + 5, y + 460);
        g.drawLine(x + 25, y + 470, x + 25, y + 520);
        g.drawLine(x + 25, y + 495, x + 555, y + 495);        

        //draw List
        if(newPcb!=null)
        {
            if(newPcb.getX()>599)
                g.setColor(Color.red);
        }
        else
            g.setColor(Color.BLACK);
        g.drawString("进程列表：", x + 690, y - 40);
        g.drawLine(x + 610, y - 15, x + 690, y - 15);
        g.drawLine(x + 685, y - 20, x + 690, y - 15);
        g.drawLine(x + 685, y - 10, x + 690, y - 15);
        g.drawRect(x + 690, y - 35, 400, 530);

        //draw every list or object

    }

    @Override
    public void update(Graphics g)
    {
        Image buffer = createImage(getWidth(), getHeight());
        Graphics GraImage = buffer.getGraphics();
        paint(GraImage);
        GraImage.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

}

