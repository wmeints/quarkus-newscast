param name string
param location string = resourceGroup().location
param tags object = {}
param imageName string = ''
param containerRegistryName string
param containerRegistryResourceGroupName string
param containerAppsEnvironmentName string
param serviceName string = 'content-api'
param databaseServerDomainName string
param databaseServerAdminUsername string
@secure()
param databaseServerAdminPassword string
@secure()
param gmailServiceAccountCredentials string

var databaseUrl = 'jdbc:postgresql://${databaseServerDomainName}:5432/content?sslmode=require'

resource containerAppsEnvironment 'Microsoft.App/managedEnvironments@2025-02-02-preview' existing = {
  name: containerAppsEnvironmentName
}

resource applicationIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2025-01-31-preview' = {
  name: 'id-content-api'
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
          name: 'gmail-credentials'
          value: gmailServiceAccountCredentials
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
              name: 'RABBITMQ_HOST'
              value: 'rabbitmq-app'
            }
          ]
          volumeMounts: [
            {
              mountPath: '/opt/secrets'
              volumeName: 'app-secrets-volume'
            }
          ]
        }
      ]
      volumes: [
        {
          name: 'app-secrets-volume'
          secrets: [
            {
              path: 'gmail-credentials.json'
              secretRef: 'gmail-credentials'
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
