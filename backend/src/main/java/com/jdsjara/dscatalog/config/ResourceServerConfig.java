package com.jdsjara.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private Environment enviroment;
	
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
		
		// Se dos PROFILES ativos estiver o profile de teste,
		// poderá ter acesso aos frames do H2 CONSOLE
		if (Arrays.asList(enviroment.getActiveProfiles()).contains("dev")) {
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		.antMatchers(ADMIN).hasRole("ADMIN")
		.anyRequest().authenticated();
		
		http.cors().configurationSource(corsConfigurationSource());
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration corsConfig = new CorsConfiguration();
	    
		// QUAIS SÃO AS ORIGENS PERMITIDAS.
		// O "*" ACEITA REQUISIÇÃO DE APLICAÇÕES DE OUTROS HOSTS
		corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
		
		// ESPECIFICA OS MÉTODOS QUE SERÃO LIBERADOS
	    corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
	    
	    // PERMISSÃO DE CREDENCIAIS
	    corsConfig.setAllowCredentials(true);
	    
	    // ESPECIFICA QUAIS CABEÇALHOS SERÃO PERMITIDOS
	    corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
	 
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", corsConfig);
	    return source;
	}
	 
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
	    FilterRegistrationBean<CorsFilter> bean
	            = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
	    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
	    return bean;
	}
	
}
