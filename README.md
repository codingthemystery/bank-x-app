	BankX Application by Caroline Jones


	Overview:

		The application is Spring REST interface which consists of the following technologies:

		1. 	Spring Boot 3.0.5, with Java 17
		2. 	MySql or H2 database options
		3. 	Kafka and Zookeeper for offline processes
		4. 	Freebase for push notifications
		4. 	Maven asd the build tool

			No frontend is provided. However, a Postman project is provided as well with all the API calls and JSON examples.

			It runs as a stand-alone jar file. A docker-compose.yaml file is provided to create the Kakfa and Zooper servers, if needed.
	
	
	Quick Start:
	
		1.	Start Docker
		2.	Navigate to root directory
		3.	Type:
				docker-compose.yaml
			Check broker and zookeeper running:
				docker ps
		4.	Build:
				mvn package -D:skip-tests
  
		5.	Run:
			java -j target/Bankx-1.0.0-SNAPSHOT.jar
		
		6.	Postman - Use:
			http://localhost:8080
	
		7.	H2 Database:
			http://localhost:8080/h2-console
			
		8. 	Send an email address to cajones6576@gmail.com for Postman access.
			
	
	Getting Started:

	1. 		Clone the application:

				https://github.com/givanthak/spring-boot-rest-api-tutorial.git


	2. 		Configure for your environment. (Helpful command: mvn help:active-profiles)

	2.1.	Maven and Spring:
	
			There are 3 Maven profiles in pom.xml: DEV, STAGE and PRD. STAGE is set as the default will be used if none is provided at build time.
	
			There are two Spring profiles: win and linux. The spring.profiles.active property (in the application.properties file) is set at build 
			time depending on which Maven profile is used to build the project. Currently, DEV will activate the win profile and STAGE and PRD will active the linux profile. This 
			can be changed in the pom.xml file or at build time (using -P [profile.id]).

				[profiles>
					[profile>
						[id>DEV[/id>
						[properties>
							[spring.profiles.active>win[/spring.profiles.active>
						[/properties>
						[activation>
							[activeByDefault>false[/activeByDefault>
						[/activation>
					[/profile>
					[profile>
						[id>STAGE[/id>
						[properties>
							[spring.profiles.active>linux[/spring.profiles.active>
						[/properties>
						[activation>
							[activeByDefault>true[/activeByDefault>
						[/activation>
					[/profile>
					[profile>
						[id>PRD[/id>
						[properties>
							[spring.profiles.active>linux[/spring.profiles.active>
						[/properties>
						[activation>
							[activeByDefault>false[/activeByDefault>
						[/activation>
					[/profile>
				[/profiles>

			Go to src/main/resources and change the application-win.properties or application-linux.properties as necessary.	
		
	2.2.	Logging:
		
			The application uses Logback. To adjust the settings, navigate to the /src/main/resources folder and makes changes 
			to:
		
				logback-spring.xml


	3. 		Database:
	
	3.1.	There are two database configurations: MySql and H2. By default, the win profile uses MySql and the linux profile 
			H2 but this can be changed. 
			
	3.1.1	To run the application in memory with the database, schema and data being refreshed on each startup, use these
			settings: (this is the default setting with the linux profile)
			
				spring.datasource.url=jdbc:h2:mem:bankx
				spring.jpa.hibernate.ddl-auto=create-drop (options: create, create-drop, validate, update)
				spring.sql.init.mode=always	
	
	3.1.2.	If you want to persist the data, you need to either supply a schema.sql file or let Spring create it on the 
			first run with these settings:
			
				spring.jpa.hibernate.ddl-auto=create-drop (options: create, create-drop, validate, update, none)
				spring.sql.init.mode=always	
				
			Once the database has been created, change the settings to:
			
				spring.jpa.hibernate.ddl-auto=none
				spring.sql.init.mode=never		
	
			If using H2, use this setting to persist to the file system:
			
				spring.datasource.url=jdbc:h2:file:./data/bankx
			
			Or, use Mysql. Note, that when using MySql, the database needs to be created before running the app:
		
				CREATE DATABASE `bankx`
				
			To recreate, first use this command:
			
				USE `bankx`;
				DROP DATABASE `bankx`;
				
			Test with:
			
				SHOW DATABASES;
				
	3.1.3.	Data:
	
			On startup, a data.sql file in the resources folder, polulates the database. Bank X and Bankz Z are created, 
			along with customer, accounts and transctions. 

			NB: CHECK THAT THE ID VALUES FOR BANK X AND BANK Y ARE NOT 1 AND 2 RESPECTIVELY. IF NOT, CHANGE THESE PROPERTIES 
			tHE SPRING PROPERTIES FILE TO MATCH YOUR DATABASE:
			
				bankx.bankid=1
				bankz.bankid=2
		
			Add to the data.sql file to suit your needs. 
	
	
	4.		Building the Application:
	
	4.1.	Build and run the app using maven. If no profile is added to the cmd, STAGE will be used by default, activating 
			the Spring linux profile.
	
			From the root directory of the project, type:
		
				mvn package -Dmaven.test.skip (optional: -P [DEV, STAGE or PRD]) 
			
			To check, use:
		
				mvn help:active-profiles
			
	
	5.		Kafka Setup
	
			The application requires a Kafka/Zookeeper server. You can change the configuration in the application-[profile].properties file
			to use an existing server, or you can use the provided docker-compose.yaml script to create a server environment.
		
	5.1.	To use an existing environment, adjust these settings in the Spring property file:
	
				spring.kafka.bootstrap-servers=localhost:9092
			
	5.2.	Topics:
	
			The following topics are created at startup:
		
				transactions
				transactions-reponses
		
				reconciliations
				reconciliations-responses
		
	5.3.	The groupid can be adjusted in the Spring properties file:
		
				bankz.groupid=bankz

	5.4.	Starting Kafka/Zookeeper:
	
			Docker needs to be installed on the machine.
			
	5.6.	From the project's root directory, run:
			
				docker-compose up -d
				
			Make sure both broker (kafka) and zookeeper are running:
				
				docker ps
			
			Useful commands:
		
				docker ps
				docker stop broker
				docker stop zookeeper
				docker kill broker 
				docker kill zookeeper
				
			To access the topics:
			
			docker exec --interactive --tty broker kafka-console-producer --bootstrap-server broker:9092 -topic [topicName] --from-beginning

			
	6. 		Run the Application:
	
			From the projects root directory, type:
		
				java -jar Bankx-1.0.0-SNAPSHOT.jar
			
			Alternatively, you can run the app without packaging it using -

				mvn spring-boot:run -Dmaven.test.skip
			
			Use CTRL+C to cancel.
		
			The app will start running on http://localhost:8080
		
		
	Explore the REST APIs
	
			The application provides two APIs. The first is accessed directly through REST API calls; the second uses 
			Kafka to write and read from topics. The intent is that internal systems and a front-end app would use the 
			first API, while external organizations, such as Bank Z would use the latter.
	
	7.1.	Internal REST API:
	
	7.1.1.	Basic CRUD Calls:
	
			There are four database tables:
				
				banks
				customers
				accounts
				transactions
			
			CRUD APIs are accessed uniformly with this URIs:
	
				GET http://[hostname:port]/[table] 
		
				GET http://[hostname:port]/[table]/[id]
		
				POST http://[hostname:port]/[table]
		
				PUT http://[hostname:port]/[table]/[id]
		
				DELETE http://[hostname:port]/[table]/[id]
				
				Deletions are set to cascade and any records in dependent tables will be erased. For  instance, deleting 
				an account will delete all transactions for that account. Deleting a customer will delete all accounts and 
				transactions for that customer. Deleting a bank will delete all customers, accounts and transactions for
				that bank.
		
	7.1.2.	Additional queries:
	
				Get Account by cardNumber:
				
				http://localhost:8080/accounts/card?cardnumber=[cardNumber]
				
				Get Accounts by CustomerId:
				
				GET http://[hostname:port]/customers/accounts/[customerId]
				
	7.1.3.	Additional Operations:
	
	7.1.3.1.Customer onboarding:
			
				POST http://[hostname:port]/customers
			
			This will create a new customer, a SAVINGS and a CURRENT account with a starting bonus of R500. A record in the 
			Transactions database will reflect this credit.
			
	7.1.3.1.Transactions:
	
			There are several types of transactions, each with their own rules:
			
				TRANSFER
				PAYMENT
				CREDIT
				DEBIT
				FEE
				INTEREST		
				
			They are accessed via:
			
				POST http://[hostname:port]/transactions	

				Change the TransactionType in the JSON to one of the above.
				
			Multiple transactions can be sent at once for immediate processing:
			
				POST http://[hostname:port]/transactions/multi
				
	
	7.2.	The Bank Z API:
	
			This service uses a DTO for Transaction and therefore the JSON is slightly different.
			
			All services are accessed starting with /api:
			
				http://[hostname:port]/api
			
	7.2.1.	Queries:
	
			Get all Transaction originated by Bank Z. Order by External Reference.
	
			GET http://localhost:8080/api/transactions
			
			Get Transaction by Transaction Id (Currently doesn't restrict to Bank Z only)
			
			GET http://localhost:8080/api/transactions/[transactionId]
			
	7.2.1.	Transactions and Reconciliations:
	
			Batches of Transactions are submitted to Kafka topics to be processed offline.
			
	7.2.1.2.Transactions: (transactions topic)
			
				POST http://localhost:8080/api/transactions/batch
			
			Responses are posted to transactions-responses:
			
				GET http://localhost:8080/api/transactions/batch
				
				Not implemented yet - retrieve from Kafka server.	
			
			
	7.2.1.2.Reconciliations: (reconciliations topic)
			
				POST http://localhost:8080/api/transactions/reconciliations
				
			Responses are posted to transactions-responses:
			
				GET http://localhost:8080/api/transactions/reconciliations	
				
				Not implemented yet - retrieve from Kafka server.	
	
	
	Known Limitations and Bugs:
	
			Due to time restrictions, some features haven't been fully implemented. There are also a few bugs.
	
	8.1.	Lack of a frontend. You can use Postman to interact with the API in the meantime.
	
	8.1.	Firebase:
	
			Firebase is used to send push notifications to customers to notify them of every transaction. For it to fully
			work, the customer would need to login in and provide a token. Without this token, the notification fails. 
			
			A workaround is to include a valid token in the token field of the Customer database.
			
	8.2.	Nice to have: Add Spring bath scheduling to the Kafta consumer processes.
	
	8.3.	Retrieving the responses for the batch submissions is not yet implemented. The producer may need to change too.
	
	8.4.	Testing:
		
			Tests haven't been provided.
			Reconciliations have been fully tested.
			Cross bank transactions handling not coherent.
			
	8.5. 	The rules applying to transacting and the debiting and crediting of accounts hasn't been robustly tested.
	
	8.6.	Business rules around what Bank Z can do transactionally are sketchy and are either not defined or enforced
			inconsistently.
			
