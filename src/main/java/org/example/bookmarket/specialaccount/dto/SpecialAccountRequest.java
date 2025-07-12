package org.example.bookmarket.specialaccount.dto;

import org.example.bookmarket.specialaccount.entity.SpecialAccountStatus;

public record SpecialAccountRequest(
        Long id,
        String nickname,
        SpecialAccountStatus status
) {
    public SpecialAccountRequest() {
        this(null, null, null);
    }
}