import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { EquipmentService } from './equipment.service';
import { environment } from '../../environments/environment';

describe('EquipmentService', () => {
  let service: EquipmentService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EquipmentService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(EquipmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should post equipment payload', () => {
    const payload = {
      name: 'GE Revolution CT Scan v2',
      type: 'CT',
      serialNumber: 'SN-CT-9999',
      installationDate: '2026-06-19',
      organizationId: 1,
      containerId: 3,
    };

    const mockResponse = { id: 101, ...payload };

    service.createEquipment(payload).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const request = httpMock.expectOne(`${environment.apiBaseUrl}/equipment`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);
    request.flush(mockResponse);
  });
});
