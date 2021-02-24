package edu.eci.arsw.moneylaundering;

import edu.eci.arsw.moneylaundering.Transaction;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class TransactionCheck extends Thread{
    private int limSuperior;
    private int limInferior;
    private List <File> transactionFiles;
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private AtomicInteger amountOfFilesProcessed;
    private AtomicBoolean paused ;

    public TransactionCheck (int limInferior, int limSuperior, List<File> transactionFiles, TransactionAnalyzer transactionAnalyzer, TransactionReader transactionReader, AtomicInteger amountOfFilesProcessed){
        this.limInferior=limInferior;
        this.limSuperior=limSuperior;
        this.transactionFiles=transactionFiles;
        this.transactionAnalyzer=transactionAnalyzer;
        this.transactionReader=transactionReader;
        this.amountOfFilesProcessed=amountOfFilesProcessed;
        this.paused=new AtomicBoolean(false);
    }
    @Override
    public void run(){




        for (int i = limInferior; i < limSuperior; i++) {

            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFiles.get(i));
            for (Transaction transaction : transactions) {
                while(paused.get()){
                    synchronized (this){
                        try {

                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.getAndIncrement();
        }




    }

    public synchronized void wakeUpSleep() {
        if(paused.get()) {
            notify();
        }
        paused.getAndSet(!paused.get());


    }

}
