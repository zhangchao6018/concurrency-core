package threadcoreknowledge.threadobjectclasscommonmethods;

/**
 * 描述：     WaitNotifyAll基础上 强调notifyAll虽然会唤醒所有持有该monitor锁的等待线程,但是这个过程是一个一个线程执行的
 */
public class WaitNotifyAll2 implements Runnable {

    private static final Object resourceA = new Object();


    public static void main(String[] args) throws InterruptedException {
        Runnable r = new WaitNotifyAll2();
        Thread threadA = new Thread(r);
        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (resourceA) {
                    System.out.println(Thread.currentThread().getName() + " got resourceA lock.");
                    try {
                        System.out.println(Thread.currentThread().getName() + " waits to start.");
                        resourceA.wait();
                        System.out.println(Thread.currentThread().getName() + "'s waiting to end.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread threadC = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (resourceA) {
                    resourceA.notifyAll();
//                    resourceA.notify();
                    System.out.println("ThreadC notified.");
                }
            }
        });
        threadA.start();
        threadB.start();
        Thread.sleep(200);
        threadC.start();
    }
    @Override
    public void run() {
        synchronized (resourceA) {
            System.out.println(Thread.currentThread().getName()+" got resourceA lock.");
            try {
                System.out.println(Thread.currentThread().getName()+" waits to start.");
                resourceA.wait();
                System.out.println(Thread.currentThread().getName()+"'s waiting to end.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
