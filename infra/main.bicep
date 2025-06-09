param databaseServerAdminLogin string
@secure()
param databaseserverAdminPassword string
param location string
param environment string

module databaseServer './postgres/postgres.bicep' = {
  name: 'database-server'
  scope: resourceGroup('rg-newscast-${environment}')
  params: {
    adminLogin: databaseServerAdminLogin
    adminPassword: databaseserverAdminPassword
    location: location
    resourceName: 'pgsql-newscast-${environment}'
  }
}

module containerRegistry './container-registry/container-registry.bicep' = {
  name: 'container-registry'
  scope: resourceGroup('rg-newscast-shared')
  params: {
    location: location
    resourceName: 'acrnewscast'
  }
}
