import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { createRequestOption, Pagination } from 'app/shared/util/request-util';
import { SERVER_API_URL } from 'app/app.constants';
import { Trade } from '../model/trade.model';

export interface TradesHistoryRequest extends Pagination {
  fromTimestamp: number;
  toTimestamp: number;
}

@Injectable({ providedIn: 'root' })
export class HistoryService {
  constructor(private http: HttpClient) {}

  getTradesHistory(request: TradesHistoryRequest): Observable<HttpResponse<Trade[]>> {
    const params: HttpParams = createRequestOption(request);
    const requestURL = SERVER_API_URL + '/api/history/trades';
    return this.http.get<Trade[]>(requestURL, {
      params,
      observe: 'response',
    });
  }
}
