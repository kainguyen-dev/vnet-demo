# Vnet DEMO

## 1. How to install this app

Requirement:
- Java 11
- Angular CLI installed
- Node JS


Open your terminal and run these commands:
```shell

# STEP 1: clone this repo
git clone https://github.com/kainguyen-dev/vnet-demo.git
cd vnet-demo

# STEP 2: Build project ( both FE and BE will be built)

# if your computer is WINDOW:
    mvnw.cmd clean package
# if your computer is MAC:
    mvnw clean package

# STEP 3: Run backend
java -jar backend\target\backend-0.0.1-SNAPSHOT.jar

# STEP 4: Build Front end 
# Open a new terminal and run 
cd frontend
ng serve 

```

Open url at http://localhost:4200/ 
to see result:

![Alt Text](DEMO.png)


## 2. Description

As the requirement describe: 

```
However, Tom's Boss is a difficult person and always changes requirements.

In the future, he can change or add more requirements such as: only aggregate by product, only aggregate by store or other requirements (not limited product and store fields). 

Therefore, you need to build the system to easily scale or modify when there is a new request from this difficult boss.
```

The main idea is to build a **KafkaStream** to aggregate these values (product, store) that satisfy Tom's boss requirement at runtime; there is no need 
to add new code when new requirements come in, we just have to add new stream in configuration file and restart applications.

In order to do that, we build a generic template for streams:
```yaml
    stream:
        aggragate-by-store:
            topic: SALE_REPORT_BY_STORE
            group-key: StoreName
            sum-field: SalesUnits, SalesRevenue
            log: false
        aggragate-by-product:
            topic: SALE_REPORT_BY_PRODUCT
            group-key: ProductName
            sum-field: SalesUnits, SalesRevenue
            log: false

```

And build each stream when the application starts.

When the browser opens, a new web socket connection is established, and the backend creates a Kafka consumer to listen for aggreated values of the streaming topic and show them to the users.

## 3. In actions

Below is the gif file record application's data is updated without refresh the page

![Alt Text](demo.gif)
