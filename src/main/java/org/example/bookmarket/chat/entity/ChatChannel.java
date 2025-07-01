package org.example.bookmarket.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bookmarket.user.entity.User;
import org.example.bookmarket.usedbook.entity.UsedBook;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @ManyToOne
    @JoinColumn(name = "used_book_id")
    private UsedBook relatedUsedBook;

    private LocalDateTime lastMessageAt;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastMessageAt = now;
    }

    public void updateLastMessageAt(LocalDateTime now) {
        this.lastMessageAt = now;
    }
}