package au.com.coles.otf.stepdefinitions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

import au.com.coles.otf.utilities.KeyvaultConnectorApplication;
import au.com.coles.otf.constant.DCCConstants;
import au.com.coles.otf.utilities.DatabaseColumnValidationUtil;
import au.com.coles.otf.utilities.EventTriggerUtil;
import au.com.coles.otf.utilities.JsonCompare;
import au.com.coles.otf.utilities.JsonCompare.ColumnResult;
import au.com.coles.otf.utilities.JsonCompare.ValidationReport;
import au.com.coles.otf.utilities.JsonFileReader;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import io.swagger.v3.oas.models.SpecVersion;

import static org.junit.Assert.*;

public class DCC {

	private static Logger logs = Logger.getLogger(DCC.class);
	private static String[] TABLENAME = new String[] { "mfs_audit", "mfs_fulfilment_version", "mfs_job_queue",
			"ofeh_event_handled", "ofeh_audit", "ofeh_job_queue", "mfs_job_queue_arch", "ofeh_job_queue_arch" };
	public static Map<String, String> keyVault;
	private static String dbConnectionString;
	private static Connection connection = null;
	private static PreparedStatement stmt = null;
	private String eventType = "";
	
	 private final ObjectMapper mapper = new ObjectMapper();
//	private final DatabaseColumnValidationUtil dbUtil = new DatabaseColumnValidationUtil();
    private final JsonCompare JsonCompare = new JsonCompare();

    private static final String SEP = "================================================================";

 
    private String payloadPath;
    private String expectedPath;
    private String schemaDir;
    private JsonNode payloadArray;

    private final List<ValidationReport> reports = new ArrayList<>();
    private final Map<String, JsonCompare.ColumnRule> columnRuleCache = new HashMap<>();
    private final Map<String, LookupConfig> lookupConfigCache = new HashMap<>();
    private final Map<String, TableColumnPolicy> tablePolicyCache = new HashMap<>();

	@Before(value = "@MFS")
	public static void beforeScenario() throws SQLException {
		try {

			keyVault = KeyvaultConnectorApplication.getKeyVaultDetails();
			dbConnectionString = keyVault.get("databaseConnectionString");
			connection = DriverManager.getConnection(dbConnectionString);
			for (int i = 0; i < TABLENAME.length; i++) {

				/*
				 * String query = "truncate table ?"; query = query.replace("?", TABLENAME[i]);
				 * stmt = connection.prepareStatement(query); stmt.execute();
				 * logs.info("Table Truncated");
				 */

			}
		} catch (Exception e) {
			logs.error("error handled in catch block");
		} finally {
			if (connection != null)
				connection.close();
			if (stmt != null)
				stmt.close();
		}
	}

	@Given("Azure Event Hub receives the event of {string} and {string}")
	public void azure_event_hub_receives_the_event_of_and(String eventTypeName, String testDataPath) {
		try {
			eventType = eventTypeName;
			assertTrue("Event is not triggered..", EventTriggerUtil.eventTrigger(testDataPath));
			logs.info(eventTypeName + " Event is triggered");
//			Thread.sleep(6000);

		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();

		}
	}

	@And("Validate {string} table schema")
	public void validate_table_schema(String tableName) {
		try {
			Map<String, String> tableDescription = new HashMap<>();
			if (tableName.equalsIgnoreCase("dcc_event_handled")) {

				tableDescription.put("event_id", DCCConstants.VARCHARH);
				tableDescription.put("event_datetime", DCCConstants.TIMESTAMPZ);

			} else if (tableName.equalsIgnoreCase("dcc_job_queue")) {
				tableDescription.put("event_id", DCCConstants.VARCHARH);
				tableDescription.put("event_type", DCCConstants.VARCHARH);
				tableDescription.put("event_datetime", DCCConstants.TIMESTAMPZ);
				tableDescription.put("event_payload", DCCConstants.TEXT);
				tableDescription.put("poi_event_payload", DCCConstants.TEXT);
				tableDescription.put("event_operation", DCCConstants.VARCHARH);
				tableDescription.put("retry_attempts", DCCConstants.LONGINTEGER);
				tableDescription.put("job_set_time", DCCConstants.TIMESTAMP);

			} else if (tableName.equalsIgnoreCase("dcc_job_queue_arch")) {
				tableDescription.put("event_id", DCCConstants.VARCHARH);
				tableDescription.put("event_type", DCCConstants.VARCHARH);
				tableDescription.put("event_datetime", DCCConstants.TIMESTAMPZ);
				tableDescription.put("event_payload", DCCConstants.TEXT);
				tableDescription.put("poi_event_payload", DCCConstants.TEXT);
				tableDescription.put("event_operation", DCCConstants.VARCHARH);
				tableDescription.put("retry_attempts", DCCConstants.LONGINTEGER);
				tableDescription.put("job_set_time", DCCConstants.TIMESTAMP);

			} else if (tableName.equalsIgnoreCase("dcc_audit")) {
				tableDescription.put("audit_id", DCCConstants.VARCHARH);
				tableDescription.put("event_id", DCCConstants.VARCHARH);
				tableDescription.put("event_operation", DCCConstants.VARCHARH);
				tableDescription.put("activity_start_datetime", DCCConstants.TIMESTAMP);
				tableDescription.put("activity_end_datetime", DCCConstants.TIMESTAMP);
				tableDescription.put("audit_datetime", DCCConstants.TIMESTAMP);
				tableDescription.put("exception", DCCConstants.TEXT);

			} else if (tableName.equalsIgnoreCase("dcc_ord_rist_items")) {
				tableDescription.put("order_id", DCCConstants.VARCHARH);
				tableDescription.put("customer_id", DCCConstants.VARCHARH);
				tableDescription.put("fulfilment_id", DCCConstants.VARCHARH);
				tableDescription.put("item_id", DCCConstants.TEXT);
				tableDescription.put("is_liq", DCCConstants.VARCHARH);
				tableDescription.put("store_id", DCCConstants.VARCHARH);
				tableDescription.put("delivery_datetime", DCCConstants.TIMESTAMP);
				tableDescription.put("created_datetime", DCCConstants.TIMESTAMP);

			} else if (tableName.equalsIgnoreCase("dcc_customer_details")) {

				tableDescription.put("customer_id", DCCConstants.VARCHARH);
				tableDescription.put("liq_flag", DCCConstants.BOOLEAN);
				tableDescription.put("order_id", DCCConstants.TEXT);
				tableDescription.put("store_id", DCCConstants.VARCHARH);
				tableDescription.put("last_update_dateime", DCCConstants.TIMESTAMP);

			} else if (tableName.equalsIgnoreCase("dcc_event_consumption_stats")) {

				tableDescription.put("event_id", DCCConstants.VARCHARH);
				tableDescription.put("event_type", DCCConstants.VARCHARH);
				tableDescription.put("event_sequence_num", DCCConstants.VARCHARF);
				tableDescription.put("partition_id", "varchar,5");
				tableDescription.put("last_enq_event_sequence_num", DCCConstants.VARCHARF);
				tableDescription.put("node_id", DCCConstants.VARCHARH);
				tableDescription.put("created_datetime", DCCConstants.TIMESTAMP);

			} else if (tableName.equalsIgnoreCase("dcc_active_queue")) {
				tableDescription.put("event_id", DCCConstants.VARCHARH);
				tableDescription.put("created_datetimestamp", DCCConstants.TIMESTAMPZ);

			}
			boolean validTable = DatabaseColumnValidationUtil.checkDBConnection(tableName.toLowerCase(),
					tableDescription);

			if (validTable) {
				logs.info("Database Schema Validation is done successfully");
			} else {
				logs.info("Database Schema Validation is not done successfully");
			}
			assertTrue("Database Schema Validation is not done successfully for " + tableName, validTable);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			logs.info("Complete");
		}
	}

