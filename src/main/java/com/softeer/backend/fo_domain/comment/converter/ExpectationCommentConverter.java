package com.softeer.backend.fo_domain.comment.converter;

import com.softeer.backend.fo_domain.comment.constant.ExpectationComment;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 기대평 Enum 객체와 comment 값을 서로 변환해주는 컨버터 클래스
 */
@Converter(autoApply = true)
public class ExpectationCommentConverter implements AttributeConverter<ExpectationComment, String> {

    @Override
    public String convertToDatabaseColumn(ExpectationComment attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getComment();
    }

    @Override
    public ExpectationComment convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        for (ExpectationComment expectationComment : ExpectationComment.values()) {
            if (expectationComment.getComment().equals(dbData)) {
                return expectationComment;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + dbData);
    }
}