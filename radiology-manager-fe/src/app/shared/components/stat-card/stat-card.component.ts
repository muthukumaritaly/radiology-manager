import { Component, input } from '@angular/core';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-stat-card',
  imports: [CardModule],
  template: `
    <p-card class="stat-card">
      <div class="stat-inner">
        <div class="stat-icon" [style.background]="accentColor()">
          <i [class]="'pi ' + icon()"></i>
        </div>
        <div class="stat-content">
          <span class="label">{{ label() }}</span>
          <span class="value">{{ value() }}</span>
          @if (hint()) {
            <span class="hint">{{ hint() }}</span>
          }
        </div>
      </div>
    </p-card>
  `,
  styles: `
    :host ::ng-deep .stat-card .p-card-body {
      padding: 1.25rem;
    }

    .stat-inner {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .stat-icon {
      display: grid;
      place-items: center;
      width: 3.25rem;
      height: 3.25rem;
      border-radius: 14px;
      color: white;
      flex-shrink: 0;
      box-shadow: var(--rm-shadow-soft);
    }

    .stat-icon i {
      font-size: 1.35rem;
    }

    .stat-content {
      display: flex;
      flex-direction: column;
      gap: 0.15rem;
      min-width: 0;
    }

    .label {
      font-size: 0.85rem;
      color: var(--rm-text-secondary);
      font-weight: 600;
    }

    .value {
      font-size: 1.85rem;
      font-weight: 800;
      color: var(--rm-text-primary);
      line-height: 1.1;
      letter-spacing: -0.02em;
    }

    .hint {
      font-size: 0.8rem;
      color: var(--rm-text-secondary);
    }
  `,
})
export class StatCardComponent {
  readonly label = input.required<string>();
  readonly value = input.required<string | number>();
  readonly icon = input('pi-chart-line');
  readonly hint = input<string | undefined>(undefined);
  readonly accentColor = input('var(--rm-gradient)');
}
