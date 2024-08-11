package com.softeer.backend.global.staticresources.constant;

import lombok.Getter;

@Getter
public enum StaticText {

    EVENT_PERIOD("%s ~ %s"),
    FCFS_INFO("매주 %s, %s %s시 선착순 %s명"),
    FCFS_TITLE("'24시 내 차' 이벤트"),
    FCFS_CONTENT("하단의 The new IONIQ 5 정보를 바탕으로 빠르게 문장 퀴즈를 맞춰\n" +
            "24시간 렌트권과 신차 할인권을 얻을 수 있어요."),

    TOTAL_DRAW_WINNER("추첨 %s명"),
    REMAIN_DRAW_COUNT("남은 경품 %s개"),
    DRAW_TITLE("매일 복권 긁고 경품 받기"),
    DRAW_CONTENT("이벤트 기간 동안 추첨을 통해 아이패드 pro 11인치, 현대백화점 10만원권, 1만원권을 드려요.\n" +
            "일주일 연속 참여 시, 스타벅스 쿠폰을 무조건 받을 수 있어요."),

    MAIN_TITLE("The new IONIQ 5"),
    MAIN_SUBTITLE("새롭게 돌아온 The new IONIQ 5를 소개합니다"),

    INTERIOR_TITLE("Interior"),
    INTERIOR_SUBTITLE("내부 인테리어"),
    INTERIOR_IMAGE_TITLE("Living Space"),
    INTERIOR_IMAGE_CONTENT("편안한 거주 공간 (Living Space) 테마를 반영하여 더 넓은 실내 공간을 즐길 수 있도록 연출합니다."),
    INTERIOR_OPENNESS_TITLE("개방감"),
    INTERIOR_OPENNESS_SUBTITLE("개방감과 와이드한 이미지 제공"),
    INTERIOR_OPENNESS_CONTENT("심리스 스타일의 12.3인치 LCD 클러스터는 탁월한 개방감으로 즐거운 드라이빙 환경을 제공합니다.\n" +
            "클러스터와 인포테인먼트 시스템에 일체형 커버글래스를 적용하여 와이드한 이미지를 제공합니다."),

    INTERIOR_WELLNESS_TITLE("웰니스"),
    INTERIOR_WELLNESS_SUBTITLE("웰니스와 친환경"),
    INTERIOR_WELLNESS_CONTENT("혼커버, 스위치, 스티어링 휠, 도어 등에 유채꽃과 옥수수에서 추출한 성분 약 10%가 함유된 바이오 페인트를 이용했습니다.\n" +
            "시트와 도어 트림에는 재활용 투명 PET병을 재활용한 원사 약 20%의 섬유가 사용됐습니다."),

    PERFORMANCE_TITLE("Performance"),
    PERFORMANCE_SUBTITLE("주행성능"),
    PERFORMANCE_IMAGE_TITLE("Large Capacity Battery"),
    PERFORMANCE_IMAGE_CONTENT("항속형 대용량 배터리를 적용하여 주행 가능 거리를 높였습니다."),
    PERFORMANCE_BRAKING_TITLE("에너지 효율"),
    PERFORMANCE_BRAKING_SUBTITLE("회생 제동 시스템"),
    PERFORMANCE_BRAKING_CONTENT("스티어링 휠의 패들쉬프트를 통해 회생제동 수준을 단계별로 조작할 수 있어\n" +
            "브레이크 및 가족 페달 작동을 최소화하여 에너지 효율을 높일 수 있습니다."),

    PERFORMANCE_DRIVING_TITLE("주행성능"),
    PERFORMANCE_DRIVING_SUBTITLE("주행 성능"),
    PERFORMANCE_DRIVING_CONTENT("1회 충전 주행 가능 거리: 485km (2WD, 19인치, 빌트인 캠 미적용 기준)\n" +
            "최고 출력 / 최대 토크: 239 kW (325 PS) / 605 Nm\n" +
            "84.0 kWh 대용량 배터리를 장착하여 보다 여유 있는 장거리 주행이 가능합니다."),

    CHARGING_TITLE("Charging"),
    CHARGING_SUBTITLE("급속 충전"),
    CHARGING_IMAGE_TITLE("V2L/Charging"),
    CHARGING_IMAGE_CONTENT("차량 외부로 전력을 공급할 수 있는 V2L 기능과 쉽고 빠르게 충전 관련 서비스는 사용자에게 새로운 경험을 제공합니다."),
    CHARGING_FAST_TITLE("초급속 충전"),
    CHARGING_FAST_SUBTITLE("18분 초급속 충전 경험"),
    CHARGING_FAST_CONTENT("400V/800V 멀티 급속 충전 시스템으로 다양한 충전 인프라를 사용할 수 있으며,\n" +
            "급속 충전기(350kW) 사용 시 18분 이내에 배터리 용량의 10%에서 80%까지 충전이 가능합니다."),

    CHARGING_V2L_TITLE("실외/실내\n" +
            "V2L"),
    CHARGING_V2L_SUBTITLE("실외/실내 V2L"),
    CHARGING_V2L_CONTENT("차량 외부에서도 실외 V2L 기능을 통해 다양한 전자기기 사용이 가능합니다.\n" +
            "2열 시트 하단의 실내 V2L을 사용하여 차량 내부에서 배터리 걱정 없이 전자기기 사용이 가능합니다."),

    SAFE_TITLE("Safe, Convenient"),
    SAFE_SUBTITLE("안전성과 편리함"),
    SAFE_IMAGE_TITLE("Safe & Convenient Environment"),
    SAFE_IMAGE_CONTENT("다양한 안전, 편의 기술로 편리하고 안전한 드라이빙 환경을 제공합니다."),
    SAFE_DRIVING_TITLE("주행 안전"),
    SAFE_DRIVING_SUBTITLE("도로 주행 중 안전"),
    SAFE_DRIVING_CONTENT("고속도로 및 자동차 전용도로 주행 시 도로 상황에 맞춰 안전한 속도로 주행하도록 도와주며,\n" +
            "안전속도 구간, 곡선 구간, 진출입로 진입 전에 자동으로 속도를 줄이고 해당 구간을 지나면 원래 설정한 속도로 복귀합니다.\n" +
            "일정 속도 이상으로 주행 시, 스티어링 휠을 잡은 상태에서 방향지시등 스위치를 변경하고자 하는 차로 방향으로 자동으로 움직입니다."),

    SAFE_ADVANCED_TITLE("후석 승객 알림"),
    SAFE_ADVANCED_SUBTITLE("어드밴스드 후석 승객 알림"),
    SAFE_ADVANCED_CONTENT("정밀한 레이더 센서가 실내의 승객을 감지하여, 운전자가 후석에 탑승한 유아를 인지하지 못하고 차를 떠나면\n" +
            "알림을 주어 안전사고를 예방합니다.");


    private final String text;

    StaticText(String text) {
        this.text = text;
    }

    public String format(Object... args) {
        return String.format(text, args);
    }
}
