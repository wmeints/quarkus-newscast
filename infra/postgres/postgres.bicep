param resourceName string
param location string
param adminLogin string
@secure()
param adminPassword string

var vmSize = 'Standard_B1ms'
var vmTier = 'Burstable'

resource postgresServer 'Microsoft.DBforPostgreSQL/flexibleServers@2025-01-01-preview' = {
  name: resourceName
  location: location
  sku: {
    name: vmSize
    tier: vmTier
  }
  properties: {
    createMode: 'Create'
    version: '16'
    administratorLogin: adminLogin
    administratorLoginPassword: adminPassword
    storage: {
      storageSizeGB: 32
    }
  }

  // Used by the content API
  resource contentDatabase 'databases' = {
    name: 'content'
  }

  // Used by the podcast API
  resource podcastDatabase 'databases' = {
    name: 'podcasts'
  }

  // Used by the temporal API
  resource temporalDatabase 'databases' = {
    name: 'temporal'
  }
}
