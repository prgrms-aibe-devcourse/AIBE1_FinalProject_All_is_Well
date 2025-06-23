
package org.example.bookmarket.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {
    private Long usedBookId;
    private String title;
    private String imageUrl;
    private Integer price;
}
