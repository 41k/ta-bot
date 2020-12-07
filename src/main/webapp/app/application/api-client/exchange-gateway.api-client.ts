import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ExchangeGateway } from '../model/exchange-gateway.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable({ providedIn: 'root' })
export class ExchangeGatewayApiClient {
  baseUrl!: string;

  constructor(private http: HttpClient) {
    this.baseUrl = SERVER_API_URL + '/api/exchange-gateways';
  }

  getExchangeGateways(): Observable<HttpResponse<ExchangeGateway[]>> {
    return this.http.get<ExchangeGateway[]>(this.baseUrl, {
      observe: 'response',
    });
  }

  getSymbols(exchangeGatewayId: string): Observable<HttpResponse<string[]>> {
    const requestUrl = this.baseUrl + `/${exchangeGatewayId}/supported-symbols`;
    return this.http.get<string[]>(requestUrl, {
      observe: 'response',
    });
  }

  getIntervals(exchangeGatewayId: string): Observable<HttpResponse<string[]>> {
    const requestUrl = this.baseUrl + `/${exchangeGatewayId}/supported-intervals`;
    return this.http.get<string[]>(requestUrl, {
      observe: 'response',
    });
  }
}
