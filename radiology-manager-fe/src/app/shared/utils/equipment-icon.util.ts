import { TreeNodeKind } from '../models/tree-node.model';

export function getNodeIconClass(kind: TreeNodeKind, equipmentType?: string): string {
  if (kind === 'organization') {
    return 'pi pi-building';
  }

  if (kind === 'container') {
    return 'pi pi-folder';
  }

  return getEquipmentIconClass(equipmentType ?? '');
}

export function getEquipmentIconClass(type: string): string {
  const normalized = type.toUpperCase();

  switch (normalized) {
    case 'CT':
      return 'pi pi-desktop';
    case 'MRI':
      return 'pi pi-heart';
    case 'X-RAY':
    case 'XR':
      return 'pi pi-wave-pulse';
    case 'MAMMOGRAM':
      return 'pi pi-shield';
    default:
      return 'pi pi-box';
  }
}

export function getEquipmentTypeColor(type: string): string {
  const normalized = type.toUpperCase();

  switch (normalized) {
    case 'CT':
      return '#0284c7';
    case 'MRI':
      return '#7c3aed';
    case 'X-RAY':
    case 'XR':
      return '#0d9488';
    case 'MAMMOGRAM':
      return '#db2777';
    default:
      return '#475569';
  }
}

export function getEquipmentSeverity(
  type: string,
): 'info' | 'success' | 'warn' | 'danger' | 'secondary' | 'contrast' {
  const normalized = type.toUpperCase();

  switch (normalized) {
    case 'CT':
      return 'info';
    case 'MRI':
      return 'warn';
    case 'X-RAY':
    case 'XR':
      return 'success';
    case 'MAMMOGRAM':
      return 'danger';
    default:
      return 'secondary';
  }
}
