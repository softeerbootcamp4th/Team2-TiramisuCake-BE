package com.softeer.backend.fo_domain.draw.test;

import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import org.springframework.stereotype.Component;

public interface ParticipateInterface {
    public DrawModalResponseDto participateDrawEvent(Integer userId);
}
