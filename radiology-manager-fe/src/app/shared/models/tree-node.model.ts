export type TreeNodeKind = 'organization' | 'container' | 'equipment';

export interface HierarchyTreeNode {
  name: string;
  id: number;
  kind: TreeNodeKind;
  equipmentType?: string;
  serialNumber?: string;
  installationDate?: string;
  organizationId?: number;
  containerId?: number | null;
  children: HierarchyTreeNode[];
  equipmentCount: number;
}
