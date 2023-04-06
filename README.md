	BankX Application by Caroline Jones


	1.	Overview:

				The application is a Spring REST interface which consists of the following technologies:

				- 	Spring Boot 3.0.5, with Java 17.
	
				- 	Choice between MySql or H2.
	
				- 	Kafka and Zookeeper for offline processes.
	
				- 	Freebase for push notifications.
	
				- 	Maven as the build tool.

				No frontend is provided. However, a Postman project is provided will be shared on request. 
				JSON examples files have been provided in the resources folder.

				The application runs as a stand-alone jar file. A docker-compose.yaml file is provided to 
				create the Kafka and Zooper servers that it needs. It can be configured to use an existing Kafka setup.
	
	
	2.	Quick Start:
	
				To get going quickly, use the defaults and take the following steps:
			
	2.1.		Clone the project:
		
					https://github.com/codingthemystery/bank-x-app.git
				
	2.2.		Start up Docker. 
				
	2.3.		Navigate to root directory of the projec and enter the command to start up the Kafka server configuration:
				
					docker-compose.yaml
				
	2.4.		Check that the containers broker and zookeeper are running:
				
					docker ps
				
	5.4.		Run the mvn build command:
		
					mvn package
				
	2.5.		Run the application:
		
					java -j target/Bankx-1.0.0-SNAPSHOT.jar
				
	7.6.		Use the following URL in Postman to access the API endpoint. For a link to the existing Postman
				project, send an email to codingthemystery@gmail.com so that the link can be shared.
			
					http://localhost:8080
				
				Locate the sample JSON files in the resources folder. The API endpoints are documented below.
			
	2.7.		To view data in the in-memory H2 Database, open your browser and type this URL: (Use the default name and a blank
				password:
			
					http://localhost:8080/h2-console
			
	
	3. 	Configuring the Application:

	3.1.		Clone the application from GitHub:
			
					https://github.com/codingthemystery/bank-x-app.git


	3.2 		Configure the Spring Environment:
	
	3.2.1.		There are three Maven profiles in pom.xml: DEV, STAGE and PRD. STAGE is set as the default will be used if 
				none is provided at build time.
	
	3.2.2.		There are two Spring profiles: win and linux. The spring.profiles.active property (in the application.properties file) 
				is set at build time depending on which Maven profile is used to build the project. 
				Currently, DEV will activate the win profile and STAGE and PRD will activate the linux profile. This 
				can be changed in the pom.xml file or at build time (using -P [profile.id] operator on the Maven command).

					<profiles>
						<profile>
							<id>DEV</id>
							<properties>
								<spring.profiles.active>win</spring.profiles.active>
							</properties>
							<activation>
								<activeByDefault>false</activeByDefault>
							</activation>
						</profile>
						<profile>
							<id>STAGE</id>
							<properties>
								<spring.profiles.active>linux</spring.profiles.active>
							</properties>
							<activation>
								<activeByDefault>true</activeByDefault>
							</activation>
						</profile>
						<profile>
							<id>PRD</id>
							<properties>
								<spring.profiles.active>linux</spring.profiles.active>
							</properties>
							<activation>
								<activeByDefault>false</activeByDefault>
							</activation>
						</profile>
					</profiles>

	3.2.3.		Go to src/main/resources and change the application-win.properties or application-linux.properties as necessary.	
		
		
	3.3.		Logging:
		
				The application uses Logback. The location of the logs differs depending on the Spring profile. 
				
	3.3.1.		To adjust the settings, navigate to the /src/main/resources folder and makes changes to:
		
					logback-spring.xml


	3.4.		Set Up Your Database:
	
				There are two database configurations: MySql and H2. By default, the win profile uses MySql and the linux profile 
				H2 but this can be changed. 
			
	3.4.1.		To run the application in memory with the database, schema and data being refreshed on each startup, use these
				settings: (this is the default setting with the linux profile)
			
					spring.datasource.url=jdbc:h2:mem:bankx
					spring.jpa.hibernate.ddl-auto=create-drop (options: create, create-drop, validate, update)
					spring.sql.init.mode=always	
	
	3.4.2.		If you want to persist the data, you first need to get the database schema (and database in the case of H2) 
				created and then change the settings afterwards. These settings will create the schema and run the data.sql script
				to populate the database tables. 
				
					spring.jpa.hibernate.ddl-auto=create-drop (options: create, create-drop, validate, update, none)
					spring.sql.init.mode=always	
				
	3.4.3.		Once the database has been created, change the settings to:
			
					spring.jpa.hibernate.ddl-auto=none
					spring.sql.init.mode=never		
	
	3.4.4.		If using H2, use this setting to persist to the file system:
			
					spring.datasource.url=jdbc:h2:file:./data/bankx
			
	3.4.5.		Or, use Mysql. Note, that when using MySql, the database needs to be created before running the app:
		
					CREATE DATABASE `bankx`
				
	3.4.5.1.	To recreate, first use this command:
			
					USE `bankx`;
					DROP DATABASE `bankx`;
				
	3.4.5.2.	Test with:
			
					SHOW DATABASES;
	
	
	3.5.		Data:
	
	3.5.1.		 startup, a data.sql file in the resources folder, polulates the database. Bank X and Bankz Z are created, 
				along with customer, accounts and transactions. 

	3.5.2.		NB: CHECK THAT THE ID VALUES FOR BANK X AND BANK Y ARE NOT 1 AND 2 RESPECTIVELY. IF NOT, CHANGE THESE PROPERTIES 
				IN THE SPRING PROPERTIES FILE TO MATCH YOUR DATABASE:
			
					bankx.bankid=1
					bankz.bankid=2


	3.6.		Building the Application:
	
	3.6.1.		Build and run the app using maven. If no profile is added to the cmd, STAGE will be used by default, activating 
				the Spring linux profile.
	
	3.6.1.1.	From the root directory of the project, type:
		
					mvn package (optional: -P [DEV, STAGE or PRD]) 
			
	3.6.1.2.	To check, use:
		
					mvn help:active-profiles
			
	
	3.7.		Kafka Setup
	
				The application requires a Kafka/Zookeeper server setup. You can change the configuration in the application-[profile].properties file
				to use an existing server, or you can use the provided docker-compose.yaml script to create a server environment.
		
	3.7.1.		To use an existing environment, alter this setting in the Spring property file:
	
					spring.kafka.bootstrap-servers=localhost:9092
			
	3.7.2.		Topics:
	
				The following topics are created at startup: (See below how to view them on the Kafka server.)
		
					transactions
					transactions-reponses
		
					reconciliations
					reconciliations-responses
		
	3.7.3.		The groupid can be adjusted in the Spring properties file:
		
					bankz.groupid=bankz

	3.7.4.		Starting Kafka/Zookeeper:
	
	3.7.4.1.	Docker needs to be installed on the machine and running. From the project's root directory, run:
			
					docker-compose up -d
				
	3.7.4.2.	Make sure both the broker and zookeeper containers are running:
					
					docker ps
			
				Useful commands:
		
					docker ps
					docker stop [containerName]
					docker kill [containerName]
				
	3.7.5.		To access the topics:
			
					docker exec --interactive --tty broker kafka-console-producer --bootstrap-server broker:9092 -topic [topicName] --from-beginning
			
				Enter CTRL+C to exit.
			
			
	4. 	Run the Application:
	
	4.1.		From the projects root directory, type:
		
					java -jar Bankx-1.0.0-SNAPSHOT.jar
			
	4.1.1.		Alternatively, you can run the app without packaging it using:

					mvn spring-boot:run
			
				Use CTRL+C to cancel.
		
				The app will start running on http://localhost:8080
		
		
	4.2. 		Explore the APIs:
	
				The application provides two APIs. The first is accessed directly through REST API calls; the second uses 
				Kafka to write and read from topics. The intent is that internal systems and a front-end app would use the 
				first API, while external organizations, such as Bank Z would use the latter.
	
	4.2.1.		Internal REST API:
	
	4.2.1.1		Basic CRUD Calls:
	
				There are four database tables:
				
					banks
					customers
					accounts
					transactions
			
	4.2.1.2.	Access the CRUD APIs with these standard URLS: :
	
	4.2.1.2.1.	Get all records:
	
					GET http://[hostname:port]/[table] 
					
	4.2.1.2.2.	Get single record by Id:
		
					GET http://[hostname:port]/[table]/[id]
					
	4.2.1.2.3.	Create a new record:
		
					POST http://[hostname:port]/[table]
					
	4.2.1.2.4	Update a record:
		
					PUT http://[hostname:port]/[table]/[id]
		
	4.2.1.2.5.	Delete a record:
		
					DELETE http://[hostname:port]/[table]/[id]
				
				Deletions are set to cascade and any records in dependent tables will be deleted too. For  instance, deleting 
				an account will delete all transactions for that account. Deleting a customer will delete all accounts and 
				transactions for that customer. Deleting a bank will delete all customers, accounts and transactions for
				that bank.
				
		
	4.2.2.		Additional Queries:
	
	4.2.2.1.	Get Account by cardNumber:
				
					http://localhost:8080/accounts/card?cardnumber=[cardNumber]
				
	4.2.2.2.	Get Accounts by CustomerId:
				
					GET http://[hostname:port]/customers/accounts/[customerId]
	
	
	4.2.3.		Additional Operations:
	
	4.2.3.1		Customer Onboarding:
			
					POST http://[hostname:port]/customers
			
				This will create a new customer, a SAVINGS and a CURRENT account with a starting bonus of R500. A record in the 
				Transactions database will reflect this credit.
			
	4.2.3.2.	Transactions:
	
				There are several types of transactions, each with their own rules:
			
					TRANSFER
					PAYMENT
					CREDIT
					DEBIT
					FEE
					INTEREST		
				
	4.2.3.2.1.	To process a transaction, enter the following POST and include the relevant transactionType in the JSON body:
			
					POST http://[hostname:port]/transactions	
		
				
	4.2.3.3.	Multiple transactions can be sent at once for immediate processing:
			
					POST http://[hostname:port]/transactions/multi
					
				
	4.2.2.		The Bank Z API:
	
				This service uses a DTO for Transaction and therefore the JSON is slightly different.
			
				All services are accessed starting with /api:
			
					http://[hostname:port]/api
			
	4.2.2.1.	Queries:
	
	4.2.2.1.1.	Get all Transaction originated by Bank Z. Order by External Reference.
	
					GET http://localhost:8080/api/transactions
			
	4.2.2.1.2.	Get Transaction by Transaction Id (Currently doesn't restrict to Bank Z only)
			
					GET http://localhost:8080/api/transactions/[transactionId]
			
			
	4.2.2.2.	Transactions: (topics: transactions, transactions-responses)
	
				Batches of Transactions are submitted to Kafka topics to be processed offline.
			
	4.2.2.2.1.	To post a batch of Transactions to the transactions topic for processing, post this URL with
				a group of transactions in the body: 
			
					POST http://localhost:8080/api/transactions/batch
					
				View on the Kafka server with this command:
					docker exec --interactive --tty broker kafka-console-consumer --bootstrap-server broker:9092  
						\ --topic transactions --from-beginning
					
	4.2.2.2.2.	Retrieve responses with the URL below. It this endpoint is not yet implemented, view responses on the Kafka server:
	
					docker exec --interactive --tty broker kafka-console-consumer --bootstrap-server broker:9092  
						\ --topic transactions-responses --from-beginning
	
					GET http://localhost:8080/api/transactions/batch
					
	
	4.2.2.3.	Reconciliations: (topics: reconciliations, reconciliations-responses)
	
				Batches of Reconciliations are submitted to Kafka topics to be processed offline. Reconciliations use the same structure as	
				transactions but with different statuses. They lookup the original tranactions and check if it was persisted to the database, the 
				amount and other details. 
			
	4.2.2.3.1.	To post a batch of Reconciliations to the reconciliations topic for processing, post this URL with
				a group of transactions in the body: 
			
					POST http://localhost:8080/api/transactions/reconciliations
					
				View on the Kafka server with this command:
					docker exec --interactive --tty broker kafka-console-consumer --bootstrap-server broker:9092  
						\ --topic reconciliations --from-beginning
					
	4.2.2.2.2.	Retrieve responses with the URL below. It this endpoint is not yet implemented, view responses on the Kafka server:
	
					docker exec --interactive --tty broker kafka-console-consumer --bootstrap-server broker:9092  
						\ --topic transactions-responses --from-beginning
	
					GET http://localhost:8080/api/transactions/reconciliations
					
	
	5.	Known Limitations and Bugs:
	
				Due to time restrictions, some features haven't been fully implemented. There are also a few bugs.
	
	5.1.		Lack of a frontend. You can use Postman to interact with the API in the meantime. Contact Caroline on codingthemystery@gmail.com for a link			
				to the Postman project with all the API endpoints and test data.
	
	5.2.		Firebase:
	
				Firebase is used to send push notifications to customers to notify them of transactions. For it to be fully implemented, however, the customer 
				would need to login in and provide a token. Without this token, the notification fails. 
			
				A workaround is to include a valid token in the token field of the Customer database.
			
	5.3.		Nice to have: Add Spring bath scheduling to the Kafta consumer processes so they can run during off-peak hours.
	
	5.4.		Retrieving the responses for the batch submissions is not yet implemented. The producer code may need to change too.
	
	5.5.		Testing:
		
				Unit and Integrations test have not been provided. The CRUD has been well tested but transactions and reconciliations may include bugs.
				
				Reconciliations have been fully tested. Cross bank transaction handling is not clearly defined or consistently implemented.
