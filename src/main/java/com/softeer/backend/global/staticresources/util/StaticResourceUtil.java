package com.softeer.backend.global.staticresources.util;

import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.exception.GeneralException;
import com.softeer.backend.global.staticresources.domain.S3Content;
import com.softeer.backend.global.staticresources.domain.TextContent;
import com.softeer.backend.global.staticresources.repository.S3ContentRepository;
import com.softeer.backend.global.staticresources.repository.TextContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 정적 리소스 util 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StaticResourceUtil {

    private final TextContentRepository textContentRepository;
    private final S3ContentRepository s3ContentRepository;

    /**
     * DB에서 정적 텍스트 데이터들을 조회하여 Map으로 반환하는 메서드
     */
    public Map<String, String> getTextContentMap() {
        return textContentRepository.findAll().stream()
                .collect(Collectors.toMap(TextContent::getTextName,
                        textContent -> textContent.getContent().replace("\\n", "\n")));
    }

    /**
     * DB에서 s3 url 데이터들을 조회하여 Map으로 반환하는 메서드
     */
    public Map<String, String> getS3ContentMap() {
        return s3ContentRepository.findAll().stream()
                .collect(Collectors.toMap(S3Content::getFileName, S3Content::getFileUrl));
    }

    /**
     * text의 동적 부분을 바인딩하는 메서드
     */
    public String format(String text, Object... args) {
        return String.format(text, args);
    }

    /**
     * 인자로 넘어온 데이터를 한글로 표현된 요일로 변환하여 반환하는 메서드
     */
    public String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                log.error("Korean day of week is not supported");
                throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}
