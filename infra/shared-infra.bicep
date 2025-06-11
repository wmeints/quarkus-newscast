param location string

module containerRegistry './shared/container-registry/container-registry.bicep' = {
  name: 'container-registry'
  scope: resourceGroup('rg-newscast-shared')
  params: {
    location: location
    resourceName: 'acrnewscast'
  }
}

module logAnalytics './shared/log-analytics/log-analytics.bicep' = {
  name: 'log-analytics'
  params: {
    resourceName: 'law-newscast'
    location: location
  }
}
