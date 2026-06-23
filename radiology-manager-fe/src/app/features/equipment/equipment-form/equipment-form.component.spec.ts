import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { EquipmentFormComponent } from './equipment-form.component';
import { EquipmentService } from '../../../services/equipment.service';
import { OrganizationService } from '../../../services/organization.service';
import { NotificationService } from '../../../core/services/notification.service';
import { markAllControlsTouched } from '../../../shared/validators/form-error.util';

describe('EquipmentFormComponent', () => {
  let fixture: ComponentFixture<EquipmentFormComponent>;
  let component: EquipmentFormComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EquipmentFormComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideAnimationsAsync(),
        MessageService,
        {
          provide: OrganizationService,
          useValue: {
            getOrganizationTree: () =>
              of({
                id: 1,
                name: 'Hospital',
                equipment: [],
                containers: [{ id: 3, name: 'Radiology', containers: [], equipment: [] }],
              }),
          },
        },
        {
          provide: EquipmentService,
          useValue: {
            createEquipment: () =>
              of({
                id: 101,
                name: 'Test',
                type: 'CT',
                serialNumber: 'SN-1',
                installationDate: '2026-01-01',
                organizationId: 1,
                containerId: null,
              }),
          },
        },
        {
          provide: NotificationService,
          useValue: {
            showSuccess: () => undefined,
            showError: () => undefined,
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EquipmentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should mark required fields invalid when empty', () => {
    component.submit();
    expect(component.form.invalid).toBe(true);
    expect(component.form.get('name')?.hasError('required')).toBe(true);
    expect(component.form.get('type')?.hasError('required')).toBe(true);
    expect(component.form.get('serialNumber')?.hasError('required')).toBe(true);
    expect(component.form.get('installationDate')?.hasError('required')).toBe(true);
  });

  it('should reject future installation dates', () => {
    const futureDate = new Date();
    futureDate.setFullYear(futureDate.getFullYear() + 1);

    component.form.patchValue({
      name: 'MRI Scanner',
      type: 'MRI',
      serialNumber: 'SN-MRI-1',
      installationDate: futureDate,
      organizationId: 1,
    });

    markAllControlsTouched(component.form);
    expect(component.form.get('installationDate')?.hasError('futureDate')).toBe(true);
    expect(component.form.invalid).toBe(true);
  });

  it('should submit valid form and reset', () => {
    const createSpy = vi.spyOn(TestBed.inject(EquipmentService), 'createEquipment');

    component.form.patchValue({
      name: 'GE Revolution CT Scan v2',
      type: 'CT',
      serialNumber: 'SN-CT-9999',
      installationDate: new Date('2026-01-15'),
      organizationId: 1,
      containerId: 3,
    });

    component.submit();

    expect(createSpy).toHaveBeenCalled();
    expect(component.form.pristine).toBe(true);
  });
});
