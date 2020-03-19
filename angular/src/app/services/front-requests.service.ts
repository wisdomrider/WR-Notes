import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FrontRequestsService {

  constructor(private http: HttpClient) {
  }

  login(form) {
  }
}
