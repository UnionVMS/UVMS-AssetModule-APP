liquibase --driver=org.postgresql.Driver --classpath=lib\postgresql-42.2.1.jar --changeLogFile=db-changelog-createTables.xml --url="jdbc:postgresql://localhost:15432/db71u" --username=asset --password=asset generateChangeLog

	  