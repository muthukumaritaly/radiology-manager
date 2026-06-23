import { Equipment } from './equipment.model';

export interface Container {
  id: number;
  name: string;
  containers: Container[];
  equipment: Equipment[];
}
