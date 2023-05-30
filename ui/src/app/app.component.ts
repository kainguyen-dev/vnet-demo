//src\app\app.component.ts
import {Component, OnInit} from '@angular/core';
import {WebsocketService} from "./websocket-service.service";


@Component({
    selector: "app-root",
    templateUrl: "./app.component.html",
    styleUrls: ["./app.component.css"],
    providers: [WebsocketService]
})

export class AppComponent implements OnInit {

    COUNTER_MAX = 30;

    counterProduct: number = this.COUNTER_MAX;
    counterStore: number = this.COUNTER_MAX;


    public data: any = {
        "product": {},
        "store": {},
    }
    public displayProduct: any[] = [];
    public displayStore: any[] = [];

    public setting: Object = {
        aggregate: "byProduct",
        ws: "aggragate-by-product"
    }

    constructor(
        private productWebsocketService: WebsocketService,
        private storeWebsocketService: WebsocketService // Add a second instance variable
    ) {
    }


    ngOnInit(): void {

        setInterval(() => {
            this.counterProduct--;
            this.counterStore--;
        }, 1000);

        this.productWebsocketService.connect('ws://localhost:9090/aggragate-by-product');

        this.productWebsocketService.getMessages().subscribe((message) => {


            if (message["group"] === "/aggragate-by-product") {
                this.counterProduct = 15;

                this.data["product"][message["entity"]] = message["data"];

                this.displayProduct = Object.keys(this.data["product"]).map(key => {
                    const store = JSON.parse(this.data["product"][key]);
                    return {
                        name: key,
                        SalesUnits: store.SalesUnits,
                        SalesRevenue: store.SalesRevenue
                    };
                });
            }
        });


        this.storeWebsocketService.connect('ws://localhost:9090/aggragate-by-store');

        this.storeWebsocketService.getMessages().subscribe((message) => {

            if (message["group"] === "/aggragate-by-store") {

                this.counterStore = 15;

                this.data["store"][message["entity"]] = message["data"];

                this.displayStore = Object.keys(this.data["store"]).map(key => {
                    const store = JSON.parse(this.data["store"][key]);
                    return {
                        name: key,
                        SalesUnits: store.SalesUnits,
                        SalesRevenue: store.SalesRevenue
                    };
                });
            }
        });

    }

    ngOnDestroy(): void {
        this.productWebsocketService.close();
        this.storeWebsocketService.close();
    }

}
