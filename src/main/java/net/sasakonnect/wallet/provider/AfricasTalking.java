package net.sasakonnect.wallet.provider;

import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.jobs.SmsProvider;
import net.sasakonnect.wallet.tools.redis.Queueable;
import net.sasakonnect.wallet.workers.FailedSmsJob;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Service
@PropertySource("classpath:application.properties")
public class AfricasTalking extends SmsProvider {
	private static final Logger logger = LoggerFactory.getLogger(AfricasTalking.class);
	private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");

	private static final long serialVersionUID = 1L;
	@Value("${AFRICAS_TAKING_API_KEY}")
	private String africasTalkingKey;

	@Value("${AFRICAS_TAKING_URL}")
	private String africasTalkingUrl;

	@Value("${WALLET_TEMPLATE_LOGIN}")
	private String template;

	@Value("${AFRICAS_TALKING_USERNAME}")
	private String username;

	@Value("${AFRICAS_TALKING_FROM}")
	private String from;

	@Value("${OTP_TTL}")
	private String otp_ttl;

	@Override
	public void executeJob(Queueable job) {
		WebClient.Builder webClient = WebClient.builder();

		try {

			Mono<String> responseMono = webClient.baseUrl(this.africasTalkingUrl).build().post()
					.uri("/version1/messaging").contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.header("apiKey", africasTalkingKey)

					.body(BodyInserters.fromFormData("message", this.template).with("to", this.getPhoneNumber())
							.with("from", from).with("username", username)

					).exchangeToMono(response -> {
						HttpStatusCode httpStatus = response.statusCode();
						if (httpStatus.equals(HttpStatus.CREATED)) {

							return response.bodyToMono(String.class);
						} else {
							// Handle other status codes if needed.
							return Mono.error(new RuntimeException("Unexpected Status Code: " + httpStatus));
						}
					});

			responseMono.subscribe(objectNode -> {
				JAXBContext jaxbContext;
				try {
					jaxbContext = JAXBContext.newInstance(AfricasTalkingResponse.class);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					StringReader reader = new StringReader(objectNode);
					var res = (AfricasTalkingResponse) unmarshaller.unmarshal(reader);
					if (Integer
							.parseInt(res.getSMSMessageData().getRecipients().getRecipient().getStatusCode()) == 101) {

					}
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Response as ObjectNode: " + objectNode);
			}, error -> {
				FailedSmsJob smsFailedJob = new FailedSmsJob();
				smsFailedJob.setFailedSmsProvider(this);
				smsFailedJob.setPhoneNumber(this.getPhoneNumber());
				smsFailedJob.setMessageTemplate(this.template);
				smsFailedJob.setRetryCount(this.getRetryCount() + 1);

				SmsManager.addFailedJob(smsFailedJob);
			});

		} catch (Exception e) {
			// Handle exceptions here
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

	@XmlRootElement(name = "AfricasTalkingResponse")
	static class AfricasTalkingResponse {
		private SMSMessageData SMSMessageData;

		@XmlElement(name = "SMSMessageData")
		public SMSMessageData getSMSMessageData() {
			return SMSMessageData;
		}

		public void setSMSMessageData(SMSMessageData SMSMessageData) {
			this.SMSMessageData = SMSMessageData;
		}
	}

	@XmlRootElement(name = "SMSMessageData")
	static class SMSMessageData {
		private String Message;
		private Recipients Recipients;

		@XmlElement(name = "Message")
		public String getMessage() {
			return Message;
		}

		public void setMessage(String Message) {
			this.Message = Message;
		}

		@XmlElement(name = "Recipients")
		public Recipients getRecipients() {
			return Recipients;
		}

		public void setRecipients(Recipients Recipients) {
			this.Recipients = Recipients;
		}
	}

	@XmlRootElement(name = "Recipients")
	static class Recipients {
		private Recipient Recipient;

		@XmlElement(name = "Recipient")
		public Recipient getRecipient() {
			return Recipient;
		}

		public void setRecipient(Recipient Recipient) {
			this.Recipient = Recipient;
		}
	}

	@XmlRootElement(name = "Recipient")
	static class Recipient {
		private String number;
		private String cost;
		private String status;
		private String statusCode;
		private String messageId;
		private String messageParts;

		@XmlElement(name = "number")
		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		@XmlElement(name = "cost")
		public String getCost() {
			return cost;
		}

		public void setCost(String cost) {
			this.cost = cost;
		}

		@XmlElement(name = "status")
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		@XmlElement(name = "statusCode")
		public String getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(String statusCode) {
			this.statusCode = statusCode;
		}

		@XmlElement(name = "messageId")
		public String getMessageId() {
			return messageId;
		}

		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}

		@XmlElement(name = "messageParts")
		public String getMessageParts() {
			return messageParts;
		}

		public void setMessageParts(String messageParts) {
			this.messageParts = messageParts;
		}
	}

}
