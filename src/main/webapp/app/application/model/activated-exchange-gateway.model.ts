export class ActivatedExchangeGateway {
  constructor(public id: string, public name: string, public accountId: number, public accountConfiguration: Map<string, string>) {}
}
