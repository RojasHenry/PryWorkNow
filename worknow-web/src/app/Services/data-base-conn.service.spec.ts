import { TestBed } from '@angular/core/testing';

import { DataBaseConnService } from './data-base-conn.service';

describe('DataBaseConnService', () => {
  let service: DataBaseConnService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataBaseConnService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
