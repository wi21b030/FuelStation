# Fuel Station Project Documentation

## About the project

This project was created by Hadi Heydari, Kevin Xhunga and Safwan Zullash
as a team for our company which manages charging stations for electric cars. The goal of this project is the fullstack development 
of a Fuel Station App. Customers can use this application to generate invoices for their electric car charging activities. 

For the frontend we used a simple JavaFX UI and to build our API we used the SpringBoot framework. 
Furthermore, we used RabbitMQ to align our messages and requests between the services and the databases, which are PostgreSQL instances.
To accommodate the whole development team and ensure the same working environments for everyone we used GitHub 
as our version control system to host our repository and additionally make use of Docker containers for the databases and the RabbitMQ instance.

## System Architecture

Hier kommt das UML-Diagramm.

## Set-Up and Installation

To use our application first you need to clone this whole project from our [GitHub repository](https://github.com/wi21b030/FuelStation). 
Then follow these steps:

1. Start your Docker Desktop application and then open your preferred command line interpreter and run the commands below. This will run the docker-compose file and create the databases and the RabbitMQ queue in a containerised environment.
    ```bash
    cd "./Backend/Databases" # switch to this directory starting from the root directory of this project
    docker compose up
    ```
2. Once the containers are up and running you need to open this project in IntelliJ.
3. Then run these parts of the application in this order:
   1. Java FX UI - [Here](./Frontend/JavaFXApp/src/main/java/com/example/javafxapp/HelloApplication.java)
   2. SpringBoot API - [Here](./Backend/SpringApp/src/main/java/com/example/springapp/SpringAppApplication.java)
   3. DataCollectionDispatcher - [Here](./Backend/DataCollectionDispatcher/src/main/java/org/example/Main.java)
   4. 

