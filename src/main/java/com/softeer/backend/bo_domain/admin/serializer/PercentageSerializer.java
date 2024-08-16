package com.softeer.backend.bo_domain.admin.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PercentageSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // 백분율로 변환하고 % 기호를 붙입니다.
            String formatted = String.format("%.2f%%", value * 100);
            gen.writeString(formatted);
        }
    }
}