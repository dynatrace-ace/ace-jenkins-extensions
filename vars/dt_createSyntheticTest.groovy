@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7' )
 
import groovyx.net.http.HTTPBuilder
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
    String testName = args.containsKey("testName") ? args.testName : ""
    String url = args.containsKey("url") ? args.url : ""
    String method = args.containsKey("method") ? args.method : "GET"
    String frequency = args.containsKey("frequency") ? args.frequency : 1
    String location = args.containsKey("location") ? args.location : ""
    
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
    /*String curlCmd = "curl -X POST \"${dtTenantUrl}/api/v1/synthetic/monitors?Api-Token=${dtApiToken}\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -d \"{" 
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
    curlCmd += " \\\"locations\\\": [\\\"${location}\\\"]}" */
      
    // push the event
    //sh "${curlCmd}"

    def http = new HTTPBuilder( ${dtTenantUrl}+'/api/v1/synthetic/monitors' )
    http.request( POST, JSON ) { req ->
      headers.'Authorization' = 'Api-Token '+${dtApiToken}
      headers.'Content-Type' = 'application/json'
      body = [
        name: ${testName},
        frequencyMin: ${frequency},
        enabled: true,
        type: "HTTP",
        script: [
          version: "1.0",
          requests: [
            [
              description: ${testName},
              url: ${url},
              method: ${method}
            ]
          ]
        ],
        locations: [
          ${location}
        ]
      ]
  
      response.success = { resp, json ->
          // response handling here
      }
      response.failure = { resp, json ->
        throw new Exception("Stopping at item POST: uri: " + uri + "\n" +
            "   Unknown error trying to create item: ${resp.status}, not creating Item." +
            "\njson = ${json}")
      }
    }

    return errorCode
}