package singleton;

/**
 * 描述：     双重检查（推荐面试使用）
 * 1.为什么推荐
 *  线程安全;
 *  延迟加载;
 *  效率较高
 * 2.单check为什么不行
 * 3.synchronized放到方法上行不行
 *  可以,但是性能不行
 * 4.为什么要用volatile
 *  新建对象实际有三个步骤(CPU有重排序  有可能执行顺序是1,3,2  ->空指针)
 *      1.创建空对象
 *      2.调用构造方法
 *      3.赋值
 *  重排序会带来空指针
 *  防止重排序
 *
 *
 */
public class Singleton6 {

    private volatile static Singleton6 instance;

    private Singleton6() {

    }

    public static Singleton6 getInstance() {
        if (instance == null) {
            synchronized (Singleton6.class) {
                if (instance == null) {
                    instance = new Singleton6();
                }
            }
        }
        return instance;
    }
}
