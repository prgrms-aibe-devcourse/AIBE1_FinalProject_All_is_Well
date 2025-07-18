package org.example.bookmarket.admin.specialaccount.dto;

import org.example.bookmarket.admin.specialaccount.entity.SpecialAccountStatus;

public record SpecialAccountRequest(
        Long id,
        String nickname,
        SpecialAccountStatus status
) {
    public SpecialAccountRequest() {
        this(null, null, null);
    }
}