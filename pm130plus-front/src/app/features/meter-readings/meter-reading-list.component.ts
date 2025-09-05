import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MeterReading } from '../../core/models/meter-reading.model';
import { MeterReadingService } from '../../core/services/meter-reading.service';


@Component({
  selector: 'app-meter-reading-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule
  ],
  templateUrl: './meter-reading-list.component.html',
  styleUrls: ['./meter-reading-list.component.scss']
})
export class MeterReadingListComponent implements OnInit {
  readings: MeterReading[] = [];
  displayedColumns: string[] = ['timestamp', 'registerAddress', 'value'];

  constructor(private meterReadingService: MeterReadingService) {}

  ngOnInit(): void {
    this.meterReadingService.getAll().subscribe({
      next: (data) => (this.readings = data),
      error: (err) => console.error('❌ Error loading readings', err)
    });
  }
}
