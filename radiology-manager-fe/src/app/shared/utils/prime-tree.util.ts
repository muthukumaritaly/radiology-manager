import { TreeNode } from 'primeng/api';
import { HierarchyTreeNode } from '../models/tree-node.model';

export function toPrimeTreeNodes(nodes: HierarchyTreeNode[], expandRoot = false): TreeNode[] {
  return nodes.map((node) => toPrimeTreeNode(node, expandRoot));
}

export function toPrimeTreeNode(node: HierarchyTreeNode, expandRoot = false): TreeNode {
  const isEquipment = node.kind === 'equipment';

  return {
    key: `${node.kind}-${node.id}`,
    label: node.name,
    data: node,
    leaf: isEquipment,
    expanded: node.kind === 'organization' && expandRoot,
    children: isEquipment ? undefined : toPrimeTreeNodes(node.children),
  };
}

export function expandAllTreeNodes(nodes: TreeNode[]): TreeNode[] {
  return nodes.map((node) => {
    const newNode = { ...node };
    if (node.children?.length) {
      newNode.expanded = true;
      newNode.children = expandAllTreeNodes(node.children);
    }
    return newNode;
  });
}

export function collapseAllTreeNodes(nodes: TreeNode[]): TreeNode[] {
  return nodes.map((node) => {
    const newNode = { ...node };
    newNode.expanded = false;
    if (node.children?.length) {
      newNode.children = collapseAllTreeNodes(node.children);
    }
    return newNode;
  });
}

export function findTreeNodeByKey(nodes: TreeNode[], key: string): TreeNode | null {
  for (const node of nodes) {
    if (node.key === key) {
      return node;
    }

    if (node.children?.length) {
      const match = findTreeNodeByKey(node.children, key);
      if (match) {
        return match;
      }
    }
  }

  return null;
}
