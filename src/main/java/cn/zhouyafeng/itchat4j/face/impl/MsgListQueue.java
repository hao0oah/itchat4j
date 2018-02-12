package cn.zhouyafeng.itchat4j.face.impl;

import cn.zhouyafeng.itchat4j.face.MsgQueue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * User: haoxiongshan
 * Date: 2018/2/11
 */
public class MsgListQueue<E> implements MsgQueue<E>{

    private Queue<E>  queue = new LinkedList<>();
    private static MsgQueue msgQueue;

    private MsgListQueue(){}

    public synchronized static MsgQueue getInstance(){
        if(msgQueue == null){
            msgQueue = new MsgListQueue();
        }
        return msgQueue;
    }

    /**
     * 加入消息队列
     */
    @Override
    public synchronized boolean offer(E e){
        return queue.offer(e);
    }

    @Override
    public synchronized E pull(){
        return queue.poll();
    }

    @Override
    public synchronized E peek(){
        return queue.peek();
    }
}
