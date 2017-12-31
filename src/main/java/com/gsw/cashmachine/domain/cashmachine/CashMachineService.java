package com.gsw.cashmachine.domain.cashmachine;

import com.gsw.cashmachine.authentication.request.AccountRequest;
import com.gsw.cashmachine.authentication.response.AccountResponse;
import com.gsw.cashmachine.domain.user.User;

/**
 * Created by eduardo on 29/12/17.
 */
public interface CashMachineService {

    AccountResponse cashout(final AccountRequest accountRequest);

    AccountResponse deposit(AccountRequest request);

    AccountResponse getBalance(String username);
}
