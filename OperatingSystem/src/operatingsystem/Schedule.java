/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package operatingsystem;

import java.util.LinkedList;

/**
 *
 * @author fanzhidong
 */
public class Schedule
{
    public int x=50;
    public int y=100;

    public static LinkedList queueList=new LinkedList();
    public static PCB runPcb =null;
    public static LinkedList completeList=new LinkedList();

    public static boolean busy =false;//拥塞

    public static void activeFirstPcb()//获取第一个PCB，并激活
    {
        for(int i=0;i<4;i++)
        {
            Queue q=(Queue)queueList.get(i);
            if(!q.isEmpty())
            {
                PCB p=(PCB)q.list.get(0);
                runPcb= p;
                runPcb.state='R';
                break;
            }
        }
    }

    public Schedule(int r[])//r的要求4个元素
    {
        for(int i=0;i<4;i++)
        {
            Queue q=new Queue(i,r[i]);
            q.x=x+25;
            q.y=y+45+50*i;
            queueList.add(q);
        }
    }

    public int getPriorListSize(int prior)
    {
        Queue q=(Queue)queueList.get(prior);
        return q.list.size();
    }


    public static boolean allowProc() // 允许产生进程
    {
        if(busy)//正在拥塞
        {
            for (int i = 0; i < 4; i++)
            {
                Queue q = (Queue) queueList.get(i);
                if (q.list.size() > 2)//always拥塞
                {
                    return false;
                }
            }
            busy=false;
            return true;
        }
        else
        {
            for (int i = 0; i < 4; i++)
            {
                Queue q = (Queue) queueList.get(i);
                if (q.list.size()>=5)//拥塞
                {
                    busy=true;
                    return false;
                }
            }
            return true;
        }
    }

    public void loadProc (PCB pcb) //载入proc
    {
        if(runPcb!=null&&runPcb.prior>pcb.prior)
        {
            runPcb.state='I';//高优先级抢占
            //runPcb.isIntr=true;//高优先级抢占
        }
        Queue q=(Queue)queueList.get(pcb.prior);
        q.add(pcb);

        activeFirstPcb();
    }

    public void lowerProc(PCB pcb)//对proc降级
    {
        Queue q=(Queue)queueList.get(pcb.prior);
        if(pcb.prior==3)//最后一级，不变
        {
            q.refresh(pcb);
            return;
        }
        q.remove(pcb);
        q=(Queue)queueList.get(pcb.prior+1);
        q.add(pcb);//移动
    }

    public void completeProc(PCB pcb)
    {
        Queue q=(Queue)queueList.get(pcb.prior);
        q.remove(pcb);
        int a=x+40+completeList.size()*55;
        int b=y+485;
        pcb.setLocation(a, b);
        completeList.add(pcb);     
    }
}



























