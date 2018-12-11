/***************************\
  This function assumes we run on a Jenkins Agent that has curl command available.

  Returns either 0(=no errors), 1(=pushing event failed)
\***************************/
def call( Map args ) 
    
    /*String dtTenantUrl, 
            String dtApiToken*/
{
    // check input arguments
    String dtTenantUrl = args.containsKey("dtTenantUrl") ? args.dtTenantUrl : ""
    String dtApiToken = args.containsKey("dtApiToken") ? args.dtApiToken : ""
    String tagRule = args.containsKey("tagRule") ? args.tagRule : ""

    // check minimum required params
    if(dtTenantUrl == "" || dtApiToken == "") {
        echo "<Dynatrace Tenant Url> and <Dynatrace API Token> are mandatory parameters!"
        return -1
    }

    String eventType = "CUSTOM_DEPLOYMENT"

    int errorCode = 0

    sh "echo ${tagRule}"

    // lets push the event
    // sh "curl -X POST \"${dtTenantUrl}/api/v1/events?Api-Token=${dtApiToken}\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -d \"{ \\\"eventType\\\": \\\"${eventType}\\\", \\\"attachRules\\\": { \\\"tagRule\\\" : [{ \\\"meTypes\\\" : [\\\"SERVICE\\\"], \\\"tags\\\" : [ { \\\"context\\\" : \\\"CONTEXTLESS\\\", \\\"key\\\" : \\\"app\\\", \\\"value\\\" : \\\"${app}\\\" }, { \\\"context\\\" : \\\"CONTEXTLESS\\\", \\\"key\\\" : \\\"environment\\\", \\\"value\\\" : \\\"${environment}\\\" } ] }] }, ${annotations}, \\\"customProperties\\\": { ${customProperties} } }\" "
    
    return errorCode
}