	@Then("Validate the {string} should be present in following tables")
	public void validate_the_should_be_present_in_following_tables(String event_id,
			io.cucumber.datatable.DataTable dataTable) throws Exception {
		List<String> tables = dataTable.asList();

		for (String tableName : tables) {
			Boolean isPersist = DatabaseColumnValidationUtil.iseventpersist(event_id, tableName);
			assertTrue("the event is not persisted in the " + tableName, isPersist);
		}
	}

	@Then("Validate {string} should persist into dcc_ord_rist_items table or not")
	public void validate_should_persist_into_dcc_ord_rist_items_table_or_not(String order_id) throws Exception {
		// Write code here that turns the phrase above into concrete actions
		Map<String, String> DBData = DatabaseColumnValidationUtil.getordlistRecord(order_id);
		assertNotNull("the event is not persisted in the dcc_ord_rist_items", DBData.get("order_id"));
	}

	@Then("Validate the {string} should not present in following tables")
	public void validate_the_should_not_present_in_following_tables(String event_id,
			io.cucumber.datatable.DataTable dataTable) throws Exception {
		List<String> tables = dataTable.asList();

		for (String tableName : tables) {
			Boolean isPersist = DatabaseColumnValidationUtil.iseventpersist(event_id, tableName);
			assertFalse("the event is persisted in the " + tableName, isPersist);
		}
	}

	@Given("Wait for {string} until MFSev receives response")
	public void wait_for_until_mf_sev_receives_response(String time) {
		try {
			Thread.sleep(Long.valueOf(time) * 40);
		} catch (InterruptedException e) {

			Thread.currentThread().interrupt();
		}
	}

	@Given("Validate the dcc_job_queue_arch table column for the following columns with {string}")
	public void validate_the_dcc_job_queue_arch_table_column_for_the_following_columns_with(String event_id,
			io.cucumber.datatable.DataTable dataTable) throws Exception {
		List<Map<String, String>> expectedDataList = dataTable.asMaps(String.class, String.class);
		Map<String, String> databaseData = DatabaseColumnValidationUtil.getdccjobqueuearchRecord(event_id);
		System.out.println(databaseData);
		System.out.println(expectedDataList);
		for (Map<String, String> expectedData : expectedDataList) {
			for (String key : expectedData.keySet()) {
				if (expectedData.get(key).equalsIgnoreCase("null")) {
					assertNull("fa in the column: " + key, databaseData.get(key));
				} else {
					assertEquals("DataMisMatch in the column: " + key, expectedData.get(key), databaseData.get(key));
				}
			}
		}
	}

	@Given("Validate the dcc_ord_rist_items table column for the following columns with {string}")
	public void validate_the_dcc_ord_rist_items_table_column_for_the_following_columns_with(String order_id,
			io.cucumber.datatable.DataTable dataTable) throws Exception {
		List<Map<String, String>> expectedDataList = dataTable.asMaps(String.class, String.class);
		Map<String, String> databaseData = DatabaseColumnValidationUtil.getordlistRecord(order_id);
		System.out.println(databaseData);
		System.out.println(expectedDataList);
		for (Map<String, String> expectedData : expectedDataList) {
			for (String key : expectedData.keySet()) {
				if (expectedData.get(key).equalsIgnoreCase("null")) {
					assertNull("fa in the column: " + key, databaseData.get(key));
				} else {
					assertEquals("DataMisMatch in the column: " + key, expectedData.get(key), databaseData.get(key));
				}
			}
		}
	}

	@Then("Valiate the following operation should persist in the ofeh_audit table or not for the {string}")
	public void valiate_the_following_operation_should_persist_in_the_ofeh_audit_table_or_not_for_the(String event_id,
			io.cucumber.datatable.DataTable dataTable) throws SQLException, IOException {
		List<String> tables = dataTable.asList();

		for (String OPERATION : tables) {
			Boolean isPersist = DatabaseColumnValidationUtil.getdccauditOps(event_id, OPERATION);
			assertTrue("Event id " + event_id + " and event_operation " + OPERATION + " is not persited in DCC_Audit",
					isPersist);
			logs.info("Event id " + event_id + " and event_operation " + OPERATION + " is persited in DCC_Audit");
		}
	}

