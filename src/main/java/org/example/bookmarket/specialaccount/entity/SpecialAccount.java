package org.example.bookmarket.specialaccount.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bookmarket.common.TimeEntity;

@Entity
@Table(name = "special_account")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpecialAccount extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialAccountStatus status = SpecialAccountStatus.ACTIVE;

    @Builder
    public SpecialAccount(String nickname, SpecialAccountStatus status) {
        this.nickname = nickname;
        if (status != null) {
            this.status = status;
        }
    }

    public void changeStatus(SpecialAccountStatus status) {
        this.status = status;
    }
}