import groovy.json.JsonOutput

/***************************\
  This function assumes we run on a Jenkins Agent that has curl command available.

  Returns either 0(=no errors), 1(=pushing event failed)
\***************************/
def call( Map args ) 
    
    /*  String dtTenantUrl, 
        String dtTenantUrl 
        String dtApiToken 
        def tagRule 
        String deploymentName 
        String deploymentVersion 
        String deploymentProject
        String ciBackLink 
        String buildId 
        String gitCommitId 
    */
{
    // check input arguments
    String dtTenantUrl = args.containsKey("dtTenantUrl") ? args.dtTenantUrl : "${DT_TENANT_URL}"
    String dtApiToken = args.containsKey("dtApiToken") ? args.dtApiToken : "${DT_API_TOKEN}"
    def tagRule = args.containsKey("tagRule") ? args.tagRule : ""
    
    String description = args.containsKey("description") ? args.description : ""
    String source = args.containsKey("source") ? args.source : ""
    String configuration = args.containsKey("configuration") ? args.configuration : ""

    String remediationAction = args.containsKey("remediationAction") ? args.remediationAction : ""

    // check minimum required params
    if(tagRule == "" ) {
        echo "tagRule is a mandatory parameter!"
        return -1
    }
 
    String eventType = "CUSTOM_CONFIGURATION"

    int errorCode = 0

    // build the curl command
    int numberOfTags = tagRule[0].tags.size()

    String curlCmd = "curl -X POST \"${dtTenantUrl}/api/v1/events?Api-Token=${dtApiToken}\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -d \"{" 
    curlCmd += " \\\"eventType\\\": \\\"${eventType}\\\","
    curlCmd += " \\\"attachRules\\\": { \\\"tagRule\\\" : [{ \\\"meTypes\\\" : [\\\"${tagRule[0].meTypes[0].meType}\\\"],"

    curlCmd += " \\\"tags\\\" : [ "
    tagRule[0].tags.eachWithIndex { tag, i ->
        curlCmd += "{ \\\"context\\\" : \\\"${tag.context}\\\", \\\"key\\\" : \\\"${tag.key}\\\", \\\"value\\\" : \\\"${tag.value}\\\" }"
        if(i < (numberOfTags - 1)) { curlCmd += ", " }
    }
    curlCmd += " ] }] },"

    curlCmd += " \\\"description\\\":\\\"${description}\\\", \\\"source\\\":\\\"${source}\\\", \\\"configuration\\\":\\\"${configuration}\\\", "
    curlCmd += " \\\"customProperties\\\": { \\\"remediationAction\\\": \\\"${remediationAction}\\\" }"
    curlCmd += " }\" "

    // push the event
    sh "${curlCmd}"      

    return errorCode
}