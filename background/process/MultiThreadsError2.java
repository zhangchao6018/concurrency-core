package background.process;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述： 改bug出现新bug
 *
 * MultiThreadsError1 的问题出现原因是判定方法不具备原子性->因此这里尝试用synchronized
 *
 * 结果:
 * 表面上结果是199994
 * 真正运行的次数200000
 * 错误次数2834
 *
 *
 * 原因分析
 * 发生多线程错误时
 * 假如线程1 & 线程2 同时将index++后修改成1(冲突了)
 *
 * 线程1在执行完synchronized方法,线程2拿到CPU也要执行synchronized方法时,此时肯定是进了if (marked[index])判断标记为错误的逻辑(这一步是没错的) 但是在线程2执行执行 marked[index] = true;时
 * 恰好线程1拿到CPU 再次执行++,使得index变成2 ,线程2于是继续执行 本应由于marked[1] = true的操作变成了marked[2] = true (synchronized线程共享资源可见性) 这会导致线程1继续下一次错误判定时,本应判断为false的确判定为true
 *
 * 因此统计出的错误次数多于实际
 *
 */
public class MultiThreadsError2 implements Runnable {

    static MultiThreadsError2 instance = new MultiThreadsError2();
    int index = 0;
    static AtomicInteger realIndex = new AtomicInteger();
    static AtomicInteger wrongCount = new AtomicInteger();

    final boolean[] marked = new boolean[10000000];

    public static void main(String[] args) throws InterruptedException {

        Thread thread1 = new Thread(instance);
        Thread thread2 = new Thread(instance);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("---------------------------------");
        System.out.println("表面上结果是" + instance.index);
        System.out.println("真正运行的次数" + realIndex.get());
        System.out.println("错误次数" + wrongCount.get());

    }

    @Override
    public void run() {
        for (int i = 0; i < 100000; i++) {
            index++;
            realIndex.incrementAndGet();
            //判断是否发生冲突
            synchronized (instance){
                if (marked[index]){
                    wrongCount.incrementAndGet();
                    System.out.println("发生并发错误"+index);
                }
                marked[index] = true;
            }
        }
    }
}
