package threadcoreknowledge.threadobjectclasscommonmethods.mine;

import java.util.Date;
import java.util.LinkedList;

/**
 * 描述:
 *
 * @Author: zhangchao
 * @Date: 7/14/20 10:22 下午
 **/
public class ProducerAndComsumer {
    public static void main(String[] args) {

        Storage storage = new Storage();

        Producer producer = new Producer(storage);
        Consumer consumer = new Consumer(storage);
        Thread thread1 = new Thread(producer);
        Thread thread2 = new Thread(consumer);
        thread1.start();
        thread2.start();

    }
    public static class Producer implements Runnable {
        private Storage storage;

        public Producer(Storage storage) {
            this.storage = storage;
        }

        @Override
        public void run() {
            for (int i=1; i<=100; i++) {
                storage.put();
            }
        }
    }
    public static class Consumer implements Runnable {
        private Storage storage;

        public Consumer(Storage storage) {
            this.storage = storage;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                storage.take();
            }
        }
    }


    public static class Storage {

        private int maxSize;
        private LinkedList<Date> storage;

        public Storage() {
            this.maxSize = 10;
            this.storage =  new LinkedList();
        }

        public synchronized void put(){
            while (storage.size()==maxSize){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            storage.add(new Date());
            System.out.println("add ...  size:" + storage.size());
            notify();
        }

        public synchronized void take(){
            while (storage.size()==0){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            storage.poll();
            System.out.println("take-----------...  size:" + storage.size());
            notify();
        }




    }

}
