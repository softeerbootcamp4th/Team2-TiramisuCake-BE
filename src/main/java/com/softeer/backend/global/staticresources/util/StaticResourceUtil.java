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

@Slf4j
@Component
@RequiredArgsConstructor
public class StaticResourceUtil {

    private final TextContentRepository textContentRepository;
    private final S3ContentRepository s3ContentRepository;

    public Map<String, String> getTextContentMap() {
        return textContentRepository.findAll().stream()
                .collect(Collectors.toMap(TextContent::getTextName,
                        textContent -> textContent.getContent().replace("\\n", "\n")));
    }

    public Map<String, String> getS3ContentMap() {
        return s3ContentRepository.findAll().stream()
                .collect(Collectors.toMap(S3Content::getFileName, S3Content::getFileUrl));
    }

    public String format(String text, Object... args) {
        return String.format(text, args);
    }

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
