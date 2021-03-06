package com.gsw.cashmachine.domain.account;

import com.gsw.cashmachine.authentication.request.AccountRequest;
import com.gsw.cashmachine.domain.user.UserNotFoundException;

/**
 * @author Eduardo Alves
 * @version 1.0
 */
public interface AccountService {

    Account checkBalanceByUsername(final AccountRequest accountRequest) throws AccountException, UserNotFoundException;

    void cashOut(final AccountRequest accountRequest) throws AccountException, UserNotFoundException;

    void deposit(final AccountRequest accountRequest) throws UserNotFoundException;

    Double getBalance(final String username) throws UserNotFoundException;

    Account findAccountByUsername(final String username) throws UserNotFoundException;
}
