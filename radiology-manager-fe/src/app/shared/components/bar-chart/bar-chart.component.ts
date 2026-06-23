import { Component, computed, input } from '@angular/core';
import { CardModule } from 'primeng/card';

export interface ChartDatum {
  label: string;
  value: number;
  color?: string;
}

@Component({
  selector: 'app-bar-chart',
  imports: [CardModule],
  template: `
    <p-card class="chart-card">
      <h3>{{ title() }}</h3>
      @if (data().length === 0) {
        <p class="empty">No data to chart.</p>
      } @else {
        <div class="chart">
          @for (item of normalizedData(); track item.label) {
            <div class="row">
              <span class="label">{{ item.label }}</span>
              <div class="bar-track">
                <div
                  class="bar"
                  [style.width.%]="item.percentage"
                  [style.background]="item.color"
                ></div>
              </div>
              <span class="count">{{ item.value }}</span>
            </div>
          }
        </div>
      }
    </p-card>
  `,
  styles: `
    :host ::ng-deep .chart-card .p-card-body {
      padding: 1.35rem;
    }

    h3 {
      margin: 0 0 1rem;
      font-size: 1.05rem;
      font-weight: 700;
      color: var(--rm-text-primary);
    }

    .empty {
      margin: 0;
      color: var(--rm-text-secondary);
    }

    .chart {
      display: flex;
      flex-direction: column;
      gap: 0.9rem;
    }

    .row {
      display: grid;
      grid-template-columns: 5.5rem 1fr 2rem;
      align-items: center;
      gap: 0.75rem;
    }

    .label {
      font-size: 0.85rem;
      font-weight: 600;
      color: var(--rm-text-secondary);
    }

    .bar-track {
      height: 0.7rem;
      border-radius: 999px;
      background: #e8f1f8;
      overflow: hidden;
    }

    .bar {
      height: 100%;
      border-radius: inherit;
      transition: width 0.4s ease;
      box-shadow: inset 0 -1px 0 rgba(255, 255, 255, 0.25);
    }

    .count {
      text-align: right;
      font-weight: 700;
      color: var(--rm-text-primary);
    }
  `,
})
export class BarChartComponent {
  readonly title = input('Chart');
  readonly data = input<ChartDatum[]>([]);
  readonly colors = input<string[]>(['#0284c7', '#7c3aed', '#0d9488', '#db2777', '#475569']);

  readonly normalizedData = computed(() => {
    const items = this.data();
    const max = Math.max(...items.map((item) => item.value), 1);

    return items.map((item, index) => ({
      ...item,
      percentage: (item.value / max) * 100,
      color: item.color ?? this.colors()[index % this.colors().length],
    }));
  });
}
