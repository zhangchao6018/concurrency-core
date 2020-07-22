package singleton;

/**
 * 描述：     证明枚举单例模式的安全性
 *
 * 效率：
 *
 * 编码简单
 */
public enum Singleton8Pro {
    INSTANCE(new Object());

    //单例对象
    private Object obj;

    Singleton8Pro(Object obj) {
        this.obj = obj;
    }

    public void whatever() {
        System.out.println(Singleton8Pro.INSTANCE.obj);
    }


    public static void main(String[] args) {
         for (int i=1; i<=2000; i++)
          {
              new  Thread(() -> {
                  Singleton8Pro.INSTANCE.whatever();
             },String.valueOf(i)).start();
         }
    }

}
