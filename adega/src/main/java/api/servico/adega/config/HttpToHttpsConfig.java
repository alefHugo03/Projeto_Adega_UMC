package api.servico.adega.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsConfig {

	@Value("${server.http.port:0}")
	private int httpPort;

	@Value("${server.port:8080}")
	private int httpsPort;

	@Value("${server.ssl.enabled:false}")
	private boolean sslEnabled;

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		if (sslEnabled && httpPort > 0) {
			Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
			connector.setScheme("http");
			connector.setPort(httpPort);
			connector.setSecure(false);
			connector.setRedirectPort(httpsPort);
			tomcat.addAdditionalTomcatConnectors(connector);
		}
		return tomcat;
	}
}
