package com.softeer.backend.global.common.code;


import com.softeer.backend.global.common.response.ResponseDto;

/**
 * 성공 응답 코드를 관리하는 인터페이스
 */
public interface BaseCode {

    ResponseDto.ReasonDto getReason();

    String getCode();

    String getMsg();

}
