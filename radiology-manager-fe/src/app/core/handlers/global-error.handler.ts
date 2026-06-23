import { ErrorHandler, Injectable, inject } from '@angular/core';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  private readonly notificationService = inject(NotificationService);

  handleError(error: unknown): void {
    console.error(error);

    const message =
      error instanceof Error ? error.message : 'An unexpected application error occurred.';

    this.notificationService.showError(message);
  }
}
