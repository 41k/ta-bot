import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { createRequestOption, Pagination } from 'app/shared/util/request-util';
import { SERVER_API_URL } from 'app/app.constants';
import { Trade } from '../model/trade.model';
import { StrategyExecution } from '../model/strategy-execution.model';

export interface GetExchangeGatewaysRequest {
  fromTimestamp: number;
  toTimestamp: number;
}

export interface GetStrategyExecutionsRequest {
  fromTimestamp: number;
  toTimestamp: number;
  exchangeGateway: string;
}

export interface GetTradesRequest extends Pagination {
  fromTimestamp: number;
  toTimestamp: number;
  exchangeGateway?: string;
  strategyExecutionId?: string;
}

@Injectable({ providedIn: 'root' })
export class HistoryApiClient {
  baseUrl!: string;

  constructor(private http: HttpClient) {
    this.baseUrl = SERVER_API_URL + '/api/history';
  }

  getExchangeGateways(request: GetExchangeGatewaysRequest): Observable<HttpResponse<string[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.baseUrl + '/exchange-gateways';
    return this.http.get<string[]>(requestURL, {
      params,
      observe: 'response',
    });
  }

  getStrategyExecutions(request: GetStrategyExecutionsRequest): Observable<HttpResponse<StrategyExecution[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.baseUrl + '/strategy-executions';
    return this.http.get<StrategyExecution[]>(requestURL, {
      params,
      observe: 'response',
    });
  }

  getTrades(request: GetTradesRequest): Observable<HttpResponse<Trade[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.baseUrl + '/trades';
    return this.http.get<Trade[]>(requestURL, {
      params,
      observe: 'response',
    });
  }
}
