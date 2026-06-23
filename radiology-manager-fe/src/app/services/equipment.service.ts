import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Equipment, EquipmentCreateRequest } from '../shared/models/equipment.model';

@Injectable({ providedIn: 'root' })
export class EquipmentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/equipment`;

  createEquipment(request: EquipmentCreateRequest): Observable<Equipment> {
    return this.http.post<Equipment>(this.baseUrl, request);
  }
}
