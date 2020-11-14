import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { createRequestOption, Pagination } from 'app/shared/util/request-util';
import { SERVER_API_URL } from 'app/app.constants';
import { Trade } from '../model/trade.model';

export interface GetExchangesRequest {
  fromTimestamp: number;
  toTimestamp: number;
}

export interface GetStrategiesRequest {
  fromTimestamp: number;
  toTimestamp: number;
  exchangeId: string;
}

export interface GetTradesRequest extends Pagination {
  fromTimestamp: number;
  toTimestamp: number;
  exchangeId?: string;
  strategyId?: string;
}

@Injectable({ providedIn: 'root' })
export class HistoryApiClient {
  historyApiUrl!: string;

  constructor(private http: HttpClient) {
    this.historyApiUrl = SERVER_API_URL + '/api/history';
  }

  getExchanges(request: GetExchangesRequest): Observable<HttpResponse<string[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.historyApiUrl + '/exchanges';
    return this.http.get<string[]>(requestURL, {
      params,
      observe: 'response',
    });
  }

  getStrategies(request: GetStrategiesRequest): Observable<HttpResponse<string[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.historyApiUrl + '/strategies';
    return this.http.get<string[]>(requestURL, {
      params,
      observe: 'response',
    });
  }

  getTrades(request: GetTradesRequest): Observable<HttpResponse<Trade[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.historyApiUrl + '/trades';
    return this.http.get<Trade[]>(requestURL, {
      params,
      observe: 'response',
    });
  }
}