import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Organization } from '../shared/models/organization.model';

export interface OrganizationBasic {
  id: number;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class OrganizationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/organizations`;

  getOrganizationTree(organizationId: number): Observable<Organization> {
    return this.http.get<Organization>(`${this.baseUrl}/${organizationId}/tree`);
  }

  getOrganizations(): Observable<OrganizationBasic[]> {
    return this.http.get<OrganizationBasic[]>(this.baseUrl);
  }
}
