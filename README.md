This component uses bosh rest API to automate programmatic deployment and update


Bosh deployer usage overview (as a service broker component)

![Alt text](http://plantuml.com/plantuml/png/XL9TRu8m57rUVyKDU-OFcB1aAijCDarqstjfBz8DAMcBqN-_rWAZYgmlq7lEkH_dDfCdY9QhRRhus6mZyb1JPGGJDQqCSA6G6Qa8OCKXaoT2YFj0zBbBjxi87j_4lZ3pii5GA6jKr0c3ACiRZK6oIwx5IqG2MYaRdikcukeS0ZcjQtAbdczsh_5c7I_Jz-VrCcXfg6rFfkIFRM4CaRhWM9VoF8xtNapMZwqlNwj56avafpuAXfGnrFfu6CDiz2znPn_4JttT6JIsESf16pmmBCjfbi8IgSOx92QhNEmRXTxizcuh-nKvyd0BLg2G6koQ9KFGaRsjDpGVlX06xlOq7pXWPSWG3rjrICiaNTGnUj-wT8Go8uw3yjRI4K6tXBkoVaiaII0JTYaj04aoOL0ETQEB6sVyc-rh-EjK5bSn_i_dW3ILJQ4sT8_ZRSr0LD_M7-S4jriGjDINgMswgw4jUzH3_qUkui5-OEOU8pqJTmh14N8fZNtelm00)


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
