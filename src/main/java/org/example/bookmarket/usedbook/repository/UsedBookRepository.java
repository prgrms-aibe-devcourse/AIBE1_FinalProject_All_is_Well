package org.example.bookmarket.usedbook.repository;

import org.example.bookmarket.usedbook.entity.UsedBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

// JpaRepository와 함께 UsedBookRepositoryCustom 인터페이스를 상속받도록 수정
public interface UsedBookRepository extends JpaRepository<UsedBook, Long>, UsedBookRepositoryCustom {
    List<UsedBook> findBySellerId(Long sellerId);

    /**
     * 중고책을 ID 역순으로 최대 4건 조회합니다.
     */
    List<UsedBook> findTop4ByOrderByIdDesc();

    /**
     * 카테고리 기반으로 추천 도서를 조회합니다.
     * @param categoryIds 사용자의 관심 카테고리 ID 목록
     * @param excludedIds 제외할 중고 도서 ID 목록 (찜, 구매 등)
     * @param userId 현재 사용자 ID (자신이 판매하는 상품 제외)
     * @param pageable 페이징 정보 (limit, 정렬)
     * @return 추천 도서 목록
     */
    @Query("SELECT ub FROM UsedBook ub WHERE ub.category.id IN :categoryIds AND ub.id NOT IN :excludedIds AND ub.seller.id <> :userId AND ub.status = 'FOR_SALE' ORDER BY ub.createdAt DESC")
    List<UsedBook> findRecommendationsByCategory(
            @Param("categoryIds") Set<Long> categoryIds,
            @Param("excludedIds") Set<Long> excludedIds,
            @Param("userId") Long userId,
            Pageable pageable
    );

    /**
     * 일반적인 최신 도서를 조회합니다. (추천 도서가 부족할 때 사용)
     * @param excludedIds 제외할 중고 도서 ID 목록
     * @param userId 현재 사용자 ID
     * @param pageable 페이징 정보
     * @return 최신 도서 목록
     */
    @Query("SELECT ub FROM UsedBook ub WHERE ub.id NOT IN :excludedIds AND ub.seller.id <> :userId AND ub.status = 'FOR_SALE' ORDER BY ub.createdAt DESC")
    List<UsedBook> findGeneralRecommendations(
            @Param("excludedIds") Set<Long> excludedIds,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT ub FROM UsedBook ub WHERE ub.seller.nickname IN :nicknames ORDER BY ub.id DESC")
    List<UsedBook> findBySellerNicknames(
            @Param("nicknames") List<String> nicknames,
            Pageable pageable
    );

    @Query("SELECT ub FROM UsedBook ub WHERE ub.seller.nickname NOT IN :nicknames ORDER BY ub.id DESC")
    List<UsedBook> findBySellerNicknamesNotIn(
            @Param("nicknames") List<String> nicknames,
            Pageable pageable
    );
}