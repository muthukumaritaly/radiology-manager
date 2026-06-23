import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MessageService, TreeNode } from 'primeng/api';
import { of } from 'rxjs';
import { OrganizationTreeComponent } from './organization-tree.component';
import { OrganizationService } from '../../../services/organization.service';
import { Organization } from '../../../shared/models/organization.model';
import { buildHierarchyTree } from '../../../shared/utils/tree.utils';
import { toPrimeTreeNodes } from '../../../shared/utils/prime-tree.util';

function findEquipmentNode(nodes: TreeNode[], serial: string): TreeNode | null {
  for (const node of nodes) {
    if (node.data?.kind === 'equipment' && node.data.serialNumber === serial) {
      return node;
    }
    if (node.children?.length) {
      const match = findEquipmentNode(node.children, serial);
      if (match) {
        return match;
      }
    }
  }
  return null;
}

describe('OrganizationTreeComponent', () => {
  let fixture: ComponentFixture<OrganizationTreeComponent>;
  let component: OrganizationTreeComponent;

  const mockOrganization: Organization = {
    id: 1,
    name: 'San Raffaele Hospital Group',
    equipment: [
      {
        id: 4,
        name: 'Hologic Selenia Mammography Van',
        type: 'Mammogram',
        serialNumber: 'SN-MG-404',
        installationDate: '2022-11-05',
        organizationId: 1,
        containerId: null,
      },
    ],
    containers: [
      {
        id: 3,
        name: 'Radiology Department',
        containers: [],
        equipment: [
          {
            id: 1,
            name: 'GE Revolution CT Scan',
            type: 'CT',
            serialNumber: 'SN-CT-101',
            installationDate: '2023-01-15',
            organizationId: 1,
            containerId: 3,
          },
        ],
      },
    ],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrganizationTreeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideAnimationsAsync(),
        MessageService,
        {
          provide: OrganizationService,
          useValue: {
            getOrganizationTree: () => of(mockOrganization),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(OrganizationTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load hierarchy tree', () => {
    expect(component).toBeTruthy();
    expect(component.rootNode()?.name).toBe('San Raffaele Hospital Group');
    expect(component.totalEquipment()).toBe(2);
  });

  it('should filter nodes by search term', () => {
    component.searchControl.setValue('CT');
    fixture.detectChanges();

    const filtered = component.filteredRoot();
    expect(filtered).toBeTruthy();

    const containsCtNode = (node: { name: string; children: { name: string; children: unknown[] }[] }): boolean =>
      node.name.includes('CT') ||
      node.children.some((child) => containsCtNode(child as typeof node));

    expect(filtered && containsCtNode(filtered)).toBe(true);
  });

  it('should select equipment and show details', () => {
    const hierarchy = buildHierarchyTree(mockOrganization);
    const primeNodes = toPrimeTreeNodes([hierarchy], true);

    const equipmentNode = findEquipmentNode(primeNodes, 'SN-CT-101');
    expect(equipmentNode).toBeTruthy();
    component.onNodeSelect({ node: equipmentNode! });

    expect(component.selectedEquipment()?.name).toBe('GE Revolution CT Scan');
    expect(component.selectedEquipment()?.serialNumber).toBe('SN-CT-101');
  });

  it('should build expandable nodes for organization hierarchy', () => {
    const root = buildHierarchyTree(mockOrganization);
    expect(root.children.length).toBeGreaterThan(0);
    expect(root.equipmentCount).toBe(2);
  });
});
