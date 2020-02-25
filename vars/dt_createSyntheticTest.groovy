import groovy.json.JsonOutput

/***************************\
  This function assumes we run on a Jenkins Agent that has curl command available.

  Returns either 0(=no errors), 1(=pushing event failed)
\***************************/
def call( Map args ) 
    
    /*  String dtTenantUrl, 
        String dtApiToken 

        String testName 
        String url
        String method
        String frequency

        String location

    */
{
    // check input arguments
    String dtTenantUrl = args.containsKey("dtTenantUrl") ? args.dtTenantUrl : "${DT_TENANT_URL}"
    String dtApiToken = args.containsKey("dtApiToken") ? args.dtApiToken : "${DT_API_TOKEN}"
    String testName = args.containsKey("testName") ? args.remediationAction : ""
    String url = args.containsKey("url") ? args.remediationAction : ""
    String method = args.containsKey("method") ? args.remediationAction : "GET"
    String frequency = args.containsKey("frequency") ? args.remediationAction : 1
    String location = args.containsKey("location") ? args.remediationAction : ""
    
    // check minimum required params
    if(testName == "" ) {
        echo "testName is a mandatory parameter!"
        return -1
    }
    if(url == "" ) {
        echo "url is a mandatory parameter!"
        return -1
    }
    if(location == "" ) {
        echo "location is a mandatory parameter!"
        return -1
    }

    int errorCode = 0

    // set Dynatrace URL, API Token and Event Type.
    String curlCmd = "curl -X POST \"${dtTenantUrl}/api/v1/synthetic/monitors?Api-Token=${dtApiToken}\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -d \"{" 
    curlCmd += " \\\"name\\\": \\\"${testName}\\\","
    curlCmd += " \\\"frequencyMin\\\": \\\"${frequency}\\\","
    curlCmd += " \\\"enabled\\\": true,"
    curlCmd += " \\\"type\\\": \\\"HTTP\\\","
    curlCmd += " \\\"script\\\": {"
    curlCmd += " \\\"version\\\": \\\"1.0\\\","
    curlCmd += " \\\"requests\\\": [{"
    curlCmd += " \\\"description\\\": \\\"${testName}\\\","
    curlCmd += " \\\"url\\\": \\\"${url}\\\","
    curlCmd += " \\\"method\\\": \\\"${method}\\\"}]},"
    curlCmd += " \\\"locations\\\": [\\\"${location}\\\"]}"
      
    // push the event
    sh "${curlCmd}"

    return errorCode
}