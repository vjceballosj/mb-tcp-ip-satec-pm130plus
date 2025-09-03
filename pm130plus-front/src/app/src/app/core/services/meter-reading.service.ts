import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MeterReading {
  id: number;
  registerAddress: number;
  value: number;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class MeterReadingService {
  private apiUrl = 'http://localhost:8080/api/readings'; // Ajustar si tu backend corre en otro host/puerto

  constructor(private http: HttpClient) {}

  getAll(): Observable<MeterReading[]> {
    return this.http.get<MeterReading[]>(this.apiUrl);
  }

  getById(id: number): Observable<MeterReading> {
    return this.http.get<MeterReading>(`${this.apiUrl}/${id}`);
  }
}
