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
    String tagRule = args.containsKey("tagRule") ? args.tagRule : ""
    
    String deploymentName = args.containsKey("deploymentName") ? args.deploymentName : "${env.JOB_NAME}"
    String deploymentVersion = args.containsKey("deploymentVersion") ? args.deploymentVersion : "${env.VERSION}"
    String deploymentProject = args.containsKey("deploymentProject") ? args.deploymentProject : ""
    String ciBackLink = args.containsKey("ciBackLink") ? args.ciBackLink : "${env.BUILD_URL}"

    // check minimum required params
    if(tagRule == "" ) {
        echo "Tag rule is a mandatory parameter!"
        return -1
    }
 
    String eventType = "CUSTOM_DEPLOYMENT"

    int errorCode = 0

    sh "echo ${tagRule}"

    // lets push the event
    sh "curl -X POST \"${dtTenantUrl}/api/v1/events?Api-Token=${dtApiToken}\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -d \"{ \\\"eventType\\\": \\\"${eventType}\\\", \\\"attachRules\\\": { \\\"tagRule\\\" : ${tagRule} }, \\\"deploymentName\\\":\\\"${env.JOB_NAME}\\\", \\\"deploymentVersion\\\":\\\"${env.VERSION}\\\", \\\"deploymentProject\\\":\\\"\\\", \\\"ciBackLink\\\":\\\"${env.BUILD_URL}\\\", \\\"source\\\":\\\"Jenkins\\\", \\\"customProperties\\\": { \\\"Jenkins Build Number\\\": \\\"${env.BUILD_ID}\\\",  \\\"Git commit\\\": \\\"${env.GIT_COMMIT}\\\" } }\" "
            
    return errorCode
}