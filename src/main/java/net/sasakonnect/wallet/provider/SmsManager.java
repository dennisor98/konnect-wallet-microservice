package net.sasakonnect.wallet.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;
import net.sasakonnect.wallet.jobs.SmsProvider;
import net.sasakonnect.wallet.tools.redis.JobProducer;
import net.sasakonnect.wallet.tools.redis.Queueable;
import net.sasakonnect.wallet.workers.FailedSmsJob;

@Configuration
@PropertySource({ "classpath:application.properties" })
public class SmsManager {
	private static final Logger logger = LoggerFactory.getLogger(SmsManager.class);
	private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");

	private JobProducer<Queueable> jobProducer;
	private static List<FailedSmsJob> failedSmsJob = new ArrayList<FailedSmsJob>();
	@Value("${PRIMARY_SMS_PROVIDER}")
	private String primary_sms_provider;

	@Value("${className}")
	private String className;

	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	public static synchronized void addFailedJob(FailedSmsJob failedSmsJob) {
		SmsManager.failedSmsJob.add(failedSmsJob);
		logger.debug(SECURITY_MARKER, "Sms Added failed job");

	}

	public static synchronized void removeJob(FailedSmsJob failedSmsJob) {
		SmsManager.failedSmsJob.remove(failedSmsJob);
	}

	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
		logger.debug(SECURITY_MARKER, "Sms init beans");

		Runnable task = () -> {
			logger.debug(SECURITY_MARKER, "Candidate Provider found");

			synchronized (SmsManager.failedSmsJob) {

				String[] classNames = className.split(",");

				if (SmsManager.failedSmsJob.size() > 0) {

					Optional<? extends SmsProvider> smsProvider = Stream.of(classNames)
							.filter((name) -> !(name.equals(this.primary_sms_provider.trim()))).map((rawClass) -> {

								try {
									logger.debug(SECURITY_MARKER, "Candidate Provider found" + rawClass);

									return Class.forName(rawClass);
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}).map((otherProviders) -> {

								Class<? extends SmsProvider> smsProviderClass = otherProviders
										.asSubclass(SmsProvider.class);

								return context.getBean(smsProviderClass);

							}).findFirst();
					if (smsProvider.isPresent()) {

						FailedSmsJob oneFailedJob = SmsManager.failedSmsJob.get(0);
						SmsProvider myBean = smsProvider.get();
						myBean.setPhoneNumber(oneFailedJob.getPhoneNumber());
						myBean.setRetryCount(oneFailedJob.getRetryCount());
						myBean.setTemplate(oneFailedJob.getMessageTemplate());
						System.err.println("Sms Destination " + SmsManager.failedSmsJob.get(0).getPhoneNumber()
								+ " failed retrying.. with " + smsProvider.get().getClass().getName());
						this.jobProducer.enqueueJob(myBean);

						SmsManager.removeJob(oneFailedJob);
					} else {
						logger.debug(SECURITY_MARKER, "Candidate Sms Provider Not found");
					}
				}
			}

		};

		scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);

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
