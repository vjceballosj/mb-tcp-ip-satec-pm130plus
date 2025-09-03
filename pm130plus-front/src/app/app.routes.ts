import { Routes } from '@angular/router';
import { MeterReadingListComponent } from './src/app/features/meter-readings/meter-reading-list.component/meter-reading-list.component';

export const routes: Routes = [
    { path: '', component: MeterReadingListComponent },
    { path: '**', redirectTo: '' }
];
