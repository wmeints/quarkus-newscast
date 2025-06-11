param resourceName string
param location  string

resource logAnalyticsWorkspace 'Microsoft.OperationalInsights/workspaces@2025-02-01'= {
  name: resourceName
  location: location
  properties: {
    sku: {
      name: 'PerGB2018'
    }
  }
}
