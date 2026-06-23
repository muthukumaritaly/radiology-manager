import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { OrganizationService } from './organization.service';
import { environment } from '../../environments/environment';

describe('OrganizationService', () => {
  let service: OrganizationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OrganizationService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(OrganizationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch organization tree by id', () => {
    const mockResponse = {
      id: 1,
      name: 'San Raffaele Hospital Group',
      equipment: [],
      containers: [],
    };

    service.getOrganizationTree(1).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const request = httpMock.expectOne(`${environment.apiBaseUrl}/organizations/1/tree`);
    expect(request.request.method).toBe('GET');
    request.flush(mockResponse);
  });
});
