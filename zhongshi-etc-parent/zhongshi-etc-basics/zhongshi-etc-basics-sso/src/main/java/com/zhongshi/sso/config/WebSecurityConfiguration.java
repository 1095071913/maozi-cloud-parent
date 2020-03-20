/*package com.funtl.oauth2.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 设置默认的加密方式
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                // 在内存中创建用户并为密码加密
                .withUser("user").password(passwordEncoder().encode("123456")).roles("USER")
                .and()
                .withUser("admin").password(passwordEncoder().encode("123456")).roles("ADMIN");

    }
}*/

package com.zhongshi.sso.config;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * 
 * 	功能说明：SpringSecurity配置
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-05 ：13:09:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Order(1)
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		// 设置默认的加密方式
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceConfig();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 使用自定义认证与授权
		auth.userDetailsService(userDetailsService());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		
		// 将 check_token 暴露出去，否则资源服务器访问时报 403 错误
		web.ignoring().antMatchers("/oauth/check_token");
		web.ignoring().antMatchers
        (
        		"/actuator",
        		"/actuator/**",
        		"/swagger-ui.html", 
        		"/doc.html",
        		"/swagger-ui.html/**",
        		"/webjars/**",
                "/swagger-resources/**", 
                "/v2/api-docs/**",
                "/swagger-resources/configuration/ui/**", 
                "/swagger-resources/configuration/security/**",
                "/images/**",
                "/js/**"
        );  
		
	}

	/**
	 * 用于支持 password 模式
	 *
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		//不允许往session放东西
		//http.exceptionHandling().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.requestMatchers()
        .antMatchers("/login")
        .antMatchers("/oauth/authorize")
        
        
        
        .and()
        .authorizeRequests()
        .anyRequest().authenticated()
        
        
        
        //
        .and()
        .formLogin().loginPage("/login").permitAll()	// 自定义登录页面，这里配置了 loginPage, 就会通过 LoginController 的 login 接口加载登录页面
        .and().csrf().disable();
		
		
		
		
//        http.exceptionHandling()
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                // 授权访问
//                .antMatchers("/user/info").hasAuthority("USER")
//                .antMatchers("/user/logout").hasAuthority("USER");
	}
	
	

}