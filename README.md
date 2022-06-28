# Azure Keyvault Cipher for Atlassian

This library allows one to directly use a password from Azure Keyvault for Database access in Atlassian Applications. 

## Prerequisites
* Atlassian Application running in Azure (e.g. on a VM)
* Managed Identity for the VM
* Access to the Keyvault with the Managed Identity ⚠

## Building
With Maven:
```shell
mvn package
```
This will produce `azurekeyvaultcipher-x.x.x.jar` in the `target` directory.

## Usage
Simply copy `azurekeyvaultcipher-x.x.x.jar` and the
[jar for the org.json dependency](https://github.com/stleary/JSON-java) to
`JIRA_INSTALL_DIR/atlassian-jira/WEB-INF/lib` and add the following lines to your database config:

```xml
<atlassian-password-cipher-provider>org.schuppentier.AzureKeyvaultCipher</atlassian-password-cipher-provider>
<password>https://example-keyvault.vault.azure.net/secrets/postgres-user-jira</password>
```
The URL will need to be adjusted for your environment.


### Examples
#### Jira
```xml
<?xml version="1.0" encoding="UTF-8"?>

<jira-database-config>
  <name>defaultDS</name>
  <delegator-name>default</delegator-name>
  <database-type>postgres72</database-type>
  <schema-name>public</schema-name>
  <jdbc-datasource>
    <url>jdbc:postgresql://example-psql.postgres.database.azure.com:5432/jira</url>
    <driver-class>org.postgresql.Driver</driver-class>

    <username>jira@example-psql</username>
    <atlassian-password-cipher-provider>org.schuppentier.AzureKeyvaultCipher</atlassian-password-cipher-provider>
    <password>https://example-keyvault.vault.azure.net/secrets/postgres-user-jira</password>
    <pool-min-size>10</pool-min-size>
    <pool-max-size>30</pool-max-size>
    <pool-max-wait>30000</pool-max-wait>
    <validation-query>select version();</validation-query>
    <min-evictable-idle-time-millis>60000</min-evictable-idle-time-millis>
    <time-between-eviction-runs-millis>300000</time-between-eviction-runs-millis>
    <pool-max-idle>10</pool-max-idle>
    <pool-remove-abandoned>true</pool-remove-abandoned>
    <pool-remove-abandoned-timeout>300</pool-remove-abandoned-timeout>
    <pool-test-on-borrow>false</pool-test-on-borrow>
    <pool-test-while-idle>true</pool-test-while-idle>
  </jdbc-datasource>
</jira-database-config>
```