import { Component, input } from '@angular/core';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
  selector: 'app-loading-spinner',
  imports: [ProgressSpinnerModule],
  template: `
    <div class="loading" [class.overlay]="overlay()" role="status" aria-live="polite">
      <p-progressSpinner [style]="{ width: diameter() + 'px', height: diameter() + 'px' }" />
      @if (message()) {
        <p>{{ message() }}</p>
      }
    </div>
  `,
  styles: `
    .loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 1rem;
      padding: 2rem;
      color: var(--rm-text-secondary);
    }

    .loading.overlay {
      position: absolute;
      inset: 0;
      background: rgba(255, 255, 255, 0.82);
      z-index: 2;
      border-radius: inherit;
    }

    p {
      margin: 0;
      font-size: 0.95rem;
    }
  `,
})
export class LoadingSpinnerComponent {
  readonly message = input('Loading...');
  readonly diameter = input(48);
  readonly overlay = input(false);
}
