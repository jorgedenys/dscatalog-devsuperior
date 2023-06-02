package com.jdsjara.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private JwtTokenStore tokenStore;
	
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };
	
	private static final String[] OPERATOR_OR_ADMIN = { "/products/**", "/categories/**" };
	
	private static final String[] ADMIN = { "/users/**" };	
 
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// H2
//		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
//			http.headers().frameOptions().disable();
//		}
		
		/*
		// CONFIGURAR AS AUTORIZAÇÕES DAS REQUISIÇÕES
		http.authorizeRequests()
		
		// QUEM ESTIVER ACESSANDO ESSA ROTA ESTARÁ LIBERADO
		// NÃO SERÁ EXIGIDO O LOGIN
		.antMatchers(PUBLIC).permitAll(
		
		// LIBERA SOMENTE O MÉTODO GET NO PAPEL DE OPERATOR OU ADMIN
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
		
		// AS ROTAS OPERATOR_OR_ADMIN PODERÃO SER ACESSADAS QUEM TIVER
		// O PAPEL DE OPERATOR OU DE ADMIN.
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		
		// AS ROTAS ADMIN PODERÃO SER ACESSADAS QUEM TIVER O PAPEL DE ADMIN.
		.antMatchers(ADMIN).hasRole("ADMIN")
		
		// QUALQUER OUTRA ROTA QUE NÃO TENHA SIDO ESPECIFICADA
		// DEVERÁ ESTAR LOGADO (AUTENTICADO)
		.anyRequest().authenticated();
		*/
		
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		.antMatchers(ADMIN).hasRole("ADMIN")
		.anyRequest().authenticated();
	}
	
}
