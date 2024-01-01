package io.woori.account.wooriaccount.dto.user;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LoginRequestDTO {
    private String email; // 이메일 (로그인 id)
    private String pwd; // 로그인 비번
}
