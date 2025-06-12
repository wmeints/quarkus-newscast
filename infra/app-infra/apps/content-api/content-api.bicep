param location string
param resourceName string
param containerImage string
param databaseServerResourceName string
@secure()
param databaseUsername string
@secure()
param databasePassword string

resource databaseServer 'Microsoft.DBforPostgreSQL/flexibleServers@2025-01-01-preview' existing = {
  name: databaseServerResourceName
}

resource contentApiApplication 'Microsoft.App/containerApps@2025-02-02-preview' = {
  name: resourceName
  location: location
  properties: {
    configuration: {
      secrets: [
        {
          name: 'DATABASE_USER'
          value: databaseUsername
        }
        {
          name: 'DATABASE_PASSWORD'
          value: databasePassword
        }
      ]
    }
    template: {
      containers: [
        {
          image: containerImage
          resources: {
            cpu: 1
            memory: '1GiB'
          }
          env:[
            {
              name: 'QUARKUS_DATASOURCE_JDBC_URL'
              value: 'jdbc:postgresql://${databaseServer.name}.postgres.database.azure.com:5432/content'
            }
            {
              name: 'QUARKUS_DATASOURCE_USERNAME'
              secretRef: 'DATABASE_USER'
            }
            {
              name: 'QUARKUS_DATASOURCE_PASSWORD'
              secretRef: 'DATABASE_PASSWORD'
            }
          ]
        }
      ]
      scale: {
        maxReplicas: 1
        minReplicas: 1
      }
    }
  }
}
