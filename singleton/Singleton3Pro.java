package singleton;

import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;

/**
 * 描述:
 *  证明饱汉模式线程不安全
 * @Author: zhangchao22
 * @Date: 2020/7/22 10:27
 **/
public class Singleton3Pro {
    private static Singleton3Pro INSTANCE=null;
    private int num;

    public Singleton3Pro(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public static Singleton3Pro getInstance(int i){
        try {
            //让线程不安全现象出现几率增大
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(INSTANCE == null){
            INSTANCE =  new Singleton3Pro(i);
        }
        return INSTANCE;
    }

    public static void main(String[] args) {
         for (int i=1; i<=20; i++)
          {
              int num = i;
              new  Thread(() -> {
                  Singleton3Pro instance = Singleton3Pro.getInstance(num);

                  if ( instance.getNum()!=1){
                      //出现非预期的结果
                      System.out.println(Thread.currentThread().getName()+"--"+instance.getNum());
                  }
              }, valueOf(i)).start();
         }
    }
}
