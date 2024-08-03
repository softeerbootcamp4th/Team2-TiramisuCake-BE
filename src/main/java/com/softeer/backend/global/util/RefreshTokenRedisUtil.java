package com.softeer.backend.global.util;

import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.entity.JwtClaimsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RefreshTokenRedisUtil {
    private final StringRedisTemplate stringRedisTemplate;

    // key 에 해당하는 데이터 얻어오는 메서드
    public String getData(String key) {
        return getStringStringValueOperations().get(key);
    }

    // key - value 데이터 설정하는 메서드
    public void setData(String key, String value) {
        getStringStringValueOperations().set(key, value);
    }

    /* key 에 해당하는 데이터 삭제하는 메소드 */
    public void deleteData(String key) {
        this.stringRedisTemplate.delete(key);
    }

    /* key 에 해당하는 데이터 만료기간 설정 메소드 */
    public void setDataExpire(String key, String value, Long duration) {
        Duration expireDuration = Duration.ofSeconds(duration);
        getStringStringValueOperations().set(key, value, expireDuration);
    }

    private ValueOperations<String, String> getStringStringValueOperations() {
        return this.stringRedisTemplate.opsForValue();
    }

    /**
     * Refresh Token을 redis에 저장할 때, 접두사를 붙여서 redis key를 반환하는 메서드
     *
     * @param jwtClaimsDto JWT의 claim 정보
     * @return 일반 유저는 "USER_{id값}", 어드민 유저는 "ADMIN_{id값}"
     */
    public String getRedisKeyForJwt(JwtClaimsDto jwtClaimsDto) {

        String id = String.valueOf(jwtClaimsDto.getId());
        RoleType roleType = jwtClaimsDto.getRoleType();

        return roleType.getRedisKeyPrefix() + id;
    }

}
