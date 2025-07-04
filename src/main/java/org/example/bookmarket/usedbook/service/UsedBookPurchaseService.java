package org.example.bookmarket.usedbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmarket.usedbook.entity.UsedBook;
import org.example.bookmarket.usedbook.repository.UsedBookRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsedBookPurchaseService {

    private final RedissonClient redissonClient;
    private final UsedBookRepository usedBookRepository;

    public void purchase(Long usedBookId) {
        // 1. 각 책마다 고유한 락 키를 생성합니다. (예: "lock:usedbook:101")
        final String lockKey = "lock:usedbook:" + usedBookId;
        final RLock lock = redissonClient.getLock(lockKey);

        try {
            // 2. 락 획득을 시도합니다. (최대 10초 대기, 락 점유 시간은 5초)
            //    - waitTime: 락을 얻기 위해 기다리는 최대 시간
            //    - leaseTime: 락을 점유하는 최대 시간 (이 시간이 지나면 자동 해제)
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);

            // 3. 락 획득에 실패하면 예외를 발생시킵니다.
            if (!isLocked) {
                log.error("책 구매 락 획득 실패. lockKey={}", lockKey);
                throw new RuntimeException("현재 다른 사용자가 구매 중입니다. 잠시 후 다시 시도해주세요.");
            }

            // --- 여기부터가 임계 구역(Critical Section): 오직 한 스레드만 접근 가능 ---
            log.info("락 획득 성공! lockKey={}", lockKey);
            processPurchase(usedBookId);
            // ----------------------------------------------------------------

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락을 기다리는 중 문제가 발생했습니다.", e);
        } finally {
            // 4. 현재 스레드가 락을 점유하고 있다면, 반드시 락을 해제합니다.
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("락 해제. lockKey={}", lockKey);
            }
        }
    }

    // 실제 구매 처리 로직 (트랜잭션 분리)
    @Transactional
    public void processPurchase(Long usedBookId) {
        UsedBook usedBook = usedBookRepository.findById(usedBookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 중고책을 찾을 수 없습니다."));

        // "판매중" 상태일 때만 구매 가능
        if (!"판매중".equals(usedBook.getStatus())) {
            throw new IllegalStateException("이미 판매 완료된 상품입니다.");
        }

        log.info("책 구매 처리 시작. 책 ID: {}, 현재 상태: {}", usedBookId, usedBook.getStatus());

        // 재고 상태를 "판매 완료"로 변경 (실제로는 재고 수량을 차감하는 로직)
        usedBook.setStatus("판매 완료");
        usedBookRepository.save(usedBook);

        // 테스트를 위해 일부러 지연 시간을 줍니다.
        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

        log.info("책 구매 처리 완료. 책 ID: {}, 변경된 상태: {}", usedBookId, usedBook.getStatus());
    }
}