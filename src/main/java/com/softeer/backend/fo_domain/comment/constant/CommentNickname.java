package com.softeer.backend.fo_domain.comment.constant;

import lombok.Getter;

/**
 * 기대평 닉네임을 관리하는 Enum 클래스
 */
@Getter
public enum CommentNickname {
    NICKNAME_1("곰"),
    NICKNAME_2("코끼리"),
    NICKNAME_3("토끼"),
    NICKNAME_4("기린"),
    NICKNAME_5("돌고래"),
    NICKNAME_6("개구리"),
    NICKNAME_7("고양이"),
    NICKNAME_8("악어"),
    NICKNAME_9("판다"),
    NICKNAME_10("호랑이");

    public static final String NICKNAME_PREFIX = "익명의 ";
    public static final String MY_NICKNAME_SUFFIX = "(나)";

    private final String nickname;

    CommentNickname(String nickname) {
        this.nickname = nickname;
    }

    // 인증 하지 않은 유저의 닉네임 생성 메서드
    public static String getRandomNickname() {
        CommentNickname[] nicknames = values();
        int index = (int) (Math.random() * nicknames.length);
        return NICKNAME_PREFIX + nicknames[index].getNickname();
    }

    // 인증한 유저의 닉네임 생성 메서드
    public static String getMyRandomNickname(int userId) {
        CommentNickname[] nicknames = values();
        int index = userId % nicknames.length;
        return NICKNAME_PREFIX + nicknames[index].getNickname();
    }
}
