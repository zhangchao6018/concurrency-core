package threadcoreknowledge.stopthreads;

/**
 * 描述：最佳实践2：在catch子语句中调用Thread.currentThread().interrupt()来恢复设置中断状态，以便于在后续的执行中，依然能够检查到刚才发生了中断
 * 回到刚才RightWayStopThreadInProd补上中断，让它跳出
 */
public class RightWayStopThreadInProd2 implements Runnable {

    @Override
    public void run() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Interrupted，程序运行结束");
                break;
            }
            reInterrupt();
        }
    }

    /**
     * 子方法,在处理线程中断后重新恢复设置中断
     */
    private void reInterrupt() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            //如果此处不抛出,则在调用方无法得知中断请求
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new RightWayStopThreadInProd2());
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }
}
