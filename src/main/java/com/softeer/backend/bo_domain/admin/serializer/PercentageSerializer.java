package com.softeer.backend.bo_domain.admin.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 'xx.xx%' 형식으로 퍼센트 값을 변환하는 Serializer 클래스
 */
public class PercentageSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {

            String formatted = String.format("%.2f%%", value * 100);
            gen.writeString(formatted);
        }
    }
}