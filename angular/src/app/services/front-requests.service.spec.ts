import { TestBed } from '@angular/core/testing';

import { FrontRequestsService } from './front-requests.service';

describe('FrontRequestsService', () => {
  let service: FrontRequestsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FrontRequestsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
