// src\app\services\websocket.service.ts
import { Injectable } from "@angular/core";
import { Observable } from 'rxjs';
import { Subject } from 'rxjs';

const CHAT_URL = "ws://localhost:6000";

export interface Message {
    source: string;
    content: string;
}

@Injectable({
    providedIn: 'root'
  })
export class WebsocketService {
    private socket$!: WebSocket;
    private messageSubject$: Subject<any> = new Subject<any>();
  
    constructor() { }
  
    public connect(url: string): void {
      this.socket$ = new WebSocket(url);
  
      this.socket$.onopen = () => {
        console.log('WebSocket connection established.');
      };
  
      this.socket$.onmessage = (event) => {
        const message = JSON.parse(event.data);
        this.messageSubject$.next(message);
      };
  
      this.socket$.onclose = () => {
        console.log('WebSocket connection closed.');
        this.messageSubject$.complete();
      };
    }
  
    public getMessages(): Observable<any> {
      return this.messageSubject$.asObservable();
    }
  
    public send(message: any): void {
      this.socket$.send(JSON.stringify(message));
    }
  
    public close(): void {
      this.socket$.close();
    }
}