	@Given("Validate the {string} dcc_job_queue_arch table column for the following columns with {string}")
	public void validate_the_dcc_job_queue_arch_table_column_for_the_following_columns_with(String FileName,
			String event_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getdccjobqueuearchRecord(event_id);
		assertNotNull("POI payload column is null", jobQueueMap.get("poi_event_payload"));
		String POIResponse = jobQueueMap.get("poi_event_payload");
		logs.info(POIResponse);
		JSONParser parser = new JSONParser();
		JSONArray jsonArr = (JSONArray) parser.parse(POIResponse);
		JSONObject json = (JSONObject) jsonArr.get(0);

		String id = (String) json.get("id");
		String time = (String) json.get("time");
		JSONObject jsonData = (JSONObject) json.get("data");
		String updatedDateTime = (String) jsonData.get("updatedDateTime");
		String aId = (String) jsonData.get("associationId");

		String json1 = POIResponse;
		json1 = json1.replace(id.toString(), "1007");
		json1 = json1.replace(time.toString(), "01-01-2025");
		json1 = json1.replace(updatedDateTime, "01-01-2025");
		json1 = json1.replace(aId, "20251031113719");

		System.out.println(json1);

		Path fileName = Path.of("src/main/resources/POIReponseFiles/" + FileName + ".json");
		String payload = Files.readString(fileName);
		logs.info(payload);

		assertEquals("Data validation is not done successfully", payload, json1);
		logs.info("Fulfilment_order data validation is done successfully");
	}

