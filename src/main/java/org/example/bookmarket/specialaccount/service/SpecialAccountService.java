package org.example.bookmarket.specialaccount.service;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.specialaccount.dto.SpecialAccountRequest;
import org.example.bookmarket.specialaccount.entity.SpecialAccount;
import org.example.bookmarket.specialaccount.entity.SpecialAccountStatus;
import org.example.bookmarket.specialaccount.repository.SpecialAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialAccountService {
    private final SpecialAccountRepository repository;

    @Transactional(readOnly = true)
    public List<SpecialAccount> getAccounts(boolean all) {
        if (all) {
            return repository.findAllByOrderByNicknameAsc();
        }
        return repository.findByStatusOrderByNicknameAsc(SpecialAccountStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<String> getActiveNicknames() {
        return repository.findByStatusOrderByNicknameAsc(SpecialAccountStatus.ACTIVE)
                .stream()
                .map(SpecialAccount::getNickname)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveAccount(SpecialAccountRequest request) {
        SpecialAccount account;
        if (request.id() != null) {
            account = getAccount(request.id());
        } else {
            account = SpecialAccount.builder().build();
        }
        account.setNickname(request.nickname());
        account.setStatus(request.status() != null ? request.status() : SpecialAccountStatus.ACTIVE);
        repository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void toggleAccountStatus(Long id) {
        SpecialAccount acc = getAccount(id);
        if (acc.getStatus() == SpecialAccountStatus.ACTIVE) {
            acc.changeStatus(SpecialAccountStatus.INACTIVE);
        } else {
            acc.changeStatus(SpecialAccountStatus.ACTIVE);
        }
    }

    @Transactional(readOnly = true)
    public SpecialAccount getAccount(Long id) {
        return repository.findById(id).orElseThrow();
    }
}