package background.process;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述： 继续改
 * MultiThreadsError3错误的原因是synchronized判断条件漏洞
 *
 * 需要在判断条件上加一个条件:if (marked[index] && marked[index - 1])
 *
 * 上一个索引位也为true
 * 由于index=0为特殊情况 需要手动置为true
 *
 * 原因:
 * 如果改index前一位是false 其实是正常情况
 *
 * 大部分情况是不会发生错误的  因此第二个线程执行arked[index] = true时,由于线程可见性,其实都将index是实际值+1的结果,因此该index上一个值=true时说明是非正常情况,发生冲突了
 * index=1  两个线程都++  index变为2(发生错误)
 * 此时synchronized判定marked[index] => marked[2]=true 但是marked[1]为false
 *
 */
public class MultiThreadsError4 implements Runnable {

    static MultiThreadsError4 instance = new MultiThreadsError4();
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
            marked[0] =true;
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
                if (marked[index] && marked[index-1]){
                    wrongCount.incrementAndGet();
                    System.out.println("发生并发错误"+index);
                }
                marked[index] = true;
            }
        }
    }
}