	@Given("Validate the poi_payload in dcc_job_queue_arch table for {string} should be null")
	public void validate_the_poi_payload_in_dcc_job_queue_arch_table_for_should_be_null(String event_id)
			throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getdccjobqueuearchRecord(event_id);
		assertNull("POI payload column is not null in the DCC_JOB_QUEUE_ARCH", jobQueueMap.get("poi_event_payload"));
	}

	@Then("Validate the {string} should not present in the dcc_ord_rist_items table")
	public void validate_the_should_not_present_in_the_dcc_ord_rist_items_table(String order_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getordlistRecord(order_id);
		assertNull("Order details is persisted in the DCC_ORD_RIST_ITEMS", jobQueueMap.get("order_id"));
	}

	@Then("Validate the {string} should present in the dcc_ord_rist_items table")
	public void validate_the_should_present_in_the_dcc_ord_rist_items_table(String order_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getordlistRecord(order_id);
		assertNotNull("Order details is not persisted in the DCC_ORD_RIST_ITEMS", jobQueueMap.get("order_id"));
	}

	@Then("Validate the dcc_event_handled table should have single entry for the {string}")
	public void validate_the_dcc_event_handled_table_should_have_single_entry_for_the(String event_id)
			throws Exception {
		assertTrue("There is no entry for the event id in DCC EVENT HANDLED table for " + event_id
				+ "in DCC EVENT HANDLED table", DatabaseColumnValidationUtil.recordcountindcchandled(event_id));
	}

	@Then("Validate the dcc_job_queue_arch table should have single entry for the {string}")
	public void validate_the_dcc_job_queue_arch_table_should_have_single_entry_for_the(String event_id)
			throws Exception {
		assertTrue(
				"There is no entry for the event id in DCC JOB QUEUE ARCH table for " + event_id
						+ "in DCC JOB QUEUE ARCH table",
				DatabaseColumnValidationUtil.recordcountindccqueuearch(event_id));
	}

	@Then("Validate Entry to be made in dcc_audit table for {string}")
	public void validate_entry_to_be_made_in_dcc_audit_table_for(String event_id) throws Exception {
		assertTrue("There is no entry for the event id in DCC Audit table for " + event_id + "in DCC Audit table",
				DatabaseColumnValidationUtil.recordcountindccaudit(event_id));
	}

	@Then("Validate the Retry_Attempt column of dcc_job_queue table for the {string} should be {string}")
	public void validate_the_retry_attempt_column_of_dcc_job_queue_table_for_the_should_be(String event_id,
			String retryAttemps) throws Exception {

		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getdccjobqueueRecord(event_id);
		assertEquals("The retry attempts is not valid for the event-id : " + event_id + " in the dcc_job_queue table",
				retryAttemps, jobQueueMap.get("retry_attempts"));
	}

	@Then("Validate the {string} should persist in the dcc_job_queue table")
	public void validate_the_should_persist_in_the_dcc_job_queue_table(String event_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getdccjobqueueRecord(event_id);
		assertEquals("The event ID is not for valid in the dcc_job_queue table", event_id, jobQueueMap.get("event_id"));

	}

	@Then("Validate the {string} should not persist in the dcc_job_queue_arch table")
	public void validate_the_should_not_persist_in_the_dcc_job_queue_arch_table(String event_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getdccjobqueuearchRecord(event_id);
		assertNull("The event ID is not null in the dcc_job_queue_arch table", jobQueueMap.get("event_id"));
	}

	@Then("Validate the Exception to be Persisted in dcc_audit table for {string}")
	public void validate_the_exception_to_be_persisted_in_dcc_audit_table_for(String event_id) throws Exception {
		assertNotNull("The Exception is not persisted in DCC_AUDIT table for the " + event_id,
				DatabaseColumnValidationUtil.exceptionindccaudit(event_id));
	}

	@Then("Validate the event operation column of dcc_job_queue table should be {string} at the end of the flow for {string}")
	public void validate_the_event_operation_column_of_dcc_job_queue_table_should_be_at_the_end_of_the_flow_for(
			String operation, String event_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getdccjobqueueRecord(event_id);
		assertEquals("The event operation is not for valid in the dcc_job_queue table for the event_id :" + event_id,
				operation, jobQueueMap.get("event_operation"));
	}

	@Then("Validate the {string} should persist in the dcc_active_queue table")
	public void validate_the_event_should_persist_in_the_dcc_active_queue_table(String event_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getactivequeuerecord(event_id);
		assertNotNull("The event ID is persisted in the dcc_active_queue table", jobQueueMap.get("event_id"));
	}

	@Given("insert the record in customer_details table for the {string}")
	public void insert_the_record_in_customer_details_table_for_the(String customer_id) throws Exception {
		assertTrue("Failed during inserting record in dcc_customer_details table",
				DatabaseColumnValidationUtil.insertcustdetailsrecord(customer_id));
	}

	@Then("Validate the {string} should present in the dcc_customer_details table")
	public void validate_the_should_present_in_the_dcc_customer_details_table(String customer_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getcustdetailsrecord(customer_id);
		assertNotNull("The Customer ID is not persisted in the dcc_customer_details table",
				jobQueueMap.get("customer_id"));
	}

	@Then("Validate the {string} should not present in the dcc_customer_details table")
	public void validate_the_should_not_present_in_the_dcc_customer_details_table(String customer_id) throws Exception {
		Map<String, String> jobQueueMap = DatabaseColumnValidationUtil.getcustdetailsrecord(customer_id);
		assertNull("The Customer ID is persisted in the dcc_customer_details table", jobQueueMap.get("customer_id"));

	}

	
	
	// Started
		

	    @Given("payload file {string} and expected file {string} and schema dir {string}")
	    public void setup(String payload, String expected, String schemaDir) throws Exception {	       
	        this.payloadPath = payload;
	        this.expectedPath = expected;
	        this.schemaDir = schemaDir;
	        this.payloadArray = mapper.readTree(Path.of(payload).toFile());
	    }

	    @Then("database values should match expected data")
	    public void validateDatabase() throws Exception {
	        printRunHeader();

	        if (!payloadArray.isArray()) {
	            throw new IllegalArgumentException("Payload file must be a JSON array");
	        }

	        List<PayloadRecord> payloadRecords = extractPayloadRecords(payloadArray);
	        if (payloadRecords.isEmpty()) {
	            throw new IllegalArgumentException("No payload records with id/order fields found in " + payloadPath);
	        }

	        Set<String> payloadEventIds = new HashSet<>();
	        Set<String> payloadOrderIds = new HashSet<>();
	        for (PayloadRecord record : payloadRecords) {
	            if (!record.eventId.isEmpty()) payloadEventIds.add(record.eventId);
	            if (!record.orderId.isEmpty()) payloadOrderIds.add(record.orderId);
	        }

	        List<ExpectedTable> tables = loadExpectedTables(payloadEventIds, payloadOrderIds);
	        if (tables.isEmpty()) {
	            throw new IllegalStateException("No matching expected rows found in " + expectedPath + " for payload event/order ids.");
	        }

	        int matchedExpectedRows = 0;
	        for (PayloadRecord payloadRecord : payloadRecords) {
	            String eventId = payloadRecord.eventId;
	            String orderId = payloadRecord.orderId;
	            printScenarioHeader(eventId, orderId);
	            
	            String eventBody = new ObjectMapper().writeValueAsString(payloadRecord);
	            if(EventTriggerUtil.eventTrigger(eventBody)) {
	            	System.out.println("Event is triggered successfully for the EventID : "+eventId+" and OrderID : "+orderId);
	            }else {
	            	System.out.println("Event is not triggered failed for the EventID : "+eventId+" and OrderID : "+orderId);
	            }

	            for (ExpectedTable table : tables) {
	                List<JsonNode> expectedRows = table.getMatchedRows(payloadRecord);
	                if (expectedRows.isEmpty()) {
	                    log("No expected rows for table=" + table.tableName + " (skipped)");
	                    continue;
	                }

	                printTableHeader(table.tableName, expectedRows.size());
	                applySchemaTablePolicy(table.tableName, table.schema);

	                for (JsonNode baseExpectedRow : expectedRows) {
	                    JsonNode expectedRow = applyTableIgnorePolicy(table.tableName, baseExpectedRow);
	                    if (expectedRow == null || !expectedRow.isObject() || expectedRow.size() == 0) {
	                        continue;
	                    }
	                    matchedExpectedRows++;

	                    LinkedHashMap<String, String> criteria = buildLookupCriteria(table.lookupConfig, payloadRecord, expectedRow);
	                    if (criteria.isEmpty()) {
	                        log("LOOKUP: no lookup values resolved for table=" + table.tableName + " row=" + expectedRow + " (skipped)");
	                        continue;
	                    }

	                    List<Map<String, Object>> actualRows;
	                    try {
	                        log("DB: fetching table=" + table.tableName + " by " + criteria);
	                        actualRows = DatabaseColumnValidationUtil.fetchByCriteria(
	                                
	                                table.tableName,
	                                criteria
	                        );
	                    } catch (SQLException ex) {
	                        log("DB ERROR: " + ex.getMessage());
	                        throw new RuntimeException("DB fetch failed for table " + table.tableName, ex);
	                    }

	                    enrichSchemaWithColumnRules(table.tableName, table.schema, expectedRow);
	                    ArrayNode expectedArray = mapper.createArrayNode().add(expectedRow);
	                    ValidationReport report = JsonCompare.validateTable("phpmyadmin", eventId, table.tableName, actualRows, expectedArray, table.schema);
	                    reports.add(report);
	                    printScenarioTableSummary(report);
	                }
	            }
	        }

	        if (matchedExpectedRows == 0) {
	            System.out.println("No expected rows matched payload IDs from " + expectedPath + ". Check payload/expected alignment.");
	        }

	        for (ValidationReport r : reports) {
	            if ("FAIL".equals(r.status)) {
	                printRunSummary();
	                throw new AssertionError("Validation failed for table " + r.tableName);
	            }
	        }

	        printRunSummary();
	    }

	    private void log(String msg) {
	        System.out.println("[Testautomation] " + msg);
	    }

	    private void enrichSchemaWithColumnRules(String tableName, JsonCompare.Schema schema, JsonNode expectedRow) throws Exception {
	        Iterator<String> fields = expectedRow.fieldNames();
	        while (fields.hasNext()) {
	            String column = fields.next();
	            JsonNode value = expectedRow.get(column);

	            boolean looksJson = value.isObject() || value.isArray() ||
	                    (value.isTextual() && (value.asText().trim().startsWith("{") || value.asText().trim().startsWith("[")));
	            if (!looksJson) {
	                continue;
	            }

	            String fileName = tableName + "_" + column + ".schema.json";
	            Path path = Path.of(schemaDir, fileName);
	            if (java.nio.file.Files.exists(path)) {
	                JsonCompare.ColumnRule rule = columnRuleCache.get(path.toString());
	                if (rule == null) {
	                    rule = JsonCompare.loadColumnRule(path);
	                    columnRuleCache.put(path.toString(), rule);
	                }
	                schema.rules.put(column, rule);
	            }
	        }
	    }

	    private List<ExpectedTable> loadExpectedTables(Set<String> payloadEventIds, Set<String> payloadOrderIds) throws Exception {
	        List<ExpectedTable> tables = new ArrayList<>();
	        Path configuredPath = Path.of(expectedPath);
	        if (!java.nio.file.Files.exists(configuredPath)) {
	            throw new IllegalArgumentException("Expected path not found: " + expectedPath);
	        }

	        Path expectedDir = java.nio.file.Files.isDirectory(configuredPath) ? configuredPath : configuredPath.getParent();
	        if (expectedDir == null || !java.nio.file.Files.exists(expectedDir)) {
	            throw new IllegalArgumentException("Expected directory not found for: " + expectedPath);
	        }

	        try (java.util.stream.Stream<Path> stream = java.nio.file.Files.list(expectedDir)) {
	            List<Path> expectedFiles = stream
	                    .filter(p -> p.getFileName().toString().endsWith("_expected_data.json"))
	                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
	                    .toList();

	            if (expectedFiles.isEmpty()) {
	                throw new IllegalArgumentException("No *_expected_data.json files found in: " + expectedDir);
	            }

	            for (Path expectedFile : expectedFiles) {
	                String file = expectedFile.getFileName().toString();
	                String tableName = file.substring(0, file.indexOf("_expected_data.json"));

	                JsonNode rows = JsonCompare.loadExpected(expectedFile);
	                if (!rows.isArray()) {
	                	System.out.println("Expected file must contain a JSON array: " + expectedFile);
	                	continue;
	                }

	                LookupConfig lookup = resolveLookup(tableName, null);
	                List<JsonNode> matchedRows = new ArrayList<>();
	                for (JsonNode row : rows) {
	                    JsonNode normalizedRow = normalizeExpectedRow(row);
	                    if (rowMatchesPayloadSets(normalizedRow, payloadEventIds, payloadOrderIds, lookup)) {
	                        matchedRows.add(normalizedRow);
	                    }
	                }

	                JsonCompare.Schema schema = new JsonCompare.Schema();
	                schema.tableName = tableName;
	                tables.add(new ExpectedTable(tableName, matchedRows, schema, lookup));
	                log("EXPECTED: loaded " + file + " rows=" + rows.size() + " matched=" + matchedRows.size() + " lookupColumns=" + lookup.columns);
	            }
	        }
	        return tables;
	    }

	    private boolean rowMatchesPayloadSets(JsonNode row, Set<String> payloadEventIds, Set<String> payloadOrderIds, LookupConfig lookup) {
	        if (row == null || !row.isObject()) return false;

	        boolean usedPayloadMappedLookup = false;
	        boolean matchedPayloadMappedLookup = false;

	        for (String column : lookup.columns) {
	            String payloadMappedType = payloadMappedTypeForColumn(column);
	            if (payloadMappedType.isEmpty()) {
	                continue;
	            }
	            usedPayloadMappedLookup = true;
	            String rowVal = rowValue(row, column);
	            if (rowVal.isEmpty()) {
	                continue;
	            }
	            if ("eventId".equals(payloadMappedType) && payloadEventIds.contains(rowVal)) {
	                matchedPayloadMappedLookup = true;
	            }
	            if ("orderId".equals(payloadMappedType) && payloadOrderIds.contains(rowVal)) {
	                matchedPayloadMappedLookup = true;
	            }
	        }

	        if (usedPayloadMappedLookup) {
	            return matchedPayloadMappedLookup;
	        }

	        String rowId = firstText(row, "id", "event_id", "event-id");
	        String rowOrder = firstText(row, "orderid", "order_id", "order-id");
	        return payloadEventIds.contains(rowId) || payloadOrderIds.contains(rowOrder);
	    }

	    private LookupConfig resolveLookup(String tableName, JsonNode expectedRow) throws Exception {
	        if (lookupConfigCache.containsKey(tableName)) {
	            return lookupConfigCache.get(tableName);
	        }

	        LookupConfig cfg = new LookupConfig();

	        Path perTableLookupFile = Path.of(schemaDir, tableName + "_lookup.json");
	        if (java.nio.file.Files.exists(perTableLookupFile)) {
	            JsonNode node = mapper.readTree(perTableLookupFile.toFile());
	            applyLookupNode(cfg, node);
	        }

	        Path globalLookupFile = Path.of(schemaDir, "lookup.json");
	        if (java.nio.file.Files.exists(globalLookupFile)) {
	            JsonNode root = mapper.readTree(globalLookupFile.toFile());
	            if (root.isObject() && root.has(tableName)) {
	                applyLookupNode(cfg, root.get(tableName));
	            }
	        }

	        if (cfg.columns.isEmpty() && expectedRow != null) {
	            if (expectedRow.has("id") || expectedRow.has("event_id") || expectedRow.has("event-id")) cfg.columns.add("id");
	            if (expectedRow.has("orderid") || expectedRow.has("order_id") || expectedRow.has("order-id")) cfg.columns.add("orderid");
	        }

	        if (cfg.columns.isEmpty()) {
	            cfg.columns.add("id");
	            cfg.columns.add("orderid");
	        }

	        cfg.columns = dedup(cfg.columns);
	        for (String c : cfg.columns) {
	            String mapped = payloadMappedTypeForColumn(c);
	            if ("eventId".equals(mapped) && cfg.idColumn == null) cfg.idColumn = c;
	            if ("orderId".equals(mapped) && cfg.orderIdColumn == null) cfg.orderIdColumn = c;
	        }

	        lookupConfigCache.put(tableName, cfg);
	        return cfg;
	    }

	    private void applyLookupNode(LookupConfig cfg, JsonNode node) {
	        if (node == null || node.isNull()) return;

	        if (node.isTextual()) {
	            cfg.columns.add(node.asText().trim());
	            return;
	        }

	        if (node.isArray()) {
	            for (JsonNode n : node) {
	                if (n.isTextual()) cfg.columns.add(n.asText().trim());
	            }
	            return;
	        }

	        if (!node.isObject()) {
	            return;
	        }

	        if (node.has("idColumn")) {
	            String v = node.get("idColumn").asText().trim();
	            if (!v.isEmpty()) {
	                cfg.idColumn = v;
	                cfg.columns.add(v);
	            }
	        }
	        if (node.has("orderIdColumn")) {
	            String v = node.get("orderIdColumn").asText().trim();
	            if (!v.isEmpty()) {
	                cfg.orderIdColumn = v;
	                cfg.columns.add(v);
	            }
	        }
	        if (node.has("columns") && node.get("columns").isArray()) {
	            for (JsonNode c : node.get("columns")) {
	                if (c.isTextual()) cfg.columns.add(c.asText().trim());
	            }
	        }
	    }

	    private LinkedHashMap<String, String> buildLookupCriteria(LookupConfig lookup, PayloadRecord payload, JsonNode expectedRow) {
	        LinkedHashMap<String, String> criteria = new LinkedHashMap<>();
	        for (String column : lookup.columns) {
	            String value = payloadValueForColumn(column, payload);
	            if (value.isEmpty()) {
	                value = rowValue(expectedRow, column);
	            }
	            if (!value.isEmpty()) {
	                criteria.put(column, value);
	            }
	        }

	        if (criteria.isEmpty()) {
	            if (lookup.idColumn != null) {
	                String v = payloadValueForColumn(lookup.idColumn, payload);
	                if (v.isEmpty()) v = rowValue(expectedRow, lookup.idColumn);
	                if (!v.isEmpty()) criteria.put(lookup.idColumn, v);
	            }
	            if (lookup.orderIdColumn != null) {
	                String v = payloadValueForColumn(lookup.orderIdColumn, payload);
	                if (v.isEmpty()) v = rowValue(expectedRow, lookup.orderIdColumn);
	                if (!v.isEmpty()) criteria.put(lookup.orderIdColumn, v);
	            }
	        }

	        return criteria;
	    }

	    private String payloadValueForColumn(String column, PayloadRecord payload) {
	        String type = payloadMappedTypeForColumn(column);
	        if ("eventId".equals(type)) return payload.eventId;
	        if ("orderId".equals(type)) return payload.orderId;
	        return "";
	    }

	    private String payloadMappedTypeForColumn(String column) {
	        if (column == null) return "";
	        String c = column.trim().toLowerCase(Locale.ROOT).replace("-", "_");
	        if (c.equals("id") || c.equals("event_id") || c.equals("eventid")) return "eventId";
	        if (c.equals("orderid") || c.equals("order_id")) return "orderId";
	        return "";
	    }

	    private String rowValue(JsonNode row, String requestedColumn) {
	        if (row == null || !row.isObject() || requestedColumn == null) return "";
	        String c = requestedColumn.trim();
	        if (c.isEmpty()) return "";

	        String cUnderscore = c.replace('-', '_');
	        String cHyphen = c.replace('_', '-');

	        return firstText(row, c, cUnderscore, cHyphen);
	    }

	    private void applySchemaTablePolicy(String tableName, JsonCompare.Schema schema) {
	        TableColumnPolicy policy = getTablePolicy(tableName);
	        for (String f : policy.required) {
	            if (!schema.requiredFields.contains(f)) schema.requiredFields.add(f);
	        }
	        for (String f : policy.optional) {
	            if (!schema.optionalFields.contains(f)) schema.optionalFields.add(f);
	        }
	    }

	    private JsonNode applyTableIgnorePolicy(String tableName, JsonNode expectedRow) {
	        if (expectedRow == null || !expectedRow.isObject()) return expectedRow;

	        TableColumnPolicy policy = getTablePolicy(tableName);
	        if (policy.ignore.isEmpty()) {
	            return expectedRow;
	        }

	        ObjectNode out = ((ObjectNode) expectedRow).deepCopy();
	        for (String c : policy.ignore) {
	            out.remove(c);
	            out.remove(c.replace('-', '_'));
	            out.remove(c.replace('_', '-'));
	        }
	        return out;
	    }

	    private TableColumnPolicy getTablePolicy(String tableName) {
	        if (tablePolicyCache.containsKey(tableName)) {
	            return tablePolicyCache.get(tableName);
	        }

	        TableColumnPolicy policy = new TableColumnPolicy();
	        Path rulesPath = Path.of(schemaDir, "table_columns.json");
	        if (java.nio.file.Files.exists(rulesPath)) {
	            try {
	                JsonNode root = mapper.readTree(rulesPath.toFile());
	                if (root.isObject() && root.has(tableName)) {
	                    JsonNode node = root.get(tableName);
	                    if (node.isArray() && node.size() > 0) node = node.get(0);
	                    if (node.isObject()) {
	                        policy.required.addAll(readStringArray(node, "required"));
	                        policy.optional.addAll(readStringArray(node, "optional"));
	                        policy.ignore.addAll(readStringArray(node, "ignore"));
	                        policy.ignore.addAll(readStringArray(node, "ignored"));
	                    }
	                }
	            } catch (Exception ex) {
	                log("TABLE POLICY: failed to load " + rulesPath + " -> " + ex.getMessage());
	            }
	        }

	        policy.required = dedup(policy.required);
	        policy.optional = dedup(policy.optional);
	        policy.ignore = dedup(policy.ignore);

	        tablePolicyCache.put(tableName, policy);
	        return policy;
	    }

	    private List<String> readStringArray(JsonNode node, String field) {
	        List<String> out = new ArrayList<>();
	        if (node == null || !node.has(field) || !node.get(field).isArray()) return out;
	        for (JsonNode v : node.get(field)) {
	            if (v.isTextual()) {
	                String s = v.asText().trim();
	                if (!s.isEmpty()) out.add(s.replace('-', '_'));
	            }
	        }
	        return out;
	    }

	    private <T> List<T> dedup(List<T> values) {
	        LinkedHashSet<T> set = new LinkedHashSet<>(values);
	        return new ArrayList<>(set);
	    }

	    private void printScenarioTableSummary(ValidationReport report) {
	        int pass = 0;
	        int fail = 0;
	        int skipped = 0;
	        for (JsonCompare.ColumnResult r : report.results) {
	            if ("PASS".equals(r.status)) pass++;
	            if ("FAIL".equals(r.status)) fail++;
	            if ("SKIPPED".equals(r.status)) skipped++;
	        }

	        System.out.println("Status      : " + report.status);
	        System.out.println("Columns     : total=" + report.results.size() + " pass=" + pass + " fail=" + fail + " skipped=" + skipped);
	        System.out.println(SEP);
	    }

	    private void printRunHeader() {
	        System.out.println(SEP);
	        System.out.println("DB EVENT VALIDATION");
	        System.out.println("Payload     : " + payloadPath);
	        System.out.println("Expected    : " + expectedPath);
	        System.out.println("Schema dir  : " + schemaDir);
	        System.out.println(SEP);
	    }

	    private void printScenarioHeader(String eventId, String orderId) {
	        System.out.println("Scenario");
	        System.out.println("  eventId   : " + eventId);
	        System.out.println("  orderId   : " + orderId);
	    }

	    private void printTableHeader(String tableName, int expectedRows) {
	        System.out.println("Table       : " + tableName);
	        System.out.println("ExpectedRows: " + expectedRows);
	    }

	    private void printRunSummary() {
	        int reportPass = 0;
	        int reportFail = 0;
	        int colPass = 0;
	        int colFail = 0;
	        int colSkipped = 0;

	        for (ValidationReport r : reports) {
	            if ("FAIL".equals(r.status)) reportFail++; else reportPass++;
	            for (ColumnResult c : r.results) {
	                if ("PASS".equals(c.status)) colPass++;
	                if ("FAIL".equals(c.status)) colFail++;
	                if ("SKIPPED".equals(c.status)) colSkipped++;
	            }
	        }

	        System.out.println("RUN SUMMARY");
	        System.out.println("  tableReports : " + reports.size() + " (pass=" + reportPass + ", fail=" + reportFail + ")");
	        System.out.println("  columns      : pass=" + colPass + ", fail=" + colFail + ", skipped=" + colSkipped);
	        Path passFile = writePassReportFile();
	        System.out.println("  passReport   : " + passFile);
	        printValidationCasesTable(false);
	        System.out.println(SEP);
	    }

	    private void printValidationCasesTable(boolean includePass) {
	        List<String[]> rows = new ArrayList<>();
	        for (ValidationReport report : reports) {
	            if (report.globalErrors != null && !report.globalErrors.isEmpty()) {
	                for (String err : report.globalErrors) {
	                    rows.add(new String[]{
	                            report.tableName,
	                            "<global>",
	                            report.eventId,
	                            "<n/a>",
	                            "<n/a>",
	                            "FAIL",
	                            normalizeCell(err)
	                    });
	                }
	            }
	            for (ColumnResult r : report.results) {
	                if (!includePass && !"FAIL".equals(r.status) && !"SKIPPED".equals(r.status)) {
	                    continue;
	                }
	                rows.add(new String[]{
	                        report.tableName,
	                        r.column,
	                        report.eventId,
	                        normalizeCell(r.expected),
	                        normalizeCell(r.actual),
	                        r.status,
	                        normalizeCell(r.reason)
	                });
	            }
	        }

	        if (rows.isEmpty()) {
	            System.out.println(includePass ? "ALL VALIDATION CASES" : "FAILED/SKIPPED CASES");
	            System.out.println("  none");
	            return;
	        }

	        String[] headers = {"table", "columnname", "order/id", "expected", "actual", "status", "error message"};
	        int[] widths = new int[headers.length];
	        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
	        for (String[] row : rows) {
	            for (int i = 0; i < row.length; i++) {
	                widths[i] = Math.max(widths[i], row[i].length());
	            }
	        }

	        for (int i = 0; i < widths.length; i++) widths[i] = Math.min(widths[i], 60);

	        System.out.println(includePass ? "ALL VALIDATION CASES" : "FAILED/SKIPPED CASES");
	        System.out.println(formatTableRow(headers, widths));
	        System.out.println(formatTableSeparator(widths));
	        for (String[] row : rows) {
	            String[] clipped = new String[row.length];
	            for (int i = 0; i < row.length; i++) clipped[i] = clip(row[i], widths[i]);
	            System.out.println(formatTableRow(clipped, widths));
	        }
	    }

	    private Path writePassReportFile() {
	        List<String[]> rows = new ArrayList<>();
	        for (ValidationReport report : reports) {
	            for (ColumnResult r : report.results) {
	                if (!"PASS".equals(r.status)) {
	                    continue;
	                }
	                rows.add(new String[]{
	                        report.tableName,
	                        r.column,
	                        report.eventId,
	                        normalizeCell(r.expected),
	                        normalizeCell(r.actual),
	                        r.status
	                });
	            }
	        }

	        try {
	            Path outDir = Path.of("target", "validation-reports");
	            Files.createDirectories(outDir);
	            Path outFile = outDir.resolve("pass-report-" + System.currentTimeMillis() + ".csv");
	            List<String> lines = new ArrayList<>();
	            lines.add("table,columnname,order/id,expected,actual,status");
	            for (String[] row : rows) {
	                lines.add(csv(row));
	            }
	            Files.write(outFile, lines, StandardCharsets.UTF_8);
	            return outFile;
	        } catch (Exception ex) {
	            log("PASS REPORT: failed to write file -> " + ex.getMessage());
	            return Path.of("target", "validation-reports", "pass-report-write-failed.csv");
	        }
	    }

	    private String csv(String[] row) {
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < row.length; i++) {
	            if (i > 0) sb.append(",");
	            String val = row[i] == null ? "" : row[i];
	            String escaped = val.replace("\"", "\"\"");
	            sb.append("\"").append(escaped).append("\"");
	        }
	        return sb.toString();
	    }

	    private String formatTableRow(String[] values, int[] widths) {
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < values.length; i++) {
	            if (i > 0) sb.append(" | ");
	            sb.append(padRight(values[i], widths[i]));
	        }
	        return sb.toString();
	    }

	    private String formatTableSeparator(int[] widths) {
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < widths.length; i++) {
	            if (i > 0) sb.append("-+-");
	            sb.append("-".repeat(widths[i]));
	        }
	        return sb.toString();
	    }

	    private String padRight(String value, int width) {
	        String v = value == null ? "" : value;
	        if (v.length() >= width) return v;
	        return v + " ".repeat(width - v.length());
	    }

	    private String clip(String value, int max) {
	        String v = value == null ? "" : value;
	        if (v.length() <= max) return v;
	        if (max <= 3) return v.substring(0, max);
	        return v.substring(0, max - 3) + "...";
	    }

	    private String normalizeCell(String value) {
	        if (value == null) return "<null>";
	        String v = value.replace("\r", " ").replace("\n", " ").trim();
	        if (v.isEmpty()) return "<empty>";
	        return v;
	    }

	    private List<PayloadRecord> extractPayloadRecords(JsonNode node) {
	        List<PayloadRecord> records = new ArrayList<>();
	        collectPayloadRecords(node, records);
	        return records;
	    }

	    private void collectPayloadRecords(JsonNode node, List<PayloadRecord> records) {
	        if (node == null || node.isNull()) {
	            return;
	        }
	        if (node.isArray()) {
	            for (JsonNode child : node) {
	                collectPayloadRecords(child, records);
	            }
	            return;
	        }
	        if (!node.isObject()) {
	            return;
	        }

	        String eventId = firstText(node, "event-id", "id", "event_id");
	        String orderId = firstText(node, "order-id", "orderid", "order_id");
	        if (orderId.isEmpty()) {
	            orderId = nestedText(node, "data", "orderId");
	        }

	        if (!eventId.isEmpty() || !orderId.isEmpty()) {
	            records.add(new PayloadRecord(eventId, orderId));
	        }
	    }

	    private String firstText(JsonNode node, String... names) {
	        for (String name : names) {
	            if (node.has(name) && !node.get(name).isNull()) {
	                String v = node.get(name).asText().trim();
	                if (!v.isEmpty()) return v;
	            }
	        }
	        return "";
	    }

	    private String nestedText(JsonNode node, String parent, String child) {
	        if (node.has(parent) && node.get(parent).isObject()) {
	            JsonNode p = node.get(parent);
	            if (p.has(child) && !p.get(child).isNull()) {
	                return p.get(child).asText().trim();
	            }
	        }
	        return "";
	    }

	    private JsonNode normalizeExpectedRow(JsonNode row) {
	        if (!row.isObject()) {
	            return row;
	        }
	        ObjectNode normalized = mapper.createObjectNode();
	        Iterator<String> fields = row.fieldNames();
	        while (fields.hasNext()) {
	            String field = fields.next();
	            String normalizedField = field.replace('-', '_');
	            normalized.set(normalizedField, row.get(field));
	        }
	        return normalized;
	    }

	    private static class PayloadRecord {
	        final String eventId;
	        final String orderId;

	        PayloadRecord(String eventId, String orderId) {
	            this.eventId = eventId == null ? "" : eventId;
	            this.orderId = orderId == null ? "" : orderId;
	        }
	    }

	    private static class LookupConfig {
	        List<String> columns = new ArrayList<>();
	        String idColumn;
	        String orderIdColumn;
	    }

	    private static class ExpectedTable {
	        final String tableName;
	        final List<JsonNode> matchedRows;
	        final JsonCompare.Schema schema;
	        final LookupConfig lookupConfig;

	        ExpectedTable(String tableName, List<JsonNode> matchedRows, JsonCompare.Schema schema, LookupConfig lookupConfig) {
	            this.tableName = tableName;
	            this.matchedRows = matchedRows;
	            this.schema = schema;
	            this.lookupConfig = lookupConfig;
	        }

	        List<JsonNode> getMatchedRows(PayloadRecord payloadRecord) {
	            LinkedHashMap<String, JsonNode> dedup = new LinkedHashMap<>();
	            for (JsonNode row : matchedRows) {
	                String rowEventId = row.has("id") ? row.get("id").asText() : row.path("event_id").asText("");
	                String rowOrderId = row.has("orderid") ? row.get("orderid").asText() : row.path("order_id").asText("");

	                boolean matchEvent = payloadRecord.eventId.isEmpty() || payloadRecord.eventId.equals(rowEventId);
	                boolean matchOrder = payloadRecord.orderId.isEmpty() || payloadRecord.orderId.equals(rowOrderId);
	                if (matchEvent || matchOrder) {
	                    dedup.put(row.toString(), row);
	                }
	            }
	            return new ArrayList<>(dedup.values());
	        }
	    }

	    private static class TableColumnPolicy {
	        List<String> required = new ArrayList<>();
	        List<String> optional = new ArrayList<>();
	        List<String> ignore = new ArrayList<>();
	    }
	}
