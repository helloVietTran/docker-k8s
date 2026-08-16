package com.viettran.reading_story_web.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailTemplateUtil {
    SpringTemplateEngine springTemplateEngine;

    public String generateContent(String templateName, Map<String, Object> variables) {
        Context context = new Context(); // context để lưu các biến
        context.setVariables(variables); // setVariables nhé
        return springTemplateEngine.process(templateName, context); // template name và context
    }

    public Map<String, Object> createVariables(Object... keyValuePairs) {
        // Object... keyValuePairs là phương pháp truyền số lượng tham số không ước lượng
        // Object để không bị giới hạn kiểu dữ liệu
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs should be even in number");
        }

        Map<String, Object> variables = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = (String) keyValuePairs[i];
            Object value = keyValuePairs[i + 1];
            variables.put(key, value);
        }
        return variables;
    }
}
