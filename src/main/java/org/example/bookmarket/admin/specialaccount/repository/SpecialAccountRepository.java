package org.example.bookmarket.admin.specialaccount.repository;

import org.example.bookmarket.admin.specialaccount.entity.SpecialAccount;
import org.example.bookmarket.admin.specialaccount.entity.SpecialAccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialAccountRepository extends JpaRepository<SpecialAccount, Long> {
    List<SpecialAccount> findByStatusOrderByNicknameAsc(SpecialAccountStatus status);
    List<SpecialAccount> findAllByOrderByNicknameAsc();
}