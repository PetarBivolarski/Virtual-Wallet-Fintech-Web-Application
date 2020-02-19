package a16team1.virtualwallet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.Random;

@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@PropertySource("classpath:swagger.properties")
@SpringBootApplication
public class VirtualWalletApplication {

    @Value("${api.basePackage}")
    private String apiBasePackage;

    @Value("${api.title}")
    private String swaggerApiTitle;

    @Value("${api.description}")
    private String swaggerApiDescription;

    @Value("${api.version}")
    private String apiVersion;

    @Value("${api.termsOfUse}")
    private String apiTermsOfUse;

    @Value("${api.contactNames}")
    private String apiAuthors;

    @Value("${api.websiteUrl}")
    private String apiWebsiteUrl;

    @Value("${api.contactEmail}")
    private String apiEmail;

    @Value("${api.license}")
    private String apiLicense;

    @Value("${api.licenseUrl}")
    private String apiLicenseUrl;

    public static void main(String[] args) {
        SpringApplication.run(VirtualWalletApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public Docket SwagInfo() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(apiBasePackage))
                .build()
                .apiInfo(apiDetails());
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                swaggerApiTitle,
                swaggerApiDescription,
                apiVersion,
                apiTermsOfUse,
                new springfox.documentation.service.Contact(apiAuthors, apiWebsiteUrl, apiEmail),
                apiLicense,
                apiLicenseUrl,
                Collections.emptyList());
    }
}


