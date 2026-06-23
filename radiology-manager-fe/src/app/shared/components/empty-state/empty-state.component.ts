import { Component, input } from '@angular/core';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-empty-state',
  imports: [ButtonModule],
  template: `
    <div class="empty-state">
      <i [class]="'pi ' + icon()"></i>
      <h3>{{ title() }}</h3>
      <p>{{ description() }}</p>
      @if (actionLabel()) {
        <p-button [label]="actionLabel()!" severity="secondary" [outlined]="true" (onClick)="onAction()" />
      }
    </div>
  `,
  styles: `
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
      gap: 0.75rem;
      padding: 3rem 1.5rem;
      color: var(--rm-text-secondary);
    }

    i {
      font-size: 2.75rem;
      color: var(--rm-primary);
      opacity: 0.85;
    }

    h3 {
      margin: 0;
      color: var(--rm-text-primary);
      font-size: 1.25rem;
      font-weight: 600;
    }

    p {
      margin: 0;
      max-width: 28rem;
      line-height: 1.5;
    }
  `,
})
export class EmptyStateComponent {
  readonly icon = input('pi-inbox');
  readonly title = input('No data available');
  readonly description = input('There is nothing to display yet.');
  readonly actionLabel = input<string | undefined>(undefined);
  readonly action = input<(() => void) | undefined>(undefined);

  onAction(): void {
    this.action()?.();
  }
}
