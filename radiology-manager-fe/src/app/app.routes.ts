import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'organizations',
        loadComponent: () =>
          import('./features/organizations/organization-tree/organization-tree.component').then(
            (m) => m.OrganizationTreeComponent,
          ),
      },
      {
        path: 'equipment/new',
        loadComponent: () =>
          import('./features/equipment/equipment-form/equipment-form.component').then(
            (m) => m.EquipmentFormComponent,
          ),
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
