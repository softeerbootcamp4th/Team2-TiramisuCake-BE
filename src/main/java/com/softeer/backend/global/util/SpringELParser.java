package com.softeer.backend.global.util;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 스프링 expression을 파싱하는 클래스
 */
public class SpringELParser {
    private SpringELParser() {
    }

    /**
     * 메서드의 매개변수 이름, 매개변수 값, key를 이용해 동적으로 값을 파싱한다.
     */
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // context에 변수 이름과 값을 저장
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // 템플릿 표현을 이용하여 동적으로 값 파싱
        return parser.parseExpression(key, new TemplateParserContext()).getValue(context, Object.class);
    }
}
