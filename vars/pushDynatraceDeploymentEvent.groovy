import groovy.json.JsonOutput

/***************************\
  This function assumes we run on a Jenkins Agent that has curl command available.

  Returns either 0(=no errors), 1(=pushing event failed)
\***************************/
def call( Map args ) 
    
    /*String dtTenantUrl, 
            String dtApiToken*/
{
    // check input arguments
    String dtTenantUrl = args.containsKey("dtTenantUrl") ? args.dtTenantUrl : "${DT_TENANT_URL}"
    String dtApiToken = args.containsKey("dtApiToken") ? args.dtApiToken : "${DT_API_TOKEN}"
    def tagRule = args.containsKey("tagRule") ? args.tagRule : ""
    
    String deploymentName = args.containsKey("deploymentName") ? args.deploymentName : "${env.JOB_NAME}"
    String deploymentVersion = args.containsKey("deploymentVersion") ? args.deploymentVersion : "${env.VERSION}"
    String deploymentProject = args.containsKey("deploymentProject") ? args.deploymentProject : ""
    String ciBackLink = args.containsKey("ciBackLink") ? args.ciBackLink : "${env.BUILD_URL}"

    // check minimum required params
    if(tagRule == "" ) {
        echo "tagRule is a mandatory parameter!"
        return -1
    }
 
    String eventType = "CUSTOM_DEPLOYMENT"

    int errorCode = 0

    // lets push the event
    //sh "curl -X POST \"${dtTenantUrl}/api/v1/events?Api-Token=${dtApiToken}\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -d \"{ \\\"eventType\\\": \\\"${eventType}\\\", \\\"attachRules\\\": { \\\"tagRule\\\" : [{ \\\"meTypes\\\" : [\\\"${tagRule[0].meTypes[0].meType}\\\"], \\\"tags\\\" : [ { \\\"context\\\" : \\\"${tagRule[0].tags[0].context}\\\", \\\"key\\\" : \\\"${tagRule[0].tags[0].key}\\\", \\\"value\\\" : \\\"${tagRule[0].tags[0].value}\\\" }, { \\\"context\\\" : \\\"${tagRule[0].tags[1].context}\\\", \\\"key\\\" : \\\"${tagRule[0].tags[1].key}\\\", \\\"value\\\" : \\\"${tagRule[0].tags[1].value}\\\" } ] }] }, \\\"deploymentName\\\":\\\"${deploymentName}\\\", \\\"deploymentVersion\\\":\\\"${deploymentVersion}\\\", \\\"deploymentProject\\\":\\\"${deploymentProject}\\\", \\\"ciBackLink\\\":\\\"${ciBackLink}\\\", \\\"source\\\":\\\"Jenkins\\\", \\\"customProperties\\\": { \\\"Jenkins Build Number\\\": \\\"${env.BUILD_ID}\\\",  \\\"Git commit\\\": \\\"${env.GIT_COMMIT}\\\" } }\" "

    int numberOfTags = tagRule[0].tags.size()

    String curlCmd = "curl -X POST \"${dtTenantUrl}/api/v1/events?Api-Token=${dtApiToken}\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -d \"{" 
    curlCmd += " \\\"eventType\\\": \\\"${eventType}\\\","
    curlCmd += " \\\"attachRules\\\": { \\\"tagRule\\\" : [{ \\\"meTypes\\\" : [\\\"${tagRule[0].meTypes[0].meType}\\\"],"

    curlCmd += " \\\"tags\\\" : [ "
    
    tagRule[0].tags.eachWithIndex { tag, i ->
        curlCmd += "{ \\\"context\\\" : \\\"${tag.context}\\\", \\\"key\\\" : \\\"${tag.key}\\\", \\\"value\\\" : \\\"${tag.value}\\\" }"
        if(i < (numberOfTags - 1)) {
            curlCmd += ", "
        }
    }
    curlCmd += " ] }] },"

    curlCmd += " \\\"deploymentName\\\":\\\"${deploymentName}\\\", \\\"deploymentVersion\\\":\\\"${deploymentVersion}\\\", \\\"deploymentProject\\\":\\\"${deploymentProject}\\\", \\\"ciBackLink\\\":\\\"${ciBackLink}\\\", \\\"source\\\":\\\"Jenkins\\\","
    curlCmd += " \\\"customProperties\\\": { \\\"Jenkins Build Number\\\": \\\"${env.BUILD_ID}\\\",  \\\"Git commit\\\": \\\"${env.GIT_COMMIT}\\\" }"
    curlCmd += " }\" "

    //sh "echo ${curlCmd}"

    sh "${curlCmd}"

    return errorCode
}