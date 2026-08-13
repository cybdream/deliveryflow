package com.deliveryflow.common.api;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ApiMessageService {
    private final MessageSource messageSource;

    public ApiMessageService(MessageSource messageSource) { this.messageSource = messageSource; }

    public String get(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    public String get(String key, Locale locale) {
        return messageSource.getMessage(key, null, key, locale);
    }

    public String businessRule(String originalMessage) {
        String key = switch (originalMessage) {
            case "이미 배송이 배정된 주문입니다." -> "error.delivery.alreadyAssigned";
            case "주문을 찾을 수 없습니다." -> "error.order.notFound";
            case "활성 상태의 배송 기사를 찾을 수 없습니다." -> "error.driver.notFound";
            case "배송 정보를 찾을 수 없습니다." -> "error.delivery.notFound";
            case "이미 등록된 이메일입니다." -> "error.user.emailDuplicate";
            default -> null;
        };
        return key == null ? originalMessage : get(key);
    }
}
