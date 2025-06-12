param databaseServerAdminLogin string
@secure()
param databaseserverAdminPassword string
param location string
param environment string
param contentApiVersion string
@secure()
param contentApiDatabaseUserName string
@secure()
param contentApiDatabasePassword string

module databaseServer './app-infra/postgres/postgres.bicep' = {
  name: 'database-server'
  scope: resourceGroup('rg-newscast-${environment}')
  params: {
    adminLogin: databaseServerAdminLogin
    adminPassword: databaseserverAdminPassword
    location: location
    resourceName: 'pgsql-newscast-${environment}'
  }
}

module containerAppEnvironment './app-infra/container-app-environment/container-app-environment.bicep' = {
  name: 'container-app-environment'
  scope: resourceGroup('rg-newscast-${environment}')
  params: {
    location: location
    resourceName: 'cae-newscast-${environment}'
    logAnalyticsWorkspaceName: 'law-newscast'
    logAnalyticsWorkspaceResourceGroup: 'rg-newscast-shared'
  }
}

module appInsights './app-infra/application-insights/application-insights.bicep' = {
  name: 'application-insights'
  scope: resourceGroup('rg-newscast-${environment}')
  params: {
    location: location
    logAnalyticsWorkspaceName: 'law-newscast'
    logAnalyticsWorkspaceResourceGroup: 'rg-newscast-shared'
    resourceName: 'ai-newscast-${environment}'
  }
}

module contentApi './app-infra/apps/content-api/content-api.bicep' = {
  name: 'content-api'
  scope: resourceGroup('rg-newscast-${environment}')
  params: {
    containerImage: 'acrnewscast.azurecr.io/newscast/content-api:${contentApiVersion}'
    databasePassword: contentApiDatabasePassword
    databaseUsername: contentApiDatabaseUserName
    location: location
    resourceName: 'content-api'
    databaseServerResourceName: 'pgsql-newscast-${environment}'
  }
}
