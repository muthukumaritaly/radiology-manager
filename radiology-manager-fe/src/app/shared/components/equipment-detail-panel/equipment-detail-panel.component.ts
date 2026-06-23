import { Component, computed, input } from '@angular/core';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { DividerModule } from 'primeng/divider';
import { HierarchyTreeNode } from '../../models/tree-node.model';
import {
  getEquipmentIconClass,
  getEquipmentSeverity,
  getEquipmentTypeColor,
} from '../../utils/equipment-icon.util';
import { normalizeEquipmentType } from '../../utils/tree.utils';

@Component({
  selector: 'app-equipment-detail-panel',
  imports: [CardModule, TagModule, DividerModule],
  templateUrl: './equipment-detail-panel.component.html',
  styleUrl: './equipment-detail-panel.component.scss',
})
export class EquipmentDetailPanelComponent {
  readonly equipment = input<HierarchyTreeNode | null>(null);
  readonly containerName = input<string | null>(null);

  readonly typeLabel = computed(() => normalizeEquipmentType(this.equipment()?.equipmentType ?? ''));
  readonly iconClass = computed(() => getEquipmentIconClass(this.equipment()?.equipmentType ?? ''));
  readonly typeColor = computed(() => getEquipmentTypeColor(this.equipment()?.equipmentType ?? ''));
  readonly severity = computed(() => getEquipmentSeverity(this.equipment()?.equipmentType ?? ''));

  formatDate(value?: string): string {
    if (!value) {
      return '—';
    }

    return new Date(value).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }
}
