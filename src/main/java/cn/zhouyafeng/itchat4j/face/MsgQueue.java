package cn.zhouyafeng.itchat4j.face;

/**
 * 消息队列
 * User: haoxiongshan
 * Date: 2018/2/11
 */
public interface MsgQueue<E> {
    boolean offer(E e);
    E pull();
    E peek();
}
