/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package operatingsystem;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.Timer;

/**
 *
 * @author fanzhidong
 */
public class PCB extends Canvas implements ActionListener ,MouseListener
{
    public int pid; //进程号
    public String name;   /*进程的名字*/ //random to produce
    public int addr=0; //进程资源首地址       //need to be set
    public int size=0; //进程申请资源的大小  //need to be set
    public int prior = 0;    /*进程的优先级*/ //random to produce
    public int round = 0;    /*分配CPU的时间片*/ //need to be set
    public int exeTime = 0;   /*CPU执行时间*/
    public int lifeTime = 0;   /*进程执行所需要的时间*/ //random to produce
    public char state = ' ';    /*进程的状态， space——产生，W——就绪，R——执行态，B——阻塞态，C——完成态*/
    public boolean isIntr =false; //是否被抢占式中断

    public int roadNo=0;//记录进程经过的路径数

    public static int ID = 0;
    public static String procNames[]=
    {
     "java.exe","QQ.exe","csrss.exe","ishare_user.exe","QQMusic.exe",
     "tskmrg.exe","explorer.exe","360tray.exe","winlogin.exe","netbeans.exe",
     "counterstrike.exe","svchost.exe","System Idle Process","service.exe","wininit.exe",
     "smss.exe","lsm.exe","csrss.exe","360rp.exe","360sd.exe"
    };//进程名字池

    public  Timer timerProcAni =new Timer(450,this);//process animation timer

    public PCB()
    {
        this(0,0,0);
    }
    public PCB(int addr , int size , int round)
    {
        this.pid=ID;
        ID++;
        this.name=procNames[(int)(Math.random()*20)];
        int p=(int)(Math.random()*100);
        if(p<50)prior=0;
        else if(p>=50&&p<=80)prior=1;
        else if(p>80&&p<95)prior=2;
        else prior =3;
        this.lifeTime=(int)(Math.sqrt(p)*2+1);
        ///////////////////////////////////////////////////
        this.addr=addr;
        this.size=size;
        this.round=round;
        ///////////////////////////////////////////////////
        this.setBounds(0, 0, 40, 20);
        ///this.setBackground(Color.gray); //使用背景色
        //////////////////////////////////////////////////
        this.timerProcAni.start();
        this.addMouseListener(this);
    }

    void run()
    {
        if(state!='R')//not running
            return ;
        if (exeTime == lifeTime)//进程执行完毕
        {
            Schedule.runPcb=null;
            state = 'C'; //完成态
            Operator.scdl.completeProc(this);
            Schedule.activeFirstPcb();
            this.timerProcAni=null;
            return;
        }
        if(round==0)//时间片是否用完
        {
            Schedule.runPcb=null;
            state='W'; //恢复就绪态
            Operator.scdl.lowerProc(this);
            Schedule.activeFirstPcb();
            return ;
        }
        //proceed execute
        //摇色子，继续还是死亡，或阻塞 （死亡概率——2% ; 阻塞概率——2% ；）
        int luck =(int)(Math.random()*500);
        if(luck<10)//死亡
        {
            Schedule.runPcb=null;
            state='D';
            Operator.killProc(this);//杀死进程
            Schedule.activeFirstPcb();
            this.timerProcAni=null;
            return ;
        }
        else if(luck>490)//阻塞
        {
            Schedule.runPcb=null;
            state='B';
            Operator.blockProc(this);//阻塞进程
            Schedule.activeFirstPcb();
            return ;
        }
        //if good luck ,run continue
        round--;
        exeTime++;
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==timerProcAni)
        {
            this.run();
            repaint();
        }
    }

    public boolean moveLine(int x0,int y0,int x1,int y1)//让块按照直线走,走完返回true
    {
        int x=this.getX();
        int y=this.getY();

        if(x0==x1&&y0==y1)//not line
            return true;
        if(x0==x1)//horizen line
        {
            if(y0>y1)
                y-=5;
            else
                y+=5;
            if(y==y1)
            {
                this.setLocation(x, y);
                return true;
            }
        }
        if(y0==y1)//vertical line
        {
            if(x0>x1)
                x-=5;
            else
                x+=5;
            if(x==x1)
            {
                this.setLocation(x, y);
                return true;
            }
        }
        this.setLocation(x, y);
        return false;
    }

    public boolean moveLines(LinkedList ps)////让块按照折线走,走完返回true
    {
        Point p=(Point)ps.get(roadNo);
        int x0=(int)p.getX();
        int y0=(int)p.getY();
        p=(Point)ps.get(roadNo+1);
        int x1=(int)p.getX();
        int y1=(int)p.getY();
        if(moveLine(x0,y0,x1,y1))//一条路径结束
        {
            roadNo++;
            if(ps.size()==roadNo+1)
            {
                roadNo=0;
                return true;
            }
        }
        return false;

    }


    @Override
    public void paint(Graphics g)
    {
        int w=this.getWidth();
        int h=this.getHeight();
        g.setColor(Color.black);
        g.drawRect(0, 0, w-1, h-1);
        g.setColor(Color.yellow);
        g.fillRect(1, 1, w-2, h-2); //draw block
        switch(state)
        {
            case 'R':g.setColor(Color.red);break;
            case 'W':g.setColor(Color.blue);break;
            case 'C':g.setColor(Color.gray);break;
            case 'I':g.setColor(Color.pink);break;
            case 'D':g.setColor(Color.cyan);break;
            case 'B':g.setColor(Color.WHITE);break;
            case 'U':g.setColor(Color.green);break;
        }
        g.fillRect(1, 1, (int)((double)exeTime/lifeTime*(w-2)), h-2);//fill
        g.setColor(Color.black);
        g.drawString(String.valueOf(pid),15, 15);
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

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e)
    {
        ProcList.tempPcb=this;
    }

    public void mouseExited(MouseEvent e)
    {
        ProcList.tempPcb=null;
    }

}




























