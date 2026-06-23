export type EquipmentType = 'CT' | 'MRI' | 'X-Ray' | 'Mammogram' | 'XR' | string;

export interface Equipment {
  id: number;
  name: string;
  type: EquipmentType;
  serialNumber: string;
  installationDate: string;
  organizationId: number;
  containerId: number | null;
}

export interface EquipmentCreateRequest {
  name: string;
  type: string;
  serialNumber: string;
  installationDate: string;
  organizationId: number;
  containerId?: number | null;
}

export const EQUIPMENT_TYPE_OPTIONS: readonly { value: string; label: string }[] = [
  { value: 'CT', label: 'CT' },
  { value: 'MRI', label: 'MRI' },
  { value: 'X-Ray', label: 'X-Ray' },
  { value: 'Mammogram', label: 'Mammogram' },
] as const;
