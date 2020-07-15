package threadcoreknowledge.threadobjectclasscommonmethods.mine;

/**
 * 描述:
 *
 * @Author: zhangchao
 * @Date: 7/14/20 9:39 下午
 **/
public class WaitNotifyPrintOddEveWaitMine {

    public  int count;
    private static Object lock = new Object();


    public static void main(String[] args) {
        WaitNotifyPrintOddEveWaitMine runnable = new WaitNotifyPrintOddEveWaitMine();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                if (runnable.count<100){
                    runnable.print(lock);
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "线程一");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                if (runnable.count<100){
                    runnable.print(lock);
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "线程二");
        thread1.start();
        thread2.start();


    }


    private  void print(Object lock) {
        synchronized (lock){
            if (count<100){
                System.out.println(Thread.currentThread().getName()+count++);
                lock.notify();
            }
        }
    }

}
