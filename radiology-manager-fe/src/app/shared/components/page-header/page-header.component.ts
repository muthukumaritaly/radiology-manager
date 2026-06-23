import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  template: `
    <header class="page-header">
      <div>
        <p class="eyebrow">{{ eyebrow() }}</p>
        <h1>{{ title() }}</h1>
        @if (subtitle()) {
          <p class="subtitle">{{ subtitle() }}</p>
        }
      </div>
      <div class="actions">
        <ng-content />
      </div>
    </header>
  `,
  styles: `
    .page-header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 1rem;
      margin-bottom: 1.5rem;
      flex-wrap: wrap;
    }

    .eyebrow {
      margin: 0 0 0.25rem;
      font-size: 0.75rem;
      font-weight: 700;
      letter-spacing: 0.1em;
      text-transform: uppercase;
      color: var(--rm-primary);
    }

    h1 {
      margin: 0;
      font-size: clamp(1.6rem, 2.2vw, 2.1rem);
      font-weight: 700;
      color: var(--rm-text-primary);
      letter-spacing: -0.02em;
    }

    .subtitle {
      margin: 0.5rem 0 0;
      color: var(--rm-text-secondary);
      max-width: 42rem;
      line-height: 1.55;
    }

    .actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
  `,
})
export class PageHeaderComponent {
  readonly eyebrow = input('Radiology Manager');
  readonly title = input('');
  readonly subtitle = input<string | undefined>(undefined);
}
