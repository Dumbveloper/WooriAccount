package io.woori.account.wooriaccount.dto.notification;

import io.woori.account.wooriaccount.constant.NotificationType;
import io.woori.account.wooriaccount.domain.NotificationArgs;
import io.woori.account.wooriaccount.domain.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TxNotifyDTO {

    private final String receiver;

    private final String content;

    private final NotificationType notificationType;

    private final NotificationArgs notificationArgs;

    private final LocalDateTime createdAt;

    @Builder
    public TxNotifyDTO(String receiver, String content, NotificationType notificationType, NotificationArgs notificationArgs, LocalDateTime createdAt) {
        this.receiver = receiver;
        this.content = content;
        this.notificationType = notificationType;
        this.notificationArgs = notificationArgs;
        this.createdAt = createdAt;
    }

    public static TxNotifyDTO from(Notification notification) {
        return TxNotifyDTO.builder()
                .receiver(notification.getReceiver().getCustomerName())
                .content(notification.getContent())
                .notificationType(notification.getNotificationType())
                .notificationArgs(notification.getNotificationArgs())
                .createdAt((notification.getCreatedTime()))
                .build();
    }

}
