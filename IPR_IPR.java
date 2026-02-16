package au.com.coles.otf.utilities;

import java.util.Map;

import org.apache.log4j.Logger;

import au.com.coles.otf.constant.DCCConstants;
import au.com.coles.otf.stepdefinitions.DCC;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;

public class EventTriggerUtil {

	EventTriggerUtil() {

	}
	private static Logger logs = Logger.getLogger(KeyvaultConnectorApplication.class);
	public static boolean eventTrigger(String testDataPath) throws Exception {

		
		Map<String, String> keyvault = KeyvaultConnectorApplication.getKeyVaultDetails();
		
	
		
		String eventHubConnectionString = keyvault.get("eventHubConnectionString");
		String fullyQualifiedNamespace = "EVH-OTF-MFS-DEV-AUE.servicebus.windows.net";
		
		String eventHubName1 = "orderfulfilmenthandler";
		

		logs.info("value of keyvault is:" +keyvault);
		
		
		EventHubProducerClient pclient = new EventHubClientBuilder().connectionString(eventHubConnectionString,eventHubName1).consumerGroup(DCCConstants.DEFAULT) .transportType(AmqpTransportType.AMQP_WEB_SOCKETS).buildProducerClient();

		logs.info("value of keyvault is:" +keyvault);

		// prepare a batch of events to send to the event hub
		  
		EventDataBatch batch = pclient.createBatch();
		
		String body = JsonFileReader.convertJsonToStringObject(testDataPath + ".json");
		EventData eventdetails = new EventData(body);

		batch.tryAdd(eventdetails);
		pclient.send(batch);

		pclient.close();
			return true;
	}	
}
