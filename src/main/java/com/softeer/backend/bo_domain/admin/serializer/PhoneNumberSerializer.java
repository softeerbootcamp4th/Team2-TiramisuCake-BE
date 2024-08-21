package com.softeer.backend.bo_domain.admin.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 전화번호 형식으로 바꿔주는 Serializer 클래스
 */
public class PhoneNumberSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        String formatted = value.replaceAll("(\\d{3})(\\d{4})(\\d+)", "$1-$2-$3");
        gen.writeString(formatted);
    }
}