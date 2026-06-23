import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageModule } from 'primeng/message';
import { environment } from '../../../../environments/environment';
import { NotificationService } from '../../../core/services/notification.service';
import { EquipmentService } from '../../../services/equipment.service';
import { OrganizationBasic, OrganizationService } from '../../../services/organization.service';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { Container } from '../../../shared/models/container.model';
import { EQUIPMENT_TYPE_OPTIONS } from '../../../shared/models/equipment.model';
import {
  getControlErrorMessage,
  hasVisibleError,
  markAllControlsTouched,
} from '../../../shared/validators/form-error.util';
import {
  getDateErrorMessage,
  pastOrPresentDateValidator,
} from '../../../shared/validators/date.validators';
import { flattenContainers } from '../../../shared/utils/tree.utils';

@Component({
  selector: 'app-equipment-form',
  imports: [
    ReactiveFormsModule,
    CardModule,
    InputTextModule,
    SelectModule,
    ButtonModule,
    DatePickerModule,
    InputNumberModule,
    MessageModule,
    PageHeaderComponent,
  ],
  templateUrl: './equipment-form.component.html',
  styleUrl: './equipment-form.component.scss',
})
export class EquipmentFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly equipmentService = inject(EquipmentService);
  private readonly organizationService = inject(OrganizationService);
  private readonly notificationService = inject(NotificationService);

  readonly equipmentTypes = [...EQUIPMENT_TYPE_OPTIONS];
  readonly containers = signal<Container[]>([]);
  readonly organizations = signal<OrganizationBasic[]>([]);
  readonly submitting = signal(false);

  readonly containerOptions = signal<{ label: string; value: number | null }[]>([
    { label: 'None — organization level', value: null },
  ]);

  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
    type: ['', Validators.required],
    serialNumber: ['', [Validators.required, Validators.maxLength(64)]],
    installationDate: [null as Date | null, [Validators.required, pastOrPresentDateValidator()]],
    organizationId: [environment.defaultOrganizationId as number | null, Validators.required],
    containerId: [null as number | null],
  });

  ngOnInit(): void {
    this.loadOrganizations();
    this.loadContainers(this.form.get('organizationId')?.value ?? environment.defaultOrganizationId);

    this.form.get('organizationId')?.valueChanges.subscribe((orgId) => {
      if (orgId) {
        this.loadContainers(Number(orgId));
      }
    });
  }

  getError(controlName: string, label: string): string {
    const control = this.form.get(controlName);
    if (controlName === 'installationDate') {
      return getDateErrorMessage(control?.errors ?? null);
    }
    return getControlErrorMessage(control, label);
  }

  showError(controlName: string): boolean {
    return hasVisibleError(this.form.get(controlName));
  }

  submit(): void {
    if (this.form.invalid) {
      markAllControlsTouched(this.form);
      return;
    }

    const value = this.form.getRawValue();
    const installationDate = value.installationDate;
    const name = value.name?.trim();
    const type = value.type;
    const serialNumber = value.serialNumber?.trim();

    if (!installationDate || !name || !type || !serialNumber) {
      return;
    }

    this.submitting.set(true);

    this.equipmentService
      .createEquipment({
        name,
        type,
        serialNumber,
        installationDate: this.formatDate(installationDate),
        organizationId: Number(value.organizationId),
        containerId: value.containerId ?? null,
      })
      .subscribe({
        next: (equipment) => {
          this.notificationService.showSuccess(
            `Equipment "${equipment.name}" registered successfully.`,
          );
          this.resetForm();
          this.submitting.set(false);
        },
        error: () => {
          this.submitting.set(false);
        },
      });
  }

  resetForm(): void {
    this.form.reset({
      name: '',
      type: '',
      serialNumber: '',
      installationDate: null,
      organizationId: environment.defaultOrganizationId,
      containerId: null,
    });
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  private loadOrganizations(): void {
    this.organizationService.getOrganizations().subscribe({
      next: (orgs) => {
        this.organizations.set(orgs);
      },
      error: () => {
        this.notificationService.showError('Unable to load organizations list.');
      },
    });
  }

  private loadContainers(organizationId: number): void {
    this.organizationService.getOrganizationTree(organizationId).subscribe({
      next: (organization) => {
        const flat = flattenContainers(organization);
        this.containers.set(flat);
        this.containerOptions.set([
          { label: 'None — organization level', value: null },
          ...flat.map((container) => ({
            label: `${container.name} (ID: ${container.id})`,
            value: container.id,
          })),
        ]);

        const currentContainerId = this.form.get('containerId')?.value;
        if (currentContainerId && !flat.some((c) => c.id === currentContainerId)) {
          this.form.get('containerId')?.setValue(null);
        }
      },
      error: () => {
        this.containers.set([]);
        this.containerOptions.set([{ label: 'None — organization level', value: null }]);
        this.form.get('containerId')?.setValue(null);
      },
    });
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
