package edu.eci.arsw.exams.moneylaunderingapi;


import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import edu.eci.arsw.exams.moneylaunderingapi.service.MoneyLaunderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MoneyLaunderingController
{
    @Autowired
    @Qualifier("MoneyLaundering")

    MoneyLaunderingService moneyLaunderingService;

    @RequestMapping( value = "/fraud-bank-accounts")
    public List<SuspectAccount> offendingAccounts() {

        return moneyLaunderingService.getSuspectAccounts();
    }

    @PostMapping("/fraud-bank-accounts")
    public ResponseEntity<?> addSuspectAccount(@RequestBody SuspectAccount suspectAccount){
        try{
            moneyLaunderingService.saveSuspectedAccount(suspectAccount);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e) {
            throw e;
        }
    }


}
