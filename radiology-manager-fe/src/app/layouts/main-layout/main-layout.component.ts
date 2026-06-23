import { Component, HostListener, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { RippleModule } from 'primeng/ripple';

interface NavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ButtonModule, ToolbarModule, RippleModule],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss',
})
export class MainLayoutComponent {
  readonly sidebarOpen = signal(true);
  readonly isMobile = signal(false);

  readonly navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: 'pi-th-large' },
    { label: 'Organizations', route: '/organizations', icon: 'pi-sitemap' },
    { label: 'Add Equipment', route: '/equipment/new', icon: 'pi-plus-circle' },
  ];

  @HostListener('window:resize')
  onResize(): void {
    const mobile = window.innerWidth < 960;
    const wasMobile = this.isMobile();

    this.isMobile.set(mobile);

    if (mobile && !wasMobile) {
      this.sidebarOpen.set(false);
    } else if (!mobile && wasMobile) {
      this.sidebarOpen.set(true);
    }
  }

  constructor() {
    this.isMobile.set(window.innerWidth < 960);
    this.sidebarOpen.set(!this.isMobile());
  }

  toggleSidebar(): void {
    this.sidebarOpen.update((open) => !open);
  }

  closeSidebarOnMobile(): void {
    if (this.isMobile()) {
      this.sidebarOpen.set(false);
    }
  }
}
