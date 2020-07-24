package deadlock;

/**
 * 描述:
 *
 * @Author: zhangchao22
 * @Date: 2020/7/23 10:42
 **/
public class TransferMoneyDeadLockEx implements Runnable{
    int flag;
    static Account from = new Account("jack", 1000);
    static Account to = new Account("rose",1000);

    public static void main(String[] args) throws InterruptedException {
        TransferMoneyDeadLock runnable = new TransferMoneyDeadLock();

        TransferMoneyDeadLock runnable2 = new TransferMoneyDeadLock();
        runnable.flag=1;
        runnable2.flag=2;

        Thread thread = new Thread(runnable);
        Thread thread2 = new Thread(runnable2);

        thread.start();
        thread2.start();

        thread.join();
        thread2.join();
        System.out.println(from.username+"余额:"+from.amount);
        System.out.println(to.username+"余额:"+to.amount);

    }
    @Override
    public void run() {

        if (flag ==1){
            transfer(from , to ,500);
        }
        if (flag ==2){
            transfer(to , from ,500);
        }
    }

    private void transfer(Account from, Account to, double amount) {
        synchronized (from){
//            try {
//                TimeUnit.MILLISECONDS.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            if (from.amount < amount){
                throw new RuntimeException("余额不足");
            }
            synchronized (to){
                from.amount = from.amount - amount;
                to.amount = to.amount + amount;
                System.out.println(from.username+"成功转账"+amount+"元给"+to.username);
            }
        }
    }

    static class Account{
        private String username;
        private double amount;

        public Account(String username, double amount) {
            this.username = username;
            this.amount = amount;
        }
    }
}
