import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { environment } from '../../../environments/environment';
import { OrganizationService } from '../../services/organization.service';
import { BarChartComponent } from '../../shared/components/bar-chart/bar-chart.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DashboardSummary } from '../../shared/models/organization.model';
import { buildDashboardSummary } from '../../shared/utils/tree.utils';

@Component({
  selector: 'app-dashboard',
  imports: [
    CardModule,
    TagModule,
    PageHeaderComponent,
    StatCardComponent,
    BarChartComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly organizationService = inject(OrganizationService);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly summary = signal<DashboardSummary | null>(null);

  readonly chartData = computed(() => {
    const currentSummary = this.summary();
    if (!currentSummary) {
      return [];
    }

    return Object.entries(currentSummary.equipmentByType).map(([label, value]) => ({
      label,
      value,
    }));
  });

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.error.set(null);

    this.organizationService.getOrganizationTree(environment.defaultOrganizationId).subscribe({
      next: (organization) => {
        this.summary.set(buildDashboardSummary(organization));
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load dashboard data.');
        this.loading.set(false);
      },
    });
  }
}
