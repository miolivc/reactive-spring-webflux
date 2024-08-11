# reactive-spring-webflux
Spring Webflux

#### Install Mongo DB in MAC

- Run the below command to install the **MongoDB**.

```
brew services stop mongodb
brew uninstall mongodb

brew tap mongodb/brew
brew install mongodb-community
```

-  How to restart MongoDB in your local machine.

```
brew services restart mongodb-community
```

#### Install Mongo DB in Windows

- Follow the steps in the below link to install Mongo db in Windows.

https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/


#### Using Mongo DB as a docker container

```
docker run --name mongodb -d -p 27017:27017 mongo
```
