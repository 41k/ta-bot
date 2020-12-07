import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { Strategy } from '../model/strategy.model';

@Injectable({ providedIn: 'root' })
export class StrategyApiClient {
  baseUrl!: string;

  constructor(private http: HttpClient) {
    this.baseUrl = SERVER_API_URL + '/api/strategies';
  }

  getStrategies(): Observable<HttpResponse<Strategy[]>> {
    return this.http.get<Strategy[]>(this.baseUrl, {
      observe: 'response',
    });
  }
}
