import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MeterReading } from '../models/meter-reading.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MeterReadingService {
  private apiUrl = `${environment.apiUrl}/readings`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<MeterReading[]> {
    return this.http.get<MeterReading[]>(this.apiUrl).pipe(
      catchError(err => {
        console.error('❌ Error fetching all readings:', err);
        return throwError(() => err);
      })
    );
  }

  getById(id: number): Observable<MeterReading> {
    return this.http.get<MeterReading>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error(`❌ Error fetching reading ${id}:`, err);
        return throwError(() => err);
      })
    );
  }
}
