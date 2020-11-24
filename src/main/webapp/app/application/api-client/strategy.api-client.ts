import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { createRequestOption } from 'app/shared/util/request-util';
import { SERVER_API_URL } from 'app/app.constants';
import { ActiveStrategy } from '../model/active-strategy.model';

export interface GetStrategiesRequest {
  exchangeGatewayId: string;
}

export interface StrategyActivationRequest {
  exchangeGatewayId: string;
  strategyId: string;
  amount: number;
}

export interface StrategyDeactivationRequest {
  exchangeGatewayId: string;
  strategyId: string;
}

@Injectable({ providedIn: 'root' })
export class StrategyApiClient {
  strategiesApiUrl!: string;

  constructor(private http: HttpClient) {
    this.strategiesApiUrl = SERVER_API_URL + '/api/strategies';
  }

  getActiveStrategies(request: GetStrategiesRequest): Observable<HttpResponse<ActiveStrategy[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.strategiesApiUrl + '/active';
    return this.http.get<ActiveStrategy[]>(requestURL, {
      params,
      observe: 'response',
    });
  }

  getInactiveStrategies(request: GetStrategiesRequest): Observable<HttpResponse<string[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.strategiesApiUrl + '/inactive';
    return this.http.get<string[]>(requestURL, {
      params,
      observe: 'response',
    });
  }

  activateStrategy(request: StrategyActivationRequest): Observable<{}> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.strategiesApiUrl + '/activate';
    return this.http.post(
      requestURL,
      {},
      {
        params,
      }
    );
  }

  deactivateStrategy(request: StrategyDeactivationRequest): Observable<{}> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = this.strategiesApiUrl + '/deactivate';
    return this.http.post(
      requestURL,
      {},
      {
        params,
      }
    );
  }
}
