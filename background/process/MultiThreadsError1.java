package background.process;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述：     第一种：运行结果出错。
 *
 * 1.AtomicInteger realIndex 统计了真正执行++的次数(当然根据代码也能得知)
 * 2.想定位发生错误的地方(索引),并收集错误次数
 * 因此定义了AtomicInteger wrongCount作为容器收集次数,定义了boolean[] marked收集每次index是否被存储
 *
 * 我们发现实际上我们统计出来的错误次数是不正确的
 *
 * 我们统计出来的是wrongCount  <= 真实的错误次数(200000-instance.index)
 *
 * 分析原因:
 *
 * condition1
 * 假设线程1 和线程2都拿到index=0 并且都在++最终赋值的时候都赋值成1了(冲突了)
 * 线程1执行完++ 且执行完 marked[1] = true标记操作后   线程2拿到CUP\执行权仅执行完++,在想要执行if (marked[index])判断的时候,线程1突然拿到CPU再次执行了++赋值
 * 导致此时线程2本应判断if (marked[1]) 由于线程1再次++师德index变成2了 结果为false  不会进入错误统计逻辑
 * 因此统计结果比真实次数要少
 *
 * condition2
 * marked[index] = true;执行前,另一个线程进行了if (marked[index])判断->本应判定为true的却判定为false了
 */
public class MultiThreadsError1 implements Runnable {

    static MultiThreadsError1 instance = new MultiThreadsError1();
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
            if (marked[index]){
                wrongCount.incrementAndGet();
                System.out.println("发生并发错误"+index);
            }
            marked[index] = true;
        }
    }
}
