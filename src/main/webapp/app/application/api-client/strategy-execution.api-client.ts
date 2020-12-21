import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { StrategyExecution } from '../model/strategy-execution.model';

export interface RunStrategyExecutionRequest {
  strategyId: string;
  symbol: string;
  amount: number;
  interval: string;
}

@Injectable({ providedIn: 'root' })
export class StrategyExecutionApiClient {
  urlPrefix!: string;

  constructor(private http: HttpClient) {
    this.urlPrefix = SERVER_API_URL + '/api/exchange-gateways/';
  }

  getStrategyExecutions(exchangeGatewayId: string, accountId: number): Observable<HttpResponse<StrategyExecution[]>> {
    const requestURL = this.urlPrefix + `${exchangeGatewayId}/accounts/${accountId}/strategy-executions`;
    return this.http.get<StrategyExecution[]>(requestURL, {
      observe: 'response',
    });
  }

  runStrategyExecution(exchangeGatewayId: string, accountId: number, requestBody: RunStrategyExecutionRequest): Observable<{}> {
    const requestURL = this.urlPrefix + `${exchangeGatewayId}/accounts/${accountId}/strategy-executions`;
    return this.http.post(requestURL, requestBody);
  }

  stopStrategyExecution(exchangeGatewayId: string, accountId: number, strategyExecutionId: string): Observable<{}> {
    const requestURL = this.urlPrefix + `${exchangeGatewayId}/accounts/${accountId}/strategy-executions/${strategyExecutionId}`;
    return this.http.delete(requestURL);
  }
}
