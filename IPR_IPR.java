[
    [
    {
      "id": "dcc-test-01",
      "source": "EOS",
      "type": "eos.orders.allocated",
      "data": {
        "orderId": "dcc-1-1001",
        "status": "Allocated"
      },
      "time": "2025-10-30T10:28:21.7223026+00:00",
      "specversion": "1.0",
      "dataschema": "#",
      "datacontenttype": "application/json",
      "xrequestid": "6f3483b16b13d94d8df7f7443f1d717a",
      "traceparent": "00-6f3483b16b13d94d8df7f7443f1d717a-5f3771e6a0385f95-00"
    }
  ],
  [
    {
      "id": "dcc-test-02",
      "source": "EOS",
      "type": "eos.orders.allocated",
      "data": {
        "orderId": "col-dcc1007",
        "status": "Allocated"
      },
      "time": "2025-10-30T10:28:21.7223026+00:00",
      "specversion": "1.0",
      "dataschema": "#",
      "datacontenttype": "application/json",
      "xrequestid": "6f3483b16b13d94d8df7f7443f1d717a",
      "traceparent": "00-6f3483b16b13d94d8df7f7443f1d717a-5f3771e6a0385f95-00"
    }
  ]
]



[
 {
  "event_id": "dcc-test-01",
  "event_type": "eos.orders.allocated",
  "event_datetime": "2025-10-30 10:28:21.722303+00",
  "event_payload": "[{\"id\":\"dcc-test-01\",\"source\":\"EOS\",\"type\":\"eos.orders.allocated\",\"data\":{\"orderId\":\"dcc-1-1001\",\"status\":\"Allocated\"},\"time\":\"2025-10-30T10:28:21.7223026+00:00\",\"specversion\":\"1.0\",\"dataschema\":\"#\",\"datacontenttype\":\"application/json\",\"xrequestid\":\"6f3483b16b13d94d8df7f7443f1d717a\",\"traceparent\":\"00-6f3483b16b13d94d8df7f7443f1d717a-5f3771e6a0385f95-00\"}]",
  "poi_event_payload": "[{\"specversion\":\"1.0\",\"type\":\"dss.orders.liq.exptype\",\"source\":\"DCC\",\"id\":\"a8cb4ed6-fabe-47e6-8acd-06195a51d662\",\"time\":\"2026-02-16T16:26:25.591662847+11:00\",\"dataschema\":\"#\",\"datacontenttype\":\"application/json\",\"data\":{\"orderId\":\"dcc-1-1001\",\"complianceRequired\":\"ID25\",\"sameDay\":false,\"vicFirstTimeLiq\":null,\"updatedDateTime\":\"2026-02-13T12:37:19Z\",\"associationId\":\"20260213123719\",\"liq\":true}}]",
  "event_operation": "PUBLISH_POI_DATA",
  "retry_attempts": "0",
  "job_set_time": "NULL"
 }
]





[
 {
  "audit_id": "3733",
  "event_id": "event-dcc-01",
  "event_operation": "ORD_PROC_EVENT",
  "activity_start_datetime": "2026-02-10 17:45:03.5566",
  "activity_end_datetime": "2026-02-10 17:45:03.620409",
  "audit_datetime": "2026-02-10 17:45:03.620588",
  "exception": "NULL"
 },
 {
  "audit_id": "3734",
  "event_id": "event-dcc-01",
  "event_operation": "PUBLISH_POI_DATA",
  "activity_start_datetime": "2026-02-10 17:45:04.117116",
  "activity_end_datetime": "2026-02-10 17:45:05.758123",
  "audit_datetime": "2026-02-10 17:45:05.771213",
  "exception": "NULL"
 }
]



@Success_Scenarios_Create
Feature: [TECH]: DCC - US1 DCC
  @DCC
 
		@Phase2
  	Scenario Outline: Validate DB data for event payload
  	Given payload file "src/main/resources/payloads/event_payload.json" and expected file "src/main/resources/expected/dcc_expected_data.json" and schema dir "src/main/resources/schemas"
    Then database values should match expected data
		
	      
	      
