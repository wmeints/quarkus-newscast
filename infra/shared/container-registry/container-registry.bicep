param resourceName string
param location string

resource containerRegistry 'Microsoft.ContainerRegistry/registries@2025-04-01' = {
  name: resourceName
  location: location
  sku: {
    name: 'Basic'
  }
}
