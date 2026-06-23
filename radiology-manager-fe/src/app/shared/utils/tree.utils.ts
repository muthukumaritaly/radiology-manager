import { Container } from '../models/container.model';
import { Equipment } from '../models/equipment.model';
import { DashboardSummary, Organization } from '../models/organization.model';
import { HierarchyTreeNode } from '../models/tree-node.model';

function countEquipmentInContainers(containers: Container[]): number {
  return containers.reduce((total, container) => {
    return total + container.equipment.length + countEquipmentInContainers(container.containers);
  }, 0);
}

function countContainersRecursive(containers: Container[]): number {
  return containers.reduce((total, container) => {
    return total + 1 + countContainersRecursive(container.containers);
  }, 0);
}

function collectEquipmentByType(
  equipment: Equipment[],
  containers: Container[],
  accumulator: Record<string, number>,
): void {
  for (const item of equipment) {
    const type = normalizeEquipmentType(item.type);
    accumulator[type] = (accumulator[type] ?? 0) + 1;
  }

  for (const container of containers) {
    collectEquipmentByType(container.equipment, container.containers, accumulator);
  }
}

export function normalizeEquipmentType(type: string): string {
  if (type === 'XR') {
    return 'X-Ray';
  }
  return type;
}

export function buildDashboardSummary(organization: Organization): DashboardSummary {
  const equipmentByType: Record<string, number> = {};
  collectEquipmentByType(organization.equipment, organization.containers, equipmentByType);

  return {
    organizationName: organization.name,
    totalContainers: countContainersRecursive(organization.containers),
    totalEquipment: organization.equipment.length + countEquipmentInContainers(organization.containers),
    equipmentByType,
  };
}

function buildContainerNode(container: Container): HierarchyTreeNode {
  const childContainers = container.containers.map((child) => buildContainerNode(child));
  const childEquipment = container.equipment.map((item) => buildEquipmentNode(item));
  const children = [...childContainers, ...childEquipment];
  const equipmentCount =
    container.equipment.length +
    childContainers.reduce((total, child) => total + child.equipmentCount, 0);

  return {
    id: container.id,
    name: container.name,
    kind: 'container',
    children,
    equipmentCount,
  };
}

function buildEquipmentNode(equipment: Equipment): HierarchyTreeNode {
  return {
    id: equipment.id,
    name: equipment.name,
    kind: 'equipment',
    equipmentType: equipment.type,
    serialNumber: equipment.serialNumber,
    installationDate: equipment.installationDate,
    organizationId: equipment.organizationId,
    containerId: equipment.containerId,
    children: [],
    equipmentCount: 0,
  };
}

export function buildHierarchyTree(organization: Organization): HierarchyTreeNode {
  const childContainers = organization.containers.map((container) => buildContainerNode(container));
  const childEquipment = organization.equipment.map((item) => buildEquipmentNode(item));
  const children = [...childContainers, ...childEquipment];
  const equipmentCount =
    organization.equipment.length +
    childContainers.reduce((total, child) => total + child.equipmentCount, 0);

  return {
    id: organization.id,
    name: organization.name,
    kind: 'organization',
    children,
    equipmentCount,
  };
}

function nodeMatchesFilter(node: HierarchyTreeNode, filter: string): boolean {
  const normalizedFilter = filter.trim().toLowerCase();
  if (!normalizedFilter) {
    return true;
  }

  return (
    node.name.toLowerCase().includes(normalizedFilter) ||
    (node.serialNumber?.toLowerCase().includes(normalizedFilter) ?? false) ||
    (node.equipmentType?.toLowerCase().includes(normalizedFilter) ?? false)
  );
}

function filterTreeNode(node: HierarchyTreeNode, filter: string): HierarchyTreeNode | null {
  if (!filter.trim()) {
    return node;
  }

  const filteredChildren = node.children
    .map((child) => filterTreeNode(child, filter))
    .filter((child): child is HierarchyTreeNode => child !== null);

  if (nodeMatchesFilter(node, filter) || filteredChildren.length > 0) {
    return {
      ...node,
      children: filteredChildren,
    };
  }

  return null;
}

export function filterHierarchyTree(
  root: HierarchyTreeNode,
  filter: string,
): HierarchyTreeNode | null {
  return filterTreeNode(root, filter);
}

export function flattenContainers(organization: Organization): Container[] {
  const result: Container[] = [];

  const walk = (containers: Container[]): void => {
    for (const container of containers) {
      result.push(container);
      walk(container.containers);
    }
  };

  walk(organization.containers);
  return result;
}
