import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { HttpClient } from '@angular/common/http';

interface MeterReading {
  id: number;
  timestamp: string;
  voltage: number;
  current: number;
  power: number;
}

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
  displayedColumns: string[] = ['timestamp', 'voltage', 'current', 'power'];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<MeterReading[]>('http://localhost:8080/api/readings')
      .subscribe(data => this.readings = data);
  }
}
