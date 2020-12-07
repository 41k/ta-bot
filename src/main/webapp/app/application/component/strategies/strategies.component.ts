import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { StrategyExecutionRunComponent } from './strategy-execution-run.component';
import { StrategyApiClient } from '../../api-client/strategy.api-client';
import { Strategy } from '../../model/strategy.model';

@Component({
  selector: 'jhi-strategies',
  templateUrl: './strategies.component.html',
})
export class StrategiesComponent {
  strategies!: Strategy[];

  constructor(private strategyApiClient: StrategyApiClient, private modalService: NgbModal) {
    this.loadStrategies();
  }

  loadStrategies(): void {
    this.strategyApiClient.getStrategies().subscribe((response: HttpResponse<Strategy[]>) => {
      const strategies = response.body;
      if (strategies && strategies.length > 0) {
        this.strategies = strategies;
      } else {
        this.strategies = [];
      }
    });
  }

  activateStrategy(strategy: Strategy): void {
    const modalRef = this.modalService.open(StrategyExecutionRunComponent);
    modalRef.componentInstance.initialize(strategy);
    modalRef.result.then(() => this.loadStrategies());
  }
}
