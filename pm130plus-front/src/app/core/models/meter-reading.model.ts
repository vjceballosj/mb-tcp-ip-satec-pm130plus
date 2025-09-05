export interface MeterReading {
  id: number;
  registerAddress: number;
  value: number;
  timestamp: string; // ISO 8601 string del backend
}
