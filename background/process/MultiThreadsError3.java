package background.process;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述： 继续改
 * MultiThreadsError2错误的原因是   index++操作前后 和判断冲突synchronized方法无法保证顺序性
 * 因此加入CyclicBarrier cyclicBarrier1  CyclicBarrier cyclicBarrier2
 *
 * 执行如果:
 * 表面上结果是199998
 * 真正运行的次数200000
 * 错误次数100000
 *
 * 错误原因分析:
 * 现在利用因此加入CyclicBarrier控制了两个线程执行synchronized判断标记方法前肯定都执行了++操作
 * synchronized判定方法在由于是原子性的 第一个线程执行完判断为false,并 marked[index] = true  下个线程执行时,总是会判定为true 因此我们看到错误次数总是错误的并且等于100000
 */
public class MultiThreadsError3 implements Runnable {

    static MultiThreadsError3 instance = new MultiThreadsError3();
    int index = 0;
    static AtomicInteger realIndex = new AtomicInteger();
    static AtomicInteger wrongCount = new AtomicInteger();
    static volatile CyclicBarrier cyclicBarrier1 = new CyclicBarrier(2);
    static volatile CyclicBarrier cyclicBarrier2 = new CyclicBarrier(2);

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
            //1.两个线程都到此后 才执行index++
            try {
                cyclicBarrier2.reset();
                cyclicBarrier1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //2.多线程操作
            index++;
            //3.两个线程都执行完index++ 后在此集合
            try {
                cyclicBarrier1.reset();
                cyclicBarrier2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            realIndex.incrementAndGet();
            //4.判断是否发生冲突
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
