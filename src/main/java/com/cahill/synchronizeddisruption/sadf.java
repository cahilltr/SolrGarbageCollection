package com.cahill.synchronizeddisruption;

import java.util.concurrent.TimeUnit;

public class sadf {

    public static void main(String[] args) throws InterruptedException {

        MyExecutor myExecutor = new MyExecutor();

        myExecutor.submit(() -> {System.out.println("Trey");},5, 5, TimeUnit.SECONDS);

        myExecutor.submit(() -> {System.out.println("Trey2");},5, 5, TimeUnit.SECONDS);


        Thread.sleep(1000000);
    }

}
