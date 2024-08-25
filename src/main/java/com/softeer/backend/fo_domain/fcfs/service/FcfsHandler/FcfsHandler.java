package com.softeer.backend.fo_domain.fcfs.service.FcfsHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsRequestDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;

public interface FcfsHandler {

    public FcfsResultResponseDto handleFcfsEvent(int userId, int round, FcfsRequestDto fcfsRequestDto) throws JsonProcessingException;

}
