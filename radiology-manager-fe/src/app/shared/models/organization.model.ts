import { Container } from './container.model';
import { Equipment } from './equipment.model';

export interface Organization {
  id: number;
  name: string;
  equipment: Equipment[];
  containers: Container[];
}

export interface DashboardSummary {
  organizationName: string;
  totalContainers: number;
  totalEquipment: number;
  equipmentByType: Record<string, number>;
}
