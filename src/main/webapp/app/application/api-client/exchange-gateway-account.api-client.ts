import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from 'app/app.constants';

@Injectable({ providedIn: 'root' })
export class ExchangeGatewayAccountApiClient {
  urlPrefix!: string;

  constructor(private http: HttpClient) {
    this.urlPrefix = SERVER_API_URL + '/api/exchange-gateways/';
  }

  createExchangeGatewayAccount(exchangeGatewayId: string, configuration: Map<string, string>): Observable<{}> {
    const requestURL = this.urlPrefix + `${exchangeGatewayId}/accounts`;
    const requestBody = Array.from(configuration).reduce((obj, [key, value]) => Object.assign(obj, { [key]: value }), {});
    return this.http.post(requestURL, requestBody);
  }

  deleteExchangeGatewayAccount(exchangeGatewayId: string, accountId: number): Observable<{}> {
    const requestURL = this.urlPrefix + `${exchangeGatewayId}/accounts/${accountId}`;
    return this.http.delete(requestURL);
  }
}
