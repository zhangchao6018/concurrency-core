package threadcoreknowledge.stopthreads;

/**
 * 描述：
 *
 * 最佳实践(处理中断的打开方式)：catch了InterruptedExcetion之后的优先选择：在方法签名中抛出异常 那么在run()就会强制try/catch
 *
 * 扩展:处理异常的打开方式,子方法,特别是业务相关代码,建议将异常抛出给顶层调用方处理
 */
public class RightWayStopThreadInProd implements Runnable {

    @Override
    public void run() {
        while (true && !Thread.currentThread().isInterrupted()) {
            System.out.println("do some task...");
            try {
                throwInMethod();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                //保存日志、停止程序
                System.out.println("保存日志");
                e.printStackTrace();
            }
        }
    }

    /**
     * 此方法这代表的是处理业务逻辑的线程任务
     *
     * 这里异常处理的正确打开方式是抛出,让业务调用方去处理
     * 如果try catch
     * 调用方
     * 1.很容易忽略该异常
     * 2.无法处理三方的中断
     *
     * 比如说这是一个VIP用户的方特权法,有一个业务:当用户VIP过期,其他线程会尝试 interrupt() vip线程
     * 这个时候,子方法中try/catch住了,程序就很难处理
     * @throws InterruptedException
     */
    private void throwInMethod() throws InterruptedException {
        System.out.println("do other task...");
            Thread.sleep(2000);
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new RightWayStopThreadInProd());
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }
}
