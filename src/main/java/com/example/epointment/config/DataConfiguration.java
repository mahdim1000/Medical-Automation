package com.example.epointment.config;

        import org.hibernate.SessionFactory;
        import org.springframework.boot.autoconfigure.domain.EntityScan;
        import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
        import org.springframework.boot.web.servlet.FilterRegistrationBean;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.core.io.ClassPathResource;
        import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
        import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
        import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
        import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.web.servlet.config.annotation.EnableWebMvc;
        import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
//        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

        import javax.servlet.annotation.WebFilter;
        import java.io.File;
        import java.io.IOException;
        import java.math.BigInteger;
        import java.security.MessageDigest;
        import java.security.SecureRandom;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.epointment.repository")
public class DataConfiguration {

//    File file;
//
//    {
//        try {
//            file = new ClassPathResource("uploads").getFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//    public static final String resources_path="H:\\terms\\term8\\Project\\java\\epointment2\\src\\main\\resources\\";
//        public static final String uploads_path="H:\\terms\\term8\\Project\\java\\epointment2\\src\\main\\resources\\uploads\\";
        public static final String smsApiKey="7654324B514F755266624357725141763945374E6575594E7164774A57336B58";
        public static final String domain="http://localhost:8080/";

//        @Bean
//        public LettuceConnectionFactory connectionFactory() {
//                return new LettuceConnectionFactory();
//        }
        public static String addSalt(String str){
                return "2348jfj#$9df"+str+"3%q@w!4#R$%/^dTR&*(oi()o.";
        }

        public static String getStatic_path(){
                File f = null;
                try {
                        f = new ClassPathResource("static/a.txt").getFile();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                String d = f.getPath();
                return d.substring(0,d.lastIndexOf('\\'))+"\\";
        }

        public static String getUploads_path(){
                File f = null;
                try {
                        f = new ClassPathResource("uploads/a.txt").getFile();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                String d = f.getPath();
                return d.substring(0,d.lastIndexOf('\\'))+"\\";
        }

        public static String getResources_path(){
                File f = null;
                try {
                        f = new ClassPathResource("a.txt").getFile();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                String d = f.getPath();
                return d.substring(0,d.lastIndexOf('\\'))+"\\";
        }
        public static String encode_pass(String pass){

//                String toReturn = null;
//                try {
//                        MessageDigest digest = MessageDigest.getInstance("SHA-512");
//                        digest.reset();
//                        digest.update(pass.getBytes("utf8"));
//                        toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
//                } catch (Exception e) {
//                        e.printStackTrace();
//                }
//
//                return toReturn;

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

                String encoded = "";
                for (int i = 1; i <= 10; i++) {
                        encoded = passwordEncoder.encode(pass);
                }
//                System.out.println("0 pass is " + ": " + encoded);

                return encoded;

        }


}
