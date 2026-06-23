import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function pastOrPresentDateValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) {
      return null;
    }

    const selectedDate = new Date(value);
    if (Number.isNaN(selectedDate.getTime())) {
      return { invalidDate: true };
    }

    const today = new Date();
    today.setHours(23, 59, 59, 999);

    return selectedDate.getTime() > today.getTime() ? { futureDate: true } : null;
  };
}

export function getDateErrorMessage(errors: ValidationErrors | null): string {
  if (!errors) {
    return '';
  }

  if (errors['required']) {
    return 'Installation date is required.';
  }

  if (errors['invalidDate']) {
    return 'Please enter a valid date.';
  }

  if (errors['futureDate']) {
    return 'Installation date cannot be in the future.';
  }

  return 'Invalid installation date.';
}
