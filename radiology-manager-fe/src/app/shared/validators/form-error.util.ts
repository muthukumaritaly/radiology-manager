import { AbstractControl, ValidationErrors } from '@angular/forms';

export function getControlErrorMessage(
  control: AbstractControl | null,
  fieldLabel: string,
  customMessages?: Record<string, string>,
): string {
  if (!control || !control.errors || !(control.touched || control.dirty)) {
    return '';
  }

  const errors = control.errors;

  if (customMessages) {
    for (const [key, message] of Object.entries(customMessages)) {
      if (errors[key]) {
        return message;
      }
    }
  }

  if (errors['required']) {
    return `${fieldLabel} is required.`;
  }

  if (errors['min']) {
    return `${fieldLabel} must be at least ${errors['min'].min}.`;
  }

  if (errors['pattern']) {
    return `${fieldLabel} format is invalid.`;
  }

  return `${fieldLabel} is invalid.`;
}

export function hasVisibleError(control: AbstractControl | null): boolean {
  return !!control && control.invalid && (control.touched || control.dirty);
}

export function markAllControlsTouched(control: AbstractControl): void {
  control.markAsTouched();

  if ('controls' in control) {
    const controls = control.controls as Record<string, AbstractControl>;
    Object.values(controls).forEach((child) => markAllControlsTouched(child));
  }
}
