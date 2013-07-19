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
public class Queue
{
    public int x;
    public int y;

    public LinkedList list = new LinkedList();
    public int prior ;
    public int round ;

    public Queue(int p,int r)
    {
        prior=p;
        round =r;
    }

    public void add(PCB pcb)
    {
        int a=x+list.size()*55+15;
        int b=y-10;
        pcb.setLocation(a, b);
        pcb.prior=prior;//不是没有必要。降级的时候要修改
        if(pcb.state=='U')//是拥塞进程被唤醒,不要修改时间片
        {
            pcb.state='W';
            list.add(pcb);
            return ;
        }
        pcb.round=round;
        list.add(pcb);
    }

    public void refresh(PCB pcb)//仅仅更新pcb的信息，不删除
    {
        pcb.round=round;
        pcb.state='R';
    }

    public void remove(PCB pcb)
    {
        list.remove(pcb);
        for(int i=0;i<list.size();i++)
        {
            PCB p=(PCB)list.get(i);
            int a=p.getX();
            int b=p.getY();
            p.setLocation(a-55, b);
        }
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }
}















