/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package operatingsystem;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.Timer;

/**
 *
 * @author fanzhidong
 */
public class ProcList extends Canvas implements ActionListener
{
    public LinkedList list = new LinkedList();
    public Timer timer=new Timer(450,this);
    public static PCB tempPcb=null;

    ProcList()
    {
        this.setBounds(741, 66, 398, 528);
        this.setBackground(Color.lightGray);
        timer.start();
    }

    public void add(PCB p)
    {
        list.add(p);
    }

    public void actionPerformed(ActionEvent e)
    {
        repaint();
    }

    public void paintRecord(Graphics g ,PCB pcb ,int num,boolean filter)//绘制第num条记录
    {
        if(filter)
        {
            if(pcb.state=='B'||pcb.state=='C'||pcb.state=='D')
                list.remove(pcb);
        }
        int x=0;
        int y=num*20+19;
        int w=this.getWidth()-1;
        String s="";
        switch(pcb.state)
        {
            case 'W':s="就绪";g.setColor(Color.blue);break;
            case ' ':s="创建";g.setColor(Color.ORANGE);break;
            case 'R':s="执行";g.setColor(Color.red);break;
            case 'C':s="完成";g.setColor(Color.gray);break;
            case 'I':s="抢占";g.setColor(Color.pink);break;
            case 'B':s="阻塞";g.setColor(Color.yellow);break;
            case 'D':s="死亡";g.setColor(Color.cyan);break;
            case 'U':s="唤醒";g.setColor(Color.green);break;
        }        
        g.fillRect(x, y+1, w, 20);
        g.setColor(Color.black);
        g.drawString(String.valueOf(pcb.pid), 3, y+15);
        g.drawString(String.valueOf(pcb.name), 28, y+15);
        g.drawString(String.valueOf(pcb.prior), 143, y+15);        
        g.drawString(s, 173, y+15);
        g.drawString(String.valueOf((int)(pcb.exeTime/(double)pcb.lifeTime*100))+"%", 203, y+15);
        g.drawString(String.valueOf(pcb.round), 238, y+15);
        g.drawString(String.valueOf(pcb.exeTime), 263, y+15);
        g.drawString(String.valueOf(pcb.lifeTime), 293, y+15);
        g.drawString(String.valueOf(pcb.size)+"K", 323, y+15);
        g.drawString("0x"+String.valueOf(pcb.addr), 357, y+15);
        g.setColor(Color.black);
        g.drawLine(0, y+20, w+1, y+20);
    }

    @Override
    public void paint(Graphics g)
    {
        for(int i=0;i<list.size();i++)
        {
            PCB pcb=(PCB)list.get(i);
            this.paintRecord(g, pcb, i,true);
        }
        if(tempPcb!=null)
            paintRecord(g,tempPcb,20,false);
        ///////////////////////////////////////////
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, this.getWidth(), 19);
        g.setColor(Color.black);
        g.drawString("PID", 3, 15);
        g.drawLine(25,0,25,528);
        g.drawString("映像名称", 53, 15);
        g.drawLine(140,0,140,528);
        g.drawString("prior", 143, 15);
        g.drawLine(170,0,170,528);
        g.drawString("状态", 173, 15);
        g.drawLine(200,0,200,528);
        g.drawString("进度", 203, 15);
        g.drawLine(235,0,235,528);
        g.drawString("rnd", 238, 15);
        g.drawLine(260,0,260,528);
        g.drawString("exeT", 263, 15);
        g.drawLine(290,0,290,528);
        g.drawString("lifeT", 293, 15);
        g.drawLine(320,0,320,528);
        g.drawString("内存", 323, 15);
        g.drawLine(355,0,355,528);
        g.drawString("地址", 365, 15);
        g.drawLine(0, 19,this.getWidth(), 19);        
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
