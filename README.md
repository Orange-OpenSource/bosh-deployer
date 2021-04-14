14/04/2021: **This repo is now deprecated. Related use-cases are now served at Orange by** https://github.com/orange-cloudfoundry/cf-ops-automation-broker 

This component uses bosh rest API to automate programmatic deployment and update

Bosh deployer usage overview (as a service broker component)

![Alt text](http://plantuml.com/plantuml/png/XLDRJuCm67sslwB1P_u0oKmSmxXaRibGNqrfFqQLKTBBulprjkCYPGnvWPxlVBTpIYQ-SR1QxvFuTRSFq5rDfEQKrwJIc749L0k9C5GCP-8R8UHUE7XBatqQuFiDFnHwMHhm4qKDahX43wMvKU0LkzIAdmBaqKfXM2vCnUGPzToM-X4dcqqwIiX9NNj8-sHIVDc6Kn6X2WPrASwZk0ly65EYqP3BNVeSxhRXAdbvtAwypT5KWrwwsJgXbV5z7IV-FbQa4wT0A0MbJiUfc4N_2dTgu_2XxnjXOxSZ32iDHmfbES-IK09HS8D4HPNpWv7GUDEURUV6KKSUccOJ910DtR1e67JanfShMWDV20DxUbe37A7IQ1Zl-Cwrg2MX3l3tS6Z7w537w4NjUY52DJyDPhDwJoHq3jrCwor4YmNEk7Mi3U35OaQz7DT65LUoDVUyxz4SvwguZk5_9dYakMeIb2O7c1uhmhpwjFeu9PYrY9DINRQsvKPBM_QaXiqN6k6UFpXoDvMSaNiApW3dGcZx5_m2)


Design:
* bosh zuul: as bosh director is usually hidden in private networks with a specific https port (25555), exposing it with an API Gateway makes sense.
The spring cloud zuul solution is used, with an errand to push it to cloudfoundry.
** the Bosh Director must be configured with ssl
** authent (basic auth) is still performed by Bosh Director

* Bosh API client: the spring cloud feign framework is used to map the Director REST API.
An additional OkHttp Client is used, to able to pass http proxies and perform some adjustement to Director REST response for Feign compatibility

* Cloudfoundry broker: the spring cloud cloudfoundry Service Broker implementation is used



see:
* http://bosh.io/docs/director-api-v1.html
* https://github.com/Netflix/feign
* https://blog.de-swaef.eu/the-netflix-stack-using-spring-boot-part-3-feign/
* https://jmnarloch.wordpress.com/2015/10/07/spring-cloud-feign-spdyhttp2/
* http://log.rowanto.com/java-8-turning-off-ssl-certificate-check/
* https://github.com/Orange-OpenSource/spring-boot-ssl-truststore-gen
* https://docs.pivotal.io/pivotalcf/1-7/buildpacks/java/bosh_custom_trusted_certs.html
* https://github.com/spring-cloud/spring-cloud-netflix/blob/master/docs/src/main/asciidoc/spring-cloud-netflix.adoc#cookies-and-sensitive-headers




bosh features
* http://bosh.io/docs/links.html
* http://bosh.io/docs/director-certs.html
* http://bosh.io/docs/jobs.html#properties-spec

cloudfoundry service broker, with spring boot
* https://github.com/spring-cloud/spring-cloud-cloudfoundry
* https://docs.cloudfoundry.org/services/api.html
* swagger desc: https://github.com/cloudfoundry-incubator/cf-swagger/blob/master/descriptions/cloudfoundry/service_broker/service_broker.json#L8 
* swagger:  tooling https://github.com/swagger-api/swagger-codegen/issues?utf8=%E2%9C%93&q=is%3Aissue+is%3Aopen+feign
* https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen-maven-plugin/README.md

consul dns reference
* https://www.consul.io/docs/agent/dns.html
* https://www.consul.io/docs/agent/options.html
* https://www.consul.io/docs/guides/dns-cache.html
