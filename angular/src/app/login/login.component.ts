import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm = {
    username: '',
    password: ''
  };

  constructor() {
  }

  ngOnInit(): void {
  }

  login() {
    console.log('LOgging IN ', this.loginForm);
  }
}
