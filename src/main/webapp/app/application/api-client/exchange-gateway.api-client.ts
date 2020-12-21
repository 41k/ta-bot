import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { ActivatedExchangeGateway } from '../model/activated-exchange-gateway.model';
import { NotActivatedExchangeGateway } from '../model/not-activated-exchange-gateway.model';

@Injectable({ providedIn: 'root' })
export class ExchangeGatewayApiClient {
  baseUrl!: string;

  constructor(private http: HttpClient) {
    this.baseUrl = SERVER_API_URL + '/api/exchange-gateways';
  }

  getActivatedExchangeGateways(): Observable<HttpResponse<ActivatedExchangeGateway[]>> {
    const requestUrl = this.baseUrl + '/activated';
    return this.http.get<ActivatedExchangeGateway[]>(requestUrl, {
      observe: 'response',
    });
  }

  getNotActivatedExchangeGateways(): Observable<HttpResponse<NotActivatedExchangeGateway[]>> {
    const requestUrl = this.baseUrl + '/not-activated';
    return this.http.get<NotActivatedExchangeGateway[]>(requestUrl, {
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

  getAccountConfigurationProperties(exchangeGatewayId: string): Observable<HttpResponse<string[]>> {
    const requestUrl = this.baseUrl + `/${exchangeGatewayId}/supported-account-configuration-properties`;
    return this.http.get<string[]>(requestUrl, {
      observe: 'response',
    });
  }
}
