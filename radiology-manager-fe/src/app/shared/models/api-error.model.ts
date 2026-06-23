export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  correlationId: string;
}

export interface ValidationErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  validationErrors: Record<string, string>;
}

export function extractErrorMessage(error: unknown): string {
  if (!error || typeof error !== 'object') {
    return 'An unexpected error occurred.';
  }

  const httpError = error as {
    error?: ErrorResponse | ValidationErrorResponse | string;
    message?: string;
    status?: number;
  };

  if (typeof httpError.error === 'string' && httpError.error.trim()) {
    return httpError.error;
  }

  if (httpError.error && typeof httpError.error === 'object') {
    const payload = httpError.error as Partial<ErrorResponse & ValidationErrorResponse>;

    if (payload.validationErrors && Object.keys(payload.validationErrors).length > 0) {
      return Object.values(payload.validationErrors).join(' ');
    }

    if (payload.message) {
      return payload.message;
    }

    if (payload.error) {
      return payload.error;
    }
  }

  if (httpError.message) {
    return httpError.message;
  }

  return 'An unexpected error occurred.';
}
