package com.example.account.service;

import com.example.account.client.AuthServiceClient;
import com.example.account.client.StatisticsServiceClient;
import com.example.account.domain.Account;
import com.example.account.domain.Currency;
import com.example.account.domain.Saving;
import com.example.account.domain.User;
import com.example.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatisticsServiceClient statisticsServiceClient;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account findByName(String accountName) {
        Assert.hasLength(accountName);
        return accountRepository.findByName(accountName);
    }

    @Override
    public Account create(User user) {
        Account existing = accountRepository.findByName(user.getUsername());
        Assert.isNull(existing, "account already exists: " + user.getUsername());

        authServiceClient.createUser(user);

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(0));
        saving.setCurrency(Currency.getDefault());
        saving.setInterest(new BigDecimal(0));
        saving.setDeposit(false);
        saving.setCapitalization(false);

        Account account = new Account();
        account.setName(user.getUsername());
        account.setLastSeen(new Date());
        account.setSaving(saving);

        accountRepository.save(account);

        log.info("new account has been created: " + account.getName());

        return account;
    }

    @Override
    public void saveChanges(String name, Account update) {
        Account account = accountRepository.findByName(name);
        Assert.notNull(account, "can't find account with name " + name);

        account.setIncomes(update.getIncomes());
        account.setExpenses(update.getExpenses());
        account.setSaving(update.getSaving());
        account.setNote(update.getNote());
        account.setLastSeen(new Date());
        accountRepository.save(account);

        log.debug("account {} changes has been saved", name);

        statisticsServiceClient.updateStatistics(name, account);
    }
}
