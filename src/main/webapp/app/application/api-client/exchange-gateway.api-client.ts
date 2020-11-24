import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ExchangeGatewayApiClient {
  constructor(private http: HttpClient) {}

  getExchangeGateways(): Observable<HttpResponse<string[]>> {
    const requestURL = '/api/exchange-gateways';
    return this.http.get<string[]>(requestURL, {
      observe: 'response',
    });
  }
}
