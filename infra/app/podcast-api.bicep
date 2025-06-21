param name string
param location string = resourceGroup().location
param tags object = {}
param imageName string = ''
param containerRegistryName string
param containerRegistryResourceGroupName string
param containerAppsEnvironmentName string
param serviceName string = 'podcast-api'
param databaseServerDomainName string
param databaseServerAdminUsername string
@secure()
param databaseServerAdminPassword string
param azureOpenAIAccountName string
param storageAccountName string
@secure()
param buzzsproutApiKey string
param buzzsproutPodcastId string

var databaseUrl = 'jdbc:postgresql://${databaseServerDomainName}:5432/podcasts?sslmode=require'

resource containerAppsEnvironment 'Microsoft.App/managedEnvironments@2025-02-02-preview' existing = {
  name: containerAppsEnvironmentName
}

resource cognitiveServicesAccount 'Microsoft.CognitiveServices/accounts@2025-04-01-preview' existing = {
  name: azureOpenAIAccountName
}

resource storageAccount 'Microsoft.Storage/storageAccounts@2023-05-01' existing = {
  name: storageAccountName
}

resource applicationIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2025-01-31-preview' = {
  name: 'id-podcast-api'
  location: location
}

module pullRoleAssignment '../core/security/registry-access.bicep' = {
  name: 'pull-role-assignment'
  scope: resourceGroup(containerRegistryResourceGroupName)
  params: {
    containerRegistryName: containerRegistryName
    principalId: applicationIdentity.properties.principalId
  }
}

resource applicationService 'Microsoft.App/containerApps@2025-01-01' = {
  name: name
  location: location
  tags: union(tags, { 'service-name': serviceName })
  dependsOn: [pullRoleAssignment]
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${applicationIdentity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppsEnvironment.id
    configuration: {
      registries: [
        {
          server: '${containerRegistryName}.azurecr.io'
          identity: applicationIdentity.id
        }
      ]
      secrets: [
        {
          name: 'database-password'
          value: databaseServerAdminPassword
        }
        {
          name: 'openai-api-key'
          value: cognitiveServicesAccount.listKeys().key1
        }
        {
          name: 'storage-connection-string'
          value: 'DefaultEndpointsProtocol=https;AccountName=${storageAccount.name};AccountKey=${storageAccount.listKeys().keys[0].value};EndpointSuffix=${environment().suffixes.storage}'
        }
        {
          name: 'buzzsprout-api-key'
          value: buzzsproutApiKey
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'app'
          image: imageName
          resources: {
            cpu: 1
            memory: '2Gi'
          }
          env: [
            {
              name: 'QUARKUS_DATASOURCE_JDBC_URL'
              value: databaseUrl
            }
            {
              name: 'QUARKUS_DATASOURCE_USERNAME'
              value: databaseServerAdminUsername
            }
            {
              name: 'QUARKUS_DATASOURCE_PASSWORD'
              secretRef: 'database-password'
            }
            {
              name: 'QUARKUS_TEMPORAL_CONNECTION_TARGET'
              value: 'temporal-app:7233'
            }
            {
              name: 'QUARKUS_LANGCHAIN4J_AZURE_OPENAI_RESOURCE_NAME'
              value: cognitiveServicesAccount.name
            }
            {
              name: 'QUARKUS_LANGCHAIN4J_AZURE_OPENAI_DEPLOYMENT_NAME'
              value: 'chatcompletions'
            }
            {
              name: 'QUARKUS_LANGCHAIN4J_AZURE_OPENAI_API_KEY'
              secretRef: 'openai-api-key'
            }
            {
              name: 'AZURE_STORAGE_CONNECTION_STRING'
              secretRef: 'storage-connection-string'
            }
            {
              name: 'AZURE_STORAGE_CONTAINER_NAME'
              value: 'episodes'
            }
            {
              name: 'BUZZSPROUT_API_KEY'
              secretRef: 'buzzsprout-api-key'
            }
            {
              name: 'BUZZSPROUT_PODCAST_ID'
              value: buzzsproutPodcastId
            }
          ]
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 1
      }
    }
  }
}
