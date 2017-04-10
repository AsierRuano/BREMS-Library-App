import { Component, ViewChild } from '@angular/core';
import { ModalLogin } from "./component/login/modal.component/modal-login";


@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  @ViewChild(ModalLogin)
  private modalLogin: ModalLogin;

  // -----------------------------------------------------------------------------------------------------------------

  openLogin() {
    this.modalLogin.open();
  }
}
