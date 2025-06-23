
package org.example.bookmarket.profile.mapper;

import com.bukgeokbukgeok.domain.user.User;
import com.bukgeokbukgeok.profile.dto.ProfileResponse;

import java.util.stream.Collectors;

public class ProfileMapper {

    public static ProfileResponse toResponse(User user) {
        return ProfileResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .interestCategories(
                    user.getUserCategories().stream()
                        .map(uc -> uc.getCategory().getName())
                        .collect(Collectors.toList())
                )
                .build();
    }
}
