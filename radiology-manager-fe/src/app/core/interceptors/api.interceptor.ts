import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';
import { extractErrorMessage } from '../../shared/models/api-error.model';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  let headers = req.headers;

  if (req.method === 'POST' && req.url.includes('/equipment')) {
    headers = headers.set('X-User-Role', 'ADMIN');
  }

  const apiRequest = req.clone({ headers });

  return next(apiRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      const showNotification = apiRequest.method !== 'GET';

      if (error.status !== 0) {
        if (showNotification) {
          notificationService.showError(extractErrorMessage(error));
        }
      } else if (showNotification) {
        notificationService.showError(
          'Unable to reach the server. Please ensure the backend is running.',
        );
      }

      return throwError(() => error);
    }),
  );
};
