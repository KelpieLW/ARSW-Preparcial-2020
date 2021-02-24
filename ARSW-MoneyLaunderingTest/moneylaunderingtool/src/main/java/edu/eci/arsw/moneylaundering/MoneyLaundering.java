package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;
    private TransactionCheck[] transactionCheckThreads;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processTransactionData(int numThread)
    {

        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        int transactionsPerThread = amountOfFilesTotal/numThread;
        int reminderTransactions=amountOfFilesTotal%numThread;
        int limitCounter=0;
        transactionCheckThreads=new TransactionCheck[numThread];
        for (int i = 0; i < numThread; i++) {
            transactionCheckThreads[i]=new TransactionCheck(limitCounter, limitCounter+transactionsPerThread,transactionFiles,transactionAnalyzer,transactionReader,amountOfFilesProcessed);


            if (i==numThread-1){
                transactionCheckThreads[i]=new TransactionCheck(limitCounter, limitCounter+transactionsPerThread+reminderTransactions,transactionFiles,transactionAnalyzer,transactionReader,amountOfFilesProcessed);
            }

            limitCounter+=transactionsPerThread;
        }
        for (int i = 0; i < transactionCheckThreads.length; i++) {

            transactionCheckThreads[i].start();
        }

    }

    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public static void main(String[] args)
    {
        MoneyLaundering moneyLaundering = new MoneyLaundering();
//        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData(5));
//        processingThread.start();
        moneyLaundering.processTransactionData(20);

        while(true)
        {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
                break;

            if (line.isEmpty()){
                for (TransactionCheck threadInList: moneyLaundering.transactionCheckThreads) {
                    threadInList.wakeUpSleep();
                }

            }

            String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";

            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
            System.out.println(message);


        }

    }


}
