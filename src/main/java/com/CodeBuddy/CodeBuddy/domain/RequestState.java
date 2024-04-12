package com.CodeBuddy.CodeBuddy.domain;

import jakarta.persistence.Enumerated;

public enum RequestState {
    /**
     * Отправлен учеником
     */
    SEND,
    /**
     * Принят ментором
     */
    ACCEPTED,
    /**
     * Отклонен ментором
     */
    REJECTED
}
