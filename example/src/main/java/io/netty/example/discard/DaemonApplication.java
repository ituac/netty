package io.netty.example.discard;

import java.util.concurrent.TimeUnit;

/**
 * Java Daemon
 */

public class DaemonApplication {

    public static void main(String[] args) throws Exception{
        long time = System.nanoTime();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.DAYS.sleep(Long.MAX_VALUE);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        },"Daemon-t1");

        // 设置为守护线程  当我非守护线程全部结束，那么这个程序就会自动关闭
        t1.setDaemon(false);
        t1.start();
        TimeUnit.SECONDS.sleep(5);
        System.out.printf("系统退出,程序执行异常：" + (System.nanoTime() - time)/1000/1000/1000 + "s");


    }


}
