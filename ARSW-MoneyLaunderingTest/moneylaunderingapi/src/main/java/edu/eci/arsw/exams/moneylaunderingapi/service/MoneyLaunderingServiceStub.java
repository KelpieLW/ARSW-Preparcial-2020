package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Qualifier("MoneyLaundering")
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {

    private final List<SuspectAccount> suspectAccounts= Collections.synchronizedList(new ArrayList<SuspectAccount>());
    public MoneyLaunderingServiceStub(){
        SuspectAccount suspectOne = new SuspectAccount("1212", 1000);
        SuspectAccount suspectTwo = new SuspectAccount("1313", 999);
        SuspectAccount suspectThree = new SuspectAccount("1414", 998);
        suspectAccounts.add(suspectOne);
        suspectAccounts.add(suspectTwo);
        suspectAccounts.add(suspectThree);
    }

    @Override
    public void updateAccountStatus(SuspectAccount suspectAccount) {
        int index=-1;
        for (int i=0; i<suspectAccounts.size();i++) {
            if(suspectAccounts.get(i).getAccountId().equals(suspectAccount.getAccountId())){
                index=i;

            }
        }
        if (index!=-1){
            suspectAccounts.set(index,suspectAccount);
        }
        if (index==-1){
            suspectAccounts.add(suspectAccount);
        }
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId) {
        for (int i = 0; i < suspectAccounts.size(); i++) {
            if (suspectAccounts.get(i).getAccountId().equals(accountId)){
                return suspectAccounts.get(i);

            }
        }
        return null;
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        return suspectAccounts;
    }

    @Override
    public void saveSuspectedAccount(SuspectAccount suspectAccount) {
        boolean flagForRepeated=false;
        int repeatedIndex=-1;
        for (int i = 0; i < suspectAccounts.size(); i++) {
            if (suspectAccounts.get(i).getAccountId().equals(suspectAccount.getAccountId())){
                flagForRepeated=true;


            }
        }
        if (flagForRepeated){
            updateAccountStatus(suspectAccount);
        }

        if (!flagForRepeated) {

            suspectAccounts.add(suspectAccount);
        }
    }
}
