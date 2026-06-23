import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { TreeNode } from 'primeng/api';
import { TreeModule } from 'primeng/tree';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { BadgeModule } from 'primeng/badge';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { environment } from '../../../../environments/environment';
import { OrganizationService } from '../../../services/organization.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { EquipmentDetailPanelComponent } from '../../../shared/components/equipment-detail-panel/equipment-detail-panel.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { HierarchyTreeNode } from '../../../shared/models/tree-node.model';
import {
  getEquipmentSeverity,
  getEquipmentTypeColor,
  getNodeIconClass,
} from '../../../shared/utils/equipment-icon.util';
import {
  collapseAllTreeNodes,
  expandAllTreeNodes,
  toPrimeTreeNodes,
} from '../../../shared/utils/prime-tree.util';
import {
  buildHierarchyTree,
  filterHierarchyTree,
  normalizeEquipmentType,
} from '../../../shared/utils/tree.utils';

@Component({
  selector: 'app-organization-tree',
  imports: [
    ReactiveFormsModule,
    TreeModule,
    CardModule,
    ButtonModule,
    InputTextModule,
    TagModule,
    BadgeModule,
    IconFieldModule,
    InputIconModule,
    PageHeaderComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    EquipmentDetailPanelComponent,
  ],
  templateUrl: './organization-tree.component.html',
  styleUrl: './organization-tree.component.scss',
})
export class OrganizationTreeComponent implements OnInit {
  private readonly organizationService = inject(OrganizationService);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly searchTerm = signal('');
  readonly rootNode = signal<HierarchyTreeNode | null>(null);
  readonly treeNodes = signal<TreeNode[]>([]);
  readonly selectedEquipment = signal<HierarchyTreeNode | null>(null);
  readonly containerNameMap = signal<Record<number, string>>({});
  selectedTreeNode: TreeNode | null = null;

  readonly filteredRoot = computed(() => {
    const root = this.rootNode();
    if (!root) {
      return null;
    }

    const term = this.searchTerm().trim();
    if (!term) {
      return root;
    }

    return filterHierarchyTree(root, term);
  });

  readonly totalEquipment = computed(() => this.rootNode()?.equipmentCount ?? 0);

  readonly selectedContainerName = computed(() => {
    const equipment = this.selectedEquipment();
    if (!equipment?.containerId) {
      return null;
    }
    return this.containerNameMap()[equipment.containerId] ?? null;
  });

  constructor() {
    effect(() => {
      const filtered = this.filteredRoot();
      this.treeNodes.set(filtered ? toPrimeTreeNodes([filtered], true) : []);
    });
  }

  ngOnInit(): void {
    this.searchControl.valueChanges.subscribe((value) => {
      this.searchTerm.set(value);
    });

    this.loadTree();
  }

  getTypeLabel(node: HierarchyTreeNode): string {
    return normalizeEquipmentType(node.equipmentType ?? '');
  }

  getTypeColor(node: HierarchyTreeNode): string {
    return getEquipmentTypeColor(node.equipmentType ?? '');
  }

  getTypeSeverity(node: HierarchyTreeNode): 'info' | 'success' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    return getEquipmentSeverity(node.equipmentType ?? '');
  }

  getNodeIcon(node: HierarchyTreeNode): string {
    return getNodeIconClass(node.kind, node.equipmentType);
  }

  onNodeSelect(event: { node: TreeNode }): void {
    this.selectedTreeNode = event.node;
    const data = event.node.data as HierarchyTreeNode | undefined;
    this.selectedEquipment.set(data?.kind === 'equipment' ? data : null);

    let parent = event.node.parent;
    while (parent) {
      parent.expanded = true;
      parent = parent.parent;
    }
  }

  expandAll(): void {
    const expandedNodes = expandAllTreeNodes(this.treeNodes());
    this.treeNodes.set(expandedNodes);
  }

  collapseAll(): void {
    const collapsedNodes = collapseAllTreeNodes(this.treeNodes());
    this.treeNodes.set(collapsedNodes);
  }

  clearSearch(): void {
    this.searchControl.setValue('');
  }

  loadTree(): void {
    this.loading.set(true);
    this.error.set(null);

    this.organizationService.getOrganizationTree(environment.defaultOrganizationId).subscribe({
      next: (organization) => {
        const hierarchy = buildHierarchyTree(organization);
        const containerMap: Record<number, string> = {};

        const walkContainers = (containers: typeof organization.containers): void => {
          for (const container of containers) {
            containerMap[container.id] = container.name;
            walkContainers(container.containers);
          }
        };
        walkContainers(organization.containers);

        this.containerNameMap.set(containerMap);
        this.rootNode.set(hierarchy);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load organization hierarchy.');
        this.loading.set(false);
      },
    });
  }
}
