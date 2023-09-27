package net.sasakonnect.wallet.provider;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import net.sasakonnect.wallet.jobs.SmsProvider;
import net.sasakonnect.wallet.tools.redis.JobProducer;
import net.sasakonnect.wallet.tools.redis.Queueable;

@Configuration
@PropertySource({ "classpath:application.properties" })
public class SmsManager {
	private JobProducer<Queueable> jobProducer;
	@Value("${PRIMARY_SMS_PROVIDER}")
	private String primary_sms_provider;

	@Value("${className}")
	private String className;

	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	@PostConstruct
	public void init() {
		String[] classNamesArray = className.split(",");

		Stream.of(classNamesArray).forEach((className) -> {
			var trimedName = className.trim();
			System.out.println(trimedName);

			try {
				Class<?> beanClass = Class.forName(trimedName);
				if (SmsProvider.class.isAssignableFrom(beanClass)) {
					context.register(beanClass);

				} else {
					System.out.print(
							"class " + trimedName + " could not be assigned,Does it implement SmsProvider.class ");
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		context.refresh();

	}

	public SmsManager(JobProducer<Queueable> jobProducer) {

		this.jobProducer = jobProducer;
	}

	public void sendMessage(String template, String phoneNumber) {
		String[] classNamesArray = className.split(",");
		System.out.println(classNamesArray);

		Stream.of(classNamesArray).forEach((className) -> {
			System.out.println("provider exist");
			System.out.println(this.primary_sms_provider);

			var trimedName = className.trim();
			System.out.println(trimedName);
			System.out.println(className);
			Class<?> loadedClass;
			if (this.primary_sms_provider.trim().equals(className)) {

				try {
					loadedClass = Class.forName(trimedName);
					if (SmsProvider.class.isAssignableFrom(loadedClass)) {

						Class<? extends SmsProvider> smsProviderClass = loadedClass.asSubclass(SmsProvider.class);
						SmsProvider myBean = context.getBean(smsProviderClass);
						myBean.setPhoneNumber(phoneNumber);
						myBean.setTemplate(template);
						this.jobProducer.enqueueJob(myBean);

					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}
}
