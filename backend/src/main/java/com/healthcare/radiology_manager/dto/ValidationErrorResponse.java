package com.healthcare.radiology_manager.dto;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
    Instant timestamp,
    int status,
    String error,
    Map<String, String> validationErrors
) {}
